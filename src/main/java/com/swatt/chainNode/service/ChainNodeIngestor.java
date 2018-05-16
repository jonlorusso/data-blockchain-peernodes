package com.swatt.chainNode.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeListener;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.util.general.OperationFailedException;

public class ChainNodeIngestor implements ChainNodeListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChainNodeIngestor.class);

    private PreparedStatement queryPreparedStatement;
    private PreparedStatement insertPreparedStatement;
    private PreparedStatement maxIdPreparedStatement;

    private static final String BLOCKDATA_QUERY = String.format("SELECT %s FROM %s WHERE BLOCKCHAIN_CODE = ? AND HEIGHT = ?", BlockData.getSqlColumnList(), BlockData.getStandardTableName());
    private static final String BLOCKDATA_INSERT = "INSERT INTO BLOCK_DATA (BLOCKCHAIN_CODE, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, DIFFICULTY_SCALE, REWARD, REWARD_SCALE, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_SCALE, AVG_FEE_RATE, AVG_FEE_RATE_SCALE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_TX_AMOUNT_SCALE, LARGEST_FEE, LARGEST_FEE_SCALE, SMALLEST_FEE, SMALLEST_FEE_SCALE, INDEXING_DURATION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String BLOCKDATA_MAX_ID_QUERY = "Select MAX(ID) FROM BLOCK_DATA";

    public static final String BLOCKCHAIN_CODES_PROPERTY = "ingestor.blockchainCodes";
    public static final String BACKFILL_PROPERTY = "ingestor.backfill";
    public static final String OVERWRITE_EXISTING_PROPERTY = "ingestor.overwriteExisting";
    public static final String STOP_HEIGHT_PROPERTY = "ingestor.stopHeight";
    
    private boolean backfill = true;
    private boolean overwriteExisting = false;
    private long stopHeight = 0;
    
    private ChainNode chainNode;
    private Connection connection;
    private Thread synchronizeChainThread;

    public ChainNodeIngestor(Properties properties, ChainNode chainNode, Connection connection) throws SQLException {
        super();
        
        backfill = Boolean.valueOf(properties.getProperty(BACKFILL_PROPERTY, String.valueOf(backfill)));
        overwriteExisting = Boolean.valueOf(properties.getProperty(OVERWRITE_EXISTING_PROPERTY, String.valueOf(overwriteExisting)));
        stopHeight = Long.valueOf(properties.getProperty(STOP_HEIGHT_PROPERTY, String.valueOf(stopHeight)));
        
        this.chainNode = chainNode;
        this.connection = connection;

        queryPreparedStatement = connection.prepareStatement(BLOCKDATA_QUERY);
        insertPreparedStatement = connection.prepareStatement(BLOCKDATA_INSERT);
        maxIdPreparedStatement = connection.prepareStatement(BLOCKDATA_MAX_ID_QUERY);
    }

    @Override
    public void newBlockAvailable(ChainNode chainNode, BlockData blockData) {
        try {
            logInfo(chainNode.getBlockchainCode(), String.format("Block available, storing: %d", blockData.getHeight()));
            insertBlockData(blockData);
        } catch (SQLException e) {
            LOGGER.error("Exception caught while storing new block: " + e.getMessage());
        }
    }

    @Override
    public void newTransactionsAvailable(ChainNode chainNode, ChainNodeTransaction[] chainTransactions) {
        throw new UnsupportedOperationException("Unimplemented.");
    }

    // TODO this should be moved to BlockData
    private synchronized BlockData insertBlockData(BlockData blockData) throws SQLException {
        boolean storedAutoCommitValue = connection.getAutoCommit();
        connection.setAutoCommit(false);

        insertPreparedStatement.clearParameters();
        insertPreparedStatement.setString(1, blockData.getBlockchainCode());
        insertPreparedStatement.setString(2, blockData.getHash());
        insertPreparedStatement.setInt(3, blockData.getTransactionCount());
        insertPreparedStatement.setLong(4, blockData.getHeight());
        insertPreparedStatement.setLong(5, (long) (blockData.getDifficulty() * Math.pow(10, blockData.getDifficultyScale())));
        insertPreparedStatement.setInt(6, blockData.getDifficultyScale());
        insertPreparedStatement.setLong(7, (long) (blockData.getReward() * Math.pow(10, blockData.getRewardScale())));
        insertPreparedStatement.setInt(8, blockData.getRewardScale());
        insertPreparedStatement.setString(9, blockData.getMerkleRoot());
        insertPreparedStatement.setLong(10, blockData.getTimestamp());
        insertPreparedStatement.setString(11, blockData.getBits());
        insertPreparedStatement.setInt(12, blockData.getSize());
        insertPreparedStatement.setString(13, blockData.getVersionHex());
        insertPreparedStatement.setLong(14, blockData.getNonce());
        insertPreparedStatement.setString(15, blockData.getPrevHash());
        insertPreparedStatement.setString(16, blockData.getNextHash());
        insertPreparedStatement.setLong(17, (long) (blockData.getAvgFee() * Math.pow(10, blockData.getAvgFeeScale())));
        insertPreparedStatement.setInt(18, blockData.getAvgFeeScale());
        insertPreparedStatement.setLong(19, (long) (blockData.getAvgFeeRate() * Math.pow(10, blockData.getAvgFeeRateScale())));
        insertPreparedStatement.setInt(20, blockData.getAvgFeeRateScale());
        insertPreparedStatement.setLong(21, blockData.getIndexed());
        insertPreparedStatement.setString(22, blockData.getLargestTxHash());
        insertPreparedStatement.setLong(23, (long) (blockData.getLargestTxAmount() * Math.pow(10, blockData.getLargestTxAmountScale())));
        insertPreparedStatement.setInt(24, blockData.getLargestTxAmountScale());
        insertPreparedStatement.setDouble(25, blockData.getLargestFee());
        insertPreparedStatement.setInt(26, blockData.getLargestFeeScale());
        insertPreparedStatement.setLong(27, (long) (blockData.getSmallestFee() * Math.pow(10, blockData.getSmallestFeeScale())));
        insertPreparedStatement.setInt(28, blockData.getSmallestFeeScale());
        insertPreparedStatement.setLong(29, blockData.getIndexingDuration());
        insertPreparedStatement.executeUpdate();

        int autoGeneratedKey = 0;

        try (ResultSet resultSet = maxIdPreparedStatement.executeQuery()) {
            if (resultSet.next())
                autoGeneratedKey = resultSet.getInt(1);
        }

        blockData.setId(autoGeneratedKey);

        if (storedAutoCommitValue) {
            connection.commit();
            connection.setAutoCommit(true);
        }

        return blockData;
    }

    private BlockData getBlockData(PreparedStatement preparedStatement, long height) throws SQLException {
        preparedStatement.clearParameters();
        preparedStatement.setString(1, chainNode.getBlockchainCode());
        preparedStatement.setLong(2, height);

        List<BlockData> blockDatas = BlockData.getBlockDatas(preparedStatement);
        if (blockDatas != null && !blockDatas.isEmpty())
            return blockDatas.get(0);

        return null;
    }

    public void synchronizeChain() {
        if (!backfill)
            return;
        
        synchronizeChainThread = new Thread(() -> {
            try {
                long height = chainNode.fetchBlockCount();

                while (height > stopHeight) {
                    BlockData blockData = getBlockData(queryPreparedStatement, height);
                    if (blockData != null && !overwriteExisting) {
                        height = height - 1;
                        continue;
                    }
                    
                    insertBlockData(chainNode.fetchBlockData(height));
                    logInfo(chainNode.getBlockchainCode(), String.format("Block ingested: %d", height));

                    height = height - 1;
                }

                LOGGER.info("SynchronizeChain complete.");
            } catch (OperationFailedException | SQLException e) {
                e.printStackTrace();
            }
        }, "SynchronizeChain-" + chainNode.getCode());

        synchronizeChainThread.start();
    }

    private static void logInfo(String blockchainCode, String message) {
        LOGGER.info(String.format("[%s] %s", blockchainCode, message));
    }
}

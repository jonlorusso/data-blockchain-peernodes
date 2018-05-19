package com.swatt.chainNode.ingestor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeListener;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.chainNode.dao.CheckProgress;
import com.swatt.util.general.CollectionsUtilities;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.sql.ConnectionPool;

public class ChainNodeIngestor implements ChainNodeListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChainNodeIngestor.class);

    private static final String BLOCKDATA_QUERY = "WHERE BLOCKCHAIN_CODE = ? AND HEIGHT = ?";

    private boolean overwriteExisting = false;

    private Thread historicalIngestionThread;
    
    private ConnectionPool connectionPool;
    private ChainNode chainNode;

    public ChainNodeIngestor(ConnectionPool connectionPool, ChainNode chainNode) {
        super();

        this.connectionPool = connectionPool;
        this.chainNode = chainNode;
        
        chainNode.addChainNodeListener(this);
    }
    
    public ChainNode getChainNode() {
        return chainNode;
    }

    @Override
    public void newBlockAvailable(ChainNode chainNode, BlockData blockData) {
        try (Connection connection = connectionPool.getConnection()) {
            LOGGER.info(String.format("Block available, storing: %d", blockData.getHeight()));
            BlockData.insertBlockData(connection, blockData);
        } catch (SQLException e) {
            LOGGER.error("Exception caught while storing new block: " + e.getMessage());
        }
    }

    @Override
    public void newTransactionsAvailable(ChainNode chainNode, ChainNodeTransaction[] chainTransactions) {
        throw new UnsupportedOperationException("Unimplemented.");
    }

    private BlockData getBlockData(Connection connection, long height) throws SQLException {
        List<BlockData> blockDatas = BlockData.getBlockDatas(connection, BLOCKDATA_QUERY, height);
        if (!CollectionsUtilities.isNullOrEmpty(blockDatas))
            return blockDatas.get(0);

        return null;
    }

    public void startHistoricalIngestion() {
        if (historicalIngestionThread != null)
            return;
        
        historicalIngestionThread = new Thread(() -> {
            try (Connection connection = connectionPool.getConnection()) {
                long height = chainNode.fetchBlockCount();

                CheckProgress checkProgress = CheckProgress.call(connection, chainNode.getCode());
                long stopHeight = checkProgress.getBlockCount();
                
                while (height > stopHeight) {
                    BlockData blockData = getBlockData(connection, height);
                    if (blockData != null && !overwriteExisting) {
                        height = height - 1;
                        continue;
                    }

                    blockData = chainNode.fetchBlockData(height);
                    BlockData.insertBlockData(connection, blockData);

                    LOGGER.info(String.format("Block ingested: %d", height));

                    height = height - 1;
                }
            } catch (OperationFailedException | SQLException e) {
                historicalIngestionThread = null;
            }
        }, "HistoricalIngestion-" + chainNode.getCode());

        historicalIngestionThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                historicalIngestionThread = null;
            }
        });
        
        historicalIngestionThread.start();
    }

    public void startNewBlockIngestion() {
        chainNode.fetchNewBlocks();
    }
}

package com.swatt.chainNode.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeListener;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.chainNode.util.DatabaseUtils;
import com.swatt.util.general.CollectionsUtilities;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.sql.ConnectionPool;

public class ChainNodeIngestor implements ChainNodeListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChainNodeIngestor.class);

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

    public ChainNodeIngestor(Properties properties, ChainNode chainNode, Connection connection) {
        super();
        
        backfill = Boolean.valueOf(properties.getProperty(BACKFILL_PROPERTY, String.valueOf(backfill)));
        overwriteExisting = Boolean.valueOf(properties.getProperty(OVERWRITE_EXISTING_PROPERTY, String.valueOf(overwriteExisting)));
        stopHeight = Long.valueOf(properties.getProperty(STOP_HEIGHT_PROPERTY, String.valueOf(stopHeight)));
        
        this.chainNode = chainNode;
        this.connection = connection;
    }

    @Override
    public void newBlockAvailable(ChainNode chainNode, BlockData blockData) {
        try {
            logInfo(chainNode.getBlockchainCode(), String.format("Block available, storing: %d", blockData.getHeight()));
            BlockData.insertBlockData(connection, blockData);
        } catch (SQLException e) {
            LOGGER.error("Exception caught while storing new block: " + e.getMessage());
        }
    }

    @Override
    public void newTransactionsAvailable(ChainNode chainNode, ChainNodeTransaction[] chainTransactions) {
        throw new UnsupportedOperationException("Unimplemented.");
    }

    public void synchronizeChain() {
        if (!backfill)
            return;
        
        synchronizeChainThread = new Thread(() -> {
            try {
                long height = chainNode.fetchBlockCount();

                while (height > stopHeight) {
                    List<BlockData> blockDatas = BlockData.getBlockDatas(connection, String.format("BLOCKCHAIN_CODE = '%s' AND HEIGHT = %d", chainNode.getBlockchainCode(), height));
                    if (blockDatas != null && !blockDatas.isEmpty() && !overwriteExisting) {
                        height = height - 1;
                        continue;
                    }
                    
                    BlockData blockData = chainNode.fetchBlockData(height);
                    BlockData.insertBlockData(connection, blockData);
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
    
    public static void main(String[] args) throws IOException, SQLException, OperationFailedException {
        Properties properties = CollectionsUtilities.loadProperties("config.properties");
        ConnectionPool connectionPool = DatabaseUtils.getConnectionPool(properties);
        ChainNodeManager chainNodeManager = new ChainNodeManager(properties);
        
        String[] blockchainCodes = properties.getProperty(BLOCKCHAIN_CODES_PROPERTY).split(",");
        for (String blockchainCode : blockchainCodes) {
            logInfo(blockchainCode, "Starting chainNodeIngestor.");

            Connection connection = connectionPool.getConnection();
            ChainNode chainNode = chainNodeManager.getChainNode(connection, blockchainCode);

            if (chainNode != null) {
                ChainNodeIngestor chainNodeIngestor = new ChainNodeIngestor(properties, chainNode, connection);
                chainNode.addChainNodeListener(chainNodeIngestor);
                chainNode.fetchNewBlocks();
                chainNodeIngestor.synchronizeChain();
            } else {
                LOGGER.error(String.format("[%s] No chainNode found.", blockchainCode));
            }
        }
    }
}

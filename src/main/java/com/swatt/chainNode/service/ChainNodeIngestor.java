package com.swatt.chainNode.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeListener;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.chainNode.dao.CheckProgress;
import com.swatt.util.general.CollectionsUtilities;
import com.swatt.util.general.ConcurrencyUtilities;
import com.swatt.util.general.OperationFailedException;

public class ChainNodeIngestor implements ChainNodeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChainNodeIngestor.class);

    private ChainNode chainNode;
    private Connection connection;
    private Thread ingestorThread;
    private int blockCount = 0;
    private static String blockchainCode;

    public ChainNodeIngestor(ChainNode chainNode, Connection connection) {
        this.chainNode = chainNode;
        this.connection = connection;
    }

    @Override
    public void newBlockAvailable(ChainNode chainNode, BlockData blockData) {
        try {
            LOGGER.info("New Block Available. Storing");
            blockData.setBlockchainCode(blockchainCode);
            BlockData.insertBlockData(connection, blockData);
        } catch (SQLException e) {
            // FIXME
        }
    }

    @Override
    public void newTransactionsAvailable(ChainNode chainNode, ChainNodeTransaction[] chainTransactions) {
    }

    public void startIngestingForwardInChain() {
        ingestorThread = new Thread(() -> {
            try {
                CheckProgress checkProgress = chainNode.getCheckProgress(connection, blockchainCode);
                long blockCount = chainNode.fetchBlockCount();

                BlockData blockData = null;
                
                if (checkProgress.getBlockHash() == null) {
                    blockData = chainNode.fetchBlockDataByHash(chainNode.getGenesisHash()); 
                    blockData.setBlockchainCode(blockchainCode);
                    BlockData.insertBlockData(connection, blockData);
                    LOGGER.info("GENESIS BLOCK INGESTED: , " + (blockCount - blockData.getHeight()) + " BLOCKS TO GO");
                } else {
                    blockData = BlockData.getFirstBlockData(connection, String.format("HASH = '%s'", checkProgress.getBlockHash())); 
                }

                while (blockData.getHeight() < blockCount) {
                    BlockData existingBlockData = BlockData.getFirstBlockData(connection, String.format("HEIGHT = %d", blockData.getHeight() + 1));
                    
                    if (existingBlockData == null) {
                        blockData = chainNode.fetchBlockData(blockData.getHeight() + 1);
                        blockData.setBlockchainCode(blockchainCode);
                        BlockData.insertBlockData(connection, blockData);
                        LOGGER.info("BLOCK INGESTED: " + blockData.getHeight() + ", " + (blockCount - blockData.getHeight()) + " BLOCKS TO GO");
                    } else {
                        blockData = existingBlockData;
                    }
                    
                    chainNode.setUpdateProgress(connection, blockchainCode, blockData.getHash(), (int)(blockCount - blockData.getHeight()));
                }
            } catch (OperationFailedException | SQLException e) {
                e.printStackTrace();
            }
        }, "ForwardIngestorThread-" + chainNode.getCode());

        ingestorThread.start();
    }

    public void startIngestingBackwardInChain(final String firstBlockHash, final int limitBlockCount) {
        ingestorThread = new Thread(() -> {
            try {
                String blockHash = firstBlockHash;

                while (blockCount < limitBlockCount) {
                    BlockData blockData = chainNode.fetchBlockDataByHash(blockHash);

                    blockData.setBlockchainCode(blockchainCode);

                    BlockData.insertBlockData(connection, blockData);

                    blockHash = blockData.getPrevHash();
                    blockCount++;

                    chainNode.setUpdateProgress(connection, blockchainCode, blockHash, limitBlockCount - blockCount);
                    System.out.println("BLOCK INGESTED, " + (limitBlockCount - blockCount) + " BLOCKS TO GO");

                    // Thread.sleep(10); // This will allow the Interrupt to break
                }

                LOGGER.info("INGESTION COMPLETED");
                System.exit(0);
            } catch (OperationFailedException | SQLException e) {
                e.printStackTrace();
            }
        }, "IngestorThread-" + chainNode.getCode());

        ingestorThread.start();
    }

    public void stopIngesting() {
        ingestorThread.interrupt();
    }

    public static void main(String[] args) {
        try {
            long runTimeMillis = 5 * 60 * 1000;

            String propertiesFileName = "config.properties";

            Properties properties = CollectionsUtilities.loadProperties(propertiesFileName);

            blockchainCode = (args.length > 0) ? args[0] : properties.getProperty("ingestionStartCode");

            ChainNodeManager chainNodeManager = new ChainNodeManager(new ChainNodeManagerConfig(properties));

            Connection connection = chainNodeManager.getConnection();

            ChainNode chainNode = chainNodeManager.getChainNode(blockchainCode);

            if (chainNode != null) {
                CheckProgress progressStart = chainNode.getCheckProgress(connection, blockchainCode);

                String firstBlockHash = progressStart.getBlockHash();
                int limitBlockCount = progressStart.getBlockCount();

                ChainNodeIngestor chainNodeIngestor = new ChainNodeIngestor(chainNode, connection);
                // chainNodeIngestor.startIngestingBackwardInChain(firstBlockHash, limitBlockCount);
                
//                chainNodeIngestor.startIngestingForwardInChain();
                chainNode.addChainNodeListener(chainNodeIngestor);
                chainNode.fetchNewBlocks();
                
                ConcurrencyUtilities.sleep(runTimeMillis);
                //chainNodeIngestor.stopIngesting();
            } else {
                LOGGER.error("No ChainNode found for type: " + blockchainCode);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

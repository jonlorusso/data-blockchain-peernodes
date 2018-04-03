package com.swatt.chainNode.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeScaling;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.chainNode.dao.CheckProgress;
import com.swatt.util.general.CollectionsUtilities;
import com.swatt.util.general.ConcurrencyUtilities;
import com.swatt.util.general.OperationFailedException;

public class ChainNodeIngestor {
    private ChainNode chainNode;
    private Connection connection;
    private Thread ingestorThread;
    private int blockCount = 0;
    private static String blockchainCode;

    public ChainNodeIngestor(ChainNode chainNode, Connection connection) {
        this.chainNode = chainNode;
        this.connection = connection;
    }

    public void startIngestingBackwardInChain(final String firstBlockHash, final int limitBlockCount) {
        ingestorThread = new Thread(() -> {
            try {
                String blockHash = firstBlockHash;

                while (blockCount < limitBlockCount) {
                    BlockData blockData = chainNode.fetchBlockDataByHash(blockHash);

                    blockData.setBlockchainCode(blockchainCode);

                    ChainNodeScaling scaling = null;
                    BlockData.insertBlockData(connection, blockData, scaling);

                    blockHash = blockData.getPrevHash();
                    blockCount++;

                    chainNode.setUpdateProgress(connection, blockchainCode, blockHash, limitBlockCount - blockCount);
                    System.out.println("BLOCK INGESTED, " + (limitBlockCount - blockCount) + " BLOCKS TO GO");

                    // Thread.sleep(10); // This will allow the Interrupt to break
                }

                System.out.println("INGESTION COMPLETED");
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

            ChainNodeManagerConfig chainNodeManagerConfig = new ChainNodeManagerConfig(properties);

            ChainNodeManager chainNodeManager = new ChainNodeManager(chainNodeManagerConfig);
            Connection connection = chainNodeManager.getConnection();

            ChainNode chainNode = chainNodeManager.getChainNode(blockchainCode);

            if (chainNode != null) {
                CheckProgress progressStart = chainNode.getCheckProgress(connection, blockchainCode);

                String firstBlockHash = progressStart.getBlockHash();
                int limitBlockCount = progressStart.getBlockCount();

                ChainNodeIngestor chainNodeIngestor = new ChainNodeIngestor(chainNode, connection);

                chainNodeIngestor.startIngestingBackwardInChain(firstBlockHash, limitBlockCount);
                ConcurrencyUtilities.sleep(runTimeMillis);
                chainNodeIngestor.stopIngesting();
            } else {
                System.out.println("No ChainNode found for type: " + blockchainCode);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

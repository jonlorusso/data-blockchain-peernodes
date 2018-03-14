package com.swatt.chainNode.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.chainNode.dao.CheckProgress;
import com.swatt.util.CollectionsUtilities;
import com.swatt.util.ConcurrencyUtilities;
import com.swatt.util.OperationFailedException;

public class ChainNodeIngestor {
    private ChainNode chainNode;
    private Connection connection;
    private Thread ingestorThread;

    public ChainNodeIngestor(ChainNode chainNode, Connection connection) {
        this.chainNode = chainNode;
        this.connection = connection;
    }

    public void startIngestingBackwardInChain(final String firstBlockHash) {

        ingestorThread = new Thread(() -> {
            try {
                String blockHash = firstBlockHash;

                for (;;) { // TODO: This should have boundaries and/or timeouts
                    BlockData blockData = chainNode.fetchBlockDataByHash(blockHash);
                    BlockData.createBlockData(connection, blockData);

                    blockHash = blockData.getPrevHash();

                    Thread.sleep(10); // This will allow the Interrupt to break
                }
            } catch (OperationFailedException | SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) { // Stop Ingesting was requested
                return;
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

            String blockchainCode = (args.length > 0) ? args[0] : properties.getProperty("ingestionStartCode");

            ChainNodeManagerConfig chainNodeManagerConfig = new ChainNodeManagerConfig(properties);

            ChainNodeManager chainNodeManager = new ChainNodeManager(chainNodeManagerConfig);
            Connection connection = chainNodeManager.getConnection();

            ChainNode chainNode = chainNodeManager.getChainNode(blockchainCode);

            CheckProgress progressStart = chainNode.getCheckProgress(connection, blockchainCode);

            String firstBlockHash = progressStart.getBlockHash();
            int blockCount = progressStart.getBlockCount();

            if (chainNode != null) {
                ChainNodeIngestor chainNodeIngestor = new ChainNodeIngestor(chainNode, connection);

                chainNodeIngestor.startIngestingBackwardInChain(firstBlockHash);
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

package com.swatt.chainNode.service;

import java.sql.Connection;
import java.sql.SQLException;

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.dao.BlockData;
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
            String blockchainCode = (args.length > 0) ? args[0] : "BTC";
            String firstBlockHash = (args.length > 1) ? args[1] : null;
            boolean ingestBlocksViaPreviousHash = true;

            long runTimeMillis = 5 * 60 * 1000;

            ChainNodeManagerConfig chainNodeManagerConfig = new ChainNodeManagerConfig();

            ChainNodeManager chainNodeManager = new ChainNodeManager(chainNodeManagerConfig);
            Connection connection = chainNodeManager.getConnection();

            ChainNode chainNode = chainNodeManager.getChainNode(blockchainCode);

            if (chainNode != null) {
                ChainNodeIngestor chainNodeIngestor = new ChainNodeIngestor(chainNode, connection);

                if (ingestBlocksViaPreviousHash)
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

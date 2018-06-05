package com.swatt.blockchain.ingestor;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.entity.CheckProgress;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.node.NodeListener;
import com.swatt.blockchain.repository.BlockDataRepository;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.sql.ConnectionPool;

public class NodeIngestor implements NodeListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeIngestor.class);

    private Thread historicalIngestionThread;
    
    private Node node;
    private ConnectionPool connectionPool;
    private BlockDataRepository blockDataRepository;

    private boolean overwriteExisting = false;

    public NodeIngestor(Node node, ConnectionPool connectionPool, BlockDataRepository blockDataRepository) {
        super();

        this.node = node;
        this.connectionPool = connectionPool;
        this.blockDataRepository = blockDataRepository;
        
        node.addNodeListener(this);
    }
    
    public void setOverwriteExisting(boolean overwriteExisting) {
        this.overwriteExisting = overwriteExisting;
    }

    private boolean existsBlockData(long height) throws OperationFailedException, SQLException {
        return blockDataRepository.findByBlockchainCodeAndHeight(node.getBlockchainCode(), height) != null;
    }

    @Override
    public void newBlockAvailable(Node node, BlockData blockData) {
        try {
            if (!existsBlockData(blockData.getHeight()) || overwriteExisting) {
                blockDataRepository.insert(blockData);
                LOGGER.info(String.format("Synced new block: %d", blockData.getHeight()));
            }
        } catch (OperationFailedException | SQLException e) {
            // FIXME
            LOGGER.error("Exception caught while storing new block: " + e.getMessage());
        }
    }

    public void start() {
        if (historicalIngestionThread == null) {
            historicalIngestionThread = new Thread(() -> {
                try (Connection connection = connectionPool.getConnection()) {
                    long height = node.fetchBlockCount();
                    CheckProgress checkProgress = CheckProgress.call(connection, node.getCode());
                    long stopHeight = checkProgress.getBlockCount();
                    
                    if (height > stopHeight) 
                        LOGGER.info(String.format("Historical ingestion running for blocks: %d through %s", height, stopHeight));
                    
                    while (height > stopHeight) {
                        if (existsBlockData(height) && !overwriteExisting) {
                            height = height - 1;
                            LOGGER.debug(String.format("Skipping existing block: %s", height));
                            continue;
                        }
                        
                        BlockData blockData = node.fetchBlockData(height);
                        blockDataRepository.insert(blockData);
                        
                        LOGGER.info(String.format("Ingested old block: %d", height));
                        
                        height = height - 1;
                    }
                } catch (Throwable t) {
                    LOGGER.error("Historical ingestion failed: " + t.getMessage());
                    historicalIngestionThread = null;
                }
            }, "HistoricalIngestion-" + node.getCode());
            
            historicalIngestionThread.start();
        }

        node.fetchNewBlocks();
    }
}

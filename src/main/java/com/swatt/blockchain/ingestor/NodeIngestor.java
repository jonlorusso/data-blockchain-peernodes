package com.swatt.blockchain.ingestor;

import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.entity.CheckProgress;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.node.NodeListener;
import com.swatt.blockchain.repository.BlockDataRepository;
import com.swatt.util.general.ConcurrencyUtilities;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.sql.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static java.lang.String.format;

public class NodeIngestor implements NodeListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeIngestor.class);

    private ExecutorService executor;
    
    private Node node;
    private ConnectionPool connectionPool;
    private BlockDataRepository blockDataRepository;

    private NodeIngestorConfig nodeIngestorConfig;

    private boolean running = false;
    
    public NodeIngestor(Node node, ConnectionPool connectionPool, BlockDataRepository blockDataRepository, NodeIngestorConfig nodeIngestorConfig) {
        super();

        this.node = node;
        this.connectionPool = connectionPool;
        this.blockDataRepository = blockDataRepository;
        this.nodeIngestorConfig = nodeIngestorConfig;
        
        node.addNodeListener(this);
    }
    
    public void init() {
    	executor = Executors.newFixedThreadPool(nodeIngestorConfig.getNumberOfThreads(), new ThreadFactory() {
        	private int i = 0;

        	@Override
    		public Thread newThread(Runnable r) {
    			i++;
    			return new Thread(r, node.getBlockchainCode() + "-NodeIngestor-" + i);
    		}
    	});
    }

    private boolean existsBlockData(long height) throws OperationFailedException, SQLException {
        return blockDataRepository.findByBlockchainCodeAndHeight(node.getBlockchainCode(), height) != null;
    }

    @Override
    public void newBlockAvailable(Node node, BlockData blockData) {
        try {
            if (!existsBlockData(blockData.getHeight()) || nodeIngestorConfig.isOverwriteExisting()) {
                blockDataRepository.insert(blockData);
                logInfo(format("Synced new block: %d", blockData.getHeight()));
            }
        } catch (OperationFailedException | SQLException e) {
            logError(format("Exception caught while storing new block: %s", e.getMessage()));
        }
    }
    
    public boolean ingestBlock(long height) throws OperationFailedException, SQLException {
    	boolean exists = existsBlockData(height);
    	
        if (exists && nodeIngestorConfig.isOverwriteExisting()) {
            BlockData blockData = node.fetchBlockData(height);
            blockDataRepository.replace(blockData);
            logInfo(format("Re-ingested block: %d", height));
            return true;
        } else if (!exists) {
            BlockData blockData = node.fetchBlockData(height);
            blockDataRepository.insert(blockData);
            logInfo(format("Ingested block: %d", height));
            return true;
        }
        
        return false;
    }

	public void start() {
        if (running)
            return;

        running = true;

        ConcurrencyUtilities.startThread(() -> {
            try (Connection connection = connectionPool.getConnection()) {

                long height = node.fetchBlockCount();
                CheckProgress checkProgress = CheckProgress.call(connection, node.getBlockchainCode());
                long stopHeight = checkProgress.getBlockCount();

                if (height > stopHeight)
                    LOGGER.info(String.format("Historical ingestion running for blocks: %d through %s", height, stopHeight));

                while (height > stopHeight) {
                    ingestBlock(height);
                    height = height - 1;
                }

            } catch (Throwable t) {
                running = false;
                LOGGER.error("[" + node.getBlockchainCode() + "] Historical ingestion failed.", t);
            }
        }, "HistoricalIngestion-" + node.getBlockchainCode());

		node.fetchNewBlocks();
	}

    private void logInfo(String infoMessage) {
    	LOGGER.info(String.format("[%s] %s", node.getBlockchainCode(), infoMessage));
    }
    
    private void logError(String errorMessage) {
    	LOGGER.error(String.format("[%s] %s", node.getBlockchainCode(), errorMessage));
    }
}

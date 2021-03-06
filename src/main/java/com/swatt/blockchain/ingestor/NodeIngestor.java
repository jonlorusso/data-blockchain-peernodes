package com.swatt.blockchain.ingestor;

import static java.lang.String.format;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.LongStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.entity.CheckProgress;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.node.NodeListener;
import com.swatt.blockchain.repository.BlockDataRepository;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.general.SystemUtilities;
import com.swatt.util.log.LoggerController;
import com.swatt.util.sql.ConnectionPool;

public class NodeIngestor implements NodeListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeIngestor.class);

    private static final String NUMBER_OF_THREADS = "NUMBER_OF_THREADS";

    private ExecutorService executor;
    
    private Node node;
    private ConnectionPool connectionPool;
    private BlockDataRepository blockDataRepository;

    private int numberOfThreads = 1;
    private Long startHeight;
    private Long endHeight;
    
    private boolean overwriteExisting = false;
    
    public NodeIngestor(Node node, ConnectionPool connectionPool, BlockDataRepository blockDataRepository) {
        super();

        this.node = node;
        this.connectionPool = connectionPool;
        this.blockDataRepository = blockDataRepository;
        
        node.addNodeListener(this);
    }
    
    public void init() {
    	if (numberOfThreads == 1) {
    		if (SystemUtilities.getEnv(NUMBER_OF_THREADS) != null) {
    			String numberOfThreadsEnvVar = SystemUtilities.getEnv(NUMBER_OF_THREADS);
    			
    			try {
    				numberOfThreads = Integer.parseInt(numberOfThreadsEnvVar); 
    			} catch (Exception e) {
    				logError("Invalid NUMBER_OF_THREADS value: " + numberOfThreadsEnvVar);
    			}
    		}
    	}
    	
    	executor = Executors.newFixedThreadPool(numberOfThreads, new ThreadFactory() {
        	private int i = 0;

        	@Override
    		public Thread newThread(Runnable r) {
    			i++;
    			return new Thread(r, node.getCode() + "-NodeIngestor-" + i);
    		}
    	});
    }
    
    public void setNumberOfThreads(int numberOfThreads) {
    	this.numberOfThreads = numberOfThreads;
    }
    
    public void setStartHeight(Long startHeight) {
    	this.startHeight = startHeight;
    }
    
    public void setEndHeight(Long endHeight) {
    	this.endHeight = endHeight;
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
                logInfo(format("Synced new block: %d", node.getCode(), blockData.getHeight()));
            }
        } catch (OperationFailedException | SQLException e) {
            logError(format("Exception caught while storing new block: %s", node.getCode(), e.getMessage()));
        }
    }
    
    public boolean ingestBlock(long height) throws OperationFailedException, SQLException {
        if (existsBlockData(height) && overwriteExisting) {
            BlockData blockData = node.fetchBlockData(height);
            blockDataRepository.replace(blockData);
            logInfo(format("Re-ingested block: %d", height));
            return true;
        } else if (!existsBlockData(height)) {
            BlockData blockData = node.fetchBlockData(height);
            blockDataRepository.insert(blockData);
            logInfo(format("Ingested block: %d", height));
            return true;
        }
        
        return false;
    }
    
    public void start() {
        try (Connection connection = connectionPool.getConnection()) {
        	long start = startHeight != null ? startHeight : CheckProgress.call(connection, node.getCode()).getBlockCount();
        	long end = endHeight != null ? endHeight : node.fetchBlockCount();
            
            logInfo(format("Historical ingestion running for blocks: %d through %d", start, end));
            
            LongStream.range(start, end).forEach(height -> {
            	executor.execute(() -> {
            		try {
            			ingestBlock(height);
            		} catch (Throwable e) {
            			logError(format("Error ingesting block %d: %s", height, e.getMessage()));
            			e.printStackTrace(System.out);
            		}
            	});
    	    });
        } catch (Throwable t) {
        	logError(format("Historical ingestion failed: %s", t.getMessage()));
        }
            
        node.fetchNewBlocks();
    }
    
    private void logInfo(String infoMessage) {
    	LOGGER.info(String.format("[%s] %s", node.getCode(), infoMessage));
    }
    
    private void logError(String errorMessage) {
    	LOGGER.error(String.format("[%s] %s", node.getCode(), errorMessage));
    }
}

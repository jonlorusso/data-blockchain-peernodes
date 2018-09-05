package com.swatt.blockchain.ingestor;

import static java.lang.String.format;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.LongStream;

import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.blockchain.node.PlatformNode;
import com.swatt.util.general.CollectionsUtilities;
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

    private ExecutorService executor;
    
    private Node node;
    private ConnectionPool connectionPool;
    private BlockDataRepository blockDataRepository;

    private NodeIngestorConfig nodeIngestorConfig;

    private List<BlockchainNodeInfo> tokens;

    public NodeIngestor(Node node, ConnectionPool connectionPool, BlockDataRepository blockDataRepository, NodeIngestorConfig nodeIngestorConfig) {
        super();

        this.node = node;
        this.connectionPool = connectionPool;
        this.blockDataRepository = blockDataRepository;
        this.nodeIngestorConfig = nodeIngestorConfig;
        
        node.addNodeListener(this);
    }

    public void setTokens(List<BlockchainNodeInfo> tokens) {
        this.tokens = tokens;
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

    private List<BlockData> fetchAllBlockDatas(long height) throws OperationFailedException {
        List<BlockData> blockDatas = new ArrayList<>();
        blockDatas.add(node.fetchBlockData(height));

        // FIXME this refetches the block and transactions.
        if (node instanceof PlatformNode) {
            blockDatas.addAll(((PlatformNode)node).fetchTokenBlockDatas(height));
        }

        return blockDatas;
    }

    public boolean ingestBlock(long height) throws OperationFailedException, SQLException {
    	boolean exists = existsBlockData(height);
    	
        if (exists && nodeIngestorConfig.isOverwriteExisting()) {
            List<BlockData> blockDatas = fetchAllBlockDatas(height);
            for (BlockData blockData : blockDatas) {
                blockDataRepository.replace(blockData);
                LOGGER.info(format("[%s] Re-ingested block: %d", blockData.getBlockchainCode(), height));
            }
            return true;
        } else if (!exists) {
            List<BlockData> blockDatas = fetchAllBlockDatas(height);
            for (BlockData blockData : blockDatas) {
                blockDataRepository.insert(blockData);
                LOGGER.info(format("[%s] Ingested block: %d", blockData.getBlockchainCode(), height));
            }
            return true;
        }
        
        return false;
    }
    
    public void start() {
        try (Connection connection = connectionPool.getConnection()) {
        	long start = nodeIngestorConfig.getStartHeight() != null ? nodeIngestorConfig.getStartHeight() : CheckProgress.call(connection, node.getBlockchainCode()).getBlockCount();
        	long end = nodeIngestorConfig.getEndHeight() != null ? nodeIngestorConfig.getEndHeight() : node.fetchBlockCount();
            
            logInfo(format("Historical ingestion running for blocks: %d through %d", start, end));

            for (long i = start; i < end; i++) {
                long height = i;
                executor.execute(() -> {
                    try {
                        ingestBlock(height);
                    } catch (Throwable e) {
                        logError(format("Error ingesting block %d: %s", height, e.getMessage()));
                    }
                });
            }
        } catch (Throwable t) {
        	logError(format("Historical ingestion failed: %s", t.getMessage()));
        }
            
        node.fetchNewBlocks();
    }
    
    private void logInfo(String infoMessage) {
    	LOGGER.info(format("[%s] %s", node.getBlockchainCode(), infoMessage));
    }
    
    private void logError(String errorMessage) {
    	LOGGER.error(format("[%s] %s", node.getBlockchainCode(), errorMessage));
    }
}

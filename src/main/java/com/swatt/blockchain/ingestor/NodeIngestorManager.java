package com.swatt.blockchain.ingestor;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.repository.BlockDataRepository;
import com.swatt.blockchain.repository.BlockchainNodeInfoRepository;
import com.swatt.blockchain.service.NodeManager;
import com.swatt.util.general.ConcurrencyUtilities;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.sql.ConnectionPool;

public class NodeIngestorManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeIngestorManager.class);
    
    private static final long ACTIVE_NODE_WATCHER_SLEEP_TIME = 60 * 1000;

    private NodeManager nodeManager;
    private ConnectionPool connectionPool;
    private BlockchainNodeInfoRepository blockchainNodeInfoRepository;
    private BlockDataRepository blockDataRepository;
    
    private Map<String, NodeIngestor> nodeIngestors = new HashMap<>();
    
    private boolean overwriteExisting = false;
    
    public NodeIngestorManager(NodeManager nodeManager, ConnectionPool connectionPool, BlockchainNodeInfoRepository blockchainNodeInfoRepository, BlockDataRepository blockDataRepository) {
    		this.nodeManager = nodeManager;
    		this.connectionPool = connectionPool;
    		this.blockchainNodeInfoRepository = blockchainNodeInfoRepository;
    		this.blockDataRepository = blockDataRepository;
    }

    public void setOverwriteExisting(boolean overwriteExisting) {
        this.overwriteExisting = overwriteExisting;
    }

    public void enableNodeIngestion(String code) {
        Node node = nodeManager.getNode(code);
        if (node != null) {
            NodeIngestor nodeIngestor = nodeIngestors.get(node.getCode());
            if (nodeIngestor == null) {
                LOGGER.info("Starting NodeIngestor for " + node.getCode());

                nodeIngestor = new NodeIngestor(node, connectionPool, blockDataRepository);
                nodeIngestor.setOverwriteExisting(overwriteExisting);
                nodeIngestors.put(node.getBlockchainCode(), nodeIngestor);
            }

            nodeIngestor.start();
        }
    }
	
	// FIXME all enabled nodes will have an ingestor started in each process
	public void start() {
		new Thread(() -> {
			LOGGER.info("Starting EnabledNodeWatcher Thread.");

			while (true) { // poll (60s) BLOCKCHAIN_NODE_INFO table for newly enabled nodes.
			    try {
			        for (BlockchainNodeInfo blockchainNodeInfo : blockchainNodeInfoRepository.findAllByEnabled(true)) {
			            enableNodeIngestion(blockchainNodeInfo.getCode());
			        }
			    } catch (SQLException | OperationFailedException e) {
			        LOGGER.error("SQLException caught in activeNodeWatcher thread.", e);
			    }
			    
			    ConcurrencyUtilities.sleep(ACTIVE_NODE_WATCHER_SLEEP_TIME);
			}
		}, "EnabledNodeWatcher").start();
	}
}

package com.swatt.blockchain.ingestor;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.repository.BlockDataRepository;
import com.swatt.blockchain.repository.BlockchainNodeInfoRepository;
import com.swatt.blockchain.service.NodeManager;
import com.swatt.util.general.ConcurrencyUtilities;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.general.StringUtilities;
import com.swatt.util.general.SystemUtilities;
import com.swatt.util.sql.ConnectionPool;

public class NodeIngestorManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeIngestorManager.class);
    
    private static final long ACTIVE_NODE_WATCHER_SLEEP_TIME = 60 * 1000;

    private static final String INGESTOR_CONFIG_ENV_VAR = "INGESTOR_CONFIG";
    
    private NodeManager nodeManager;
    private ConnectionPool connectionPool;
    private BlockchainNodeInfoRepository blockchainNodeInfoRepository;
    private BlockDataRepository blockDataRepository;
    
    private Map<String, NodeIngestor> nodeIngestors = new HashMap<>();
    private Map<String, NodeIngestorConfig> nodeIngestorConfigs;
    
    public NodeIngestorManager(NodeManager nodeManager, ConnectionPool connectionPool, BlockchainNodeInfoRepository blockchainNodeInfoRepository, BlockDataRepository blockDataRepository) {
    		this.nodeManager = nodeManager;
    		this.connectionPool = connectionPool;
    		this.blockchainNodeInfoRepository = blockchainNodeInfoRepository;
    		this.blockDataRepository = blockDataRepository;
    }
    
    public void init() throws Exception {
    	String configString = SystemUtilities.getEnv(INGESTOR_CONFIG_ENV_VAR);
    	
    	if (!StringUtilities.isNullOrAllWhiteSpace(configString)) {
    		nodeIngestorConfigs = new HashMap<>();
    		List<NodeIngestorConfig> nodeIngestorConfigs = new ObjectMapper().readValue(configString, new TypeReference<List<NodeIngestorConfig>>(){});
    		for (NodeIngestorConfig nodeIngestorConfig : nodeIngestorConfigs) {
    			this.nodeIngestorConfigs.put(nodeIngestorConfig.getBlockchainCode(), nodeIngestorConfig);
    		}
    	}
    }
    
    private NodeIngestor createNodeIngestor(NodeIngestorConfig nodeIngestorConfig) {
        Node node = nodeManager.getNode(nodeIngestorConfig.getBlockchainCode());
        if (node != null) {
            NodeIngestor nodeIngestor = nodeIngestors.get(node.getCode());
            if (nodeIngestor == null) {
                LOGGER.info("Starting NodeIngestor for " + node.getCode());
                nodeIngestor = new NodeIngestor(node, connectionPool, blockDataRepository, nodeIngestorConfig);
                nodeIngestor.init();
                nodeIngestors.put(node.getBlockchainCode(), nodeIngestor);
            }

            return nodeIngestor;
        }

        return null;
    }

    public void enableNodeIngestion(NodeIngestorConfig nodeIngestorConfig) {
        NodeIngestor nodeIngestor = nodeIngestors.get(nodeIngestorConfig.getBlockchainCode());
        nodeIngestor = nodeIngestor != null ? nodeIngestor : createNodeIngestor(nodeIngestorConfig);
        
        if (nodeIngestor != null)
        	nodeIngestor.start();
    }
	
	public void start() {
		ConcurrencyUtilities.startThread(() -> {
			LOGGER.info("Starting EnabledNodeWatcher Thread.");

			while (true) { // poll (60s) BLOCKCHAIN_NODE_INFO table for newly enabled nodes.
				try {
					blockchainNodeInfoRepository.findAllByEnabled(true).stream().forEach(b -> {
						NodeIngestorConfig nodeIngestorConfig = null;
						
						if (nodeIngestorConfigs == null) {
							nodeIngestorConfig = new NodeIngestorConfig();
							nodeIngestorConfig.setBlockchainCode(b.getCode());
						} else {
							nodeIngestorConfig = nodeIngestorConfigs.get(b.getCode());
						}
						
						if (nodeIngestorConfig != null)
							enableNodeIngestion(nodeIngestorConfig);
					});
				} catch (SQLException | OperationFailedException e) {
					LOGGER.error("SQLException caught in enabledNodeWatcher thread.", e);
				}

				ConcurrencyUtilities.sleep(ACTIVE_NODE_WATCHER_SLEEP_TIME);
			}
		}, "EnabledNodeWatcher");

	}
}

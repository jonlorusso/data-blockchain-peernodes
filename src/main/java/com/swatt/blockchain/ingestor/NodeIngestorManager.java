package com.swatt.blockchain.ingestor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.repository.BlockDataRepository;
import com.swatt.blockchain.repository.BlockchainNodeInfoRepository;
import com.swatt.blockchain.service.NodeManager;
import com.swatt.util.general.ConcurrencyUtilities;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.general.StringUtilities;
import com.swatt.util.general.SystemUtilities;
import com.swatt.util.sql.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    private NodeIngestor createNodeIngestor(BlockchainNodeInfo blockchainNodeInfo, NodeIngestorConfig nodeIngestorConfig) {
        Node node = nodeManager.getNode(blockchainNodeInfo);

        if (node != null) {
            NodeIngestor nodeIngestor = nodeIngestors.get(node.getBlockchainCode());

            if (nodeIngestor == null) {
                LOGGER.info("Starting NodeIngestor for " + node.getBlockchainCode());
                nodeIngestor = new NodeIngestor(node, connectionPool, blockDataRepository, nodeIngestorConfig);
                nodeIngestor.init();
                nodeIngestors.put(node.getBlockchainCode(), nodeIngestor);
            }

            return nodeIngestor;
        }

        return null;
    }

    /**
     * If no <code>INGESTOR_CONFIG</code> enviornment variable is set,
     * all enbaled blockchain_node_infos will have ingestors started for them with default
     * values.
     */
    public void enableNodeIngestion(BlockchainNodeInfo blockchainNodeInfo) {
        if (blockchainNodeInfo.isToken())
            return;

        NodeIngestorConfig nodeIngestorConfig = getNodeIngestorConfig(blockchainNodeInfo.getCode());

        if (nodeIngestorConfig != null) {
            NodeIngestor nodeIngestor = nodeIngestors.get(nodeIngestorConfig.getBlockchainCode());
            nodeIngestor = nodeIngestor != null ? nodeIngestor : createNodeIngestor(blockchainNodeInfo, nodeIngestorConfig);

            if (nodeIngestor != null)
                nodeIngestor.start();
        }
    }

    private NodeIngestorConfig getNodeIngestorConfig(String blockchainCode) {
        NodeIngestorConfig nodeIngestorConfig;

        if (nodeIngestorConfigs == null) {
            nodeIngestorConfig = new NodeIngestorConfig();
        } else {
            nodeIngestorConfig = nodeIngestorConfigs.get(blockchainCode);
        }

        return nodeIngestorConfig;
    }

	public void start() {
		ConcurrencyUtilities.startThread(() -> {
			LOGGER.info("Starting EnabledNodeWatcher Thread.");

			while (true) { // poll (60s) BLOCKCHAIN_NODE_INFO table for newly enabled nodes.
				try {
					blockchainNodeInfoRepository.findAllByEnabled(true).stream().forEach(this::enableNodeIngestion);
				} catch (SQLException e) {
					LOGGER.error("SQLException caught in enabledNodeWatcher thread.", e);
				}

				ConcurrencyUtilities.sleep(ACTIVE_NODE_WATCHER_SLEEP_TIME);
			}
		}, "EnabledNodeWatcher");
	}
}

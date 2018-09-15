package com.swatt.blockchain.ingestor;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.swatt.blockchain.ApplicationContext;
import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.blockchain.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swatt.blockchain.node.Node;
import com.swatt.util.general.ConcurrencyUtilities;
import com.swatt.util.general.OperationFailedException;

import static com.swatt.blockchain.util.LogUtils.error;
import static com.swatt.util.general.StringUtilities.isNullOrAllWhiteSpace;
import static com.swatt.util.general.SystemUtilities.getEnv;
import static java.lang.String.format;

public class NodeIngestorManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeIngestorManager.class);
    
    private static final long ACTIVE_NODE_WATCHER_SLEEP_TIME = 60 * 1000;

    private static final String INGESTOR_CONFIG_ENV_VAR = "INGESTOR_CONFIG";

    private ApplicationContext applicationContext;

    private Map<String, NodeIngestor> nodeIngestors = new HashMap<>();
    private Map<String, NodeIngestorConfig> nodeIngestorConfigs;

    private Class<? extends BlockProducer> blockProducerClass;

    private boolean running = false;

    public NodeIngestorManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;

        String configString = getEnv(INGESTOR_CONFIG_ENV_VAR);
        if (!isNullOrAllWhiteSpace(configString)) {
            try {
                nodeIngestorConfigs = new HashMap<>();
                List<NodeIngestorConfig> nodeIngestorConfigs = new ObjectMapper().readValue(configString, new TypeReference<List<NodeIngestorConfig>>(){});
                for (NodeIngestorConfig nodeIngestorConfig : nodeIngestorConfigs) {
                    this.nodeIngestorConfigs.put(nodeIngestorConfig.getBlockchainCode(), nodeIngestorConfig);
                }
            } catch (IOException e) {
                LOGGER.error(format("Exception caught parsing INGESTOR_CONFIG: %s", e.getMessage()));
            }
        }
    }

    public void setBlockProducerClass(Class<? extends BlockProducer> blockSupplierClass) {
        this.blockProducerClass = blockSupplierClass;
    }

    private NodeIngestorConfig getNodeIngestorConfig(BlockchainNodeInfo blockchainNodeInfo) {
        NodeIngestorConfig nodeIngestorConfig = nodeIngestorConfigs == null ? new NodeIngestorConfig() : nodeIngestorConfigs.get(blockchainNodeInfo.getCode());

        if (nodeIngestorConfig != null) {
            nodeIngestorConfig.setBlockchainCode(blockchainNodeInfo.getCode());
        }

        return nodeIngestorConfig;
    }

    private BlockProducer createBlockProducer(Node node, NodeIngestorConfig nodeIngestorConfig) throws IllegalAccessException, InstantiationException {
        BlockProducer blockProducer = (BlockProducer) blockProducerClass.newInstance();
        blockProducer.setApplicationContext(applicationContext);
        blockProducer.setNodeIngestorConfig(nodeIngestorConfig);
        blockProducer.setNode(node);
        blockProducer.start();

        node.addNodeListener(blockProducer);

        return blockProducer;
    }

	public void start() {
        if (running)
            return;

		ConcurrencyUtilities.startThread(() -> {
			LOGGER.info("Starting EnabledNodeWatcher Thread.");

            running = true;

			while (running) { // poll (60s) BLOCKCHAIN_NODE_INFO table for newly enabled nodes.
				try {
					applicationContext.getBlockchainNodeInfoRepository().findAllByEnabled(true).stream().forEach(b -> {
					    if (nodeIngestors.containsKey(b.getCode()))
					        return;

					    if (b.getClassName().contains("ERC"))
					        return;

						NodeIngestorConfig nodeIngestorConfig = getNodeIngestorConfig(b);
                        NodeIngestor nodeIngestor = null;

						if (nodeIngestorConfig != null) {
                            Node node = applicationContext.getNodeManager().getNode(nodeIngestorConfig.getBlockchainCode());

                            if (node != null) {
                                try {
                                    nodeIngestor = new NodeIngestor(applicationContext, nodeIngestorConfig, createBlockProducer(node, nodeIngestorConfig));
                                    nodeIngestor.start();
                                } catch (IllegalAccessException | InstantiationException e) {
                                    error(node, "Unable to create BlockProducer.", e);
                                }
                            }
                        }

                        nodeIngestors.put(b.getCode(), nodeIngestor);
					});
				} catch (SQLException | OperationFailedException e) {
                    LOGGER.error("SQLException caught in enabledNodeWatcher thread.", e);
				    running = false;
				}

				ConcurrencyUtilities.sleep(ACTIVE_NODE_WATCHER_SLEEP_TIME);
			}
		}, "EnabledNodeWatcher");
	}

	public void stop() {
        running = false;
    }
}

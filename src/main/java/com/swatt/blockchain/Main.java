package com.swatt.blockchain;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.blockchain.ingestor.NodeIngestorManager;
import com.swatt.blockchain.repository.BlockDataRepository;
import com.swatt.blockchain.repository.BlockchainNodeInfoRepository;
import com.swatt.blockchain.service.NodeManager;
import com.swatt.blockchain.util.DatabaseUtils;
import com.swatt.util.general.CollectionsUtilities;
import com.swatt.util.log.LoggerController;
import com.swatt.util.sql.ConnectionPool;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String PROPERTIES_FILENAME = "config.properties";
    private static final String NODE_OVERRIDE_IP_ENV_VAR_NAME = "NODE_OVERRIDE_IP";
    private static final String NODE_OVERRIDE_PORTS_ENV_VAR_NAME = "NODE_OVERRIDE_PORTS";

    public static void main(String[] args) {
        try {
            Properties properties = CollectionsUtilities.loadProperties(PROPERTIES_FILENAME);

            /** logger **/
            LoggerController.init(properties);

            ConnectionPool connectionPool = DatabaseUtils.configureConnectionPoolFromEnvironment(properties);

            BlockchainNodeInfoRepository blockchainNodeInfoRepository = new BlockchainNodeInfoRepository(connectionPool);
            BlockDataRepository blockDataRepository = new BlockDataRepository(connectionPool);
            NodeManager nodeManager = new NodeManager(blockchainNodeInfoRepository);

            String nodeOverrideIp = System.getenv(NODE_OVERRIDE_IP_ENV_VAR_NAME);
            if (nodeOverrideIp != null) {
                nodeManager.setOverrideIp(nodeOverrideIp);
            }

            String nodeOverridePortsValue = System.getenv(NODE_OVERRIDE_PORTS_ENV_VAR_NAME);
            if (nodeOverridePortsValue != null) {
                for (String pair : nodeOverridePortsValue.split(",")) {
                    String[] keyValue = pair.split("=");
                    nodeManager.setOverridePort(keyValue[0], Integer.parseInt(keyValue[1]));
                }
            }

            NodeIngestorManager nodeIngestorManager = new NodeIngestorManager(nodeManager, connectionPool, blockchainNodeInfoRepository, blockDataRepository);
            nodeIngestorManager.start();
        } catch (IOException e) {
            LOGGER.error("Exception caught in com.swatt.blockchain.Main: ", e);
        }
    }
}

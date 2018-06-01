package com.swatt.blockchain;

import static com.swatt.util.general.CollectionsUtilities.loadPropertiesFromClasspath;
import static com.swatt.util.general.CollectionsUtilities.mergeProperties;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.blockchain.ingestor.NodeIngestorManager;
import com.swatt.blockchain.repository.BlockDataRepository;
import com.swatt.blockchain.repository.BlockchainNodeInfoRepository;
import com.swatt.blockchain.service.NodeManager;
import com.swatt.blockchain.util.DatabaseUtils;
import com.swatt.util.general.StringUtilities;
import com.swatt.util.log.LoggerController;
import com.swatt.util.sql.ConnectionPool;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    // Defaults are stored in config.properties
    private static final String PROPERTIES_FILENAME = "config.properties";

    private static final String INGESTOR_ENABLED_PROPERTY = "ingestor.enabled";
    // private static final String JSON_POOL_SIZE_PROPERTY = "jsonPoolSize"; // TODO
    // move to db
    private static final String INGESTOR_OVERWRITE_EXISTING_PROPERTY = "ingestor.overwriteExisting"; // not-implemented yet

    // Configurable via environment variables
    private static final String INGESTOR_ENABLED_ENV_VAR_NAME = "INGESTOR_ENABLED";

    private static final String NODE_OVERRIDE_IP_ENV_VAR_NAME = "NODE_OVERRIDE_IP";
    private static final String NODE_OVERRIDE_PORTS_ENV_VAR_NAME = "NODE_OVERRIDE_PORTS";

    // private static final String INGESTOR_OVERWRITE_EXISTING_ENV_VAR_NAME =
    // "INGESTOR_OVERWRITE_EXISTING"; // not-implemented yet

    private static Properties loadProperties(String filename) throws IOException {
        return mergeProperties(loadPropertiesFromClasspath(filename));
    }

    private static boolean getBooleanValue(Properties properties, String name) {
        String value = properties.getProperty(name);
        return Boolean.valueOf(value);
    }

    private static boolean isEnabled(String environmentValue, boolean defaultValue) {
        if (StringUtilities.isNullOrAllWhiteSpace(environmentValue))
            return defaultValue;

        if (environmentValue.equalsIgnoreCase("true") || environmentValue.equals("1"))
            return true;

        if (environmentValue.equalsIgnoreCase("false") || environmentValue.equals("0"))
            return false;

        return defaultValue;
    }

    public static void main(String[] args) {
        try {
            Properties properties = loadProperties(PROPERTIES_FILENAME);

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

            /** logger **/
            LoggerController.init(properties);

            /** ingestor **/
            if (isEnabled(System.getenv(INGESTOR_ENABLED_ENV_VAR_NAME), getBooleanValue(properties, INGESTOR_ENABLED_PROPERTY))) {
                NodeIngestorManager nodeIngestorManager = new NodeIngestorManager(nodeManager, connectionPool, blockchainNodeInfoRepository, blockDataRepository);
                nodeIngestorManager.setOverwriteExisting(getBooleanValue(properties, INGESTOR_OVERWRITE_EXISTING_PROPERTY));
                nodeIngestorManager.start();
            }
        } catch (IOException e) {
            LOGGER.error("Exception caught in com.swatt.blockchain.Main: ", e);
        }
    }
}

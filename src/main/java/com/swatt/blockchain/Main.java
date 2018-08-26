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
import com.swatt.util.general.SystemUtilities;
import com.swatt.util.log.LoggerController;
import com.swatt.util.sql.ConnectionPool;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String OVERWRITE_EXISTING_ENV_VAR = "OVERWRITE_EXISTING";
    
    private static final String PROPERTIES_FILENAME = "config.properties";

    private static final String CODES = "CODES";
    
    public static void main(String[] args) {
        try {
            Properties properties = CollectionsUtilities.loadProperties(PROPERTIES_FILENAME);

            String codesEnvVar = SystemUtilities.getEnv(CODES);
            String[] codes = codesEnvVar != null ? codesEnvVar.split(",") : null;
            
            /** logger **/
            LoggerController.init(properties);

            ConnectionPool connectionPool = DatabaseUtils.configureConnectionPoolFromEnvironment(properties);
            BlockchainNodeInfoRepository blockchainNodeInfoRepository = new BlockchainNodeInfoRepository(connectionPool);
            BlockDataRepository blockDataRepository = new BlockDataRepository(connectionPool);
            
            NodeManager nodeManager = new NodeManager(blockchainNodeInfoRepository);
            
            NodeIngestorManager nodeIngestorManager = new NodeIngestorManager(nodeManager, connectionPool, blockchainNodeInfoRepository, blockDataRepository);
            nodeIngestorManager.setSupportedCodes(codes);
            
            String overwriteExistingValue = SystemUtilities.getEnv(OVERWRITE_EXISTING_ENV_VAR, "false");
            boolean overwriteExisting = overwriteExistingValue.equalsIgnoreCase("true");
            nodeIngestorManager.setOverwriteExisting(overwriteExisting);
            
            nodeIngestorManager.start();
        } catch (IOException e) {
            LOGGER.error("Exception caught in com.swatt.blockchain.Main: ", e);
        }
    }
}

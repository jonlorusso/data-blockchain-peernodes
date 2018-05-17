package com.swatt.chainNode;

import static com.swatt.util.general.CollectionsUtilities.loadProperties;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.chainNode.service.ChainNodeIngestor;
import com.swatt.chainNode.service.ChainNodeManager;
import com.swatt.chainNode.service.RESTService;
import com.swatt.chainNode.util.DatabaseUtils;
import com.swatt.util.general.OperationFailedException;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChainNode.class);
    private static final String PROPERTIES_FILENAME = "config.properties";

    public static void main(String[] args) {
        String filename = args.length > 0 ? args[0] : PROPERTIES_FILENAME;

        try {
            Properties properties = loadProperties(filename);
            
            /** api **/
            RESTService restService = new RESTService(properties);
            restService.init();
            restService.start();

            /** ingestor **/
            ChainNodeManager chainNodeManager = new ChainNodeManager(properties);
            ConnectionPool connectionPool = DatabaseUtils.getConnectionPool(jdbcUrl, databaseUser, databasePassword, databaseMaxPoolSize);

            ChainNodeIngestorManager chainNodeIngestorManager = new ChainNodeIngestorManager(connectionPool, chainNodeManager);
	    chainNodeIngestorManager.startActiveNodeWatcher();
        } catch (IOException | SQLException | OperationFailedException e) {
            LOGGER.error("Exception caught in com.swatt.chainNode.Main: ", e);
        }
    }
}

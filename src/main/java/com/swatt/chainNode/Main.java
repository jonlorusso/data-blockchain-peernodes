package com.swatt.chainNode;

import static com.swatt.util.general.CollectionsUtilities.loadProperties;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.chainNode.ingestor.ChainNodeIngestorManager;
import com.swatt.chainNode.service.ChainNodeManager;
import com.swatt.chainNode.service.RESTService;
import com.swatt.chainNode.util.DatabaseUtils;
import com.swatt.util.sql.ConnectionPool;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChainNode.class);
    private static final String PROPERTIES_FILENAME = "config.properties";

    private static final String PORT_PROPERTY = "servicePort";

    public static void main(String[] args) {
        String filename = args.length > 0 ? args[0] : PROPERTIES_FILENAME;

        try {
            Properties properties = loadProperties(filename);
            
            ConnectionPool connectionPool = DatabaseUtils.getConnectionPool(properties);
            ChainNodeManager chainNodeManager = new ChainNodeManager(properties);

            /** api **/
            int port = Integer.parseInt(properties.getProperty(PORT_PROPERTY));

            RESTService restService = new RESTService(chainNodeManager, port, connectionPool);
            restService.init();
            restService.start();

            /** ingestor **/
            new ChainNodeIngestorManager(connectionPool, chainNodeManager).startActiveNodeWatcher();

        } catch (IOException e) {
            LOGGER.error("Exception caught in com.swatt.chainNode.Main: ", e);
        }
    }
}

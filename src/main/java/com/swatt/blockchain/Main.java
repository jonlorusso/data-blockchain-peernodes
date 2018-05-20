package com.swatt.blockchain;

import static com.swatt.util.general.CollectionsUtilities.loadProperties;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.blockchain.ingestor.NodeIngestorManager;
import com.swatt.blockchain.repository.BlockDataRepository;
import com.swatt.blockchain.repository.BlockchainNodeInfoRepository;
import com.swatt.blockchain.service.LoggerController;
import com.swatt.blockchain.service.NodeManager;
import com.swatt.blockchain.service.RESTService;
import com.swatt.blockchain.util.DatabaseUtils;
import com.swatt.util.sql.ConnectionPool;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String PROPERTIES_FILENAME = "config.properties";

    private static final String PORT_PROPERTY = "servicePort";

    public static void main(String[] args) {
        String filename = args.length > 0 ? args[0] : PROPERTIES_FILENAME;

        try {
            Properties properties = loadProperties(filename);
            
            ConnectionPool connectionPool = DatabaseUtils.getConnectionPool(properties);
            BlockchainNodeInfoRepository blockchainNodeInfoRepository = new BlockchainNodeInfoRepository(connectionPool);
            BlockDataRepository blockDataRepository = new BlockDataRepository(connectionPool);

            NodeManager nodeManager = new NodeManager(properties, blockchainNodeInfoRepository);

            /** logger **/
            LoggerController.init(properties);
            
            /** api **/
            int port = Integer.parseInt(properties.getProperty(PORT_PROPERTY));

            RESTService restService = new RESTService(nodeManager, port, connectionPool);
            restService.init();
            restService.start();

            /** ingestor **/
            NodeIngestorManager nodeIngestorManager = new NodeIngestorManager(nodeManager, connectionPool, blockchainNodeInfoRepository, blockDataRepository);
            nodeIngestorManager.startActiveNodeWatcher();

        } catch (IOException e) {
            LOGGER.error("Exception caught in com.swatt.blockchain.Main: ", e);
        }
    }
}

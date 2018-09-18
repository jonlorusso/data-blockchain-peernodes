package com.swatt.blockchain;

import com.swatt.blockchain.repository.BlockDataRepository;
import com.swatt.blockchain.repository.BlockchainNodeInfoRepository;
import com.swatt.blockchain.service.NodeManager;
import com.swatt.blockchain.util.DatabaseUtils;
import com.swatt.util.general.CollectionsUtilities;
import com.swatt.util.log.LoggerController;
import com.swatt.util.sql.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class ApplicationContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

    private static final String PROPERTIES_FILENAME = "config.properties";

    private ConnectionPool connectionPool;
    private BlockchainNodeInfoRepository blockchainNodeInfoRepository;
    private BlockDataRepository blockDataRepository;
    private NodeManager nodeManager;

    public ApplicationContext() throws IOException {
        Properties properties = CollectionsUtilities.loadProperties(PROPERTIES_FILENAME);

        /** logger **/
        LoggerController.init(properties);

        connectionPool = DatabaseUtils.configureConnectionPoolFromEnvironment(properties);
        blockchainNodeInfoRepository = new BlockchainNodeInfoRepository(connectionPool);
        blockDataRepository = new BlockDataRepository(connectionPool);
        nodeManager = new NodeManager(blockchainNodeInfoRepository);
    }

    public ConnectionPool getConnectionPool() { return connectionPool; }
    public BlockchainNodeInfoRepository getBlockchainNodeInfoRepository() { return blockchainNodeInfoRepository; }
    public BlockDataRepository getBlockDataRepository() { return blockDataRepository; }
    public NodeManager getNodeManager() { return nodeManager; }
}

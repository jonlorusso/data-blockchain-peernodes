package com.swatt.chainNode;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.chainNode.service.ChainNodeIngestor;
import com.swatt.chainNode.service.ChainNodeManager;
import com.swatt.chainNode.service.ChainNodeService;
import com.swatt.chainNode.util.DatabaseUtils;
import com.swatt.util.general.CollectionsUtilities;
import com.swatt.util.general.OperationFailedException;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChainNodeService.class);
    
    public static void main(String[] args) {
        try {
            Properties properties = CollectionsUtilities.loadProperties("config.properties");

            /** api **/
            ChainNodeService chainNodeService = new ChainNodeService(properties);
            chainNodeService.init();
            chainNodeService.start();
            
            /** ingestor **/
            ChainNodeManager chainNodeManager = new ChainNodeManager(properties);
            
            String[] blockchainCodes = properties.getProperty(ChainNodeIngestor.BLOCKCHAIN_CODES_PROPERTY).split(",");
            for (String blockchainCode : blockchainCodes) {
                LOGGER.info(String.format("[%s] Starting chainNodeIngestor.", blockchainCode));
                
                Connection connection = DatabaseUtils.getConnection(properties);
                ChainNode chainNode = chainNodeManager.getChainNode(connection, blockchainCode);
                
                if (chainNode != null) {
                    ChainNodeIngestor chainNodeIngestor = new ChainNodeIngestor(properties, chainNode, connection);
                    chainNode.addChainNodeListener(chainNodeIngestor);
                    chainNode.fetchNewBlocks();
                    chainNodeIngestor.synchronizeChain();
                } else {
                    LOGGER.error(String.format("[%s] No chainNode found.", blockchainCode));
                }
            }
        } catch (IOException | SQLException | OperationFailedException e) {
            LOGGER.error("Exception caught in com.swatt.chainNode.Main: ", e);
        }
    }
}

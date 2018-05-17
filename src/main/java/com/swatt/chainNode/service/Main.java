package com.swatt.chainNode.service;

import static com.swatt.util.general.CollectionsUtilities.loadProperties;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.chainNode.ChainNode;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChainNode.class);
    private static final String PROPERTIES_FILENAME = "config.properties";

    public static void main(String[] args) {
        String filename = args.length > 0 ? args[0] : PROPERTIES_FILENAME;

        try {
            Properties properties = loadProperties(filename);

            /** logger **/
            LoggerController.init(properties);

            /** api **/
            RESTService restService = new RESTService(properties);
            restService.init();
            restService.start();

            /** ingestor **/
            /*
             * ChainNodeManager chainNodeManager = new ChainNodeManager(properties);
             * 
             * String[] blockchainCodes =
             * properties.getProperty(ChainNodeIngestor.BLOCKCHAIN_CODES_PROPERTY).split(","
             * ); for (String blockchainCode : blockchainCodes) {
             * LOGGER.info(String.format("[%s] Starting chainNodeIngestor.",
             * blockchainCode));
             * 
             * Connection connection = DatabaseUtils.getConnection(properties); ChainNode
             * chainNode = chainNodeManager.getChainNode(connection, blockchainCode);
             * 
             * if (chainNode != null) { ChainNodeIngestor chainNodeIngestor = new
             * ChainNodeIngestor(properties, chainNode, connection);
             * chainNode.addChainNodeListener(chainNodeIngestor);
             * chainNode.fetchNewBlocks(); chainNodeIngestor.synchronizeChain(); } else {
             * LOGGER.error(String.format("[%s] No chainNode found.", blockchainCode)); } }
             */
        } catch (IOException e) {
            LOGGER.error("Exception caught in com.swatt.chainNode.Main: ", e);
        }
    }
}

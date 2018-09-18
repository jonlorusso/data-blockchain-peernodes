package com.swatt.blockchain.util;

import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.blockchain.ingestor.NodeIngestor;
import com.swatt.blockchain.ingestor.NodeIngestorConfig;
import com.swatt.blockchain.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.class.getName());

    public static void info(Logger logger, BlockData blockData, String infoMessage) {
        logger.info(String.format("[%s] %s", blockData.getBlockchainCode(), infoMessage));
    }

    public static void info(Logger logger, String blockchainCode, String infoMessage) {
        logger.info(String.format("[%s] %s", blockchainCode, infoMessage));
    }

    public static void info(String blockchainCode, String infoMessage) {
        info(LOGGER, blockchainCode, infoMessage);
    }

    public static void info(Logger logger, BlockchainNodeInfo blockchainNodeInfo, String infoMessage) {
        info(logger, blockchainNodeInfo.getCode(), infoMessage);
    }

    public static void info(BlockchainNodeInfo blockchainNodeInfo, String infoMessage) {
        info(LOGGER, blockchainNodeInfo.getCode(), infoMessage);
    }

    public static void info(Logger logger, Node node, String infoMessage) {
        info(logger, node.getBlockchainCode(), infoMessage);
    }

    public static void info(Node node, String infoMessage) {
        info(LOGGER, node.getBlockchainCode(), infoMessage);
    }

    public static void info(Logger logger, NodeIngestorConfig nodeIngestorConfig, String infoMessage) {
        info(logger, nodeIngestorConfig.getBlockchainCode(), infoMessage);
    }

    public static void info(NodeIngestorConfig nodeIngestorConfig, String infoMessage) {
        info(LOGGER, nodeIngestorConfig.getBlockchainCode(), infoMessage);
    }

    public static void error(Logger logger, String blockchainCode, String errorMessage, Throwable e) {
        logger.error(String.format("[%s] %s", blockchainCode, errorMessage), e);
    }

    public static void error(String blockchainCode, String errorMessage, Throwable e) {
        error(LOGGER, blockchainCode, errorMessage, e);
    }

    public static void error(Logger logger, String blockchainCode, String errorMessage) {
        logger.error(String.format("[%s] %s", blockchainCode, errorMessage));
    }

    public static void error(String blockchainCode, String errorMessage) {
        error(LOGGER, blockchainCode, errorMessage);
    }

    public static void error(Logger logger, BlockchainNodeInfo blockchainNodeInfo, String errorMessage) {
        error(logger, blockchainNodeInfo.getCode(), errorMessage);
    }

    public static void error(BlockchainNodeInfo blockchainNodeInfo, String errorMessage) {
        error(LOGGER, blockchainNodeInfo.getCode(), errorMessage);
    }

    public static void error(Logger logger, BlockchainNodeInfo blockchainNodeInfo, String errorMessage, Throwable e) {
        error(logger, blockchainNodeInfo.getCode(), errorMessage, e);
    }

    public static void error(BlockchainNodeInfo blockchainNodeInfo, String errorMessage, Throwable e) {
        error(LOGGER, blockchainNodeInfo.getCode(), errorMessage, e);
    }

    public static void error(Logger logger, Node node, String errorMessage) {
        error(logger, node.getBlockchainCode(), errorMessage);
    }

    public static void error(Node node, String errorMessage) {
        error(LOGGER, node.getBlockchainCode(), errorMessage);
    }

    public static void error(Logger logger, Node node, String errorMessage, Throwable e) {
        error(logger, node.getBlockchainCode(), errorMessage, e);
    }

    public static void error(Node node, String errorMessage, Throwable e) {
        error(LOGGER, node.getBlockchainCode(), errorMessage, e);
    }

    public static void error(Logger logger, NodeIngestorConfig nodeIngestorConfig, String errorMessage, Throwable e) {
        error(logger, nodeIngestorConfig.getBlockchainCode(), errorMessage, e);
    }

    public static void error(NodeIngestorConfig nodeIngestorConfig, String errorMessage, Throwable e) {
        error(LOGGER, nodeIngestorConfig.getBlockchainCode(), errorMessage, e);
    }

    public static void error(Logger logger, NodeIngestorConfig nodeIngestorConfig, String errorMessage) {
        error(logger, nodeIngestorConfig.getBlockchainCode(), errorMessage);
    }

    public static void error(NodeIngestorConfig nodeIngestorConfig, String errorMessage) {
        error(LOGGER, nodeIngestorConfig.getBlockchainCode(), errorMessage);
    }
}

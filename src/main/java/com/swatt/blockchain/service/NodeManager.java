package com.swatt.blockchain.service;

import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.node.PlatformNode;
import com.swatt.blockchain.repository.BlockchainNodeInfoRepository;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.general.SystemUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;

public class NodeManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeManager.class);

    private static final String USE_FORWARDED_PORTS = "USE_FORWARDED_PORTS";

    private HashMap<BlockchainNodeInfo, Node> nodes = new HashMap<>();

    private BlockchainNodeInfoRepository blockchainNodeInfoRepository;

    private boolean useForwardedPorts = false;

    public NodeManager(BlockchainNodeInfoRepository blockchainNodeInfoRepository) {
        super();

        this.blockchainNodeInfoRepository = blockchainNodeInfoRepository;
        this.useForwardedPorts = Boolean.valueOf(SystemUtilities.getEnv(USE_FORWARDED_PORTS));
    }

    private Node createNode(BlockchainNodeInfo blockchainNodeInfo) {
        try {
            Class<?> clazz = Class.forName(blockchainNodeInfo.getClassName());
            Node node = (Node) clazz.newInstance();
            node.setBlockchainNodeInfo(blockchainNodeInfo);

            if (node instanceof PlatformNode) {
                PlatformNode platformNode = (PlatformNode)node;
                platformNode.setTokens(blockchainNodeInfoRepository.findAllByPlatformCode(blockchainNodeInfo.getCode()));
            }

            node.init();
            return node;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {
            LOGGER.error(String.format("Exception caught creating %s node: %s", blockchainNodeInfo.getCode(), e.getMessage()), e);
        }

        return null;
    }

    public Node getNode(BlockchainNodeInfo blockchainNodeInfo) {
        if (blockchainNodeInfo == null)
            return null;

        Node node = nodes.get(blockchainNodeInfo);
        if (node == null) {
            if (useForwardedPorts)
                blockchainNodeInfo.setPort(blockchainNodeInfo.getForwardedPort());

            node = createNode(blockchainNodeInfo);

            if (node != null)
                nodes.put(blockchainNodeInfo, node);
        }

        return node;
    }
}

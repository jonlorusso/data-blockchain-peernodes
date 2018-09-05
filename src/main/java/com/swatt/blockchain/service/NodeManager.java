package com.swatt.blockchain.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.node.PlatformNode;
import com.swatt.blockchain.repository.BlockchainNodeInfoRepository;
import com.swatt.blockchain.repository.BlockchainTokenRepository;
import com.swatt.util.general.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;

public class NodeManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeManager.class);

    private static final String NODE_OVERRIDE_IP_ENV_VAR_NAME = "NODE_OVERRIDE_IP";
    private static final String NODE_OVERRIDE_PORTS_ENV_VAR_NAME = "NODE_OVERRIDE_PORTS";
    
    private HashMap<String, Node> nodes = new HashMap<>();

    private String nodeOverrideIp;
    private Map<String, Integer> nodeOverridePorts = new HashMap<>();

    private BlockchainNodeInfoRepository blockchainNodeInfoRepository;
    private BlockchainTokenRepository blockchainTokenRepository;

    public NodeManager(BlockchainNodeInfoRepository blockchainNodeInfoRepository, BlockchainTokenRepository blockchainTokenRepository) {
        super();

        String nodeOverrideIp = System.getenv(NODE_OVERRIDE_IP_ENV_VAR_NAME);
        if (nodeOverrideIp != null) {
            this.nodeOverrideIp = nodeOverrideIp;
        }
        
        String nodeOverridePortsValue = System.getenv(NODE_OVERRIDE_PORTS_ENV_VAR_NAME);
        if (nodeOverridePortsValue != null) {
            for (String pair : nodeOverridePortsValue.split(",")) {
                String[] keyValue = pair.split("=");
                setOverridePort(keyValue[0], Integer.parseInt(keyValue[1]));
            }
        }
        
        this.blockchainNodeInfoRepository = blockchainNodeInfoRepository;
        this.blockchainTokenRepository = blockchainTokenRepository;
    }

    public void setOverrideIp(String nodeOverrideIp) {
        this.nodeOverrideIp = nodeOverrideIp;
    }

    public void setOverridePort(String blockchainCode, int port) {
        nodeOverridePorts.put(blockchainCode, port);
    }

    private Node createNode(BlockchainNodeInfo blockchainNodeInfo) {
        try {
            Class<?> clazz = Class.forName(blockchainNodeInfo.getClassName());
            Node node = (Node) clazz.newInstance();
            node.setBlockchainNodeInfo(blockchainNodeInfo);

            if (node instanceof PlatformNode)
                ((PlatformNode)node).setTokens(blockchainTokenRepository.findAllByPlatformCode(blockchainNodeInfo.getCode()));

            node.init();
            return node;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {
            LOGGER.error(String.format("Exception caught creating %s node: %s", blockchainNodeInfo.getCode(), e.getMessage()), e);
        }

        return null;
    }

    public Node getNode(String code) {
        code = code.toUpperCase();
        Node node = nodes.get(code);

        if (node == null) {
            try {
                BlockchainNodeInfo blockchainNodeInfo = blockchainNodeInfoRepository.findByCode(code);
                if (nodeOverrideIp != null) {
                    blockchainNodeInfo.setIp(nodeOverrideIp);
                    blockchainNodeInfo.setPort(nodeOverridePorts.get(code) != null ? nodeOverridePorts.get(code) : blockchainNodeInfo.getPort());
                }
                node = createNode(blockchainNodeInfo);

                if (node != null)
                    nodes.put(code, node);
            } catch (SQLException | OperationFailedException e) {
                LOGGER.error(String.format("Exception caught retreiving %s node: %s", code, e.getMessage()), e);
            }
        }

        return node;
    }
}

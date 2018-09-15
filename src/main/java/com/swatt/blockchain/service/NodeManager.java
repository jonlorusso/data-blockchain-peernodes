package com.swatt.blockchain.service;

import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.repository.BlockchainNodeInfoRepository;
import com.swatt.blockchain.util.LogUtils;
import com.swatt.util.general.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.swatt.blockchain.util.LogUtils.error;

public class NodeManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeManager.class.getName());

    private static final String NODE_OVERRIDE_IP_ENV_VAR_NAME = "NODE_OVERRIDE_IP";
    private static final String NODE_OVERRIDE_PORTS_ENV_VAR_NAME = "NODE_OVERRIDE_PORTS";
    
    private HashMap<String, Node> nodes = new HashMap<>();

    private String nodeOverrideIp;
    private Map<String, Integer> nodeOverridePorts = new HashMap<>();

    private BlockchainNodeInfoRepository blockchainNodeInfoRepository;

    public NodeManager(BlockchainNodeInfoRepository blockchainNodeInfoRepository) {
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
    }

    public void setOverrideIp(String nodeOverrideIp) {
        this.nodeOverrideIp = nodeOverrideIp;
    }

    public void setOverridePort(String blockchainCode, int port) {
        nodeOverridePorts.put(blockchainCode, port);
    }

    private Node createNode(BlockchainNodeInfo blockchainNodeInfo) {
        Node node = null;

        try {
            Class<?> clazz = Class.forName(blockchainNodeInfo.getClassName());
            node = (Node) clazz.newInstance();
            node.setBlockchainNodeInfo(blockchainNodeInfo);
            node.init();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            error(LOGGER, blockchainNodeInfo, "Unable to createNode.", e);
        }

        if (node == null) {
            error(LOGGER, blockchainNodeInfo, "Node is null after instantiation attempt.");
        }

        return node;
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
                error(LOGGER, code, "Unable to getNode.", e);
            }
        }

        return node;
    }
}

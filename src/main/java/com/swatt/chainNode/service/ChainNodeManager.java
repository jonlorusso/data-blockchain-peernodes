package com.swatt.chainNode.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.dao.BlockchainNodeInfo;;

public class ChainNodeManager {
    private HashMap<String, ChainNode> chainNodes = new HashMap<>();

    private String nodeOverrideIp;
    private Map<String, Integer> nodeOverridePorts = new HashMap<>();

    public ChainNodeManager(Properties properties) {
        super();
    }

    public void setOverrideIp(String nodeOverrideIp) {
        this.nodeOverrideIp = nodeOverrideIp;
    }

    public void setOverridePort(String blockchainCode, int port) {
        nodeOverridePorts.put(blockchainCode, port);
    }

    private ChainNode createChainNode(BlockchainNodeInfo blockchainNodeInfo) {
        try {
            Class<?> clazz = Class.forName(blockchainNodeInfo.getClassName());
            ChainNode chainNode = (ChainNode) clazz.newInstance();
            chainNode.setBlockchainNodeInfo(blockchainNodeInfo);
            chainNode.init();
            return chainNode;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        }

        return null;
    }

    public ChainNode getChainNode(Connection connection, String blockchainCode) {
        ChainNode chainNode = chainNodes.get(blockchainCode);
        blockchainCode = blockchainCode.toUpperCase();

        if (chainNode == null) {
            try {
                BlockchainNodeInfo blockchainNodeInfo = BlockchainNodeInfo.getBlockchainNodeInfo(connection, blockchainCode);
                blockchainNodeInfo.setIp(nodeOverrideIp);
                blockchainNodeInfo.setPort(nodeOverridePorts.get(blockchainCode) != null ? nodeOverridePorts.get(blockchainCode) : blockchainNodeInfo.getPort());
                chainNode = createChainNode(blockchainNodeInfo);

                if (chainNode != null)
                    chainNodes.put(blockchainCode, chainNode);
            } catch (SQLException e) {
                // FIXME logging?
            }
        }

        return chainNode;
    }
}

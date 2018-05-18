package com.swatt.chainNode.service;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.dao.BlockchainNodeInfo;;

public class ChainNodeManager {
    private HashMap<String, ChainNode> chainNodes = new HashMap<>();
    
    private Map<String, String> overrideIps = new HashMap<>();
    private Map<String, Integer> overridePorts = new HashMap<>();
    
    public ChainNodeManager(Properties properties) {
        super();
    
        if (properties.getProperty("chainNode.overrideIp") != null)
            overrideIps = stream(properties.getProperty("chainNode.overrideIp").split(",")).collect(toMap(s -> s.split("=")[0], s -> s.split("=")[1]));
        
        if (properties.getProperty("chainNode.overridePort") != null)
            overridePorts = stream(properties.getProperty("chainNode.overridePort").split(",")).collect(toMap(s -> s.split("=")[0], s -> parseInt(s.split("=")[1])));
    }
    
    private ChainNode createChainNode(BlockchainNodeInfo blockchainNodeInfo) {
        try {
            Class<?> clazz = Class.forName(blockchainNodeInfo.getClassName());
            ChainNode chainNode = (ChainNode)clazz.newInstance();
            chainNode.setBlockchainNodeInfo(blockchainNodeInfo);
            chainNode.init();
            return chainNode;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        }

        return null;
    }

    private void applyProperties(BlockchainNodeInfo blockchainNodeInfo) {
        if (overrideIps.containsKey(blockchainNodeInfo.getCode()))
            blockchainNodeInfo.setIp(overrideIps.get(blockchainNodeInfo.getCode()));
        
        if (overridePorts.containsKey(blockchainNodeInfo.getCode()))
            blockchainNodeInfo.setPort(overridePorts.get(blockchainNodeInfo.getCode()));
    }
    
    public ChainNode getChainNode(Connection connection, String blockchainCode) {
        ChainNode chainNode = chainNodes.get(blockchainCode);
        blockchainCode = blockchainCode.toUpperCase();

        if (chainNode == null) {
            try {
                BlockchainNodeInfo blockchainNodeInfo = BlockchainNodeInfo.getBlockchainNodeInfo(connection, blockchainCode);
                applyProperties(blockchainNodeInfo);
                chainNode = createChainNode(blockchainNodeInfo);

                if (chainNode != null)
                    chainNodes.put(blockchainCode, chainNode);
            } catch (SQLException e) {
            }
        }

        return chainNode;
    }
}

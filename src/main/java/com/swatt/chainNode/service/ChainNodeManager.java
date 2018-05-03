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
import com.swatt.chainNode.dao.BlockchainNodeInfo;
import com.swatt.util.general.OperationFailedException;;

public class ChainNodeManager {
    private HashMap<String, ChainNode> chainNodes = new HashMap<>();
    
    private Map<String, String> overrideIps = new HashMap<>();
    private Map<String, Integer> overridePorts = new HashMap<>();
    
    public ChainNodeManager(Properties properties) {
        super();
        
        overrideIps = stream(properties.getProperty("chainNode.overrideIp").split(",")).collect(toMap(s -> s.split("=")[0], s -> s.split("=")[1]));
        overridePorts = stream(properties.getProperty("chainNode.overridePort").split(",")).collect(toMap(s -> s.split("=")[0], s -> parseInt(s.split("=")[1])));
    }
    
    private ChainNode createChainNode(BlockchainNodeInfo blockchainNodeInfo) throws OperationFailedException {
        try {
            Class<?> clazz = Class.forName(blockchainNodeInfo.getClassName());
            ChainNode chainNode = (ChainNode)clazz.newInstance();
            chainNode.setBlockchainNodeInfo(blockchainNodeInfo);
            chainNode.init();
            return chainNode;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new OperationFailedException(e);
        }
    }
    
    private void applyProperties(BlockchainNodeInfo blockchainNodeInfo) {
        if (overrideIps.containsKey(blockchainNodeInfo.getCode()))
            blockchainNodeInfo.setIp(overrideIps.get(blockchainNodeInfo.getCode()));
        
        if (overridePorts.containsKey(blockchainNodeInfo.getCode()))
            blockchainNodeInfo.setPort(overridePorts.get(blockchainNodeInfo.getCode()));
    }
    
    public ChainNode getChainNode(Connection connection, String blockchainCode) throws OperationFailedException {
        ChainNode chainNode = chainNodes.get(blockchainCode);
        blockchainCode = blockchainCode.toUpperCase();

        if (chainNode == null) {
            try {
                BlockchainNodeInfo blockchainNodeInfo = BlockchainNodeInfo.getBlockchainNodeInfo(connection, blockchainCode);
                applyProperties(blockchainNodeInfo);
                chainNode = createChainNode(blockchainNodeInfo);
                chainNodes.put(blockchainCode, chainNode);
            } catch (SQLException e) {
                throw new OperationFailedException(e);
            }
        }

        return chainNode;
    }
}

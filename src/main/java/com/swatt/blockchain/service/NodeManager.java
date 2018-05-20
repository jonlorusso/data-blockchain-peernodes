package com.swatt.blockchain.service;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.repository.BlockchainNodeInfoRepository;
import com.swatt.util.general.OperationFailedException;;

public class NodeManager {
    private HashMap<String, Node> nodes = new HashMap<>();
    
    private Map<String, String> overrideIps = new HashMap<>();
    private Map<String, Integer> overridePorts = new HashMap<>();
    
    private BlockchainNodeInfoRepository blockchainNodeInfoRepository;
    
    public NodeManager(Properties properties, BlockchainNodeInfoRepository blockchainNodeInfoRepository) {
        super();

        this.blockchainNodeInfoRepository = blockchainNodeInfoRepository;
    
        if (properties.getProperty("node.overrideIp") != null)
            overrideIps = stream(properties.getProperty("node.overrideIp").split(",")).collect(toMap(s -> s.split("=")[0], s -> s.split("=")[1]));
        
        if (properties.getProperty("node.overridePort") != null)
            overridePorts = stream(properties.getProperty("node.overridePort").split(",")).collect(toMap(s -> s.split("=")[0], s -> parseInt(s.split("=")[1])));
    }
    
    private Node createNode(BlockchainNodeInfo blockchainNodeInfo) {
        try {
            Class<?> clazz = Class.forName(blockchainNodeInfo.getClassName());
            Node node = (Node)clazz.newInstance();
            node.setBlockchainNodeInfo(blockchainNodeInfo);
            node.init();
            return node;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            // FIXME
        }

        return null;
    }

    private void applyProperties(BlockchainNodeInfo blockchainNodeInfo) {
        if (overrideIps.containsKey(blockchainNodeInfo.getCode()))
            blockchainNodeInfo.setIp(overrideIps.get(blockchainNodeInfo.getCode()));
        
        if (overridePorts.containsKey(blockchainNodeInfo.getCode()))
            blockchainNodeInfo.setPort(overridePorts.get(blockchainNodeInfo.getCode()));
    }
    
    public Node getNode(String code) {
        code = code.toUpperCase();
        Node node = nodes.get(code);

        if (node == null) {
            try {
                BlockchainNodeInfo blockchainNodeInfo = blockchainNodeInfoRepository.findByCode(code);
                
                System.out.println(blockchainNodeInfo);
                applyProperties(blockchainNodeInfo);
                node = createNode(blockchainNodeInfo);

                if (node != null)
                    nodes.put(code, node);
            } catch (SQLException | OperationFailedException e) {
            }
        }

        return node;
    }
}

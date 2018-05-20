package com.swatt.blockchain.service;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.repository.BlockchainNodeInfoRepository;
import com.swatt.util.general.OperationFailedException;;

public class NodeManager {
    private HashMap<String, Node> nodes = new HashMap<>();
    

    private String nodeOverrideIp;
    private Map<String, Integer> nodeOverridePorts = new HashMap<>();
    
    private BlockchainNodeInfoRepository blockchainNodeInfoRepository;
    
    public NodeManager(BlockchainNodeInfoRepository blockchainNodeInfoRepository) {
        super();

        this.blockchainNodeInfoRepository = blockchainNodeInfoRepository;
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
            Node node = (Node)clazz.newInstance();
            node.setBlockchainNodeInfo(blockchainNodeInfo);
            node.init();
            return node;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            // FIXME
        }

        return null;
    }

    public Node getNode(String code) {
        code = code.toUpperCase();
        Node node = nodes.get(code);

        if (node == null) {
            try {
                BlockchainNodeInfo blockchainNodeInfo = blockchainNodeInfoRepository.findByCode(code);
                blockchainNodeInfo.setIp(nodeOverrideIp);
                blockchainNodeInfo.setPort(nodeOverridePorts.get(blockchainCode) != null ? nodeOverridePorts.get(blockchainCode) : blockchainNodeInfo.getPort());
                node = createNode(blockchainNodeInfo);

                if (node != null)
                    nodes.put(code, node);
            } catch (SQLException | OperationFailedException e) {
                // FIXME logging/exception handling
            }
        }

        return node;
    }
}

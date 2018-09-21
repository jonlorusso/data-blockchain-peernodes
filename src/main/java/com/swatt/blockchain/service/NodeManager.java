package com.swatt.blockchain.service;

import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.node.PlatformNode;
import com.swatt.blockchain.repository.BlockchainNodeInfoRepository;
import com.swatt.util.general.StringUtilities;
import com.swatt.util.general.SystemUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.swatt.blockchain.util.LogUtils.error;
import static java.util.stream.Collectors.toList;

public class NodeManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeManager.class.getName());

    private HashMap<String, Node> nodes = new HashMap<>();

    private static final String LOCALHOST = "127.0.0.1";
    private static final String USE_FORWARDED_PORTS = "USE_FORWARDED_PORTS";

    private BlockchainNodeInfoRepository blockchainNodeInfoRepository;

    private boolean useForwardedPorts = false;

    public NodeManager(BlockchainNodeInfoRepository blockchainNodeInfoRepository) {
        super();

        this.blockchainNodeInfoRepository = blockchainNodeInfoRepository;
        this.useForwardedPorts = Boolean.valueOf(SystemUtilities.getEnv(USE_FORWARDED_PORTS));
    }

    private Node createNode(BlockchainNodeInfo blockchainNodeInfo) {
        Node node = null;

        try {
            Class<?> clazz = Class.forName(blockchainNodeInfo.getClassName());
            node = (Node) clazz.newInstance();
            node.setBlockchainNodeInfo(blockchainNodeInfo);

            if (node instanceof PlatformNode) {
                PlatformNode platformNode = (PlatformNode)node;

                List<BlockchainNodeInfo> tokens = blockchainNodeInfoRepository.findAllByPlatformCode(blockchainNodeInfo.getCode())
                        .stream()
                        .filter(BlockchainNodeInfo::isEnabled)
                        .collect(toList());

                platformNode.setTokens(tokens);
            }

            node.init();
            return node;
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            // FIXME
            throw new RuntimeException(e);
        }
    }

    public Node getNode(String code) {
        if (StringUtilities.isNullOrAllWhiteSpace(code))
            return null;

        Node node = nodes.get(code);
        if (node == null) {
            try {
                BlockchainNodeInfo blockchainNodeInfo = blockchainNodeInfoRepository.findByCode(code);

                if (useForwardedPorts) {
                    blockchainNodeInfo.setIp(LOCALHOST);
                    blockchainNodeInfo.setPort(blockchainNodeInfo.getForwardedPort());
                }
                node = createNode(blockchainNodeInfo);

                if (node != null)
                    nodes.put(code, node);
            } catch (SQLException e ) {
                error(LOGGER, code, "Unable to getNode.", e);
            }
        }

        return node;
    }
}

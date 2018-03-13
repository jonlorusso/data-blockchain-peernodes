package com.swatt.chainNode.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import com.swatt.chainNode.ChainNode;
import com.swatt.util.ConnectionPool;
import com.swatt.util.OperationFailedException;
import com.swatt.util.SqlUtilities;

public class ChainNodeManager {
    private ChainNodeManagerConfig chainNodeManagerConfig;
    private HashMap<String, ChainNode> chainNodes = new HashMap<String, ChainNode>();

    public ChainNodeManager(ChainNodeManagerConfig chainNodeManagerConfig) {
        this.chainNodeManagerConfig = chainNodeManagerConfig;
    }

    public ChainNode getChainNode(String blockchainCode) throws OperationFailedException {
        ChainNode chainNode = chainNodes.get(blockchainCode);
        blockchainCode = blockchainCode.toUpperCase();

        if (chainNode == null) {
            ChainNodeConfig chainNodeConfig = chainNodeManagerConfig.getChainNodeConfig(blockchainCode);

            if (chainNodeConfig == null) {
                throw new OperationFailedException("No ChainNodeConfig found for: " + blockchainCode);
            }

            try {
                String className = chainNodeConfig.getClassName();
                Class<?> clazz = Class.forName(className);
                chainNode = (ChainNode) clazz.newInstance();
                chainNode.setChainNodeConfig(chainNodeConfig);
                chainNode.init();
            } catch (Throwable t) {
                throw new OperationFailedException("Unable to create ChainNode: " + blockchainCode, t);
            }
        }

        return chainNode;
    }

    public Connection getConnection() throws SQLException {
        String jdbcUrl = chainNodeManagerConfig.getAttribute("dbURL", null);
        String user = chainNodeManagerConfig.getAttribute("dbUser", null);
        String password = chainNodeManagerConfig.getAttribute("dbPassword", null);

        return SqlUtilities.getConnection(jdbcUrl, user, password);
    }

    public ConnectionPool getConnectionPool() {
        String dbUrl = chainNodeManagerConfig.getAttribute("dbURL", null);
        String dbUser = chainNodeManagerConfig.getAttribute("dbUser", null);
        String dbPassword = chainNodeManagerConfig.getAttribute("dbPassword", null);
        int dbMaxPoolSize = chainNodeManagerConfig.getIntAttribute("dbMaxPoolSize ", 1);

        ConnectionPool connectionPool = new ConnectionPool(dbUrl, dbUser, dbPassword, dbMaxPoolSize);
        return connectionPool;
    }
}

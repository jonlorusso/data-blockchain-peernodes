package com.swatt.chainNode.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import com.swatt.chainNode.dao.BlockchainNodeInfo;
import com.swatt.util.Attributable;
import com.swatt.util.CollectionsUtilities;
import com.swatt.util.SqlUtilities;

public class ChainNodeManagerConfig extends Attributable {
    HashMap<String, ChainNodeConfig> chainNodeConfigs = new HashMap<>();
    private static Connection conn = null;
    private static String rootURL = null;

    public ChainNodeManagerConfig(Properties properties) {
        super(properties);

        String dbUrl = this.getAttribute("dbURL", null);
        String dbUser = this.getAttribute("dbUser", null);
        String dbPassword = this.getAttribute("dbPassword", null);

        rootURL = this.getAttribute("rootURL", null);

        createConnection(dbUrl, dbUser, dbPassword);
    }

    public ChainNodeManagerConfig() throws IOException {
        String propertiesFileName = "config.properties";

        Properties properties = CollectionsUtilities.loadProperties(propertiesFileName);

        ChainNodeManagerConfig chainNodeManagerConfig = new ChainNodeManagerConfig(properties);

        String dbUrl = chainNodeManagerConfig.getAttribute("dbURL", null);
        String dbUser = chainNodeManagerConfig.getAttribute("dbUser", null);
        String dbPassword = chainNodeManagerConfig.getAttribute("dbPassword", null);

        rootURL = chainNodeManagerConfig.getAttribute("rootURL", null);

        createConnection(dbUrl, dbUser, dbPassword);
    }

    private void createConnection(String dbUrl, String dbUser, String dbPassword) {
        try {
            conn = SqlUtilities.getConnection(dbUrl, dbUser, dbPassword);

            getBlockchainInfo();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addChainNodeConfig(String blockchainCode, ChainNodeConfig chainNodeConfig) {
        chainNodeConfigs.put(blockchainCode, chainNodeConfig);
    }

    private final void getBlockchainInfo() throws SQLException {
        ArrayList<BlockchainNodeInfo> results = BlockchainNodeInfo.getAllBlockchainNodeInfos(conn);

        Iterator<BlockchainNodeInfo> iterator = results.iterator();
        while (iterator.hasNext()) {
            BlockchainNodeInfo blockchainNodeInfo = iterator.next();
            String blockchainCode = blockchainNodeInfo.getCode();

            String url = rootURL + ":" + blockchainNodeInfo.getForwardedPort();

            addChainNodeConfig(blockchainCode, new ChainNodeConfig(blockchainCode, blockchainNodeInfo.getClassName(),
                    url, blockchainNodeInfo.getRpcUn(), blockchainNodeInfo.getRpcPw()));
        }
    }

    public Connection getConnection() {
        return this.conn;
    }

    public ChainNodeConfig getChainNodeConfig(String blockchainCode) {
        return chainNodeConfigs.get(blockchainCode);
    }
}

package com.swatt.chainNode.service;

import java.sql.Connection;
import java.util.Properties;

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.Transaction;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.util.CollectionsUtilities;
import com.swatt.util.ConcurrencyUtilities;
import com.swatt.util.ConnectionPool;
import com.swatt.util.JsonUtilities;

import io.javalin.Javalin;

public class ChainNodeService {
    private Javalin app;

    /*
     * TODO re-enable SSL private static SslContextFactory getSslContextFactory() {
     * SslContextFactory sslContextFactory = new SslContextFactory();
     * sslContextFactory.setKeyStorePath(EmbeddedServer.class.getResource(
     * "/keystore.jks").toExternalForm());
     * sslContextFactory.setKeyStorePassword("password"); return sslContextFactory;
     * }
     */

    public ChainNodeService(final ChainNodeManager chainNodeManager, int port, final ConnectionPool connectionPool) {
        app = Javalin.create().port(port).enableCorsForOrigin("*");

        /*
         * embeddedServer(new EmbeddedJettyFactory(() -> { Server server = new Server();
         * ServerConnector sslConnector = new ServerConnector(server,
         * getSslContextFactory()); sslConnector.setPort(7000); ServerConnector
         * connector = new ServerConnector(server); connector.setPort(7070);
         * server.setConnectors(new Connector[] { sslConnector, connector }); return
         * server; })).
         */

        app.get("/:chainName/txn/:transactionHash", ctx -> {
            String chainName = ctx.param("chainName");
            String transactionHash = ctx.param("transactionHash");

            ChainNode chainNode = chainNodeManager.getChainNode(chainName);
            boolean calculateFees = true;

            Transaction chainTransaction = chainNode.fetchTransactionByHash(transactionHash, calculateFees); // Fetch
                                                                                                             // the
                                                                                                             // transactions
                                                                                                             // and
                                                                                                             // calculate
                                                                                                             // fees

            String result = JsonUtilities.objectToJsonString(chainTransaction);
            ctx.result(result);
        });

        app.get("/:blockchainCode/fetch/:blockHash", ctx -> { // TODO: This is just for debugging, this is not meant to
                                                              // be part of the Service API.
            String blockchainCode = ctx.param("blockchainCode");
            String blockHash = ctx.param("blockHash");

            Connection conn = connectionPool.getConnection(); // Do not use the JDK 1.7+ try with resource as we do NOT
                                                              // want to close the pooled connections

            ChainNode chainNode = null;
            try {
                chainNode = chainNodeManager.getChainNode(blockchainCode);
                BlockData blockData = chainNode.getBlockDataByHash(conn, blockHash);

                String result = JsonUtilities.objectToJsonString(blockData);
                ctx.result(result);
            } catch (Throwable t) {
                connectionPool.returnConnection(conn);
            }

            BlockData blockData = chainNode.fetchBlockDataByHash(blockHash);

            String result = JsonUtilities.objectToJsonString(blockData);
            ctx.result(result);

        });

        app.get("/:blockchainCode/blk/:blockHash", ctx -> {
            String blockchainCode = ctx.param("blockchainCode");
            String blockHash = ctx.param("blockHash");

            Connection conn = connectionPool.getConnection(); // Do not use the JDK 1.7+ try with resource as we do NOT
                                                              // want to close the pooled connections

            try {
                ChainNode chainNode = chainNodeManager.getChainNode(blockchainCode);
                BlockData blockData = chainNode.getBlockDataByHash(conn, blockHash);

                String result = JsonUtilities.objectToJsonString(blockData);
                ctx.result(result);
            } catch (Throwable t) {
                connectionPool.returnConnection(conn);
            }

        });

        app.get("/:blockchainCode/chn/:from/:to", ctx -> {
            String blockchainCode = ctx.param("blockchainCode");
            String sFrom = ctx.param("from");
            String sTo = ctx.param("to");

            // ChainNode chainNode = chainNodeManager.getChainNode(chainName);
            // BlockchainNodeData data =
            // blockchain.getDataForInterval(Long.parseLong(ctx.param("from")),
            // Long.parseLong(ctx.param("to")));
            //
            // ctx.result(returnObject(data));
        });
    }

    public void start() {
        app.start();
    }

    public void stop() {
        app.stop();
    }

    public static void main(String[] args) {
        try {
            String propertiesFileName = "config.properties";
            Properties properties = CollectionsUtilities.loadProperties(propertiesFileName);

            int port = Integer.parseInt(properties.getProperty("servicePort"));

            ChainNodeManagerConfig chainNodeManagerConfig = new ChainNodeManagerConfig();
            ChainNodeManager chainNodeManager = new ChainNodeManager(chainNodeManagerConfig);

            if (port > 0) {
                ConnectionPool connectionPool = chainNodeManager.getConnectionPool();

                ChainNodeService chainNodeService = new ChainNodeService(chainNodeManager, port, connectionPool);

                chainNodeService.start();

                long autoExitTimeout = 30 * 1000;

                ConcurrencyUtilities.startAutoDestructTimer(autoExitTimeout); // This is useful while debugging so you
                                                                              // don't have to constantly stop server to
                                                                              // restart it
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
package com.swatt.chainNode.service;

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.util.Properties;

import org.junit.Test;

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.chainNode.dao.BlockDataByInterval;
import com.swatt.util.general.CollectionsUtilities;
import com.swatt.util.general.ConcurrencyUtilities;
import com.swatt.util.json.JsonUtilities;
import com.swatt.util.sql.ConnectionPool;

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

        app.get("/:chainName/txn/:transactionHash", ctx -> {
            String chainName = ctx.param("chainName");
            String transactionHash = ctx.param("transactionHash");

            ChainNode chainNode = chainNodeManager.getChainNode(chainName);
            ChainNodeTransaction chainTransaction = chainNode.fetchTransactionByHash(transactionHash, true);

            String result = JsonUtilities.objectToJsonString(chainTransaction);
            ctx.result(result);
        });

        app.get("/:blockchainCode/blck/:blockHash", ctx -> {
            String blockchainCode = ctx.param("blockchainCode");
            String blockHash = ctx.param("blockHash");

            Connection conn = connectionPool.getConnection(); // Do not use the JDK 1.7+ try with resource as we do NOT
                                                              // want to close the pooled connections

            try {
                ChainNode chainNode = chainNodeManager.getChainNode(blockchainCode);
                BlockData blockData = chainNode.getBlockDataByHash(conn, blockHash);

                connectionPool.returnConnection(conn);

                String result = JsonUtilities.objectToJsonString(blockData);
                ctx.result(result);
            } catch (Throwable t) {
                connectionPool.returnConnection(conn);
            }
        });

        app.get("/:blockchainCode/block/:blockHash", ctx -> {
            String blockchainCode = ctx.param("blockchainCode");
            String blockHash = ctx.param("blockHash");

            Connection conn = connectionPool.getConnection(); // Do not use the JDK 1.7+ try with resource as we do NOT
                                                              // want to close the pooled connections

            try {
                ChainNode chainNode = chainNodeManager.getChainNode(blockchainCode);
                BlockData blockData = chainNode.getBlockDataByHash(conn, blockHash);

                connectionPool.returnConnection(conn);

                String result = JsonUtilities.objectToJsonString(blockData);
                ctx.result(result);
            } catch (Throwable t) {
                connectionPool.returnConnection(conn);
            }
        });

        app.get("/:blockchainCode/report/:from/:to", ctx -> {
            String blockchainCode = ctx.param("blockchainCode");
            String From = ctx.param("from");
            String To = ctx.param("to");

            Connection conn = connectionPool.getConnection(); // Do not use the JDK 1.7+ try with resource as we do NOT
                                                              // want to close the pooled connections

            try {
                ChainNode chainNode = chainNodeManager.getChainNode(blockchainCode);

                BlockDataByInterval aggregateData = chainNode.getDataForInterval(conn, blockchainCode,
                        Long.parseLong(From), Long.parseLong(To));

                String result = JsonUtilities.objectToJsonString(aggregateData);
                connectionPool.returnConnection(conn);

                ctx.result(result);
            } catch (Throwable t) {
                t.printStackTrace();
                connectionPool.returnConnection(conn);
            }
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

            ChainNodeManagerConfig chainNodeManagerConfig = new ChainNodeManagerConfig(properties);
            ChainNodeManager chainNodeManager = new ChainNodeManager(chainNodeManagerConfig);

            if (port > 0) {
                ConnectionPool connectionPool = chainNodeManager.getConnectionPool();

                ChainNodeService chainNodeService = new ChainNodeService(chainNodeManager, port, connectionPool);

                chainNodeService.start();

                long autoExitTimeout = 3000 * 1000;

                ConcurrencyUtilities.startAutoDestructTimer(autoExitTimeout); // This is useful while debugging so you
                                                                              // don't have to constantly stop server to
                                                                              // restart it
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Test
    public void test() {
        fail("Not yet implemented");
    }
}
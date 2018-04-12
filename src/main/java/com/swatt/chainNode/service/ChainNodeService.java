package com.swatt.chainNode.service;

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Properties;

import org.junit.Test;

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.dao.APIBlockData;
import com.swatt.chainNode.dao.APIBlockDataByInterval;
import com.swatt.chainNode.dao.APIPair;
import com.swatt.chainNode.dao.APIRateDay;
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

        app.get("/:blockchainCode/block/:blockHash", ctx -> {
            String blockchainCode = ctx.param("blockchainCode");
            String blockHash = ctx.param("blockHash");

            Connection conn = connectionPool.getConnection(); // Do not use the JDK 1.7+ try with resource as we do NOT
                                                              // want to close the pooled connections

            try {
                ChainNode chainNode = chainNodeManager.getChainNode(blockchainCode);
                APIBlockData blockData = chainNode.getBlockDataByHash(conn, blockHash);

                String result = JsonUtilities.objectToJsonString(blockData);
                connectionPool.returnConnection(conn);

                ctx.result(result);
            } catch (Throwable t) {
                connectionPool.returnConnection(conn);
            }
        });

        app.get("/dayhistory/:fromCcy/:toCcy/:day", ctx -> {
            String fromCcy = ctx.param("fromCcy");
            String toCcy = ctx.param("toCcy");
            String dayText = ctx.param("day");
            dayText = dayText.substring(0, 4) + "-" + dayText.substring(4, 6) + "-" + dayText.substring(6, 8);

            java.sql.Date day = java.sql.Date.valueOf(dayText);

            Connection conn = connectionPool.getConnection(); // Do not use the JDK 1.7+ try with resource as we do NOT
                                                              // want to close the pooled connections

            try {
                APIRateDay rateDay = APIRateDay.call(conn, fromCcy, toCcy, day);

                String result = JsonUtilities.objectToJsonString(rateDay);
                connectionPool.returnConnection(conn);

                ctx.result(result);
            } catch (Throwable t) {
                connectionPool.returnConnection(conn);
            }
        });

        app.get("/dayhistory/:fromCcy/:toCcy/:fromDay/:toDay", ctx -> {
            String fromCcy = ctx.param("fromCcy");
            String toCcy = ctx.param("toCcy");

            String fromDayText = ctx.param("fromDay");
            fromDayText = fromDayText.substring(0, 4) + "-" + fromDayText.substring(4, 6) + "-"
                    + fromDayText.substring(6, 8);
            java.sql.Date fromDay = java.sql.Date.valueOf(fromDayText);

            String toDayText = ctx.param("toDay");
            toDayText = toDayText.substring(0, 4) + "-" + toDayText.substring(4, 6) + "-" + toDayText.substring(6, 8);
            java.sql.Date toDay = java.sql.Date.valueOf(toDayText);

            Connection conn = connectionPool.getConnection(); // Do not use the JDK 1.7+ try with resource as we do NOT
                                                              // want to close the pooled connections

            try {
                ArrayList<APIRateDay> rateDays = APIRateDay.call(conn, fromCcy, toCcy, fromDay, toDay);

                String result = JsonUtilities.objectToJsonString(rateDays);
                connectionPool.returnConnection(conn);

                ctx.result(result);
            } catch (Throwable t) {
                connectionPool.returnConnection(conn);
            }
        });

        app.get("/pairs", ctx -> {

            Connection conn = connectionPool.getConnection(); // Do not use the JDK 1.7+ try with resource as we do NOT
                                                              // want to close the pooled connections

            try {
                ArrayList<APIPair> rateDays = APIPair.call(conn);

                String result = JsonUtilities.objectToJsonString(rateDays);
                connectionPool.returnConnection(conn);

                ctx.result(result);
            } catch (Throwable t) {
                connectionPool.returnConnection(conn);
            }
        });

        app.get("/:blockchainCode/blocks/:from/:to", ctx -> {
            String blockchainCode = ctx.param("blockchainCode");
            String From = ctx.param("from");
            String To = ctx.param("to");

            Connection conn = connectionPool.getConnection(); // Do not use the JDK 1.7+ try with resource as we do NOT
                                                              // want to close the pooled connections

            try {
                ChainNode chainNode = chainNodeManager.getChainNode(blockchainCode);
                ArrayList<APIBlockData> blockData = chainNode.getBlocks(conn, Long.parseLong(From), Long.parseLong(To));

                String result = JsonUtilities.objectToJsonString(blockData);
                connectionPool.returnConnection(conn);

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

                APIBlockDataByInterval aggregateData = chainNode.getDataForInterval(conn, Long.parseLong(From),
                        Long.parseLong(To));

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

                long autoExitTimeout = 300 * 1000;

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
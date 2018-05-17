package com.swatt.chainNode.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.dao.ApiBlockData;
import com.swatt.chainNode.dao.ApiBlockDataByDay;
import com.swatt.chainNode.dao.ApiBlockDataByInterval;
import com.swatt.chainNode.dao.ApiTime;
import com.swatt.chainNode.dao.ApiUser;
import com.swatt.chainNode.util.DatabaseUtils;
import com.swatt.util.sql.ConnectionPool;

import io.javalin.Context;
import io.javalin.Javalin;

public class RESTService {
//    private static final Logger LOGGER = LoggerFactory.getLogger(RESTService.class);

    private static final String PORT_PROPERTY = "servicePort";

    private Javalin app;

    private int port;
    private ConnectionPool connectionPool;
    private ChainNodeManager chainNodeManager;

    /*
     * TODO re-enable SSL private static SslContextFactory getSslContextFactory() {
     * SslContextFactory sslContextFactory = new SslContextFactory();
     * sslContextFactory.setKeyStorePath(EmbeddedServer.class.getResource(
     * "/keystore.jks").toExternalForm());
     * sslContextFactory.setKeyStorePassword("password"); return sslContextFactory;
     * }
     */

    public RESTService(ChainNodeManager chainNodeManager, int port, ConnectionPool connectionPool) {
        this.chainNodeManager = chainNodeManager;
        this.port = port;
        this.connectionPool = connectionPool;
    }

    public RESTService(Properties properties) {
        chainNodeManager = new ChainNodeManager(properties);

        port = Integer.parseInt(properties.getProperty(PORT_PROPERTY));
        connectionPool = DatabaseUtils.getConnectionPool(properties);
    }

    public void init() {
        app = Javalin.create().port(port).enableCorsForOrigin("*").enableStandardRequestLogging();

        app.get("/time", ctx -> {
            try (Connection connection = connectionPool.getConnection()) {
                ctx.json(ApiTime.authCredentials(connection));
            } 
        });

        app.get("/blockchain/:blockchainCode/txn/:transactionHash", ctx -> {
            String blockchainCode = ctx.param("blockchainCode");

            try (Connection connection = connectionPool.getConnection()) {
                if (!validateKey(connection, ctx)) {
                    ctx.status(401);
                    return;
                }

                String transactionHash = ctx.param("transactionHash");

                ChainNode chainNode = chainNodeManager.getChainNode(connection, blockchainCode);
                ChainNodeTransaction chainTransaction = chainNode.fetchTransactionByHash(transactionHash, true);

                ctx.json(chainTransaction);
            }
        });

        app.get("/blockchain/:blockchainCode/blocks/:blockKey", ctx -> {
            String blockchainCode = ctx.param("blockchainCode");

            // TODO switch for height
            String blockHash = ctx.param("blockKey");

            try (Connection connection = connectionPool.getConnection()) {
                if (!validateKey(connection, ctx)) {
                    ctx.status(401);
                    return;
                }
                
                ChainNode chainNode = chainNodeManager.getChainNode(connection, blockchainCode);
                ApiBlockData blockData = chainNode.getBlockDataByHash(connection, blockHash);
                
                ctx.json(blockData);
            }
        });

        /*
         * 
         * app.get("/dayhistory/:fromCcy/:toCcy/:day", ctx -> { String fromCcy =
         * ctx.param("fromCcy"); String toCcy = ctx.param("toCcy"); String dayText =
         * ctx.param("day"); dayText = dayText.substring(0, 4) + "-" +
         * dayText.substring(4, 6) + "-" + dayText.substring(6, 8);
         * 
         * java.sql.Date day = java.sql.Date.valueOf(dayText);
         * 
         * Connection conn = connectionPool.getConnection(); // Do not use the JDK 1.7+
         * try with resource as we do NOT // want to close the pooled connections
         * 
         * try { ApiRateDay rateDay = ApiRateDay.call(conn, fromCcy, toCcy, day);
         * 
         * String result = JsonUtilities.objectToJsonString(rateDay);
         * connectionPool.returnConnection(conn);
         * 
         * ctx.result(result); } catch (Throwable t) {
         * connectionPool.returnConnection(conn); } });
         * 
         * app.get("/dayhistory/:fromCcy/:toCcy/:fromDay/:toDay", ctx -> { String
         * fromCcy = ctx.param("fromCcy"); String toCcy = ctx.param("toCcy");
         * 
         * String fromDayText = ctx.param("fromDay"); fromDayText =
         * fromDayText.substring(0, 4) + "-" + fromDayText.substring(4, 6) + "-" +
         * fromDayText.substring(6, 8); java.sql.Date fromDay =
         * java.sql.Date.valueOf(fromDayText);
         * 
         * String toDayText = ctx.param("toDay"); toDayText = toDayText.substring(0, 4)
         * + "-" + toDayText.substring(4, 6) + "-" + toDayText.substring(6, 8);
         * java.sql.Date toDay = java.sql.Date.valueOf(toDayText);
         * 
         * Connection conn = connectionPool.getConnection(); // Do not use the JDK 1.7+
         * try with resource as we do NOT // want to close the pooled connections
         * 
         * try { ArrayList<ApiRateDay> rateDays = ApiRateDay.call(conn, fromCcy, toCcy,
         * fromDay, toDay);
         * 
         * String result = JsonUtilities.objectToJsonString(rateDays);
         * connectionPool.returnConnection(conn);
         * 
         * ctx.result(result); } catch (Throwable t) {
         * connectionPool.returnConnection(conn); } });
         * 
         * app.get("/pairs", ctx -> { System.out.println("Pairs api");
         * 
         * Connection conn = connectionPool.getConnection(); // Do not use the JDK 1.7+
         * try with resource as we do NOT // want to close the pooled connections
         * 
         * System.out.println("Connected " + conn.toString());
         * 
         * try { ArrayList<ApiPair> rateDays = ApiPair.call(conn);
         * 
         * String result = JsonUtilities.objectToJsonString(rateDays);
         * 
         * connectionPool.returnConnection(conn);
         * 
         * ctx.result(result); } catch (Throwable t) { System.out.println(t.toString());
         * connectionPool.returnConnection(conn); } });
         */

        app.get("/blockchain/:blockchainCode/blocks/", ctx -> {
            String blockchainCode = ctx.param("blockchainCode");
            String From = ctx.queryParam("fromdate");
            String To = ctx.queryParam("todate");

            try (Connection connection = connectionPool.getConnection()) {
                if (!validateKey(connection, ctx)) {
                    ctx.status(401);
                    return;
                }
                
                ChainNode chainNode = chainNodeManager.getChainNode(connection, blockchainCode);
                ArrayList<ApiBlockData> blockData = chainNode.getBlocks(connection, Long.parseLong(From), Long.parseLong(To));
                
                ctx.json(blockData);
            }
        });

        app.get("/blockchain/:blockchainCode/summary/", ctx -> {
            String blockchainCode = ctx.param("blockchainCode");
            String summarizeBy = ctx.queryParam("by");
            String from = ctx.queryParam("fromdate");
            String to = ctx.queryParam("todate");

            try (Connection connection = connectionPool.getConnection()) {
                if (!validateKey(connection, ctx)) {
                    ctx.status(401);
                    return;
                }

                if (summarizeBy == null) {
                    System.out.println("all");

                    ChainNode chainNode = chainNodeManager.getChainNode(connection, blockchainCode);
                    ApiBlockDataByInterval aggregateData = chainNode.getDataForInterval(connection, Long.parseLong(from), Long.parseLong(to));

                    ctx.json(aggregateData);
                } else if (summarizeBy.equals("day")) {
                    ChainNode chainNode = chainNodeManager.getChainNode(connection, blockchainCode);
                    ArrayList<ApiBlockDataByDay> blockData = chainNode.getBlocksByDay(connection, Long.parseLong(from), Long.parseLong(to));

                    ctx.json(blockData);
                }
            }
        });

        app.post("/auth", ctx -> {
            String email = ctx.formParam("email");
            String passwordHash = ctx.formParam("password");

            try (Connection connection = connectionPool.getConnection()) {
                ApiUser user = ApiUser.authCredentials(connection, email, passwordHash);

                if (user == null)
                    ctx.status(401);
                else
                    ctx.json(user);
            }
        });
    }

    public boolean validateKey(Connection conn, Context ctx) {
        boolean valid = false;
        String apiKey = ctx.header("Authorization");

        final String BEARER_PREFIX = "Bearer ";

        if (apiKey == null)
            return valid;

        String apiKeyPrefix = apiKey.substring(0, Math.min(BEARER_PREFIX.length(), apiKey.length()));

        if (!apiKeyPrefix.equals(BEARER_PREFIX))
            return valid;

        apiKey = apiKey.replace(BEARER_PREFIX, "");

        try {
            valid = ApiUser.authKey(conn, apiKey);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return valid;
    }

    public void start() {
        app.start();
    }

    public void stop() {
        app.stop();
    }
}
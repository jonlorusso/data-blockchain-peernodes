package com.swatt.internal.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swatt.blockchain.BlockchainBlockData;
import com.swatt.blockchain.BlockchainNode;
import com.swatt.blockchain.BlockchainNodeData;
import com.swatt.blockchain.BlockchainTransaction;
import com.swatt.blockchain.NodePicker;

import io.javalin.Javalin;

public class API {
    public static void main(String[] args) {
        Javalin app = Javalin.create().port(7000).enableCorsForOrigin("*");
        new NodePicker();

        /*
         * embeddedServer(new EmbeddedJettyFactory(() -> { Server server = new Server();
         * ServerConnector sslConnector = new ServerConnector(server,
         * getSslContextFactory()); sslConnector.setPort(7000); ServerConnector
         * connector = new ServerConnector(server); connector.setPort(7070);
         * server.setConnectors(new Connector[] { sslConnector, connector }); return
         * server; })).
         */

        app.start();

        app.get("/:ticker/transaction/:hash", ctx -> {
            BlockchainNode blockchain = NodePicker.getBlockchain(ctx.param("ticker"));
            BlockchainTransaction transaction = blockchain.findTransactionByHash(ctx.param("hash"), true);

            ctx.result(returnObject(transaction));
        });

        app.get("/:ticker/block/:hash", ctx -> {
            BlockchainNode blockchain = NodePicker.getBlockchain(ctx.param("ticker"));
            BlockchainBlockData block = blockchain.retrieveBlockByHash(ctx.param("hash"));

            ctx.result(returnObject(block));
        });

        app.get("/:ticker/data/:from/:to", ctx -> {
            BlockchainNode blockchain = NodePicker.getBlockchain(ctx.param("ticker"));

            Long fromTime = Long.parseLong(ctx.param("from"));
            Long toTime = Long.parseLong(ctx.param("to"));

            BlockchainNodeData data = blockchain.getDataForInterval(fromTime, toTime);

            ctx.result(returnObject(data));
        });
    }

    /*
     * TODO re-enable SSL private static SslContextFactory getSslContextFactory() {
     * SslContextFactory sslContextFactory = new SslContextFactory();
     * sslContextFactory.setKeyStorePath(EmbeddedServer.class.getResource(
     * "/keystore.jks").toExternalForm());
     * sslContextFactory.setKeyStorePassword("password"); return sslContextFactory;
     * }
     */

    private static String returnObject(Object rtn) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(rtn);
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }

        return json;
    }
}
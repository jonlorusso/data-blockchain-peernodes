package com.swatt.internal.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swatt.blockchain.BlockchainBlockData;
import com.swatt.blockchain.BlockchainNode;
import com.swatt.blockchain.BlockchainTransaction;

import io.javalin.Javalin;

public class API {
    public static void main(String[] args) {
        Javalin app = Javalin.create().port(7000).enableCorsForOrigin("*");

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
            BlockchainNode blockchain = getBlockchain(ctx.param("ticker"));
            BlockchainTransaction transaction = blockchain.findTransactionByHash(ctx.param("hash"), true);

            ctx.result(returnObject(transaction));
        });

        app.get("/:ticker/block/:hash", ctx -> {
            BlockchainNode blockchain = getBlockchain(ctx.param("ticker"));
            BlockchainBlockData block = blockchain.retrieveBlockByHash(ctx.param("hash"));

            ctx.result(returnObject(block));
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

    private static com.swatt.blockchain.BlockchainNode getBlockchain(String blockchainTicker) {
        com.swatt.blockchain.BlockchainNode blockchain = null;

        switch (blockchainTicker) {
        case "btc":
            blockchain = new com.swatt.blockchain.btc.BlockchainNode();
            break;
        case "eth":
            blockchain = new com.swatt.blockchain.eth.BlockchainNode();
            break;
        }

        System.out.println(blockchain.getTicker());

        return blockchain;
    }
}
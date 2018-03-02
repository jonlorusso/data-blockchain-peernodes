package com.swatt.internal.rest;

import io.javalin.Javalin;

public class API {
    public static void main(String[] args) {
        Javalin app = Javalin.create().port(7000).enableCorsForOrigin("http://localhost").start();

        app.get("/:ticker/transactionHashes/:lookup", ctx -> {
            com.swatt.blockchain.BlockchainNode blockchain = null;

            switch (ctx.param("ticker")) {
            case "btc":
                blockchain = new com.swatt.blockchain.btc.BlockchainNode();
                break;
            }

            Long blockId = null;
            String blockHash = null;
            String transactions = null;

            try {
                blockId = Long.parseLong(ctx.param("lookup"));

                transactions = blockchain.getBlockTransactionsById(blockId);
            } catch (NumberFormatException nfe) {
                blockHash = ctx.param("lookup");

                transactions = blockchain.getBlockTransactionsByHash(blockHash);
            }

            ctx.result("Transactions: " + transactions);
        });

        app.get("/:ticker/transactionHashes/", ctx -> {
            com.swatt.blockchain.BlockchainNode blockchain = null;

            switch (ctx.param("ticker")) {
            case "btc":
                blockchain = new com.swatt.blockchain.btc.BlockchainNode();
                break;
            }

            String transactions = blockchain.getLatestBlockTransactions();

            ctx.result("Transactions: " + transactions);
        });

        app.get("/:ticker/transaction/:hash", ctx -> {
            com.swatt.blockchain.BlockchainNode blockchain = null;

            switch (ctx.param("ticker")) {
            case "btc":
                blockchain = new com.swatt.blockchain.btc.BlockchainNode();
                break;
            }

            String transactionHash = blockchain.getTransaction(ctx.param("hash"));

            ctx.result("Transaction: " + transactionHash);
        });

        app.get("/buy/:symbol/:quantity", ctx -> {
            ctx.result("Buy confirmed " + ctx.param("symbol") + ':' + ctx.param("quantity"));
        });

        app.get("/sell/:symbol/:quantity", ctx -> {
            ctx.result("Sell confirmed " + ctx.param("symbol") + ':' + ctx.param("quantity"));
        });

        app.get("/cancel/:symbol", ctx -> {
            ctx.result("Cancel confirmed " + ctx.param("symbol"));
        });
    }
}
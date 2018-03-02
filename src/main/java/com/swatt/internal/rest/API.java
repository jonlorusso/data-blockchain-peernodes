package com.swatt.internal.rest;

import io.javalin.Javalin;

public class API {
    public static void main(String[] args) {
        Javalin app = Javalin.create().port(7000).enableCorsForOrigin("*").start();

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

        app.get("/buy/:symbol/:quantity/:price", ctx -> {
            ctx.result("Buy received " + ctx.param("symbol") + ':' + ctx.param("quantity") + ':' + ctx.param("price"));
        });

        app.get("/sell/:symbol/:quantity/:price", ctx -> {
            ctx.result("Sell received " + ctx.param("symbol") + ':' + ctx.param("quantity") + ':' + ctx.param("price"));
        });

        app.get("/cancel/:symbol", ctx -> {
            ctx.result("Cancel received " + ctx.param("symbol"));
        });

        app.get("/getAll", ctx -> {
            Toy tradesToy = new Toy();
            String trades = tradesToy.getMessages();
            ctx.result(trades);
        });
    }
}
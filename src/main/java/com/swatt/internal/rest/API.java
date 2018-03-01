package com.swatt.internal.rest;

import io.javalin.Javalin;

public class API {
    public static void main(String[] args) {
        Javalin app = Javalin.start(7000);
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

        app.get("/:ticker/block/", ctx -> {
            com.swatt.blockchain.BlockchainNode blockchain = null;

            switch (ctx.param("ticker")) {
            case "btc":
                blockchain = new com.swatt.blockchain.btc.BlockchainNode();
                break;
            }

            String transactions = blockchain.getLatestBlockTransactions();

            ctx.result("Transactions: " + transactions);
        });
    }
}
package com.swatt.internal.rest;

import com.swatt.blockchain.BlockchainNode;
import com.swatt.blockchain.BlockchainTransaction;

import io.javalin.Javalin;

public class API {
    public static void main(String[] args) {
        Javalin app = Javalin.create().port(7000).enableCorsForOrigin("*").start();

        /*
         * app.get("/:ticker/transactionHashes/:lookup", ctx -> {
         * com.swatt.blockchain.BlockchainNode blockchain = null;
         * 
         * switch (ctx.param("ticker")) { case "btc": blockchain = new
         * com.swatt.blockchain.btc.BlockchainNode(); break; case "eth": blockchain =
         * new com.swatt.blockchain.eth.BlockchainNode(); break; }
         * 
         * Long blockId = null; String blockHash = null; String transactions = null;
         * 
         * try { blockId = Long.parseLong(ctx.param("lookup"));
         * 
         * transactions = blockchain.getBlockTransactionsById(blockId); } catch
         * (NumberFormatException nfe) { blockHash = ctx.param("lookup");
         * 
         * transactions = blockchain.getBlockTransactionsByHash(blockHash); }
         * 
         * ctx.result("Transactions: " + transactions); });
         */

        app.get("/:ticker/transactionHashes/", ctx -> {
            BlockchainNode blockchain = null;

            switch (ctx.param("ticker")) {
            case "btc":
                blockchain = new com.swatt.blockchain.btc.BlockchainNode();
                break;
            }

            // String transactions = blockchain.getLatestBlockTransactions();

            // /ctx.result("Transactions: " + transactions);
        });

        app.get("/:ticker/transaction/:hash", ctx -> {
            BlockchainNode blockchain = null;

            switch (ctx.param("ticker")) {
            case "btc":
                blockchain = new com.swatt.blockchain.btc.BlockchainNode();
                break;
            }

            BlockchainTransaction transaction = blockchain.findTransactionByHash(ctx.param("hash"));

            ctx.result("Transaction: " + transaction.getHash());
        });
    }
}
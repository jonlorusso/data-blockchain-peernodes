package com.swatt.internal.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swatt.blockchain.BlockchainBlock;
import com.swatt.blockchain.BlockchainNode;
import com.swatt.blockchain.BlockchainTransaction;

import io.javalin.Javalin;

public class API {
    public static void main(String[] args) {
        Javalin app = Javalin.create().port(7000).enableCorsForOrigin("*").start();

        app.get("/:ticker/transaction/:hash", ctx -> {
            BlockchainNode blockchain = getBlockchain(ctx.param("ticker"));
            BlockchainTransaction transaction = blockchain.findTransactionByHash(ctx.param("hash"), true);

            ctx.result(returnObject(transaction));
        });

        app.get("/:ticker/block/:hash", ctx -> {
            BlockchainNode blockchain = getBlockchain(ctx.param("ticker"));
            BlockchainBlock block = blockchain.findBlockByHash(ctx.param("hash"));

            ctx.result(returnObject(block));
        });
    }

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
        }

        return blockchain;
    }
}
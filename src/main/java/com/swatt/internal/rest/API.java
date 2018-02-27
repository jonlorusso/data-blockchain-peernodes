package com.swatt.internal.rest;

import io.javalin.Javalin;
import com.swatt.blockchain.persistence.*;

public class API {
    public static void main(String[] args) {
        Javalin app = Javalin.start(7000);
        app.get("/:ticker/transactionhash/:hash", ctx -> {
	        	com.swatt.blockchain.BlockchainNode blockchain = null;
	        	
            switch (ctx.param("ticker")) {
            case "btc":  
            		blockchain = new com.swatt.blockchain.btc.BlockchainNode();
                break;
            }
            
            blockchain.findTransactionByHash(ctx.param("hash"));
            
            ctx.result("Ticker: " + ctx.param("ticker"));
        });
        
        app.get("/size", ctx -> {
        		com.swatt.blockchain.BlockchainNode btc = new com.swatt.blockchain.btc.BlockchainNode();
//    		BlockchainDB.main(null);
        		ctx.json(100);
        });               
    }
}
package com.swatt.blockchain.persistence;

import com.swatt.blockchain.BlockchainBlock;

public class Ingestor {
    public static final String DEFAULT_BLOCKCHAIN_TICKER = "btc";

    public static void main(String[] args) {
        String ticker = null;

        if (args.length == 0)
            ticker = DEFAULT_BLOCKCHAIN_TICKER;
        else
            ticker = args[0];

        com.swatt.blockchain.BlockchainNode blockchain = null;

        switch (ticker) {
        case "btc":
            blockchain = new com.swatt.blockchain.btc.BlockchainNode();
            break;
        case "eth":
            blockchain = new com.swatt.blockchain.eth.BlockchainNode();
            break;
        }

        // BlockchainTransaction tx = null;
        BlockchainBlock block = null;

        try {
            // firstTx = blockchain.getLatestBlockTransactions();
            // tx =
            // blockchain.findTransactionByHash("c63ea1f5b4c7e5cc359e26c7cccf91b661d3bfcf332b7073f9fcd18383e048ad");
            block = blockchain.findBlockByHash("00000000000000000024fb37364cbf81fd49cc2d51c09c75c35433c3a1945d04");
            System.out.println("Fee " + Double.toString(block.getAverageFee()));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}

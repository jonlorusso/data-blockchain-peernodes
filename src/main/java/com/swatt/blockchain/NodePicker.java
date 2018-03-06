package com.swatt.blockchain;

public class NodePicker {
    public static final String DEFAULT_BLOCKCHAIN_TICKER = "btc";

    public static BlockchainNode getBlockchain() {
        return getBlockchain(DEFAULT_BLOCKCHAIN_TICKER);
    }

    public static BlockchainNode getBlockchain(String blockchainTicker) {
        BlockchainNode blockchain = null;

        switch (blockchainTicker) {
        case "btc":
        case "bch":
            blockchain = new com.swatt.blockchain.btc.BlockchainNode(blockchainTicker);
            break;
        case "eth":
            blockchain = new com.swatt.blockchain.eth.BlockchainNode(blockchainTicker);
            break;
        }

        return blockchain;
    }
}

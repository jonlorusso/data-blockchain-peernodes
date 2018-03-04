package com.swatt.blockchain;

public abstract class BlockchainBlock {
    private String hash;

    public BlockchainBlock(String blockHash) {
        this.hash = blockHash;
    }

    public BlockchainBlock() {
        this(null);
    }

    public abstract Double getAverageFee();
}
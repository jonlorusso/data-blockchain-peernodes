package com.swatt.blockchain;

public abstract class BlockchainBlock {
    private String hash;

    public BlockchainBlock(BlockchainNode node, String blockHash) {
        this.hash = blockHash;
    }

    public BlockchainBlock(BlockchainNode node) {
        this(node, null);
    }

    public BlockchainBlock() {
        this(null, null);
    }

    public String getHash() {
        return this.hash;
    }

    public abstract Double getAverageFee();

    public abstract Double getAverageFeeRate();

    public abstract Double getLargestFee();

    public abstract Double getSmallestFee();

    public abstract String getLargestTxHash();

    public abstract Long getTransactionCount();

    public abstract int getHeight();

    public abstract Double getDifficulty();

    public abstract String getMerkleRoot();

    public abstract Long getTimestamp();

    public abstract String getBits();

    public abstract int getSize();

    public abstract String getVersionHex();

    public abstract Long getNonce();

    public abstract String getPrevHash();

    public abstract String getNextHash();

    public Double getLargestTxAmount() {
        // TODO Auto-generated method stub
        return null;
    }
}
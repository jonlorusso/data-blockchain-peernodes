package com.swatt.blockchain;

/**
 * Information about a specific Blockchain Transaction
 */
public abstract class BlockchainTransaction {
    String hash;

    public BlockchainTransaction(String hash) {
        this.hash = hash;
    }

    public abstract Double getFee();

    public abstract String getHash();

    public abstract Long getTimestamp();

    public abstract Double getAmount();
}
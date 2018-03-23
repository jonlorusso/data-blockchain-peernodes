package com.swatt.chainNode;

import java.util.logging.Logger;

public class ChainNodeTransaction {
    private static final Logger LOGGER = Logger.getLogger(ChainNodeTransaction.class.getName());

    private String hash;
    private long timestamp;

    private double fee;
    private double feeRate;
    private double amount;

    private String blockHash;

    public ChainNodeTransaction(String hash) {
        this.hash = hash;
    }

    public final String getHash() {
        return hash;
    }

    public final void setHash(String hash) {
        this.hash = hash;
    }

    public final double getFee() {
        return fee;
    }

    public final void setFee(double fee) {
        this.fee = fee;
    }

    public final double getFeeRate() {
        return feeRate;
    }

    public final void setFeeRate(double feeRate) {
        this.feeRate = feeRate;
    }

    public final long getTimestamp() {
        return timestamp;
    }

    public final void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public final String getBlockHash() {
        return blockHash;
    }

    public final void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public final double getAmount() {
        return amount;
    }

    public final void setAmount(double amount) {
        this.amount = amount;
    }
}

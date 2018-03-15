package com.swatt.chainNode;

import java.util.logging.Logger;

public class ChainNodeTransaction {
    private static final Logger LOGGER = Logger.getLogger(ChainNodeTransaction.class.getName());

    private String hash;
    private long timestamp;

    private double fee; // All
    private boolean minted = false; // All
    private double feeRate; // Bitcoin?
    private long size; // All
    private double amount; // ?? Christian had this be lazily synthetic

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

    public final boolean isNewlyMinted() {
        return minted;
    }

    public final void setNewlyMinted(boolean minted) {
        this.minted = minted;
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

    public final long getSize() {
        return size;
    }

    public final void setSize(long size) {
        this.size = size;
    }

    public final double getAmount() {
        return amount;
    }

    public final void setAmount(double amount) {
        this.amount = amount;
    }
}

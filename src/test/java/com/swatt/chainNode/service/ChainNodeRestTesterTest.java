package com.swatt.chainNode.service;

//import org.junit.jupiter.api...BeforeAll;
//import org.junit.jupiter.api.BeforeEach;

import com.swatt.util.general.OperationFailedException;

public class ChainNodeRestTesterTest {
    private String hash;
    private long timestamp;
    private double fee;
    private double feeRate;
    private double amount;
    private String blockHash;

    // @BeforeAll
    static void init() throws Exception {

    }

    // @BeforeEach
    void recreateExchange() throws OperationFailedException {
        // simpleExchange = new SimpleExchange();

        // clientOrderIds.clear();
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public double getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(double feeRate) {
        this.feeRate = feeRate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof ChainNodeRestTesterTest) {
            ChainNodeRestTesterTest other = (ChainNodeRestTesterTest) obj;
            return other.getHash().equals(getHash()) && (other.getTimestamp() == getTimestamp())
                    && (other.getFee() == getFee()) && (other.getFeeRate() == getFeeRate())
                    && (other.getAmount() == getAmount()) && other.getBlockHash().equals(getBlockHash());

        } else {
            return false;
        }
    }
}

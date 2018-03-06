package com.swatt.internal.rest;

public class TransactionMessage {
    public String hash;
    public String symbol;
    public Long quantity;
    public Double price;

    // boolean onChain;
    double transactionAmount;
    double transactionFee;
    Long timestamp;

    public TransactionMessage(String hash, double amount, double fee, Long timestamp) {
        this.hash = hash;
        this.transactionAmount = amount;
        this.transactionFee = fee;
        this.timestamp = timestamp;
    }

    public String getHash() {
        return hash;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public double getTransactionFee() {
        return transactionFee;
    }
}
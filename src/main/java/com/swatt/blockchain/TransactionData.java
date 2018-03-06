package com.swatt.blockchain;

public class TransactionData {
    boolean onChain;
    double transactionAmount;
    double transactionFee;

    public TransactionData(double transactionAmount, double transactionFee) {
        this.transactionAmount = transactionAmount;
        this.transactionFee = transactionFee;
    }

    public boolean isOnChain() {
        return onChain;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public double getTransactionFee() {
        return transactionFee;
    }
}
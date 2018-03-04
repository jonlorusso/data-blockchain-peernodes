package com.swatt.blockchain;

/**
 * Information about a specific Blockchain Transaction
 */
public abstract class BlockchainTransaction {
    String hash;
    String blockHash;
    String[] inputs;
    double[] outputValues;
    Double fee;

    public BlockchainTransaction(String hash, String blockHash, String[] inputs, double[] outputs) {
        this.hash = hash;
        this.blockHash = blockHash;
        this.inputs = inputs;
        this.outputValues = outputs;
    }

    public abstract Double getFee();

    public abstract String getHash();
}
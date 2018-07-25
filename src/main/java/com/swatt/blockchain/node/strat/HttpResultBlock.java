package com.swatt.blockchain.node.strat;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HttpResultBlock {
    private String hash;
    private int size;
    private String version;
    private String merkleRoot;
    private long time;
    private String nonce;
    private String bits;
    private double difficulty;
    private String previousBlockHash;
    private List<String> transactions;

    public String getHash() {
        return hash;
    }

    public int getSize() {
        return size;
    }

    public String getVersion() {
        return version;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public long getTime() {
        return time;
    }

    public String getNonce() {
        return nonce;
    }

    public String getBits() {
        return bits;
    }

    public double getDifficulty() {
        return difficulty;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public List<String> getTransactions() {
        return transactions;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public void setBits(String bits) {
        this.bits = bits;
    }

    public void setDifficulty(double difficulty) {
        this.difficulty = difficulty;
    }

    public void setPreviousBlockHash(String previousBlockHash) {
        this.previousBlockHash = previousBlockHash;
    }

    public void setTransactions(List<String> transactions) {
        this.transactions = transactions;
    }
}

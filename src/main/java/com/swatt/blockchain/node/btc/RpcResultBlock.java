package com.swatt.blockchain.node.btc;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RpcResultBlock {
    private String hash;
    private int size;
    private int height;
    private String versionHex;
    private String merkleroot;
    
    @JsonProperty("tx")
    private List<String> transactions;
    
    private Long time;
    private String nonce;
    private String bits;
    private double difficulty;
    private String previousblockhash;
    private String nextblockhash;

    public int getHeight() {
        return height;
    }

    public String getHash() {
        return hash;
    }

    public int getSize() {
        return size;
    }

    public String getVersionHex() {
        return versionHex;
    }

    public String getMerkleroot() {
        return merkleroot;
    }

    public List<String> getTransactions() {
        return transactions;
    }

    public Long getTime() {
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

    public String getPreviousblockhash() {
        return previousblockhash;
    }

    public String getNextblockhash() {
        return nextblockhash;
    }
}
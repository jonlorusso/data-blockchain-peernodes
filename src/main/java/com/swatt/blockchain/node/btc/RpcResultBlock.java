package com.swatt.blockchain.node.btc;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swatt.util.general.CollectionsUtilities;

import static com.swatt.util.general.CollectionsUtilities.*;
import static java.util.stream.Collectors.toList;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RpcResultBlock {
    private String hash;
    private int size;
    private int height;
    private String versionHex;
    private String merkleroot;
    
    @JsonProperty("tx")
    private List<JsonNode> transactionsInternal;

    private List<String> transactionStrings;
    public List<RpcResultTransaction> transactions;

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

    public List<String> getTransactionStrings() {
        if (!isNullOrEmpty(transactionsInternal)) {
            return transactionsInternal.stream().map(t -> t.asText()).collect(toList());
        }

        return null;
    }

    public List<RpcResultTransaction> getTransactions() {
        if (!isNullOrEmpty(transactionsInternal)) {
            return transactionsInternal.stream().map(t -> t.isObject() ? new RpcResultTransaction(t) : null).collect(toList());
        }

        return null;
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

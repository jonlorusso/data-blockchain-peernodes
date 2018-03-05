package com.swatt.blockchain.btc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RPCBlock {
    public String hash;
    public int confirmations;
    public int strippedsize;
    public int size;
    public int weight;
    public int height;
    public int version;
    public String versionHex;
    public String merkleroot;
    public List<String> tx = null;
    public Long time;
    public int mediantime;
    public Long nonce;
    public String bits;
    public double difficulty;
    public String chainwork;
    public String previousblockhash;
    public String nextblockhash;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}

package com.swatt.chainNode.btc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RPCBlock {
    String hash;
    int confirmations;
    int strippedsize;
    int size;
    int weight;
    int height;
    int version;
    String versionHex;
    String merkleroot;
    List<String> tx = null;
    Long time;
    int mediantime;
    Long nonce;
    String bits;
    double difficulty;
    String chainwork;
    String previousblockhash;
    String nextblockhash;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}

package com.swatt.chainNode.eth;

import java.util.HashMap;
import java.util.Map;

public class Web3jTransaction {

    public String blockHash;
    public String blockNumber;
    public String from;
    public String gas;
    public String gasPrice;
    public String hash;
    public String input;
    public String nonce;
    public String to;
    public String transactionIndex;
    public double value;
    public String v;
    public String r;
    public String s;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
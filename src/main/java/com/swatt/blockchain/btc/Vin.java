package com.swatt.blockchain.btc;

import java.util.HashMap;
import java.util.Map;

public class Vin {

    public String coinbase;
    public int sequence;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
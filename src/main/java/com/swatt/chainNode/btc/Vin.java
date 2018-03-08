package com.swatt.chainNode.btc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vin {

    String txid;
    int vout;
    ScriptSig scriptSig;
    List<String> txinwitness = null;
    String coinbase;
    int sequence;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}

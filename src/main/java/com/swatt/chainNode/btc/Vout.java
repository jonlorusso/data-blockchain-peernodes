package com.swatt.chainNode.btc;

import java.util.HashMap;
import java.util.Map;

class Vout {

    double value;
    int n;
    ScriptPubKey scriptPubKey;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

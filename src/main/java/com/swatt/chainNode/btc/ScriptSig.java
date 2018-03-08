package com.swatt.chainNode.btc;

import java.util.HashMap;
import java.util.Map;

public class ScriptSig {
    String asm;
    String hex;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}

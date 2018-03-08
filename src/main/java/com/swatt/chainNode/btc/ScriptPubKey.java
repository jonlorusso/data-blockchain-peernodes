package com.swatt.chainNode.btc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptPubKey {

    String asm;
    String hex;
    int reqSigs;
    String type;
    List<String> addresses = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

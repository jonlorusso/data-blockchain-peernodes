package com.swatt.chainNode.btc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RpcResultScriptPubKey {
    // Simple container object to receive results of JSONRPC call - public
    // properties poulated using introspection by jsonrpcClient
    // All fields in feed must be defined, even if not needed
    public String asm;
    public String hex;
    public int reqSigs;
    public String type;
    public List<String> addresses = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
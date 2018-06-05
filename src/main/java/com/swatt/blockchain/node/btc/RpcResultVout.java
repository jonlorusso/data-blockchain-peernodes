package com.swatt.blockchain.node.btc;

import java.util.HashMap;
import java.util.Map;


public final class RpcResultVout {
    // Simple container object to receive results of JSONRPC call - public
    // properties poulated using introspection by jsonrpcClient
    // All fields in feed must be defined, even if not needed
    public double value;
    public double valueSat;
    public double valueZat;
    public int n;
    public RpcResultScriptPubKey scriptPubKey;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}

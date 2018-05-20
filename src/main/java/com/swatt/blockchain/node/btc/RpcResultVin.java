package com.swatt.blockchain.node.btc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RpcResultVin {
    // Simple container object to receive results of JSONRPC call - public
    // properties poulated using introspection by jsonrpcClient
    // All fields in feed must be defined, even if not needed
    public String txid;
    public int vout;
    public RpcResultScriptSig scriptSig;
    public List<String> txinwitness = null;
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
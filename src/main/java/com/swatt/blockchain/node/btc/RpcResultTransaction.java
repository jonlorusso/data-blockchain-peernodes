package com.swatt.blockchain.node.btc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class RpcResultTransaction {
    // Simple container object to receive results of JSONRPC call - public
    // properties poulated using introspection by jsonrpcClient
    // All fields in feed must be defined, even if not needed
    public String txid;
    public String hash;
    public Long version;
    public Long height;
    public Long size;
    public Long vsize;
    public Long locktime;
    public List<RpcResultVin> vin = null;
    public List<RpcResultVout> vout = null;
    public String hex;
    public String blockhash;
    public Long confirmations;
    public Long time;
    public Long blocktime;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

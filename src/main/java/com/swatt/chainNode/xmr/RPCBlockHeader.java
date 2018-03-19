package com.swatt.chainNode.xmr;

import java.util.HashMap;
import java.util.Map;

public class RPCBlockHeader {
    // Simple container object to receive results of JSONRPC call - public
    // properties poulated using introspection by jsonrpcClient
    // All fields in feed must be defined, even if not needed

    public int blockSize;
    public int depth;
    public int difficulty;
    public String hash;
    public int height;
    public int majorVersion;
    public int minorVersion;
    public int nonce;
    public int numTxes;
    public boolean orphanStatus;
    public String prevHash;
    public int reward;
    public int timestamp;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}

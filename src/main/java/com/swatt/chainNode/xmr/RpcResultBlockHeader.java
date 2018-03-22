package com.swatt.chainNode.xmr;

import java.util.HashMap;
import java.util.Map;

public class RpcResultBlockHeader {
    // Simple container object to receive results of JSONRPC call - public
    // properties poulated using introspection by jsonrpcClient
    // All fields in feed must be defined, even if not needed

    public int block_size;
    public int depth;
    public int difficulty;
    public String hash;
    public int height;
    public int major_version;
    public int minor_version;
    public int nonce;
    public int num_txes;
    public boolean orphan_status;
    public String prev_hash;
    public int reward;
    public int timestamp;
    private Map<String, Object> additional_properties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additional_properties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additional_properties.put(name, value);
    }
}

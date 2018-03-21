package com.swatt.chainNode.xmr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RPCTx {
    public String as_hex;
    public String as_json;
    public int block_height;
    public boolean in_pool;
    public List<Integer> output_indices = null;
    public String tx_hash;
    private Map<String, Object> additional_properties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additional_properties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additional_properties.put(name, value);
    }
}
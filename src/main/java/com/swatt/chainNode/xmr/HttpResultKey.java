package com.swatt.chainNode.xmr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResultKey {

    public long amount;
    public List<Integer> key_offsets = null;
    public String k_image;
    private Map<String, Object> additional_properties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additional_properties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additional_properties.put(name, value);
    }

}
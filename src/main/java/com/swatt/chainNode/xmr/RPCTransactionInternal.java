package com.swatt.chainNode.xmr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RPCTransactionInternal {

    public int version;
    public int unlock_time;
    public List<RPCVin> vin = null;
    public List<RPCVout> vout = null;
    public List<Integer> extra = null;
    public List<String> signatures = null;
    private Map<String, Object> additional_properties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additional_properties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additional_properties.put(name, value);
    }

}
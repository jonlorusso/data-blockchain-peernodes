package com.swatt.chainNode.xmr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResultTransaction {

    public String status;
    public List<HttpResultTx> txs = null;
    public List<String> txs_as_hex = null;
    public List<String> txs_as_json = null;
    private Map<String, Object> additional_properties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additional_properties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additional_properties.put(name, value);
    }

}
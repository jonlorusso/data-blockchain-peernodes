package com.swatt.chainNode.btc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RPCTransaction {

    String txid;
    String hash;
    Long version;
    Long size;
    Long vsize;
    Long locktime;
    List<Vin> vin = null;
    List<Vout> vout = null;
    String hex;
    String blockhash;
    Long confirmations;
    Long time;
    Long blocktime;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

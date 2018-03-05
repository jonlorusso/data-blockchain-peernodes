package com.swatt.blockchain.btc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RPCTransaction {

    public String txid;
    public String hash;
    public Long version;
    public Long size;
    public Long vsize;
    public Long locktime;
    public List<Vin> vin = null;
    public List<Vout> vout = null;
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
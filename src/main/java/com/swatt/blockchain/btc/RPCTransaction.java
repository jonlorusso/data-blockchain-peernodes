package com.swatt.blockchain.btc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RPCTransaction {

    public String txid;
    public String hash;
    public int version;
    public int size;
    public int vsize;
    public int locktime;
    public List<Vin> vin = null;
    public List<Vout> vout = null;
    public String hex;
    public String blockhash;
    public int confirmations;
    public int time;
    public int blocktime;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
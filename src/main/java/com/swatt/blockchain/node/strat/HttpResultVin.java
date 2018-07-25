package com.swatt.blockchain.node.strat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HttpResultVin {
    private String txid;
    private int vout;

    public String getTxid() {
        return txid;
    }

    public int getVout() {
        return vout;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public void setVout(int vout) {
        this.vout = vout;
    }

    @Override
    public String toString() {
        return "HttpResultVin [txid=" + txid + ", vout=" + vout + "]";
    }
}

package com.swatt.blockchain.node.strat;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HttpResultTransaction {
    private int size;
    private List<HttpResultVout> vout;
    private List<HttpResultVin> vin;

    public int getSize() {
        return size;
    }

    public List<HttpResultVout> getVout() {
        return vout;
    }

    public List<HttpResultVin> getVin() {
        return vin;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setVout(List<HttpResultVout> vout) {
        this.vout = vout;
    }

    public void setVin(List<HttpResultVin> vin) {
        this.vin = vin;
    }

    @Override
    public String toString() {
        return "HttpResultTransaction [size=" + size + ", vout=" + vout + ", vin=" + vin + "]";
    }
}

package com.swatt.blockchain.node.zec;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RpcResultTransaction extends com.swatt.blockchain.node.btc.RpcResultTransaction {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VJoinSplit {
        @JsonProperty("vpub_old")
        public double vPubOld; // public input

        @JsonProperty("vpub_new")
        public double vPubNew; // public output
    }

    public String txid;

    @JsonProperty("vjoinsplit")
    public List<VJoinSplit> vJoinSplits;
}

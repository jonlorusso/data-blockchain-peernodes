package com.swatt.blockchain.node.btc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class RpcResultVin {
    public String txid;
    public int vout;
}

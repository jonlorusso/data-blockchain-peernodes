package com.swatt.blockchain.node.neo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swatt.blockchain.node.btc.RpcResultTransaction;

public class RpcResultBlock extends com.swatt.blockchain.node.btc.RpcResultBlock {
    
    @JsonProperty("index")
    private int height;
    
    @JsonProperty("tx")
    private List<RpcResultTransaction> transactions;
    
    public int getHeight() {
        return this.height;
    }
}

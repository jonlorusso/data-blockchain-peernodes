package com.swatt.blockchain.node.steem;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RpcResultDynamicGlobalProperties {

    @JsonProperty("head_block_number")
    public long blockCount;
}

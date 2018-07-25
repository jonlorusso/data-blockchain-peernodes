package com.swatt.blockchain.node.lsk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HttpResultNodeStatus {
    public long height;
}

package com.swatt.blockchain.node.lsk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HttpResultBlock {
    
    public String id;
    public int version;
    public long height;
    public long timestamp;
    public String previousBlockId;
    
    public int numberOfTransactions;
    public long totalAmount;
    public long totalFee;
    public long reward;
}

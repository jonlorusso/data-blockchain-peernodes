package com.swatt.blockchain.node.lsk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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

/**

Example Value
Model
{
  "data": [
    {
      "id": "6258354802676165798",
      "version": 0,
      "height": 123,
      "timestamp": 28227090,
     
      "generatorAddress": "12668885769632475474L",
      "generatorPublicKey": "968ba2fa993ea9dc27ed740da0daf49eddd740dbd7cb1cb4fc5db3a20baf341b",
      "payloadLength": 117,
      "payloadHash": "4e4d91be041e09a2e54bb7dd38f1f2a02ee7432ec9f169ba63cd1f193a733dd2",
      "blockSignature": "a3733254aad600fa787d6223002278c3400be5e8ed4763ae27f9a15b80e20c22ac9259dc926f4f4cabdf0e4f8cec49308fa8296d71c288f56b9d1e11dfe81e07",
      "confirmations": 200,
      "previousBlockId": "15918760246746894806",
      "numberOfTransactions": 15,
      "totalAmount": "150000000",
      "totalFee": "15000000",
      "reward": "50000000",
      "totalForged": "65000000"
    }
  ],
**/
package com.swatt.blockchain.node.lsk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HttpResultTransaction {
    public long amount;
    public long fee;
    
    @JsonProperty("id")
    public String hash;
}
/**
{
    "data": [
      {
        "id": "222675625422353767",
        "amount": "150000000",
        "fee": "1000000",
        "type": 0,
        "height": 0,
        "blockId": "6258354802676165798",
        "timestamp": 28227090,
        "senderId": "12668885769632475474L",
        "senderPublicKey": "2ca9a7143fc721fdc540fef893b27e8d648d2288efa61e56264edf01a2c23079",
        "senderSecondPublicKey": "2ca9a7143fc721fdc540fef893b27e8d648d2288efa61e56264edf01a2c23079",
        "recipientId": "12668885769632475474L",
        "recipientPublicKey": "2ca9a7143fc721fdc540fef893b27e8d648d2288efa61e56264edf01a2c23079",
        "signature": "2821d93a742c4edf5fd960efad41a4def7bf0fd0f7c09869aed524f6f52bf9c97a617095e2c712bd28b4279078a29509b339ac55187854006591aa759784c205",
        "signSignature": "2821d93a742c4edf5fd960efad41a4def7bf0fd0f7c09869aed524f6f52bf9c97a617095e2c712bd28b4279078a29509b339ac55187854006591aa759784c205",
        "signatures": [
          "72c9b2aa734ec1b97549718ddf0d4737fd38a7f0fd105ea28486f2d989e9b3e399238d81a93aa45c27309d91ce604a5db9d25c9c90a138821f2011bc6636c60a"
        ],
        "confirmations": 0,
        "asset": {},
        "receivedAt": "2018-07-21T01:32:07.784Z",
        "relays": 0,
        "ready": false
      }
    ],
    "meta": {
      "offset": 0,
      "limit": 0,
      "count": 100
    },
    "links": {}
  **/
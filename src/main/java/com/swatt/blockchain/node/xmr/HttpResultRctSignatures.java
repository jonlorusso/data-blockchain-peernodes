package com.swatt.blockchain.node.xmr;

import java.util.List;

public class HttpResultRctSignatures {
    public long type;
    public long txnFee;
    public List<HttpResultEcdhInfo> ecdhInfo = null;
    public List<String> outPk = null;
    public List<String> pseudoOuts = null;
}

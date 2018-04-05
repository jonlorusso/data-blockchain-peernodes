package com.swatt.chainNode.xmr;

import java.util.List;

public class HttpResultTransactionInternal {

    public int version;
    public long unlock_time;

    public List<HttpResultVin> vin = null;
    public List<HttpResultVout> vout = null;
    public List<Integer> extra = null;
    public List<String> signatures = null;

    public HttpResultRctSignatures rct_signatures;
    public HttpResultRctsigPrunable rctsig_prunable;
}
package com.swatt.chainNode.xmr;

import java.util.List;

public class HttpResultTransactionInternal {

    public int version;
    public int unlock_time;
    public List<HttpResultVin> vin = null;
    public List<HttpResultVout> vout = null;
    public List<Integer> extra = null;
    public List<String> signatures = null;
}
package com.swatt.blockchain.node.xmr;

import java.util.List;

public class HttpResultTransaction {

    public String status;
    public List<HttpResultTx> txs = null;
    public List<String> txs_as_hex = null;
    public List<String> txs_as_json = null;
    public boolean untrusted;
}
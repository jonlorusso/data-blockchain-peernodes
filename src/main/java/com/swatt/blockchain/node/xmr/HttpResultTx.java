package com.swatt.blockchain.node.xmr;

import java.util.List;

public class HttpResultTx {
    public String as_hex;
    public String as_json;
    public int block_height;
    public boolean in_pool;
    public List<Integer> output_indices = null;
    public String tx_hash;
    public long block_timestamp;
    public boolean double_spend_seen;
}
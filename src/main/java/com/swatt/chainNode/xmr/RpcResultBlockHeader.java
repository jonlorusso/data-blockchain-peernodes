package com.swatt.chainNode.xmr;

public class RpcResultBlockHeader {
    // Simple container object to receive results of JSONRPC call - public
    // properties poulated using introspection by jsonrpcClient
    // All fields in feed must be defined, even if not needed

    public int block_size;
    public int depth;
    public int difficulty;
    public String hash;
    public int height;
    public int major_version;
    public int minor_version;
    public int nonce;
    public int num_txes;
    public boolean orphan_status;
    public String prev_hash;
    public int reward;
    public int timestamp;
}

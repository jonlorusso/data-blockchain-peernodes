package com.swatt.chainNode.xmr;

import java.util.List;

public class RpcResultBlock {
    // Simple container object to receive results of JSONRPC call - public
    // properties poulated using introspection by jsonrpcClient
    // All fields in feed must be defined, even if not needed
    public String blob;
    public RpcResultBlockHeader block_header;
    public String json;
    public String status;
    public List<String> tx_hashes = null;
}
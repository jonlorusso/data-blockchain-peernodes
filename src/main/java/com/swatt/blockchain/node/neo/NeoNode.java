package com.swatt.blockchain.node.neo;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.node.btc.BitcoinNode;
import com.swatt.util.general.OperationFailedException;

public class NeoNode extends BitcoinNode {

    @Override
    public long fetchBlockCount() throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            return jsonRpcHttpClient.invoke("getblockcount", new Object[] { null }, Long.class);
        } catch (Throwable t) {
            throw new OperationFailedException("Error fetching latest Block: ", t);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }
    
    @Override
    protected RpcResultBlock getBlock(String hash) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            return jsonRpcHttpClient.invoke("getblock", new Object[] { hash, true }, RpcResultBlock.class);
        } catch (Throwable t) {
            throw new OperationFailedException(t);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }
}
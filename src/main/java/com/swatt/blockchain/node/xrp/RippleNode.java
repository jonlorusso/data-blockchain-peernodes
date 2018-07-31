package com.swatt.blockchain.node.xrp;

import java.lang.reflect.Type;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.node.JsonRpcHttpClientNode;
import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.util.general.OperationFailedException;

public class RippleNode extends JsonRpcHttpClientNode<RpcResultLedger, RpcResultTransaction> {

    @Override
    protected String getBlockByHashRpcMethodName() throws OperationFailedException {
        return "ledger";
        // ledger hash
    }

    @Override
    protected String getRpcTransactionMethodName() throws OperationFailedException {
        return "tx";
    }

    @Override
    protected NodeTransaction toNodeTransaction(RpcResultTransaction rpcResultTransaction) throws OperationFailedException {
        NodeTransaction nodeTransaction = new NodeTransaction("FIXME");
        return nodeTransaction;
    }

    @Override
    protected String getBlockByHeightRpcMethodName() throws OperationFailedException {
        return "ledger";
        // ledger index
    }

    @Override
    protected BlockData toBlockData(RpcResultLedger rpcResultLedger) throws OperationFailedException {
        BlockData blockData = new BlockData();
        return blockData;
    }

    @Override
    public long fetchBlockCount() throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();
        
        Type returnType = new TypeReference<Map<String, Long>>() {}.getType();
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Long> ledgerCurrent = (Map<String, Long>)jsonRpcHttpClient.invoke("ledger_current", new Object[] {}, returnType);
            return ledgerCurrent.get("ledger_current_index");
        } catch (Throwable e) {
            throw new OperationFailedException("Error fetching block", e);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }
}

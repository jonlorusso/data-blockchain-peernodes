package com.swatt.blockchain.node.dash;

import com.swatt.blockchain.node.btc.BitcoinNode;
import com.swatt.blockchain.node.btc.RpcResultBlock;
import com.swatt.blockchain.node.btc.RpcResultTransaction;
import com.swatt.util.general.OperationFailedException;

public class DashNode extends BitcoinNode<RpcResultBlock, RpcResultTransaction> {

    @Override
    protected Object[] getBlockByHashRpcMethodParameters(String hash) throws OperationFailedException {
        return new Object[] { hash, true };
    }

    @Override
    protected Object[] getTransactionRpcMethodParameters(String hash) throws OperationFailedException {
        return new Object[] { hash, 1 };
    }
}

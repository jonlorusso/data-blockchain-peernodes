package com.swatt.blockchain.node.btc;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.util.general.OperationFailedException;

public class BitcoinTransaction extends NodeTransaction {

    private boolean coinbase = false;

    public BitcoinTransaction(RpcResultTransaction rpcResultTransaction) {
        super(rpcResultTransaction.hash);
    }

    public final boolean getCoinbase() {
        return this.coinbase;
    }

    public final void setCoinbase(boolean coinbase) {
        this.coinbase = coinbase;
    }
}

package com.swatt.chainNode.xmr;

import java.util.logging.Logger;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.btc.BitcoinTransaction;
import com.swatt.util.OperationFailedException;

public class MoneroTransaction extends ChainNodeTransaction {
    private static final Logger LOGGER = Logger.getLogger(BitcoinTransaction.class.getName());

    public MoneroTransaction(String hash) {
        super(hash);
    }

    MoneroTransaction(JsonRpcHttpClient jsonrpcClient, String hash, boolean calculateFee)
            throws OperationFailedException {
        super(hash);

    }
}

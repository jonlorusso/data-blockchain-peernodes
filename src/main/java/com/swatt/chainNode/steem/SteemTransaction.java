package com.swatt.chainNode.steem;

import java.util.logging.Logger;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.util.general.OperationFailedException;

public class SteemTransaction extends ChainNodeTransaction {
    private static final Logger LOGGER = Logger.getLogger(SteemTransaction.class.getName());
    private double inValue;
    private boolean minted = false;
    private long size;

    SteemTransaction(JsonRpcHttpClient jsonrpcClient, String hash, boolean calculateFee)
            throws OperationFailedException {
        super(hash);

        /*
         * RPCTransaction rpcTransaction = fetchFromBlockchain(jsonrpcClient, hash);
         * 
         * this.vout = rpcTransaction.vout;
         * 
         * // Compute amount double amount = 0.0;
         * 
         * // TODO clean up vout array once values read, no further need for (int i = 0;
         * i < rpcTransaction.vout.size(); i++) { RPCVout outTransaction =
         * rpcTransaction.vout.get(i); amount += outTransaction.value; }
         * 
         * setAmount(amount); setTimestamp(rpcTransaction.time);
         * 
         * // Compute Size
         * 
         * if (rpcTransaction.vsize != null) { setSize(rpcTransaction.vsize); } else {
         * setSize(rpcTransaction.size); }
         * 
         * super.setBlockHash(rpcTransaction.blockhash);
         * 
         * if (calculateFee) calculateFee(jsonrpcClient, rpcTransaction);
         */
    }

}

package com.swatt.blockchain.node.btc;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.util.general.OperationFailedException;

public class BitcoinTransaction extends NodeTransaction {
    private static final Logger LOGGER = Logger.getLogger(BitcoinTransaction.class.getName());
    private double inValue;
    private boolean coinbase = false;
    private long size;

    private List<RpcResultVout> vout;

    public BitcoinTransaction(JsonRpcHttpClient jsonrpcClient, Object transaction, boolean calculateFee) throws OperationFailedException {
        super(transaction instanceof RpcResultTransaction ? ((RpcResultTransaction)transaction).hash : (String)transaction);
        
        RpcResultTransaction rpcResultTransaction;
        String transactionHash;
        
        if (transaction instanceof RpcResultTransaction) {
            rpcResultTransaction = (RpcResultTransaction)transaction;
            transactionHash = rpcResultTransaction.hash;
        } else {
            transactionHash = (String)transaction;
            rpcResultTransaction = fetchFromBlockchain(jsonrpcClient, transactionHash);
        }

        this.vout = rpcResultTransaction.vout;

        double amount = vout.stream().mapToDouble(v -> v.value).sum();
        setAmount(amount);

        if (rpcResultTransaction.vsize != null)
            setSize(rpcResultTransaction.vsize);
        if (rpcResultTransaction.size != null)
            setSize(rpcResultTransaction.size);

        super.setBlockHash(rpcResultTransaction.blockhash);

        if (calculateFee)
            calculateFee(jsonrpcClient, rpcResultTransaction);
    }

    public static RpcResultTransaction fetchFromBlockchain(JsonRpcHttpClient jsonrpcClient, String transactionHash)throws OperationFailedException {
        try {
            return jsonrpcClient.invoke(RpcMethodsBitcoin.GET_RAW_TRANSACTION, new Object[] { transactionHash, 1 }, RpcResultTransaction.class);
        } catch (Throwable t) {
            OperationFailedException e = new OperationFailedException("Error fetching transaction from Blockchain: " + transactionHash, t);
            LOGGER.log(Level.SEVERE, e.toString(), e);
            throw e;
        }
    }

    public final double getInValue() {
        return inValue;
    }

    public final void setInValue(double inValue) {
        this.inValue = inValue;
    }

    public final long getSize() {
        return size;
    }

    public final void setSize(long size) {
        this.size = size;
    }

    public final boolean getCoinbase() {
        return this.coinbase;
    }

    public final void setCoinbase(boolean coninbase) {
//        this.coinbase = coinbase;
    }

    private void calculateFee(JsonRpcHttpClient jsonrpcClient, RpcResultTransaction rpcTransaction) throws OperationFailedException {

        // Perform Calculations

        double amount = getAmount();
        double inValue = 0.0;
        double fee = 0.0;

        RpcResultVin inTransaction = null;

        for (int i = 0; i < rpcTransaction.vin.size(); i++) {
            inTransaction = rpcTransaction.vin.get(i);

            if (inTransaction.txid != null) {
                RpcResultTransaction rpcResultTransaction = fetchFromBlockchain(jsonrpcClient, inTransaction.txid);
                inValue += rpcResultTransaction.vout.get(inTransaction.vout).value;
            }
        }

        setInValue(inValue);

        if (inValue > 0) {
            fee = inValue - amount;
            double feeRate = (1000.0 * fee) / getSize();

            setFee(fee);
            setFeeRate(feeRate);
            setCoinbase(false);
        } else {
            setCoinbase(true);
        }
    }
}

package com.swatt.chainNode.btc;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.util.general.OperationFailedException;

public class BitcoinTransaction extends ChainNodeTransaction {
    private static final Logger LOGGER = Logger.getLogger(BitcoinTransaction.class.getName());
    private double inValue;
    private boolean coinbase = false;
    private long size;

    private List<RpcResultVout> vout;

    BitcoinTransaction(JsonRpcHttpClient jsonrpcClient, RpcResultTransaction rpcTransaction, boolean calculateFee)
            throws OperationFailedException {
        super(rpcTransaction.txid);

        this.vout = rpcTransaction.vout;

        // Compute amount
        double amount = vout.stream().mapToDouble(v -> v.value).sum();

        setAmount(amount);
        setTimestamp(rpcTransaction.time);

        // Compute Size

        if (rpcTransaction.vsize != null) {
            setSize(rpcTransaction.vsize);
        } else {
            setSize(rpcTransaction.size);
        }

        super.setBlockHash(rpcTransaction.blockhash);

        if (calculateFee)
            calculateFee(jsonrpcClient, rpcTransaction);
    }

    // FIXME pull this out into its own file
    @SuppressWarnings("serial")
    public static class Cache extends LinkedHashMap<String, RpcResultTransaction> {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, RpcResultTransaction> eldest) {
            return size() > 10000;
        }
    }

    public static Cache transactions = new Cache();

    public static RpcResultTransaction fetchFromBlockchain(JsonRpcHttpClient jsonrpcClient, String transactionHash)
            throws OperationFailedException {
        if (transactions.get(transactionHash) != null)
            return transactions.get(transactionHash);

        try {

            Object parameters[] = new Object[] { transactionHash, true };

            RpcResultTransaction rtn;
            rtn = jsonrpcClient.invoke(RpcMethodsBitcoin.GET_RAW_TRANSACTION, parameters, RpcResultTransaction.class);
            transactions.put(transactionHash, rtn);

            return rtn;
        } catch (Throwable t) {
            OperationFailedException e = new OperationFailedException(
                    "Error fetching transaction from Blockchain: " + transactionHash, t);
            LOGGER.log(Level.SEVERE, e.toString(), e);
            throw e;
        }
    }

    // private double outputValue(int i) {
    // return vout.get(i).value;
    // }

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
        this.coinbase = coinbase;
    }

    private void calculateFee(JsonRpcHttpClient jsonrpcClient, RpcResultTransaction rpcTransaction)
            throws OperationFailedException {

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

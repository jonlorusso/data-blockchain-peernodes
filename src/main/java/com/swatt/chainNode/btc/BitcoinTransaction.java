package com.swatt.chainNode.btc;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.util.OperationFailedException;

public class BitcoinTransaction extends ChainNodeTransaction {
    private static final Logger LOGGER = Logger.getLogger(BitcoinTransaction.class.getName());
    private double inValue;

    private List<RPCVout> vout;

    BitcoinTransaction(JsonRpcHttpClient jsonrpcClient, String hash, boolean calculateFee)
            throws OperationFailedException {
        super(hash);

        RPCTransaction rpcTransaction = fetchFromBlockchain(jsonrpcClient, hash);

        this.vout = rpcTransaction.vout;

        // Compute amount
        double amount = 0.0;

        // TODO clean up vout array once values read, no further need
        for (int i = 0; i < rpcTransaction.vout.size(); i++) {
            RPCVout outTransaction = rpcTransaction.vout.get(i);
            amount += outTransaction.value;
        }

        setAmount(amount);
        setTimestamp(rpcTransaction.time);

        // Compute Size

        if (rpcTransaction.vsize != null) {
            setSize(rpcTransaction.vsize);
        } else {
            setSize(rpcTransaction.size);
        }

        if (calculateFee)
            calculateFee(jsonrpcClient, rpcTransaction);
    }

    private RPCTransaction fetchFromBlockchain(JsonRpcHttpClient jsonrpcClient, String transactionHash)
            throws OperationFailedException {
        try {

            Object parameters[] = new Object[] { transactionHash, true };

            RPCTransaction rtn;
            rtn = jsonrpcClient.invoke(BTCMethods.GET_RAW_TRANSACTION, parameters, RPCTransaction.class);

            return rtn;
        } catch (Throwable t) {
            OperationFailedException e = new OperationFailedException(
                    "Error fetching transaction from Blockchain: " + transactionHash, t);
            LOGGER.log(Level.SEVERE, e.toString(), e);
            throw e;
        }
    }

    private double outputValue(int i) {
        return vout.get(i).value;
    }

    public final double getInValue() {
        return inValue;
    }

    public final void setInValue(double inValue) {
        this.inValue = inValue;
    }

    private void calculateFee(JsonRpcHttpClient jsonrpcClient, RPCTransaction rpcTransaction)
            throws OperationFailedException {

        // Perform Calculations

        double amount = getAmount();
        double inValue = 0.0;
        double fee = 0.0;

        RPCVin inTransaction = null;

        for (int i = 0; i < rpcTransaction.vin.size(); i++) {
            inTransaction = rpcTransaction.vin.get(i);

            if (inTransaction.txid != null) {
                BitcoinTransaction transaction = new BitcoinTransaction(jsonrpcClient, inTransaction.txid, false);
                inValue += transaction.outputValue(inTransaction.vout);
            }
        }

        setInValue(inValue);

        if (inValue > 0) {
            fee = inValue - amount;
            double feeRate = (1000.0 * fee) / getSize();

            setFee(fee);
            setFeeRate(feeRate);
            setNewlyMinted(false);
        } else {
            setNewlyMinted(true);
        }
    }

}

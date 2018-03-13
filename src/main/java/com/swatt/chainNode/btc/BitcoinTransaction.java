package com.swatt.chainNode.btc;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.chainNode.Transaction;
import com.swatt.util.OperationFailedException;

public class BitcoinTransaction extends Transaction {
    private static final Logger LOGGER = Logger.getLogger(BitcoinTransaction.class.getName());

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

            System.out.println(jsonrpcClient.getServiceUrl());

            RPCTransaction rtn;
            rtn = jsonrpcClient.invoke(BTCMethods.GET_RAW_TRANSACTION, parameters, RPCTransaction.class);

            ObjectMapper mapper = new ObjectMapper();
            String json = null;
            try {
                json = mapper.writeValueAsString(rtn);
            } catch (JsonProcessingException e1) {
                e1.printStackTrace();
            }

            System.out.println(json);

            System.out.println(jsonrpcClient.getServiceUrl());

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
            setMinted(false);
        } else {
            setMinted(true);
        }
    }

}

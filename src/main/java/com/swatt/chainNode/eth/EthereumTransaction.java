package com.swatt.chainNode.eth;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;

import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.btc.RPCTransaction;
import com.swatt.util.OperationFailedException;

public class EthereumTransaction extends ChainNodeTransaction {
    private static final Logger LOGGER = Logger.getLogger(EthereumTransaction.class.getName());

    EthereumTransaction(Web3j web3j, String transactionHash, boolean calculateFee) {
        super(transactionHash);

        try {
            Web3jTransaction transaction = fetchFromBlockchain(web3j, transactionHash);
        } catch (OperationFailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private Web3jTransaction fetchFromBlockchain(Web3j web3j, String transactionHash) throws OperationFailedException {
        try {

            EthTransaction ethTransaction = web3j.ethGetTransactionByHash(transactionHash).send();
            Transaction transaction = ethTransaction.getTransaction().get();

            /*
             * { hash: "0x953c1aff91f44a3489e3c562ba09d05d38c4c4e002f39709239f0d735f41a417",
             * fee: 0, inValue: 0, feeRate: 0, timestamp: 0, size: 0, amount: 0,
             * outputValue: 0, newlyMinted: false }
             */

            // System.out.println("Math.pow(2, 8) : " + result);
            BigInteger valueWei = transaction.getValue();

            super.setAmount(valueWei.doubleValue() * Math.pow(10, (-1 * EthereumChainNode.POWX_ETHER_WEI)));

            return null;
        } catch (Throwable t) {
            OperationFailedException e = new OperationFailedException(
                    "Error fetching transaction from Blockchain: " + transactionHash, t);
            LOGGER.log(Level.SEVERE, e.toString(), e);
            throw e;
        }
    }

    private void calculateFee(RPCTransaction rpcTransaction) throws OperationFailedException {

        // Perform Calculations
    }

}

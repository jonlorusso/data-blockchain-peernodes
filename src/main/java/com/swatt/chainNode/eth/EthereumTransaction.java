package com.swatt.chainNode.eth;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.util.general.OperationFailedException;

public class EthereumTransaction extends ChainNodeTransaction {
    private static final Logger LOGGER = Logger.getLogger(EthereumTransaction.class.getName());

    EthereumTransaction(Web3j web3j, String transactionHash) {
        super(transactionHash);

        try {
            fetchFromBlockchain(web3j, transactionHash);
        } catch (OperationFailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void fetchFromBlockchain(Web3j web3j, String transactionHash) throws OperationFailedException {
        try {
            EthTransaction ethTransaction = web3j.ethGetTransactionByHash(transactionHash).send();
            EthGetTransactionReceipt ethTransactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();
            Transaction transaction = ethTransaction.getTransaction().get();
            TransactionReceipt transactionReceipt = ethTransactionReceipt.getTransactionReceipt().get();

            EthBlock ethBlock = web3j.ethGetBlockByHash(transaction.getBlockHash(), false).send();
            Block block = ethBlock.getBlock();

            super.setBlockHash(transaction.getBlockHash());

            super.setTimestamp(block.getTimestamp().longValue());

            super.setFee(transactionReceipt.getGasUsed().doubleValue());

            BigInteger priceWei = transaction.getGasPrice();
            double priceEther = priceWei.doubleValue() * Math.pow(10, (-1 * EthereumChainNode.POWX_ETHER_WEI));
            double feeRate = priceEther * transactionReceipt.getGasUsed().doubleValue();
            super.setFeeRate(feeRate);

            BigInteger valueWei = transaction.getValue();
            double valueEther = valueWei.doubleValue() * Math.pow(10, (-1 * EthereumChainNode.POWX_ETHER_WEI));
            super.setAmount(valueEther);
        } catch (Throwable t) {
            OperationFailedException e = new OperationFailedException(
                    "Error fetching transaction from Blockchain: " + transactionHash, t);
            LOGGER.log(Level.SEVERE, e.toString(), e);
            throw e;
        }
    }
}

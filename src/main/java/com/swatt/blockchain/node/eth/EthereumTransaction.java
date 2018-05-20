package com.swatt.blockchain.node.eth;

import java.io.IOException;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

import com.swatt.blockchain.NodeTransaction;

import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

public class EthereumTransaction extends NodeTransaction {
    private static final Logger LOGGER = Logger.getLogger(EthereumTransaction.class.getName());
    
    protected EthereumTransaction(Web3j web3j, String transactionHash) {
        super(transactionHash);

        try {
            EthTransaction ethTransaction = web3j.ethGetTransactionByHash(transactionHash).send();
            EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();
            EthBlock ethBlock = web3j.ethGetBlockByHash(ethTransaction.getTransaction().get().getBlockHash(), false).send();
            
            setTransactionAndReceipt(ethTransaction.getTransaction().get(), ethGetTransactionReceipt, ethBlock);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }
    
    public EthereumTransaction(Web3j web3j, Transaction transaction) {
        super(transaction.getHash());
        
        try {
            EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(transaction.getHash()).send();
            EthBlock ethBlock = web3j.ethGetBlockByHash(transaction.getBlockHash(), false).send();
            
            setTransactionAndReceipt(transaction, ethGetTransactionReceipt, ethBlock);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }
    
    private void setTransactionAndReceipt(Transaction transaction, EthGetTransactionReceipt ethGetTransactionReceipt, EthBlock ethBlock) {
        TransactionReceipt transactionReceipt = ethGetTransactionReceipt.getTransactionReceipt().get();
        Block block = ethBlock.getBlock();

        super.setBlockHash(transaction.getBlockHash());
        super.setTimestamp(block.getTimestamp().longValue());
        super.setFee(transactionReceipt.getGasUsed().doubleValue());

        BigInteger priceWei = transaction.getGasPrice();
        double priceEther = priceWei.doubleValue() * Math.pow(10, (-1 * EthereumNode.POWX_ETHER_WEI));
        double feeRate = priceEther * transactionReceipt.getGasUsed().doubleValue();
        super.setFeeRate(feeRate);

        BigInteger valueWei = transaction.getValue();
        double valueEther = valueWei.doubleValue() * Math.pow(10, (-1 * EthereumNode.POWX_ETHER_WEI));
        super.setAmount(valueEther);
    }
}

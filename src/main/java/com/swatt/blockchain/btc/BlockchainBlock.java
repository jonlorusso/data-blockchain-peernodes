package com.swatt.blockchain.btc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Properties;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

public class BlockchainBlock {
    JsonRpcHttpClient jsonrpcClient = null;
    RPCBlock block;

    public BlockchainBlock(String blockHash) {
        if (jsonrpcClient == null) {
            initJSONRPC();
        }

        findBlockByHash(blockHash);
    }

    public BlockchainBlock(Long blockId) {
        if (jsonrpcClient == null) {
            initJSONRPC();
        }

        findBlockById(blockId);
    }

    public BlockchainBlock() {
        if (jsonrpcClient == null) {
            initJSONRPC();
        }

        findLatestBlock();
    }

    private void findBlockByHash(String blockHash) {
        try {
            block = jsonrpcClient.invoke(BTCMethods.GET_BLOCK, new Object[] { blockHash }, RPCBlock.class);
            System.out.println(block.tx);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void findBlockById(Long blockId) {
        String blockHash;

        try {
            blockHash = jsonrpcClient.invoke(BTCMethods.GET_BLOCK_HASH, new Object[] { blockId }, String.class);

            // RPCTransaction tx = jsonrpcClient.invoke(BTCMethods.GET_RAW_TRANSACTION, new
            // Object[] {"this tx hash"}, RPCTransaction.class);
            findBlockByHash(blockHash);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void findLatestBlock() {
        Long blockCount;

        try {
            blockCount = jsonrpcClient.invoke(BTCMethods.GET_BLOCK_COUNT, new Object[] {}, Long.class);
            findBlockById(blockCount);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void initJSONRPC() {
        Properties prop = new Properties();
        InputStream input;
        URL uri;

        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            uri = new URL(prop.getProperty("url"));

            final String rpcuser = prop.getProperty("rpcuser");
            final String rpcpassword = prop.getProperty("rpcpassword");

            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(rpcuser, rpcpassword.toCharArray());
                }
            });

            jsonrpcClient = new JsonRpcHttpClient(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTransactionHashes() {
        return block.tx.get(1);
    }

    public void processTransactions() {
        for (int i = 0; i < block.tx.size(); i++) {
            processTransaction(block.tx.get(i));
        }
    }

    public void processTransaction(String transactionHash) {

    }
}
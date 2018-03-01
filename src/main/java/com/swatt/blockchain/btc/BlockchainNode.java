package com.swatt.blockchain.btc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.BlockchainNodeData;
import com.swatt.blockchain.BlockchainNodeInfo;
import com.swatt.blockchain.BlockchainTransaction;

public class BlockchainNode extends com.swatt.blockchain.BlockchainNode {
    private URL uri;

    public BlockchainNode() {
        try {
            Properties prop = new Properties();
            InputStream input = null;

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
        } catch (FileNotFoundException e) {
            // TODO error to developer if no props file
            e.printStackTrace();
        } catch (IOException e) {
            // TODO error to developer if props file doesn't load
            e.printStackTrace();
        }
    }

    @Override
    public BlockchainNodeInfo getInfo() {
        String ticker = "BTC";
        String name = "Bitcoin";
        String description = "Full blockchain node for Bitcoin";
        BlockchainNodeInfo info = new BlockchainNodeInfo(ticker, name, description);

        return info;
    }

    @Override
    public BlockchainTransaction findTransactionByHash(String hash) {
        JSONObject blockchainTransaction;
        String blockHash = null;
        String[] inputs = null;
        String[] outputs = null;
        double[] outputValues = null;

        try {
            String s = fetchT();
            // blockchainTransaction = makeRequest(BTCMethods.GET_RAW_TRANSACTION, hash);

            /*
             * JSONArray vin = (JSONArray) blockchainTransaction.get("vin"); JSONArray vout
             * = (JSONArray) blockchainTransaction.get("vout");
             * 
             * inputs = new String[vin.size()]; outputs = new String[vout.size()];
             * outputValues = new double[2];
             * 
             * @SuppressWarnings("unchecked") Iterator<String> it = vin.iterator(); while
             * (it.hasNext()) { System.out.println("leg = " + it.next()); }
             */
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        BlockchainTransaction rtn = new BlockchainTransaction(hash, blockHash, inputs, outputValues);
        return rtn;
    }

    private JSONObject makeRequest(String method, String input) throws IOException {
        JsonRpcHttpClient client = new JsonRpcHttpClient(uri);
        Long res = null;

        try {
            res = client.invoke(BTCMethods.GET_BLOCK_COUNT, new Object[] {}, Long.class);
        } catch (Throwable e) {
            // TODO error to developer if props file doesn't load
            e.printStackTrace();
        }

        System.out.println(res);

        JSONObject json = null;
        try {
            JSONParser parser = new JSONParser();
            json = (JSONObject) parser.parse("contents");
        } catch (Exception e) {
            //
        }

        return json;
    }

    private String fetchT() throws IOException {
        JsonRpcHttpClient client = new JsonRpcHttpClient(uri);
        Long latestBlockCount = null;
        String latestBlockHash = null;
        RPCBlock latestBlock = null;

        try {
            latestBlockCount = client.invoke(BTCMethods.GET_BLOCK_COUNT, new Object[] {}, Long.class);
            latestBlockHash = client.invoke(BTCMethods.GET_BLOCK_HASH, new Object[] { latestBlockCount }, String.class);
            latestBlock = client.invoke(BTCMethods.GET_BLOCK, new Object[] { latestBlockHash }, RPCBlock.class);
        } catch (Throwable e) {
            // TODO error to developer if props file doesn't load
            e.printStackTrace();
        }

        System.out.println(latestBlockHash);

        return "json";
    }

    @Override
    public BlockchainTransaction findTransactionByAddress(String address) {
        return null;
    }

    @Override
    public BlockchainNodeData getDataForInterval(long fromTime, long toTime) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getLatestBlockTransactions() {
        BlockchainBlock block = new BlockchainBlock();
        String transactions = block.getTransactionHashes();

        return transactions;
    }

    @Override
    public String getBlockTransactionsById(Long blockId) {
        BlockchainBlock block = new BlockchainBlock(blockId);
        String transactions = block.getTransactionHashes();

        return transactions;
    }

    @Override
    public String getBlockTransactionsByHash(String blockHash) {
        BlockchainBlock block = new BlockchainBlock(blockHash);
        String transactions = block.getTransactionHashes();

        return transactions;
    }
}
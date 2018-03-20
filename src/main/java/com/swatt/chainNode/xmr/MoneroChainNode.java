package com.swatt.chainNode.xmr;

import java.math.BigDecimal;
//For creating URLs
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.util.JsonRpcHttpClientPool;
import com.swatt.util.OperationFailedException;
import com.swatt.util.general.KeepNewestHash;

//The Base package for representing JSON-RPC 2.0 messages
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;

public class MoneroChainNode extends ChainNode {
    private static final Logger LOGGER = Logger.getLogger(MoneroChainNode.class.getName());
    private static KeepNewestHash transactions;
    private int requestId = 0;
    private JSONRPC2Session rpcSession;

    // The JSON-RPC 2.0 server URL
    URL serverURL = null;

    try
    {
        serverURL = new URL("http://jsonrpc.example.com:8080");

    }catch(
    MalformedURLException e)
    {
        // handle exception...
    }

    public static final int POWX_ATOMIC_UNITS = 12;

    private JsonRpcHttpClientPool jsonRpcHttpClientPool;

    public MoneroChainNode() throws MalformedURLException {
        // Create new JSON-RPC 2.0 client session
        JSONRPC2Session mySession = new JSONRPC2Session(serverURL);
    }

    @Override
    public void init() {
        String url = chainNodeConfig.getURL();
        int maxSize = 10; // TODO: Should get from chainNodeConfig
        // chainNodeManagerConfig.getIntAttribute("jsonPoolSize ", 10);

        jsonRpcHttpClientPool = new JsonRpcHttpClientPool(url, maxSize);
    }

    @Override
    public BlockData fetchBlockDataByHash(String blockHash) throws OperationFailedException {
        return null;
    }

    private BlockData fetchBlockByHash(JsonRpcHttpClient jsonrpcClient, String blockHash)
            throws OperationFailedException {

        try {
            long start = Instant.now().getEpochSecond();

            Object parameters[] = new Object[] { blockHash };
            RPCBlock rpcBlock = jsonrpcClient.invoke(XMRMethods.GET_BLOCK, parameters, RPCBlock.class);

            BlockData blockData = new BlockData();
            blockData.setHash(rpcBlock.blockHeader.hash);
            blockData.setHeight(rpcBlock.blockHeader.height);
            blockData.setVersionHex(rpcBlock.versionHex); // TODO address version
            blockData.setTimestamp(rpcBlock.blockHeader.timestamp);
            blockData.setNonce(rpcBlock.blockHeader.nonce);
            blockData.setDifficulty(rpcBlock.blockHeader.difficulty);
            blockData.setPrevHash(rpcBlock.blockHeader.prevHash);

            blockData.setReward(rpcBlock.blockHeader.reward);

            /*
             * The fee will vary based on the base reward and the current
             * 
             * block size limit:
             * 
             * fee = (R/R0) * (M0/M) * F0
             * 
             * R: base reward
             * 
             * R0: reference base reward (10 monero)
             * 
             * M: block size limit
             * 
             * M0: minimum block size limit (60000)
             * 
             * F0: 0.002 monero
             * https://monero.stackexchange.com/questions/4562/how-does-the-dynamic-
             * blocksize-and-the-dynamic-fees-work-together-in-monero
             */

            blockData.setBlockchainCode(blockchainCode);

            System.out.println("CALCULATING BLOCK: " + rpcBlock.blockHeader.hash);

            // calculate(jsonrpcClient, blockData, rpcBlock);

            long indexingDuration = Instant.now().getEpochSecond() - start;
            long now = Instant.now().toEpochMilli();

            blockData.setIndexed(now);
            blockData.setIndexingDuration(indexingDuration);

            return blockData;

        } catch (Throwable t) {
            if (t instanceof OperationFailedException)
                throw (OperationFailedException) t;
            else {
                OperationFailedException e = new OperationFailedException("Error fetching latest Block: ", t);
                LOGGER.log(Level.SEVERE, e.toString(), e);
                throw e;
            }
        }
    }

    @Override
    public ChainNodeTransaction fetchTransactionByHash(String transactionHash, boolean calculateFee)
            throws OperationFailedException {
        return null;
    }

    /*
     * public BigDecimal getBalance() throws JSONRPC2SessionException { BigDecimal
     * balance = null; JSONObject result = rpcCall("getbalance", null); if (result
     * != null) { balance = BigDecimal.valueOf(Long.valueOf("" +
     * result.get("balance")), 12); } return balance; }
     * 
     * private JSONObject rpcCall(String method, List<Object> params) throws
     * JSONRPC2SessionException { JSONRPC2Request request = new
     * JSONRPC2Request(method, params, ++requestId); JSONRPC2Response response =
     * rpcSession.send(request); JSONObject result = null; if
     * (response.indicatesSuccess()) { result = (JSONObject) response.getResult(); }
     * else { // TODO: Throw exception
     * System.err.println(response.getError().getMessage()); } return result; }
     */
}
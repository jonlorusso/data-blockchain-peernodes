package com.swatt.chainNode.xmr;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.btc.BitcoinChainNode;
import com.swatt.chainNode.btc.RPCBlock;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.util.JsonRpcHttpClientPool;
import com.swatt.util.OperationFailedException;
import com.swatt.util.general.KeepNewestHash;

public class MoneroChainNode extends ChainNode {
    private static final Logger LOGGER = Logger.getLogger(BitcoinChainNode.class.getName());
    private static KeepNewestHash transactions;

    public static final int POWX_ATOMIC_UNITS = 12;

    private JsonRpcHttpClientPool jsonRpcHttpClientPool;

    public MoneroChainNode() {
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
            blockData.setHash(rpcBlock.hash);
            blockData.setSize(rpcBlock.size);
            blockData.setHeight(rpcBlock.height);
            blockData.setVersionHex(rpcBlock.versionHex);
            blockData.setMerkleRoot(rpcBlock.merkleroot);
            blockData.setTimestamp(rpcBlock.time);
            blockData.setNonce(rpcBlock.nonce);
            blockData.setBits(rpcBlock.bits);
            blockData.setDifficulty(rpcBlock.difficulty);
            blockData.setPrevHash(rpcBlock.previousblockhash);
            blockData.setNextHash(rpcBlock.nextblockhash);

            // blockData.setReward(BITCOIN_BLOCK_REWARD_BTC);

            blockData.setBlockchainCode(blockchainCode);

            System.out.println("CALCULATING BLOCK: " + rpcBlock.hash);

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
}
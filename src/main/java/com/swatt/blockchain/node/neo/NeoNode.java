package com.swatt.blockchain.node.neo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.node.btc.BitcoinNode;
import com.swatt.util.general.ConcurrencyUtilities;
import com.swatt.util.general.OperationFailedException;

public class NeoNode extends BitcoinNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(NeoNode.class.getName());
    private static final int NEW_BLOCK_POLLING_FREQ_MS = 10 * 1000;

    @Override
    public long fetchBlockCount() throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            long blockCount = jsonRpcHttpClient.invoke("getblockcount", new Object[] { null }, Long.class); 

            // block cannot be retrieved by blockNumber until it is no longer the latest.
            // we decrement here to avoid this error.
            return blockCount - 1; 
        } catch (Throwable t) {
            throw new OperationFailedException("Error fetching latest Block: ", t);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }

    @Override
    protected RpcResultBlock getBlock(String hash) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            return jsonRpcHttpClient.invoke("getblock", new Object[] { hash, true }, RpcResultBlock.class);
        } catch (Throwable t) {
            throw new OperationFailedException(t);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }

    @Override
    public void fetchNewBlocks() {
        if (blockListener != null)
            return;

        blockListener = new Thread(() -> {
            LOGGER.info("Starting fetchNewBlocks thread.");

            try {
                long height = fetchBlockCount();

                while (true) {
                    long newHeight = fetchBlockCount();

                    if (newHeight > height) {
                        BlockData blockData = fetchBlockData(newHeight, false);
                        nodeListeners.stream().forEach(n -> n.newBlockAvailable(this, blockData));
                        height = blockData.getHeight();
                    }

                    ConcurrencyUtilities.sleep(NEW_BLOCK_POLLING_FREQ_MS);
                }

            } catch (Throwable t) {
                blockListener = null;
            }
        }, "BlockListener-" + getBlockchainCode());

        blockListener.start();
    }
}
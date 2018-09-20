package com.swatt.blockchain.node.neo;

import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.node.btc.BitcoinNode;
import com.swatt.blockchain.node.btc.RpcResultTransaction;
import com.swatt.util.general.ConcurrencyUtilities;
import com.swatt.util.general.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.swatt.util.general.ConcurrencyUtilities.startThread;

public class NeoNode extends BitcoinNode<RpcResultBlock, RpcResultTransaction>  {

    private static final Logger LOGGER = LoggerFactory.getLogger(NeoNode.class.getName());
    private static final int NEW_BLOCK_POLLING_FREQ_MS = 10 * 1000;

    private boolean running = false;

    @Override
    protected Object[] getBlockCountRpcMethodParameters() {
        return new Object[] { null };
    }

    @Override
    public void fetchNewBlocks() {
        if (running)
            return;

        startThread(() -> {
            LOGGER.info("[NEO] Starting fetchNewBlocks thread.");

            try {
                long height = fetchBlockCount();

                while (true) {
                    long newHeight = fetchBlockCount();

                    if (newHeight > height) {
                        BlockData blockData = fetchBlockData(newHeight - 1);
                        nodeListeners.stream().forEach(n -> n.newBlockAvailable(this, blockData));
                        height = blockData.getHeight();
                    }

                    ConcurrencyUtilities.sleep(NEW_BLOCK_POLLING_FREQ_MS);
                }
            } catch (OperationFailedException e) {
                LOGGER.error("[NEO] Exception caught while fetching new blocks.", e);
            } finally {
                running = false;
            }
        }, "BlockListener-" + getBlockchainCode());
    }
}
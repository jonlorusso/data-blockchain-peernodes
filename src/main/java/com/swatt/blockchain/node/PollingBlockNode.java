package com.swatt.blockchain.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.blockchain.entity.BlockData;
import com.swatt.util.general.ConcurrencyUtilities;

public abstract class PollingBlockNode extends Node {

    private static final Logger LOGGER = LoggerFactory.getLogger(PollingBlockNode.class.getName());
    
    private static final int NEW_BLOCK_POLLING_FREQ_MS = 10 * 1000;
    
    private Thread newBlocksThread;
    
    @Override
    public void fetchNewBlocks() {
        if (newBlocksThread != null)
            return;

        newBlocksThread = new Thread(() -> {
            LOGGER.info("Starting fetchNewBlocks thread.");

            try {
                long height = fetchBlockCount();
                
                while (true) {
                    long newHeight = fetchBlockCount();
                    
                    if (newHeight > height) {
                        BlockData blockData = fetchBlockData(newHeight);
                        nodeListeners.stream().forEach(n -> n.newBlockAvailable(this, blockData));
                        height = blockData.getHeight();
                    }

                    ConcurrencyUtilities.sleep(NEW_BLOCK_POLLING_FREQ_MS);
                }
            } catch (Throwable t) {
                newBlocksThread = null;
            }
        }, "BlockListener-" + getCode());

        newBlocksThread.start();
    }
}

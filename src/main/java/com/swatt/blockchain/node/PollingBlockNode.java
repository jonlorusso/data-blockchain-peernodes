package com.swatt.blockchain.node;

import com.swatt.blockchain.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.blockchain.entity.BlockData;
import com.swatt.util.general.ConcurrencyUtilities;

import static com.swatt.blockchain.util.LogUtils.info;
import static com.swatt.util.general.ConcurrencyUtilities.sleep;
import static com.swatt.util.general.ConcurrencyUtilities.startThread;
import static java.lang.String.format;

public abstract class PollingBlockNode extends Node {

    private static final Logger LOGGER = LoggerFactory.getLogger(PollingBlockNode.class.getName());
    
    private static final int NEW_BLOCK_POLLING_FREQ_MS = 10 * 1000;

    private boolean running = false;

    @Override
    public void fetchNewBlocks() {
        if (running)
            return;

        running = true;

        startThread(() -> {
            info(LOGGER, this, "Starting fetchNewBlocks thread.");

            try {
                long height = fetchBlockCount();
                
                while (true) {
                    long newHeight = fetchBlockCount();
                    
                    if (newHeight > height) {
                        BlockData blockData = fetchBlockData(newHeight, false);
                        nodeListeners.stream().forEach(n -> n.newBlockAvailable(this, blockData));
                        height = blockData.getHeight();
                    }

                    sleep(NEW_BLOCK_POLLING_FREQ_MS);
                }
            } catch (Throwable t) {
                running = false;
            }
        }, "BlockListener-" + getBlockchainCode());
    }
}

package com.swatt.blockchain.ingestor;

import com.swatt.blockchain.ApplicationContext;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.util.general.ConcurrencyUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static com.swatt.blockchain.util.LogUtils.error;
import static com.swatt.blockchain.util.LogUtils.info;
import static java.lang.String.format;

public class NodeIngestor {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeIngestor.class);

    private ApplicationContext applicationContext;
    private NodeIngestorConfig nodeIngestorConfig;
    private BlockProducer blockProducer;

    public NodeIngestor(ApplicationContext applicationContext, NodeIngestorConfig nodeIngestorConfig, BlockProducer blockProducer) {
        this.applicationContext = applicationContext;
        this.nodeIngestorConfig = nodeIngestorConfig;
        this.blockProducer = blockProducer;
    }

    public void start() {
        ConcurrencyUtilities.startThread(() -> {
            blockProducer.stream().forEach(this::ingestBlock);
        }, String.format("BlockConsumer-%s", nodeIngestorConfig.getBlockchainCode()));
    }

    private void ingestBlock(BlockData blockData) {
        try {
            long height = blockData.getHeight();
            BlockData existingBlockData = applicationContext.getBlockDataRepository().findByBlockchainCodeAndHeight(nodeIngestorConfig.getBlockchainCode(), height);

            if (existingBlockData != null && nodeIngestorConfig.isOverwriteExisting()) {
                applicationContext.getBlockDataRepository().replace(blockData);
                info(LOGGER, nodeIngestorConfig, format("Re-ingested block: %d", height));
            } else if (existingBlockData == null) {
                applicationContext.getBlockDataRepository().insert(blockData);
                info(LOGGER, nodeIngestorConfig, format("Ingested block: %d", height));
            }
        } catch (SQLException e) {
            error(LOGGER, nodeIngestorConfig, format("Exception caught while storing fetched block: %s", e.getMessage()));
        }
    }
}

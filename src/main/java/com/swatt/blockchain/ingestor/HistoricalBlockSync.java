package com.swatt.blockchain.ingestor;

import com.swatt.blockchain.ApplicationContext;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.node.PlatformNode;
import com.swatt.blockchain.repository.BlockDataRepository;
import com.swatt.util.general.ConcurrencyUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.LongStream;

import static com.swatt.blockchain.util.LogUtils.error;
import static com.swatt.blockchain.util.LogUtils.info;
import static com.swatt.util.general.ConcurrencyUtilities.startThread;
import static java.lang.String.format;

public class HistoricalBlockSync {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoricalBlockSync.class);

    public static class HistoricalBlockProducer extends BlockProducer {

        private static final Logger LOGGER = LoggerFactory.getLogger(HistoricalBlockProducer.class);

        private ExecutorService executor;
        private boolean running = false;

        public HistoricalBlockProducer() {
            super();
        }

        @Override
        public void start() {
            if (running)
                return;

            executor = Executors.newFixedThreadPool(nodeIngestorConfig.getNumberOfThreads(), new ThreadFactory() {
                private int i = 0;

                @Override
                public Thread newThread(Runnable r) {
                    i++;
                    return new Thread(r, node.getBlockchainCode() + "-NodeIngestor-" + i);
                }
            });

            running = true;

            startThread(() -> {
                try {
                    BlockDataRepository blockDataRepository = applicationContext.getBlockDataRepository();
                    long start = nodeIngestorConfig.getStartHeight() == null ? blockDataRepository.findMaxContiguousHeight(nodeIngestorConfig.getBlockchainCode()) : nodeIngestorConfig.getStartHeight();
                    long end = nodeIngestorConfig.getEndHeight() != null ? nodeIngestorConfig.getEndHeight() : node.fetchBlockCount();

                    info(LOGGER, node, format("Historical ingestion running for blocks: %d through %d", start, end));

                    LongStream.range(start, end).forEach(height -> {
                        executor.execute(() -> {
                            try {
                                node.fetchBlockData(height);
                                if (node instanceof PlatformNode) {
                                    PlatformNode platformNode = (PlatformNode) node;
                                    platformNode.fetchTokenBlockDatas(height);
                                }
                            } catch (Throwable e) {
                              error(LOGGER, node, format("Error ingesting block %d.", height), e);
                            }
                        });
                    });
                } catch (Throwable e) {
                    running = false;
                    error(LOGGER, node, format("Historical ingestion failed: %s", e));
                }
            });
        }

        @Override
        public void blockFetched(Node node, BlockData blockData) {
            enqueueBlock(blockData);
        }
    }

    public static void main(String[] args) {
        try {
            NodeIngestorManager nodeIngestorManager = new NodeIngestorManager(new ApplicationContext());
            nodeIngestorManager.setBlockProducerClass(HistoricalBlockProducer.class);
            nodeIngestorManager.start();
        } catch (IOException e) {
            LOGGER.error("Exception caught in com.swatt.blockchain.Main: ", e);
        }
    }
}

package com.swatt.blockchain.ingestor;

import com.swatt.blockchain.ApplicationContext;
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

            try (Connection connection = applicationContext.getConnectionPool().getConnection()) {
                long start = 0;

                if (nodeIngestorConfig.getStartHeight() == null) {
                    start = applicationContext.getBlockDataRepository().findMaxContiguousHeight(nodeIngestorConfig.getBlockchainCode());
                } else {
                    start = nodeIngestorConfig.getStartHeight();
                }

                long end = nodeIngestorConfig.getEndHeight() != null ? nodeIngestorConfig.getEndHeight() : node.fetchBlockCount();

                info(LOGGER, node, format("Historical ingestion running for blocks: %d through %d", start, end));

                LongStream.range(start, end).forEach(height -> {
                    executor.execute(() -> {
                        try {
                            node.fetchBlockData(height);
                        } catch (Throwable e) {
                            error(LOGGER, node, format("Error ingesting block %d: %s", height), e);
                        }
                    });
                });
            } catch (Throwable e) {
                running = false;
                error(LOGGER, node, format("Historical ingestion failed: %s", e));
            }
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

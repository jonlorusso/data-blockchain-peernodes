package com.swatt.blockchain;

import com.swatt.blockchain.ingestor.HistoricalBlockSync;
import com.swatt.blockchain.ingestor.NewBlockSync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.blockchain.ingestor.NodeIngestorManager;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            ApplicationContext applicationContext = new ApplicationContext();

            NodeIngestorManager historicalIngestion = new NodeIngestorManager(applicationContext);
            historicalIngestion.setBlockProducerClass(HistoricalBlockSync.HistoricalBlockProducer.class);
            historicalIngestion.start();

            NodeIngestorManager newBlockIngestion = new NodeIngestorManager(applicationContext);
            newBlockIngestion.setBlockProducerClass(NewBlockSync.NewBlockProducer.class);
            newBlockIngestion.start();

        } catch (Exception e) {
            LOGGER.error("Exception caught in com.swatt.blockchain.Main: ", e);
        }
    }
}

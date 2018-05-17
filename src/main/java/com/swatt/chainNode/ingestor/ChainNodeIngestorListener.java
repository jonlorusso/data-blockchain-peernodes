package com.swatt.chainNode.ingestor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ChainNodeIngestorListener {
    static final Logger LOGGER = LoggerFactory.getLogger(ChainNodeIngestorListener.class);
    
    default public void historicalIngestionComplete(ChainNodeIngestor chainNodeIngestor) {
        LOGGER.info("Ingestion complete.");
    }
    
    default public void historicalIngestionFailed(ChainNodeIngestor chainNodeIngestor) {
        LOGGER.info("historicalIngestion failed.");
    }
    
    default public void newBlockIngestionFailed(ChainNodeIngestor chainNodeIngestor) {
        LOGGER.info("newBlockIngestion failed.");
    }
}

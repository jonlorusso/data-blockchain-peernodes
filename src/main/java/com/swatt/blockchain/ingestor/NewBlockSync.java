package com.swatt.blockchain.ingestor;

import com.swatt.blockchain.ApplicationContext;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NewBlockSync {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewBlockSync.class);

    public static class NewBlockProducer extends BlockProducer {
        @Override
        public void start(){
            node.fetchNewBlocks();
        }

        @Override
        public void newBlockAvailable(Node node, BlockData blockData) {
            enqueueBlock(blockData);
        }
    }

    public static void main(String[] args) {
        try {
            NodeIngestorManager nodeIngestorManager = new NodeIngestorManager(new ApplicationContext());
            nodeIngestorManager.setBlockProducerClass(NewBlockProducer.class);
            nodeIngestorManager.start();
        } catch (IOException e) {
            LOGGER.error("Exception caught in com.swatt.blockchain.Main: ", e);
        }
    }
}

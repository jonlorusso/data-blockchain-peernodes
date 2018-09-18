package com.swatt.blockchain.ingestor;

import com.swatt.blockchain.ApplicationContext;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.node.NodeListener;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class BlockProducer implements Iterator<BlockData>, NodeListener {

    private final BlockingQueue<BlockData> blockingQueue;

    protected ApplicationContext applicationContext;
    protected NodeIngestorConfig nodeIngestorConfig;
    protected Node node;

    public BlockProducer() {
        this.blockingQueue = new LinkedBlockingQueue<>();
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setNodeIngestorConfig(NodeIngestorConfig nodeIngestorConfig) {
        this.nodeIngestorConfig = nodeIngestorConfig;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public abstract void start();

    @Override
    public void newBlockAvailable(Node node, BlockData blockData) {}

    @Override
    public void blockFetched(Node node, BlockData blockData) {}

    protected void enqueueBlock(BlockData blockData) {
        try {
            blockingQueue.put(blockData);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Unable to enqueue fetched blockData.");
        }
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public BlockData next() {
        try {
            return blockingQueue.take();
        } catch (InterruptedException e) {
            throw new IllegalStateException("Could not retrieve next blockData", e);
        }
    }

    public Stream<BlockData> stream() {
        Spliterator<BlockData> spliterator = Spliterators.spliteratorUnknownSize(this, 0);
        return StreamSupport.stream(spliterator, false);
    }
}

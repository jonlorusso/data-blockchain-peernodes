package com.swatt.blockchain.node;

import com.swatt.blockchain.entity.BlockData;

public interface NodeListener {
	public default void newBlockAvailable(Node node, BlockData blockData) {};
	public default void blockFetched(Node node, BlockData blockData) {};
}

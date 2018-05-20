package com.swatt.blockchain.node;

import com.swatt.blockchain.NodeTransaction;
import com.swatt.blockchain.entity.BlockData;

public interface NodeListener {
	public void newBlockAvailable(Node node, BlockData blockData);
	public void newTransactionsAvailable(Node node, NodeTransaction[] nodeTransactions);
}

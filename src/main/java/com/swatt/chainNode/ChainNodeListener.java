package com.swatt.chainNode;

import com.swatt.chainNode.dao.BlockData;

public interface ChainNodeListener {
	public void newBlockAvailable(ChainNode chainNode, BlockData blockData);
	public void newTransactionsAvailable(ChainNode chainNode, ChainNodeTransaction chainTransactions[]);
}

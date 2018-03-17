package com.swatt.chainNode.xmr;

import java.util.logging.Logger;

import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.btc.BitcoinTransaction;

public class MoneroTransaction extends ChainNodeTransaction {
    private static final Logger LOGGER = Logger.getLogger(BitcoinTransaction.class.getName());

    public MoneroTransaction(String hash) {
        super(hash);
    }
}

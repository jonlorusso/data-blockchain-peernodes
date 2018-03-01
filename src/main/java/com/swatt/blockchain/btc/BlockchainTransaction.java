package com.swatt.blockchain.btc;

public class BlockchainTransaction extends com.swatt.blockchain.BlockchainTransaction {

    public BlockchainTransaction(String hash, String blockHash, String[] inputs, double[] outputs) {
        super(hash, blockHash, inputs, outputs);

    }

    @Override
    public Long getFee() {
        // TODO Auto-generated method stub
        return null;
    }
}

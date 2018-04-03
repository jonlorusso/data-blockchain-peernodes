package com.swatt.chainNode.btc;

import com.swatt.chainNode.ChainNodeScaling;

public class BitcoinScaling extends ChainNodeScaling {

    @Override
    public int Difficulty() {
        return 11;
    }

    @Override
    public int Reward() {
        return 11;
    }

    @Override
    public int AvgFee() {
        return 11;
    }

    @Override
    public int AvgFeeRate() {
        return 11;
    }

    @Override
    public int LargestTxAmount() {
        return 11;
    }

    @Override
    public int LargestFee() {
        return 11;
    }

    @Override
    public int SmallestFee() {
        return 10;
    }

}

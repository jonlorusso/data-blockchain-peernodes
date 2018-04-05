package com.swatt.chainNode.service;

import com.swatt.util.general.Attributable;

public class ChainNodeConfig extends Attributable {
    private String blockchainCode; // Name of this chain
    private String className; // Class of blockchain interface
    private String url; // URL to remote blockchain
    private String rpcUser; // If any username required
    private String rpcPassword; // Accompanying password
    private int difficultyScaling;
    private int rewardScaling;
    private int feeScaling;
    private int amountScaling;

    public ChainNodeConfig(String blockchainCode, String className, String url, String rpcUser, String rpcPassword,
            int difficultyScaling, int rewardScaling, int feeScaling, int amountScaling) {
        this.blockchainCode = blockchainCode;
        this.className = className;
        this.url = url;
        this.rpcUser = rpcUser;
        this.rpcPassword = rpcPassword;

        this.difficultyScaling = difficultyScaling;
        this.rewardScaling = rewardScaling;
        this.feeScaling = feeScaling;
        this.amountScaling = amountScaling;
    }

    public final String getCode() {
        return blockchainCode;
    }

    public final String getRpcUser() {
        return rpcUser;
    }

    public final String getRpcPassword() {
        return rpcPassword;
    }

    public final String getClassName() {
        return className;
    }

    public String getURL() {
        return url;
    }

    public int getDifficultyScaling() {
        return difficultyScaling;
    }

    public int getRewardScaling() {
        return rewardScaling;
    }

    public int getFeeScaling() {
        return feeScaling;
    }

    public int getAmountScaling() {
        return amountScaling;
    }
}

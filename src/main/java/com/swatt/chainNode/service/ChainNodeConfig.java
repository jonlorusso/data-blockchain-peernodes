package com.swatt.chainNode.service;

import com.swatt.util.Attributable;

public class ChainNodeConfig extends Attributable {
    private String blockchainCode; // Name of this chain
    private String className; // Class of blockchain interface
    private String url; // URL to remote blockchain
    private String rpcUser; // If any username required
    private String rpcPassword; // Accompanying password

    public ChainNodeConfig(String blockchainCode, String className, String url, String rpcUser, String rpcPassword) {
        this.blockchainCode = blockchainCode;
        this.className = className;
        this.url = url;
        this.rpcUser = rpcUser;
        this.rpcPassword = rpcPassword;
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
}

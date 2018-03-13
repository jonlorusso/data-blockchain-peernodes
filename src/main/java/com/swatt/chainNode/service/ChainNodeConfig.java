package com.swatt.chainNode.service;

import com.swatt.util.Attributable;

public class ChainNodeConfig extends Attributable {
    private String blockchainCode; // Name of this chain
    private String className; // Class of blockchain interface
    private int forwardedPort; // Local port forwarded to remote blockchain
    private String rpcUser; // If any username required
    private String rpcPassword; // Accompanying password

    public ChainNodeConfig(String blockchainCode, String className, int forwardedPort, String rpcUser,
            String rpcPassword) {
        this.blockchainCode = blockchainCode;
        this.className = className;
        this.forwardedPort = forwardedPort;
        this.rpcUser = rpcUser;
        this.rpcPassword = rpcPassword;
    }

    public final String getCode() {
        return blockchainCode;
    }

    public final int getForwardedPort() {
        return forwardedPort;
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
}

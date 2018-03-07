package com.swatt.blockchain.eth;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.BlockchainBlock;
import com.swatt.blockchain.BlockchainNodeData;
import com.swatt.blockchain.BlockchainNodeInfo;
import com.swatt.blockchain.BlockchainTransaction;

public class BlockchainNode extends com.swatt.blockchain.BlockchainNode {
    private String blockchainTicker;
    private static final Logger LOGGER = Logger.getLogger(BlockchainNode.class.getName());

    public BlockchainNode(JsonRpcHttpClient jsonrpcClient, String blockchainTicker) {
        super(blockchainTicker);

        Web3j web3j = Web3j.build(new HttpService("https://127.0.0.1:")); // FIXME: Enter your Infura token here;
        try {
            LOGGER.log(Level.INFO,
                    "Connected to Ethereum client version: " + web3j.web3ClientVersion().send().getWeb3ClientVersion());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public BlockchainNodeInfo getInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockchainNodeData getDataForInterval(long fromTime, long toTime) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockchainTransaction findTransactionByHash(String hash) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockchainBlock findBlockByHash(String blockHash) {
        System.out.println(blockHash);
        // 5,200,698

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockchainTransaction findTransactionByHash(String hash, boolean calculate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTicker() {
        return this.blockchainTicker;
    }
}
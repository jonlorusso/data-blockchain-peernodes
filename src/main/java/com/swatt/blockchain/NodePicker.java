package com.swatt.blockchain;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.persistence.BlockchainDB;

public class NodePicker {
    public static final String DEFAULT_BLOCKCHAIN_TICKER = "btc";
    public static final String DEFAULT_BLOCKCHAIN_URL = "http://127.0.0.1";
    private static JsonRpcHttpClient jsonrpcClient = null;

    public static BlockchainNode getBlockchain() {
        return getBlockchain(DEFAULT_BLOCKCHAIN_TICKER);
    }

    public static BlockchainNode getBlockchain(String blockchainTicker) {
        BlockchainNode blockchain = null;
        BlockchainDB db = new BlockchainDB();

        CallableStatement preparedStatement = null;

        try {
            preparedStatement = db.connection.prepareCall("{CALL GetBlockchainDetails(?, ?, ?, ?, ?)}");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            preparedStatement.setString(BlockchainInfoColumns.BLOCKCHAIN_TICKER.ordinal(), blockchainTicker);
            preparedStatement.registerOutParameter(BlockchainInfoColumns.FORK_TICKER.ordinal(), Types.VARCHAR);
            preparedStatement.registerOutParameter(BlockchainInfoColumns.FORWARDED_PORT.ordinal(), Types.INTEGER);
            preparedStatement.registerOutParameter(BlockchainInfoColumns.RPC_USER.ordinal(), Types.VARCHAR);
            preparedStatement.registerOutParameter(BlockchainInfoColumns.RPC_PW.ordinal(), Types.VARCHAR);

            preparedStatement.execute();

            String forkTicker = preparedStatement.getString(BlockchainInfoColumns.FORK_TICKER.ordinal());
            int forwardedPort = preparedStatement.getInt(BlockchainInfoColumns.FORWARDED_PORT.ordinal());
            String rpcUser = preparedStatement.getString(BlockchainInfoColumns.RPC_USER.ordinal());
            String rpcPassword = preparedStatement.getString(BlockchainInfoColumns.RPC_PW.ordinal());

            String url = DEFAULT_BLOCKCHAIN_URL + ":" + forwardedPort;

            jsonrpcClient = Utility.initJSONRPC(url, rpcUser, rpcPassword);

            switch (forkTicker) {
            case "BTC":
                blockchain = new com.swatt.blockchain.btc.BlockchainNode(jsonrpcClient, blockchainTicker);
                break;
            case "ETH":
                blockchain = new com.swatt.blockchain.eth.BlockchainNode(jsonrpcClient, blockchainTicker);
                break;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return blockchain;
    }
}

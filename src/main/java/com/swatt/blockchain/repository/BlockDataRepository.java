package com.swatt.blockchain.repository;

import java.sql.Connection;
import java.sql.SQLException;

import com.swatt.blockchain.entity.BlockData;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.sql.ConnectionPool;

public class BlockDataRepository extends Repository {

    private static final String GET_BY_CODE_AND_HEIGHT_WHERE_CLAUSE = "BLOCKCHAIN_CODE = ? AND HEIGHT = ?";
    
    public BlockDataRepository(ConnectionPool connectionPool) {
        super(connectionPool);
    }

    public BlockData findByBlockchainCodeAndHeight(String blockchainCode, long height) throws OperationFailedException, SQLException {
        try (Connection connection = this.connectionPool.getConnection()) {
            return BlockData.getFirstBlockData(connection, GET_BY_CODE_AND_HEIGHT_WHERE_CLAUSE, blockchainCode, Long.valueOf(height));
        }
    }

    public BlockData insert(BlockData blockData) throws SQLException {
        try (Connection connection = this.connectionPool.getConnection()) {
            return BlockData.insertBlockData(connection, blockData);
        }
    }
}

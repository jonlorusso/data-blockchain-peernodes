package com.swatt.blockchain.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.swatt.blockchain.entity.BlockData;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.sql.ConnectionPool;

public class BlockDataRepository extends Repository {

    private static final String SELECT_ALL_QUERY = "SELECT " + BlockData.getSqlColumnList() + " FROM " + BlockData.getStandardTableName() + " USE INDEX ( IDX_BLOCKS_HEIGHT )";
    private static final String GET_BY_CODE_AND_HEIGHT_WHERE_CLAUSE = "BLOCKCHAIN_CODE = ? AND HEIGHT = ?";
    
    public BlockDataRepository(ConnectionPool connectionPool) {
        super(connectionPool);
    }

    public BlockData findByBlockchainCodeAndHeight(String blockchainCode, long height) throws OperationFailedException, SQLException {
        try (Connection connection = this.connectionPool.getConnection()) {
            String query = SELECT_ALL_QUERY + " WHERE " + GET_BY_CODE_AND_HEIGHT_WHERE_CLAUSE;

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, blockchainCode);
                preparedStatement.setLong(2, height);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return BlockData.getNextBlockData(resultSet);
                }
            }
        }
    }

    public BlockData insert(BlockData blockData) throws SQLException {
        try (Connection connection = this.connectionPool.getConnection()) {
            return BlockData.insertBlockData(connection, blockData);
        }
    }

    public BlockData replace(BlockData blockData) throws SQLException {
        try (Connection connection = this.connectionPool.getConnection()) {
            return BlockData.replaceBlockData(connection, blockData);
        }
    }
}

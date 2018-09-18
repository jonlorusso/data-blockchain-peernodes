package com.swatt.blockchain.repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.swatt.blockchain.ApplicationContext;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.util.sql.ConnectionPool;

public class BlockDataRepository extends Repository {

    private static final String SELECT_ALL_QUERY = "SELECT " + BlockData.getSqlColumnList() + " FROM " + BlockData.getStandardTableName() + " USE INDEX ( IDX_BLOCKS_HEIGHT )";
    private static final String GET_BY_CODE_AND_HEIGHT_WHERE_CLAUSE = "BLOCKCHAIN_CODE = ? AND HEIGHT = ?";

    private static final String MISSING_BLOCK_QUERY =
        "SELECT height FROM BLOCK_DATA mo USE INDEX ( IDX_BLOCKS_HEIGHT ) " +
        "WHERE BLOCKCHAIN_CODE = ? " +
        "AND NOT EXISTS ( SELECT NULL FROM BLOCK_DATA mi USE INDEX ( IDX_BLOCKS_HEIGHT ) WHERE BLOCKCHAIN_CODE = ? AND mi.height = mo.height + 1 ) " +
        "ORDER BY height " +
        "LIMIT 1";

    public BlockDataRepository(ConnectionPool connectionPool) {
        super(connectionPool);
    }

    public BlockData findByBlockchainCodeAndHeight(String blockchainCode, long height) throws SQLException {
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

    public long findMaxContiguousHeight(String blockchainCode) throws SQLException {
        try (Connection connection = this.connectionPool.getConnection()) {
            String query = MISSING_BLOCK_QUERY;

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, blockchainCode);
                preparedStatement.setString(2, blockchainCode);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next())
                        return resultSet.getLong(1);

                    throw new SQLException("Unable to query max contiguous height.");
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

    public static void main(String[] args) throws IOException, SQLException {
        System.out.println("foo");
        ApplicationContext applicationContext = new ApplicationContext();
        long height = applicationContext.getBlockDataRepository().findMaxContiguousHeight("BCH");
        System.out.println(height);
    }
}

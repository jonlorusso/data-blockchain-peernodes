package com.swatt.blockchain.repository;

import static com.swatt.util.general.CollectionsUtilities.loadProperties;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.util.DatabaseUtils;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.sql.ConnectionPool;

public class BlockDataRepository extends CrudRepository<BlockData> {

    public BlockDataRepository(ConnectionPool connectionPool) {
        super(connectionPool);
    }

    public BlockData findByBlockchainCodeAndHeight(String blockchainCode, long height) throws OperationFailedException, SQLException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("BLOCKCHAIN_CODE", blockchainCode);
        parameters.put("HEIGHT", height);
        
        return findBy(parameters);
    }
    
    public static void main(String[] args) throws Exception {
        Properties properties = loadProperties("config.properties");
        ConnectionPool connectionPool = DatabaseUtils.getConnectionPool(properties);
        BlockDataRepository blockDataRepository = new BlockDataRepository(connectionPool);
        blockDataRepository.findByBlockchainCodeAndHeight("ETH", 5643572);
    }
}

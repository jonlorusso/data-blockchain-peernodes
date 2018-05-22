package com.swatt.blockchain.repository;

import com.swatt.util.sql.ConnectionPool;

public class Repository {

    protected ConnectionPool connectionPool;
    
    public Repository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
}

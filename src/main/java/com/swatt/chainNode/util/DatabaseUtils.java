package com.swatt.chainNode.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.swatt.util.general.CollectionsUtilities;
import com.swatt.util.sql.ConnectionPool;
import com.swatt.util.sql.SqlUtilities;

public class DatabaseUtils {

    public static final String DATABASE_URL_PROPERTY = "database.url";
    public static final String DATABASE_USER_PROPERTY = "database.user";
    public static final String DATABASE_PASSWORD_PROPERTY = "database.password";
    public static final String DATABASE_MAX_POOL_SIZE_PROPERTY = "database.maxPoolSize";
    
    private static ConnectionPool connectionPool;
    
    public static ConnectionPool getConnectionPool(String propertiesFileName) throws IOException {
        return getConnectionPool(CollectionsUtilities.loadProperties(propertiesFileName));
    }
    
    public static ConnectionPool getConnectionPool(Properties properties) {
        if (connectionPool == null) {
            String url = properties.getProperty(DATABASE_URL_PROPERTY);
            String user = properties.getProperty(DATABASE_USER_PROPERTY);
            String password = properties.getProperty(DATABASE_PASSWORD_PROPERTY);
            int maxSize = Integer.valueOf(properties.getProperty(DATABASE_MAX_POOL_SIZE_PROPERTY));
            
            connectionPool = new ConnectionPool(url, user, password, maxSize);
        }
        
        return connectionPool;
    }
    
    public static Connection getConnection(Properties properties) throws SQLException {
        String url = properties.getProperty(DATABASE_URL_PROPERTY);
        String user = properties.getProperty(DATABASE_USER_PROPERTY);
        String password = properties.getProperty(DATABASE_PASSWORD_PROPERTY);
        
        return SqlUtilities.getConnection(url, user, password);
    }
}

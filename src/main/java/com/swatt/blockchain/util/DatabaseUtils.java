package com.swatt.blockchain.util;

import static com.swatt.util.environment.Environment.getEnvironmentVariableValueOrDefault;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.util.sql.ConnectionPool;

public class DatabaseUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseUtils.class);
    
    private static final String databaseType = "mysql"; 
    
    private static final String DATABASE_HOST_ENV_VAR_NAME = "DATABASE_HOST";
    private static final String DATABASE_PORT_ENV_VAR_NAME = "DATABASE_PORT";
    private static final String DATABASE_USER_ENV_VAR_NAME = "DATABASE_USER";
    private static final String DATABASE_PASSWORD_ENV_VAR_NAME = "DATABASE_PASSWORD";
    private static final String DATABASE_NAME_ENV_VAR_NAME = "DATABASE_NAME";
    private static final String DATABASE_MAX_POOL_SIZE_ENV_VAR_NAME = "DATABASE_MAX_POOL_SIZE";

    private static final String DATABASE_PORT_PROPERTY = "database.port";
    private static final String DATABASE_USER_PROPERTY = "database.user";
    private static final String DATABASE_NAME_PROPERTY = "database.name";
    private static final String DATABASE_MAX_POOL_SIZE_PROPERTY = "database.maxPoolSize";

    private static ConnectionPool connectionPool;

    public static String getJdbcUrl(String databaseType, String host, String port, String databaseName) {
        return String.format("jdbc:%s://%s:%s/%s", databaseType, host, port, databaseName);
    }

    public static ConnectionPool getConnectionPool(String url, String user, String password, int maxSize) {
        if (connectionPool == null) {
            LOGGER.info(String.format("Connecting to database: %s", url));
            connectionPool = new ConnectionPool(url, user, password, maxSize);
        }

        return connectionPool;
    }

    public static ConnectionPool configureConnectionPoolFromEnvironment(Properties properties) throws IOException {
        String databaseHost = System.getenv(DATABASE_HOST_ENV_VAR_NAME);
        String databasePort = getEnvironmentVariableValueOrDefault(DATABASE_PORT_ENV_VAR_NAME, DATABASE_PORT_PROPERTY, properties);
        
        String databaseUser = getEnvironmentVariableValueOrDefault(DATABASE_USER_ENV_VAR_NAME, DATABASE_USER_PROPERTY, properties);
        String databasePassword = System.getenv().get(DATABASE_PASSWORD_ENV_VAR_NAME);
        
        String databaseName = getEnvironmentVariableValueOrDefault(DATABASE_NAME_ENV_VAR_NAME, DATABASE_NAME_PROPERTY, properties);
        String jdbcUrl = DatabaseUtils.getJdbcUrl(databaseType, databaseHost, databasePort, databaseName); 

        int databaseMaxPoolSize = Integer.parseInt(getEnvironmentVariableValueOrDefault(DATABASE_MAX_POOL_SIZE_ENV_VAR_NAME, DATABASE_MAX_POOL_SIZE_PROPERTY, properties));
        return DatabaseUtils.getConnectionPool(jdbcUrl, databaseUser, databasePassword, databaseMaxPoolSize);
    }
}

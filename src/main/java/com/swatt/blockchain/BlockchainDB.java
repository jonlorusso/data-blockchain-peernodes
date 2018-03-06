package com.swatt.blockchain;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * BlockchainDB is the container for the local DB persistence of the blockchain
 * data
 */
public final class BlockchainDB {
    public Connection connection;

    public BlockchainDB() {
        Properties prop = new Properties();
        InputStream input;

        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            final String dburl = prop.getProperty("dburl");
            final String dbname = prop.getProperty("dbname");
            final String dbuser = prop.getProperty("dbuser");
            final String dbpassword = prop.getProperty("dbpassword");

            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + dburl + "/" + dbname, dbuser, dbpassword);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
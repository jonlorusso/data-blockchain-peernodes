package com.swatt.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;

public class SqlUtilities {
	public static final String JDBC_ODBC_DRIVER_CLASS_NAME = "sun.jdbc.odbc.JdbcOdbcDriver";
	public static final String MYSQL_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
	public static final String ORACLE_DRIVER_CLASS_NAME = "oracle.jdbc.driver.OracleDriver";
	public static final String SQL_SERVER_DRIVER_CLASS_NAME = "com.jnetdirect.jsql.JSQLDriver";
	public static final String SQL_LITE_DRIVER_CLASS_NAME = "org.sqlite.JDBC";
	
	private static final String allDrivers[] = { JDBC_ODBC_DRIVER_CLASS_NAME, MYSQL_DRIVER_CLASS_NAME, ORACLE_DRIVER_CLASS_NAME, SQL_SERVER_DRIVER_CLASS_NAME,  SQL_LITE_DRIVER_CLASS_NAME} ;
	
	public static void loadJdbcOdbcDriver() throws ClassNotFoundException {
		Class.forName(JDBC_ODBC_DRIVER_CLASS_NAME);
	}
	
	public static void loadJdbcOracleDriver() throws ClassNotFoundException {
		Class.forName(ORACLE_DRIVER_CLASS_NAME);
	}
	
	public static void loadJdbcOMySqlDriver() throws ClassNotFoundException {
		Class.forName(MYSQL_DRIVER_CLASS_NAME);
	}
	
	public static void loadStandardDrivers() {
		for (int i=0; i < allDrivers.length; i++) {
			try {
				Class.forName(allDrivers[i]);			// load the ones that are available
			} catch (ClassNotFoundException e) { }
		}
	}
	
	public static void loadJdbcSqlServerDriver() throws ClassNotFoundException {
		Class.forName(SQL_SERVER_DRIVER_CLASS_NAME);
	}
	
	public static void loadMySqlDriver() throws ClassNotFoundException {
		Class.forName(MYSQL_DRIVER_CLASS_NAME);
	}
	
	public static void loadJdbcSqlLiteDriver() throws ClassNotFoundException {
		Class.forName(SQL_LITE_DRIVER_CLASS_NAME);
	}


	public static Connection getConnection(String jdbcUrl) throws SQLException {
		return getConnection(jdbcUrl, null, null);
	}

	
	public static Connection getConnection(String jdbcUrl, String user, String password) throws SQLException {
		Connection conn = null;
		
		try {
			if (user == null)
				return DriverManager.getConnection(jdbcUrl);
			else
				return DriverManager.getConnection(jdbcUrl, user, password);
			
		} catch (SQLException e) {
				throw e;
		}
	}
	

	private static void display(PrintStream out, String text) {
		if (out != null)
			out.println(text);
	}
	
	
	public static int emptyTable(Connection conn, String tableName) throws SQLException {
		Statement statement = conn.createStatement();
		int result = emptyTable(statement, tableName);
		statement.close();
		return result;
	}
	
	public static int emptyTable(Statement statement, String tableName) throws SQLException {
		String sql = getEmptyTableSql(tableName);
		return  statement.executeUpdate(sql);
	}
	
	public static String getEmptyTableSql(String tableName) {
		return "DELETE from " + tableName;
	}
	
	public static void close(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) { }
	}
	
	public static void close(Statement statement) {
		try {
			statement.close();
		} catch (SQLException e) { }
	}
}

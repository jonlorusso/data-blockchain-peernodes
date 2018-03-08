package com.swatt.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

public class ConnectionPool {			// FIXME: Not Industrial Strength.  Does not deal with un-returned connections
	private String jdbcUrl;
	private String user;
	private String password;
	private int maxSize;
	private LinkedList<Connection> freeConnections = new LinkedList<Connection>();
	private LinkedList<Connection> busyConnections = new LinkedList<Connection>();
	
	static {
		try {
			SqlUtilities.loadMySqlDriver();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public ConnectionPool(String jdbcUrl, String user, String password, int maxSize) {
		this.jdbcUrl = jdbcUrl;
		this.user = user;
		this.password = password;
		this.maxSize = maxSize;
	}
	
	public Connection getConnection() throws SQLException {
		synchronized(freeConnections) {
			Connection conn = null;
			
			if (freeConnections.size() > 0) {
				conn = freeConnections.removeFirst();
			} else if ((freeConnections.size() + busyConnections.size()) < maxSize) {
				conn = SqlUtilities.getConnection(jdbcUrl, user, password);
			} else {
				while (freeConnections.size() == 0) {
					ConcurrencyUtilities.waitOn(freeConnections);
				}
					
				conn = freeConnections.removeFirst();
			}
			
			busyConnections.add(conn);
			return conn;
		}
	}
	
	public void returnConnection(Connection conn) {
		synchronized(freeConnections) {
			busyConnections.remove(conn);
			freeConnections.add(conn);
			ConcurrencyUtilities.notifyAll(freeConnections);
		}
		
	}

}
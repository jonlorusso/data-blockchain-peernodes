package com.swatt.blockchain.persistence;

import java.sql.*;

/** BlockchainDB is the container for the local DB persistence of the blockchain data
 */
public final class BlockchainDB{  
	public static void main(String arguments[]){  
		try{ 
			Class.forName("com.mysql.jdbc.Driver");  
			Connection connection = DriverManager.getConnection("jdbc:mysql://35.196.100.73:3306/getyfi","root","Getyfi123$");  
			
			Statement statement = connection.createStatement();  
			
			ResultSet resultSet = statement.executeQuery("select * from dns_history");  
		
			while(resultSet.next())  
				System.out.println(resultSet.getInt(1)+"  "+resultSet.getString(2)+"  "+resultSet.getString(3));
			
			connection.close();  
		}catch(Exception e){ System.out.println(e);}  
	}
}
package api;
import java.sql.*;

import zookeeper.Configuration;

public class Database {
	static Connection conn=null;
	static PreparedStatement stmt=null;
	public static String getURL() {
		return Configuration.getDBURL();
	}
	
	public static String getUsername() {
		return Configuration.getDBUSER();
	}
	public static String getUsername(int userid) {
		try {
			Connection conn=getConnection();
			String selectString = "SELECT USERNAME FROM USERS "
			 		+ "WHERE USERID = ?"; 
			 stmt = conn.prepareStatement(selectString);
			 stmt.setInt(1, userid);
			 ResultSet rs =stmt.executeQuery();
			 if (rs.next()) {
				 return rs.getString("USERNAME");
			 }
			 return "";
		} catch (ClassNotFoundException e) {
		
			e.printStackTrace();
			return "";
		} catch (SQLException e) {
			
			e.printStackTrace();
			return "";
		}
		 finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }
		   }
	
	}
	
	public static String getPassword() {
		return Configuration.getDBPASS();
	}
	
	static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";    
	 
	 //  Database credentials
	 static final String USER = Database.getUsername();
	 static final String PASS = Database.getPassword();
	 
	 public static Connection getConnection() throws ClassNotFoundException, SQLException{
		 final String DB_URL = Database.getURL();
     try {
			Class.forName(JDBC_DRIVER);
	
		Connection	conn = DriverManager.getConnection(DB_URL, USER, PASS);
	
		return conn;
	     } catch (ClassNotFoundException ClassNotFound) {
				// TODO Auto-generated catch block
				throw ClassNotFound;
		} catch (SQLException SQL) {
				throw SQL;
			
		}
	 }
	 
	 
	 
	 public static int getUserID(String username) {
		 try {
			conn=Database.getConnection();
			String selectString = "SELECT USERID FROM USERS WHERE USERNAME = ? ";
			 stmt = conn.prepareStatement(selectString);
			 stmt.setString(1, username);
			 ResultSet rs =stmt.executeQuery();
			if( rs.next())
			 return rs.getInt("USERID");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();			
		}
		 finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }
		   }
		return 0;	
	 }
}




package api;
import java.sql.*;

public class Database {
	public static String getURL() {
		return "jdbc:mysql://localhost/DIRSERVICE";
	}
	
	public static String getUsername() {
		return "savvas";
	}
	
	public static String getPassword() {
		return "root";
	}
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";    
	 
	 //  Database credentials
	 static final String USER = Database.getUsername();
	 static final String PASS = Database.getPassword();
	 
	 public static Connection getConnection() throws ClassNotFoundException, SQLException{
		 final String DB_URL = Database.getURL();
     try {
			Class.forName("com.mysql.jdbc.Driver");
	
		Connection	conn = DriverManager.getConnection(DB_URL, USER, PASS);
		return conn;
	     } catch (ClassNotFoundException ClassNotFound) {
				// TODO Auto-generated catch block
				throw ClassNotFound;
		} catch (SQLException SQL) {
				throw SQL;
			
		}
	 }
}




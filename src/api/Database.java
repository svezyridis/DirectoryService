package api;
import java.sql.*;

public class Database {
	static Connection conn=null;
	static PreparedStatement stmt=null;
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
	 
	 
	 
	 public static int getUserID(String username) {
		 try {
			conn=Database.getConnection();
			String selectString = "SELECT USERID FROM USER WHERE USERNAME = ? ";
			 stmt = conn.prepareStatement(selectString);
			 stmt.setString(1, username);
			 ResultSet rs =stmt.executeQuery();
			if( rs.next())
			 return rs.getInt("USERID");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return 0;	
	 }
}




package api;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;


public class CheckNewUser {
	//initialize connection and statement
	static Connection conn=null;
	static PreparedStatement stmt=null;

	/**
	 * Method to get check if a users exist in the database and if not add him.                          
	 * @param token
	 * The decrypted token containing user information.          
	 * @return String: the username of the corresponding token.
	 */
	
	public static String checkIfnewAndadd(JSONObject token) {


		String username= token.getString("userid").split("\\@")[0];
		System.out.println("Connecting to a selected database...");
	 try {	 
		 
		 JSONObject usermeta=token.getJSONObject("usermeta");
		 //check if user exists
		 
		 conn=Database.getConnection();
		 System.out.println("Checking if user exists");
		 String selectString = "SELECT * FROM USERS WHERE USERNAME = ? ";
		 
		 stmt = conn.prepareStatement(selectString);
		 stmt.setString(1, username);
		 ResultSet rs =stmt.executeQuery();
		 
		 //if user does not exist add him
		 if(!rs.next()) {
			 String insertString = "INSERT INTO USERS"
						+ "(USERNAME,NAME,NICKNAME) VALUES"
						+ "(?,?,?)";
			 stmt = conn.prepareStatement(insertString);
		      stmt.setString(1, username);
		      stmt.setString(2, usermeta.getString("name"));
		      stmt.setString(3, usermeta.getString("nick"));
		      stmt.executeUpdate();
		      System.out.println("Inserted user into the table...");
		      int userid=Database.getUserID(username);
		      insertString = "INSERT INTO GALLERIES"
						+ "(NAME,OWNER) VALUES"
						+ "(?,?)";
			 stmt = conn.prepareStatement(insertString);
		      stmt.setString(1, "DEFAULT");
		      stmt.setInt(2, userid);
		      stmt.executeUpdate();
		      System.out.println("Created DEFAULT GALLERY...");
		 }
	    
	} catch (ClassNotFoundException | SQLException  e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 return username;
	}

}

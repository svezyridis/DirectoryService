package api;

import api.Database;
import crypto.Token;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.sql.*;


import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

public class Friends {
	static Connection conn=null;
	static PreparedStatement stmt=null;
	
	/**
	 * Method to add friends                          
	 * @param username
	 * The username of the user who wants to add friend.
	 * @param friendid
	 * The id of the friend that user wants to add.            
	 * @return String: Error message (if any).
	 */

	public static String addFriend(String username, String friend) {
		
		System.out.println("Connecting to a selected database...");
	 try {
		 int friendid=Database.getUserID(friend);
		 int userid=Database.getUserID(username);
		 
		 conn=Database.getConnection();
		 System.out.println("Inserting records into the table...");
		 String insertString = "INSERT INTO FRIENDSHIP"
					+ "(USERID,FRIENDID) VALUES"
					+ "(?,?)";
		 stmt = conn.prepareStatement(insertString);
		 stmt.setInt(1, userid);
		 stmt.setInt(2, friendid);
		 stmt.executeUpdate();
	     System.out.println("Inserted records into the table...");
	} catch (ClassNotFoundException  e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return e.getMessage();
	}
	 catch(SQLException e) {
		 e.printStackTrace();
		 if(e.getErrorCode()==1062)
			 return "Friendship allready exists";
		 if(e.getErrorCode()==1452)
			 return "Could not find friend";
		return e.getMessage();
	 }
	 
		 
		return "";
	}

}

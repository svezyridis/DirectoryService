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

	public static String addFriend(JSONObject token, String friendid) {
		
		System.out.println("Connecting to a selected database...");
	 try {
		 int intfriendid=Integer.parseInt(friendid);
		 conn=Database.getConnection();
		 String selectString = "SELECT * FROM USER WHERE USERNAME = ? ";
		 String username= token.getString("userid").split("\\@")[0];
		 stmt = conn.prepareStatement(selectString);
		 stmt.setString(1, username);
		 ResultSet rs =stmt.executeQuery();
		 rs.next();
		 int userid=rs.getInt("USERID");
		 
		 System.out.println("Inserting records into the table...");
		 String insertString = "INSERT INTO FRIENDSHIP"
					+ "(USERID,FRIENDID) VALUES"
					+ "(?,?)";
		 stmt = conn.prepareStatement(insertString);
		 stmt.setInt(1, userid);
		 stmt.setInt(2, intfriendid);
		 stmt.executeUpdate();
	     System.out.println("Inserted records into the table...");
	} catch (ClassNotFoundException | SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return e.getMessage();
	}

	 
	 
		 
		 
		 
		 
		return "";
	}

}

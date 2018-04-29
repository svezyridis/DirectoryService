package api;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import crypto.Token;

public class CheckNewUser {
	//initialize connection and statement
	static Connection conn=null;
	static PreparedStatement stmt=null;
	
	public static void checkIfnewAndadd(JSONObject token) {


		
		System.out.println("Connecting to a selected database...");
	 try {	 
		 String username= token.getString("userid").split("\\@")[0];
		 JSONObject usermeta=token.getJSONObject("usermeta");
		 //check if user exists
		 conn=Database.getConnection();
		 System.out.println("Checking if user exists");
		 String selectString = "SELECT * FROM USER WHERE USERNAME = ? ";
		 
		 stmt = conn.prepareStatement(selectString);
		 stmt.setString(1, username);
		 ResultSet rs =stmt.executeQuery();
		 
		 //if user does not exist add him
		 if(!rs.next()) {
			 String insertString = "INSERT INTO USER"
						+ "(USERNAME,NAME,NICKNAME) VALUES"
						+ "(?,?,?)";
			 stmt = conn.prepareStatement(insertString);
		      stmt.setString(1, username);
		      stmt.setString(2, usermeta.getString("name"));
		      stmt.setString(3, usermeta.getString("nick"));
		      stmt.executeUpdate();
		      System.out.println("Inserted records into the table...");
		 }
	    
	} catch (ClassNotFoundException | SQLException  e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

}

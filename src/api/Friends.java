package api;

import api.Database;
import crypto.Token;
import images.Gallery;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

	public static JSONObject addFriend(String username, String friend) {
		if(friend==null || friend.equals("")) {
			JSONObject resJSON = new JSONObject();
			resJSON.put("error","Invalid friend name");
			return resJSON;
		}
		
		System.out.println("Connecting to a selected database...");
	 try {
		 int friendid=Database.getUserID(friend);
		 if(friendid==0) {
				JSONObject resJSON=new JSONObject();
				resJSON.put("error","User "+friend+" does not exist");
				return resJSON;
		 }
		 
		 int userid=Database.getUserID(username);
		 
		 conn=Database.getConnection();
		 System.out.println("Inserting records into the table...");
		 String insertString = "INSERT INTO FRIENDSHIPS"
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
		JSONObject resJSON = new JSONObject();
		resJSON.put("error",e.getMessage());
		return resJSON;
	}
	 catch(SQLException e) {
		 e.printStackTrace();
		 if(e.getErrorCode()==1062) {
			 JSONObject resJSON = new JSONObject();
			 resJSON.put("error","Friendship allready exists");
			 return resJSON;
		 }
		 if(e.getErrorCode()==1452) {
			 JSONObject resJSON = new JSONObject();
			 resJSON.put("error","Could not find friend");
			 return resJSON;
		 }
		 JSONObject resJSON = new JSONObject();
		 resJSON.put("error",e.getMessage());
		 return resJSON;
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
	 
		 
	 JSONObject resJSON = new JSONObject();
	 resJSON.put("error","");
	 return resJSON;
	}

	public static JSONObject deleteFriend(String username, String friend) {
		if(friend==null || friend.equals("")) {
			JSONObject resJSON = new JSONObject();
			resJSON.put("error","Invalid friend name");
			return resJSON;
		}
		int userid=Database.getUserID(username);
		int friendid=Database.getUserID(friend);
		if(friendid==0) {
			JSONObject resJSON=new JSONObject();
			resJSON.put("error","User "+friend+" does not exist");
			return resJSON;
	 }
		System.out.println("Connecting to a selected database...");
		try {
			// Check if user (friendname) is friend with user (username)
			 conn=Database.getConnection();
			 String selectString = "DELETE FROM FRIENDSHIPS WHERE USERID = ? AND FRIENDID = ?  ";
			 stmt = conn.prepareStatement(selectString);
			 stmt.setInt(1, userid);
			 stmt.setInt(2, friendid);
			 
			 if(stmt.executeUpdate()!=0) {
				 JSONObject resJSON=new JSONObject();
				 resJSON.put("error", "");
				 return resJSON;
			 }
			 else {
				 JSONObject resJSON=new JSONObject();
				 resJSON.put("error", "You are not friends with "+friend);
				 return resJSON;
			 }
			 
			
		} catch (ClassNotFoundException e) {
			JSONObject resJSON=new JSONObject();
			resJSON.put("error", e.getMessage());
			return resJSON;
		} catch (SQLException e) {
			JSONObject resJSON=new JSONObject();
			resJSON.put("error", e.getMessage());
			return resJSON;
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

	public static JSONObject getFriends(String username) {
		try {
			
			 int userid=Database.getUserID(username);
			 conn=Database.getConnection();
			 String selectString = "SELECT USERS.USERID, USERNAME FROM FRIENDSHIPS "
			 		+ "INNER JOIN USERS ON FRIENDID = USERS.USERID"
			 		+ " WHERE FRIENDSHIPS.USERID = ? ";
			 stmt = conn.prepareStatement(selectString);
			 stmt.setInt(1, userid);
			 ResultSet rs =stmt.executeQuery();
			 
			 if(!rs.next()) {
				 JSONObject resJSON=new JSONObject();
				 resJSON.put("error", "no friends found for user "+ username);
				 return resJSON;
			 }
			 rs.beforeFirst();
			 ArrayList<HashMap<String,String>> friends = new ArrayList<HashMap<String,String>>();
			 HashMap<String,String> friend = null;
			 while (rs.next()) {
				 friend = new HashMap<String,String>();
				 friend.put("friendid", rs.getString("USERID"));
				 friend.put("friendname", rs.getString("USERNAME"));
				 friends.add(friend);		 
			 }
			 List<JSONObject> jsonList = new ArrayList<JSONObject>();

			 for(HashMap<String, String> data : friends) {
			     JSONObject obj = new JSONObject(data);
			     jsonList.add(obj);
			 }
			 
			 JSONObject resJSON=new JSONObject();
			 resJSON.put("error", "");
			 resJSON.put("result", jsonList);
			 return resJSON;
			 
		} catch ( SQLException e) {
			JSONObject resJSON=new JSONObject();
			 resJSON.put("error", e.getMessage());
			 return resJSON;
		} catch (ClassNotFoundException e) {
			JSONObject resJSON=new JSONObject();
			
			 resJSON.put("error", e.getMessage());
			 return resJSON;
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
	public static boolean isFriend(int userid, int friendid) {
		try {
			conn=Database.getConnection();
			String selectString = "SELECT FRIENDSHIPID FROM FRIENDSHIPS WHERE USERID = ? AND FRIENDID = ?  ";
			 stmt = conn.prepareStatement(selectString);
			 stmt.setInt(1, userid);
			 stmt.setInt(2, friendid);
			 ResultSet rs =stmt.executeQuery();
			 return rs.next();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return false;
		}
		 
	}
	

}

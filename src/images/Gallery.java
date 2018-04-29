package images;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import org.json.JSONObject;
import api.Database;


public class Gallery {
	static Connection conn=null;
	static PreparedStatement stmt=null;
	
	/**
	 * Method to get user gallery                          
	 * @param username
	 * The username of the gallerie's owner.          
	 * @return A JSON containing two values: "error" (if any)" and a JSONList with the names of the galleries and their id.
	 */
	public static JSONObject getGallery(String username) {
		try {
		
			 int userid=Database.getUserID(username);
			 conn=Database.getConnection();
			 String selectString = "SELECT * FROM GALLERY WHERE OWNER = ? ";
			 stmt = conn.prepareStatement(selectString);
			 stmt.setInt(1, userid);
			 ResultSet rs =stmt.executeQuery();
			 
			 if(!rs.next()) {
				 JSONObject resJSON=new JSONObject();
				 resJSON.put("error", "no galleries found");
				 return resJSON;
			 }
			 rs.beforeFirst();
			 ArrayList<HashMap<String,String>> galleries = new ArrayList<HashMap<String,String>>();
			 HashMap<String,String> gallery = null;
			 while (rs.next()) {
				 gallery = new HashMap<String,String>();
				 gallery.put("galleryid", rs.getString("GALLERYID"));
				 gallery.put("galleryname", rs.getString("NAME"));
				 galleries.add(gallery);		 
			 }
			 List<JSONObject> jsonList = new ArrayList<JSONObject>();

			 for(HashMap<String, String> data : galleries) {
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
		
		
	}

	public static JSONObject getUserGalleries(String username, String friendname) {
		int userid=Database.getUserID(username);
		int friendid=Database.getUserID(friendname);
		try {
			// Check if user (friendname) is friend with user (username)
			conn=Database.getConnection();
			 String selectString = "SELECT FRIENDSHIPID FROM FRIENDSHIP WHERE USERID = ? AND FRIENDID = ?  ";
			 stmt = conn.prepareStatement(selectString);
			 stmt.setInt(1, friendid);
			 stmt.setInt(2, userid);
			 ResultSet rs =stmt.executeQuery();
			 JSONObject resJSON=new JSONObject();
			 if(rs.next()) {
				 JSONObject JSON = Gallery.getGallery(friendname);
				 resJSON.put("result", JSON);
				 return resJSON;
			 }
			 resJSON.put("error", "You dont have access rights to user's "+friendname+" galleries");
				return resJSON;
			 
			
		} catch (ClassNotFoundException e) {
			JSONObject resJSON=new JSONObject();
			resJSON.put("error", e.getMessage());
			return resJSON;
		} catch (SQLException e) {
			JSONObject resJSON=new JSONObject();
			resJSON.put("error", e.getMessage());
			return resJSON;
		}
		
		
	}

}


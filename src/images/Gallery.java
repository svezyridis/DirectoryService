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
	public static JSONObject getUserGalleries(String username) {
		try {
		
			 int userid=Database.getUserID(username);
			 conn=Database.getConnection();
			 String selectString = "SELECT * FROM GALLERIES WHERE OWNER = ? ";
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

	public static JSONObject getFriendGalleries(String username, String friendname) {
		int userid=Database.getUserID(username);
		int friendid=Database.getUserID(friendname);
		if(friendid==0) {
			JSONObject resJSON=new JSONObject();
			resJSON.put("error","User "+friendname+" does not exist");
			return resJSON;
		}
		try {
			// Check if user (friendname) is friend with user (username)
			 conn=Database.getConnection();
			 String selectString = "SELECT FRIENDSHIPID FROM FRIENDSHIPS WHERE USERID = ? AND FRIENDID = ?  ";
			 stmt = conn.prepareStatement(selectString);
			 stmt.setInt(1, friendid);
			 stmt.setInt(2, userid);
			 ResultSet rs =stmt.executeQuery();
			 JSONObject resJSON=new JSONObject();
			 if(rs.next()) {
				 JSONObject JSON = Gallery.getUserGalleries(friendname);
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

	public static JSONObject createGallery(String username, String galleryname) {
		if(galleryname==null || galleryname.equals("")) {
			JSONObject resJSON = new JSONObject();
			resJSON.put("error","Invalid gallery name");
			return resJSON;
		}
		System.out.println("Connecting to a selected database...");
		 try {
			 int userid=Database.getUserID(username); 
			 conn=Database.getConnection();
			 System.out.println("Inserting records into the table...");
			 String insertString = "INSERT INTO GALLERIES"
						+ "(NAME,OWNER) VALUES"
						+ "(?,?)";
			 stmt = conn.prepareStatement(insertString);
			 stmt.setString(1, galleryname);
			 stmt.setInt(2, userid);
			 stmt.executeUpdate();
		     System.out.println("Inserted records into the table...");
		} catch (ClassNotFoundException  e) {
			 e.printStackTrace();
			 JSONObject resJSON = new JSONObject();
			 resJSON.put("error",e.getMessage());
			 return resJSON;
		}
		 catch(SQLException e) {
			 e.printStackTrace();
			 if(e.getErrorCode()==1062) {
				 JSONObject resJSON = new JSONObject();
				 resJSON.put("error","Gallery with the same name allready exists");
				 return resJSON;
			 }
			 JSONObject resJSON = new JSONObject();
			 resJSON.put("error",e.getMessage());
			 return resJSON;
		 }		 
		 JSONObject resJSON = new JSONObject();
		 resJSON.put("error","");
		 return resJSON;
	}
	
	public static JSONObject deleteGallery(String username, String galleryname) {
		
		int userid=Database.getUserID(username);
		System.out.println("Connecting to a selected database...");
		try {
			 conn=Database.getConnection();
			 String selectString = "DELETE FROM GALLERIES WHERE OWNER = ? AND NAME = ?  ";
			 stmt = conn.prepareStatement(selectString);
			 stmt.setInt(1, userid);
			 stmt.setString(2, galleryname);	 
			 if(stmt.executeUpdate()!=0) {
				 JSONObject resJSON=new JSONObject();
				 resJSON.put("error", "");
				 return resJSON;
			 }
			 else {
				 JSONObject resJSON=new JSONObject();
				 resJSON.put("error", "Gallery "+galleryname+" not found or you are not the owner of the gallery");
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
		
	}

}


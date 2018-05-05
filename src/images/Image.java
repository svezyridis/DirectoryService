package images;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

import api.Database;

public class Image {
	static Connection conn=null;
	static PreparedStatement stmt=null;
	public static JSONObject deleteImage(String username, String imageid) {
		int imgid=Integer.parseInt(imageid);
		int userid=Database.getUserID(username);
		System.out.println("Connecting to a selected database...");
		try {
			// Check if user is the owner of image
			 conn=Database.getConnection();
			 String selectString = "SELECT * FROM IMAGES "
			 		+ "INNER JOIN GALLERIES ON GALLERY = GALLERYID "
			 		+ "INNER JOIN USERS ON OWNER = USERID "
			 		+ "WHERE USERID = ? AND IMAGEID = ?"; 
			 stmt = conn.prepareStatement(selectString);
			 stmt.setInt(1, userid);
			 stmt.setInt(2, imgid);
			 ResultSet rs =stmt.executeQuery();
			 JSONObject resJSON=new JSONObject();
			 if(rs.next()) {
				 String updateString = "UPDATE IMAGES SET GALLERY = NULL";
				 stmt = conn.prepareStatement(updateString);
				 stmt.executeUpdate();
				 resJSON.put("error","" );
				 return resJSON;
			 }
			 resJSON.put("error", "Invalid imageid or you don't have access to this image");
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



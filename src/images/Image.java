package images;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.http.HttpEntity;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONObject;

import api.Database;
import api.Friends;
import storage.StorageAPI;
import zookeeper.Zookeeper;

public class Image {
	
	
	private static final String CHARSET = "UTF-8";
	static Connection conn=null;
	static PreparedStatement stmt=null;
	public static int getImageOwner(int imageid) {
		 try {
			conn=Database.getConnection();
		 
		 String selectString = "SELECT OWNER FROM IMAGES "
		 		+ "INNER JOIN GALLERIES ON GALLERY = GALLERYID "
		 		+ "WHERE IMAGEID = ?"; 
		 stmt = conn.prepareStatement(selectString);
		 stmt.setInt(1, imageid);
		 ResultSet rs =stmt.executeQuery();
		 if (rs.next()) {
			 return rs.getInt("OWNER");
		 }
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return 0;
		}
		 return 0;
		
	}
	public static JSONObject deleteImage(String username, String imageid) {
		int imgid=Integer.parseInt(imageid);
		int userid=Database.getUserID(username);
		System.out.println("Connecting to a selected database...");
		try {
			// Check if user is the owner of image	 
			 int owner =getImageOwner(imgid);
			 JSONObject resJSON=new JSONObject();
			 if(owner==userid) {
				 conn=Database.getConnection();
				 String updateString = "UPDATE IMAGES SET GALLERY = NULL WHERE IMAGEID = ?";
				 stmt = conn.prepareStatement(updateString);
				 stmt.setInt(1, imgid);
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
	public static JSONObject getImages(String username, String galleryid) {
		int userid=Database.getUserID(username);
		JSONObject resJSON=new JSONObject();
		if(galleryid==null || galleryid.equals("")) {
			resJSON.put("error", "invalid gallery id");
			return resJSON;
		}
		int glryid=Integer.parseInt(galleryid);
		int owner=Gallery.getOwner(glryid);
		try {
			// Check if user (owner) is friend with user (username)
		
			 if(Friends.isFriend(owner, userid) || userid==owner) {
				 conn=Database.getConnection();
				 String selectString = "SELECT * FROM IMAGES WHERE GALLERY = ? ";
				 stmt = conn.prepareStatement(selectString);
				 stmt.setInt(1, glryid);
				 ResultSet rs =stmt.executeQuery();
				 
				 if(!rs.next()) {
					 resJSON.put("error", "no images found");
					 return resJSON;
				 }
				 rs.beforeFirst();
				 ArrayList<HashMap<String,String>> images = new ArrayList<HashMap<String,String>>();
				 HashMap<String,String> image = null;
				 while (rs.next()) {
					 image = new HashMap<String,String>();
					 image.put("imageURL", StorageAPI.getURL(rs.getInt("IMAGEID")));
					 image.put("timestamp",rs.getTimestamp("TIMESTAMP").toString());
					 image.put("id", rs.getString("IMAGEID"));
					 images.add(image);		 
				 }
				 List<JSONObject> jsonList = new ArrayList<JSONObject>();

				 for(HashMap<String, String> data : images) {
				     JSONObject obj = new JSONObject(data);
				     int id = obj.getInt("id");
				     obj.put("comments",Comments.getComments(id) );
				     jsonList.add(obj);
				 }
				 
				 resJSON.put("error", "");
				 resJSON.put("result", jsonList);
				 return resJSON;
			 }
			 resJSON.put("error", "You dont have access rights to this gallery");
				return resJSON;
			 
			
		} catch (SQLException e) {
			
			resJSON.put("error", e.getMessage());
			return resJSON;
		} catch (ClassNotFoundException e) {
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
	public static InputStream postImage(String username,HttpServletRequest request) {
		
		try {
			Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
		    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
		    InputStream fileContent = filePart.getInputStream();
			HttpEntity entity = MultipartEntityBuilder.create()
			        .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
			        .setCharset(Charset.forName(CHARSET))
			        .addBinaryBody("file", fileContent, ContentType.MULTIPART_FORM_DATA, fileName)
			        .addTextBody("text", fileName)
			        .build();
			
			Content content = Request.Post("http://localhost:8080/DirectoryService/test")
			        .connectTimeout(2000)
			        .socketTimeout(2000)
			        .body(entity)
			        .execute().returnContent();
					return content.asStream();
			
		} catch (IOException e) {
			JSONObject token= new JSONObject();
			token.put("error", e.getMessage());
			return null;
		} catch (ServletException e) {
			JSONObject token= new JSONObject();
			token.put("error", e.getMessage());
			return null;
		}
	}
}



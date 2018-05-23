package images;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import api.Database;
import api.Friends;
import crypto.Encryption;
import storage.FileServices;
import storage.Storage;
import zookeeper.Configuration;


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
			// Check if user (owner) is friend with user (username) or is the owner of the gallery
		
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
				 resJSON.put("error", "");
				 while (rs.next()) {
					 
					 image = new HashMap<String,String>();
					 String URL=Storage.getURL(rs.getInt("IMAGEID"));
					 if(URL==null)
						 resJSON.put("error", "URLs for one or more images could not be loaded");
						 
					 if (URL!=null) {
						 image.put("imageURL", URL);
						 image.put("timestamp",rs.getTimestamp("TIMESTAMP").toString());
						 image.put("id", rs.getString("IMAGEID"));
						 images.add(image);
					 }
				 }
				 List<JSONObject> jsonList = new ArrayList<JSONObject>();

				 for(HashMap<String, String> data : images) {
				     JSONObject obj = new JSONObject(data);
				     int id = obj.getInt("id");
				     obj.put("comments",Comments.getComments(id) );
				     jsonList.add(obj);
				 }
			
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
		} catch (IllegalStateException e) {
			resJSON.put("error", e.getMessage());
			return resJSON;
		} catch (UnsupportedEncodingException e) {
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
	public static JSONObject postImage(String username,HttpServletRequest request) {
	
		int userid=Database.getUserID(username);
		String galleryid=request.getParameter("galleryid");
		JSONObject resJSON=new JSONObject();
		if(galleryid==null || galleryid.equals("")) {
			resJSON.put("error", "invalid gallery id");
			return resJSON;
		}
		int glryid=Integer.parseInt(galleryid);
		int owner=Gallery.getOwner(glryid);
		try {
			conn=Database.getConnection();
			
				 if (userid!=owner) {
					 resJSON.put("error", "You are not the owner of this gallery");
						return resJSON;
				 }
				 System.out.println("Inserting records into the table...");
				 String insertString = "INSERT INTO IMAGES"
					+ "(GALLERY) VALUES"
					+ "(?)";
				 stmt = conn.prepareStatement(insertString);
				 stmt.setInt(1, glryid);
				 stmt.executeUpdate();
				 System.out.println("Inserted records into the table...");	
				 
			
		} catch (ClassNotFoundException e) {
			resJSON.put("error",e.getMessage());
			e.printStackTrace();
			return resJSON;
		} catch (SQLException e) {
			resJSON.put("error",e.getMessage());
			e.printStackTrace();
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
		
		
		
		
		String validtill=Integer.toString((int)(System.currentTimeMillis()/1000));
		List<Map> systems=FileServices.getAvaliableServicesSubset(2);
	
		Content content;
		for (Map system:systems) {
			System.out.println("Connecting with fs "+system.get("URL").toString());
		try {
			conn=Database.getConnection();
			Statement st = conn.createStatement();			
			ResultSet rs = st.executeQuery("SELECT MAX(IMAGEID) from IMAGES");			
			rs.next();	
			int lastid = rs.getInt(1);
			System.out.println(String.valueOf(lastid));
			Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
		    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
		    InputStream fileContent = filePart.getInputStream();
		    String hmac=Encryption.hmac(String.valueOf(lastid)+username+validtill, system.get("keybase64").toString());
		    StringBody stringBodyFilename = new StringBody(String.valueOf(lastid), ContentType.MULTIPART_FORM_DATA);
		    StringBody stringBodyUsername = new StringBody(username, ContentType.MULTIPART_FORM_DATA);
		    StringBody stringBodyValidtill = new StringBody(validtill, ContentType.MULTIPART_FORM_DATA);
		    StringBody stringBodyHMAC = new StringBody(hmac, ContentType.MULTIPART_FORM_DATA);
		    System.out.println(fileName);
		    System.out.println(username);
		    System.out.println(validtill);
		    System.out.println(hmac);
			HttpEntity entity = MultipartEntityBuilder.create()
			        .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
			        .setCharset(Charset.forName(CHARSET))
			        .addBinaryBody("file", fileContent, ContentType.MULTIPART_FORM_DATA, fileName)
			        .addPart("fileid", stringBodyFilename)
			        .addPart("userid", stringBodyUsername)
			        .addPart("validtill", stringBodyValidtill)
			        .addPart("hmac", stringBodyHMAC)
			        .build();
			
					content = Request.Post(system.get("URL").toString())
			        .connectTimeout(20000)
			        .socketTimeout(20000)
			        .body(entity)
			        .execute().returnContent();
			
					
					
					System.out.println(content.asString());
								
					
					JSONObject response=new JSONObject(content.asString());
					if(!response.getString("error").equals("")) {
				
						String deleteString = "DELETE FROM IMAGES WHERE IMAGEID = ?  ";
						stmt = conn.prepareStatement(deleteString);
						stmt.setInt(1, lastid);
						stmt.executeUpdate();
						return new JSONObject(content.asString());
					}
			
			System.out.println("Inserting records into the table...");
			 String insertString = "INSERT INTO FS_IMG"
				+ "(IMAGEID, FILESERVICEID) VALUES"
				+ "(?,?)";
			 stmt = conn.prepareStatement(insertString);
			 stmt.setInt(1, lastid);
			 stmt.setString(2, system.get("identifier").toString());
			 stmt.executeUpdate();
			 System.out.println("Inserted records into the table...");	
						
			
			
		} catch (IOException e) {			
			resJSON.put("error", e.getMessage());
			e.printStackTrace();
			return resJSON;
		} catch (ServletException e) {			
			resJSON.put("error", e.getMessage());
			e.printStackTrace();
			return resJSON;
		} catch (InvalidKeyException e) {		
			resJSON.put("error",e.getMessage());
			e.printStackTrace();
			return resJSON;
		} catch (NoSuchAlgorithmException e) {;
			resJSON.put("error",e.getMessage());
			e.printStackTrace();
			return resJSON;
		} catch (ClassNotFoundException e) {
			resJSON.put("error",e.getMessage());
			e.printStackTrace();
			return resJSON;
		} catch (SQLException e) {
			resJSON.put("error",e.getMessage());
			e.printStackTrace();
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
		resJSON.put("error","");
		return resJSON;	
}
	
	
	public static String getImageOwnerUsername(int imageid) {
		 try {
			conn=Database.getConnection();
		 
		 String selectString = "SELECT * FROM IMAGES "
		 		+ "INNER JOIN GALLERIES ON GALLERY = GALLERYID "
				 +"INNER JOIN USERS ON OWNER=USERID "
		 		+ "WHERE IMAGEID = ?"; 
		 stmt = conn.prepareStatement(selectString);
		 stmt.setInt(1, imageid);
		 ResultSet rs =stmt.executeQuery();
		 if (rs.next()) {
			 return rs.getString("USERNAME");
		 }
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return null;
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
		 return null;
		
	}
}



package images;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import api.Database;
import api.Friends;

public class Comments {
	static Connection conn=null;
	static PreparedStatement stmt=null;

	public static JSONObject postComment(String username, String imageid, String comment) {
		int userid=Database.getUserID(username);
		if(imageid==null || imageid.equals("")) {
			JSONObject resJSON = new JSONObject();
			resJSON.put("error","Invalid image id");
			return resJSON;
		}
		int imgid=Integer.parseInt(imageid);
		try {
			// Check if user (username) is friend with image owner or is the image owner
			int owner=Image.getImageOwner(imgid);
			 JSONObject resJSON=new JSONObject();
			if(userid==owner) {
			  conn=Database.getConnection();
			  String insertString = "INSERT INTO COMMENTS"
						+ "(TEXT, COMMENTER, IMAGE ) VALUES"
						+ "(?,?,?)";
			  stmt = conn.prepareStatement(insertString);
		      stmt.setString(1, comment);
		      stmt.setInt(2, userid);
		      stmt.setInt(3, imgid);
		      stmt.executeUpdate();
		      System.out.println("Comment successfully added");
			 resJSON.put("error", "");
			 return resJSON;				
			}
	
			 if(Friends.isFriend(owner, userid)) {
				 String insertString = "INSERT INTO COMMENTS"
							+ "(TEXT, COMMENTER, IMAGE ) VALUES"
							+ "(?,?,?)";
				  conn=Database.getConnection();
				  stmt = conn.prepareStatement(insertString);
			      stmt.setString(1, comment);
			      stmt.setInt(2, userid);
			      stmt.setInt(3, imgid);
			      stmt.executeUpdate();
			      System.out.println("Comment successfully added");
				 resJSON.put("error", "");
				 return resJSON;
			 }
			 resJSON.put("error", "You don't have access rights to this image");
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
	
	public static List<JSONObject> getComments(int imageid) {
		try {
			
			 conn=Database.getConnection();
			 String selectString = "SELECT * FROM COMMENTS "
					 +"INNER JOIN USERS ON COMMENTER = USERID"
			 		+ " WHERE IMAGE = ? ";
			 stmt = conn.prepareStatement(selectString);
			 stmt.setInt(1, imageid);
			 ResultSet rs =stmt.executeQuery();
			 
			 if(!rs.next()) {
				 return null;
			 }
			 rs.beforeFirst();
			 ArrayList<HashMap<String,String>> comments = new ArrayList<HashMap<String,String>>();
			 HashMap<String,String> comment = null;
			 while (rs.next()) {
				 comment = new HashMap<String,String>();
				 comment.put("commenter", rs.getString("USERNAME"));
				 comment.put("text", rs.getString("TEXT"));
				 comment.put("timestamp", rs.getTimestamp("TIMESTAMP").toString());
				 comments.add(comment);		 
			 }
			 List<JSONObject> jsonList = new ArrayList<JSONObject>();

			 for(HashMap<String, String> data : comments) {
			     JSONObject obj = new JSONObject(data);
			     jsonList.add(obj);
			 }
			 
			 return jsonList;
			 
		} catch ( SQLException e) {
			return null;
		} catch (ClassNotFoundException e) {
			JSONObject resJSON=new JSONObject();
			
			 resJSON.put("error", e.getMessage());
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
	}

}

package storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import api.Database;
import zookeeper.Configuration;

public class FileServices {
	static Connection conn=null;
	static PreparedStatement stmt=null;
	
	public static void updateDB(){
		try {
			
			conn=Database.getConnection();
			List<Map> systems=Configuration.getAvailableFs();
			String insertString = "INSERT INTO FILESERVICES"
					+ "(IDENTIFIER,URL,SHAREDKEY) VALUES"
					+ "(?,?,?)";
			String selectString="SELECT * FROM FILESERVICES "
					+ "WHERE IDENTIFIER =?";		
			for(Map fservice:systems) {
				stmt = conn.prepareStatement(selectString);
				stmt.setString(1, fservice.get("identifier").toString());
				System.out.println(stmt.toString());
				ResultSet rs =stmt.executeQuery();	 
				 //if service does not exist add it
				 if(!rs.next()) {
					 stmt = conn.prepareStatement(insertString);
				      stmt.setString(1, fservice.get("identifier").toString());
				      stmt.setString(2, fservice.get("URL").toString());
				      stmt.setString(3, fservice.get("keybase64").toString());
				      stmt.executeUpdate();
				 }	 
			}	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static  Map getFileService(String id) {				
		List<Map> systems=Configuration.getAvailableFs();
		for(Map fservice:systems) {
			System.out.println(fservice.get("identifier"));
			if (id.equals(fservice.get("identifier").toString())) {
				return fservice;
				}
		}
		return null;	
	}
	public static String getRandomID(int imageid) {
		try {
			conn=Database.getConnection();
			String selectString="SELECT * FROM FS_IMG "
					+ "INNER JOIN IMAGES ON FS_IMG.IMAGEID=IMAGES.IMAGEID "
					+ "INNER JOIN FILESERVICES ON FS_IMG.FILESERVICEID=FILESERVICES.IDENTIFIER"
					+ "WHERE IMAGES.IMAGEID =?";
			stmt = conn.prepareStatement(selectString);
			stmt.setInt(1, imageid);
			ResultSet rs=stmt.executeQuery();
			if(rs.next()){
				int rowcount = 0;
				if (rs.last()) {
					rowcount = rs.getRow();
				}
				int randomNum = ThreadLocalRandom.current().nextInt(1, rowcount + 1);
				rs.absolute(randomNum);
				return rs.getString("IDENTIFIER");
			}
			
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}

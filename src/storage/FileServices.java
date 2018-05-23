package storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
			System.out.println("Checking if identifier matches "+fservice.get("identifier"));
			if (id.equals(fservice.get("identifier").toString())) {
				return fservice;
				}
		}
		return null;	
	}	

	public static String getRandomAvailableServiceID(int imageid) {
		try {
			conn=Database.getConnection();
			String selectString="SELECT * FROM FS_IMG "
					+ "INNER JOIN IMAGES ON FS_IMG.IMAGEID=IMAGES.IMAGEID "
					+ "INNER JOIN FILESERVICES ON FS_IMG.FILESERVICEID=FILESERVICES.IDENTIFIER "
					+ "WHERE IMAGES.IMAGEID =?";
			stmt = conn.prepareStatement(selectString);
			stmt.setInt(1, imageid);
			ResultSet rs=stmt.executeQuery();
		
			if(rs.next()){
				int rowcount = 0;
				if (rs.last()) {
					rowcount = rs.getRow();
					System.out.println("servers containig the image:"+String.valueOf(rowcount));
				}
				List<Map> systems=Configuration.getAvailableFs();
				System.out.println("available servers:"+String.valueOf(systems.size()));
				rs.beforeFirst();
				List<String> identifiers=new ArrayList<String>();
				for(Map system:systems) {
					rs.beforeFirst();
					while(rs.next()) {
						if (rs.getString("IDENTIFIER").equals(system.get("identifier").toString())) {
							identifiers.add(rs.getString("IDENTIFIER"));
							rs.beforeFirst();
							break;
						}
					}
				}
				System.out.println("set of available servers containing the image:"+String.valueOf(identifiers.size()));
				if(identifiers.size()>0) {
					int randomNum = ThreadLocalRandom.current().nextInt(0, identifiers.size());
					System.out.println("Random index selected:"+String.valueOf(identifiers.size()));
					return identifiers.get(randomNum);
				}
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
	public static List<Map> getAvaliableServicesSubset(int noOfServices){
		List<Map> availableServices=Configuration.getAvailableFs();
		if(availableServices.size()<=2)
			return availableServices;
		List<Map> randomSet= new ArrayList<Map>();
		Set<String> tempset = new HashSet<String>();
		do {
			int randomNum = ThreadLocalRandom.current().nextInt(0, availableServices.size());
			if(tempset.add(String.valueOf(randomNum))) {
				randomSet.add(availableServices.get(randomNum));
			}
			
		}while(randomSet.size()<noOfServices);
		return randomSet;
	}
}



package storage;

import java.util.UUID;

public class StorageAPI {
	
	public static String getURL(int imageid) {
		return generateString();
	}
	
	
	
	 public static String generateString() {
	       String uuid = UUID.randomUUID().toString();
	       return "uuid = " + uuid;
	   }
	

}

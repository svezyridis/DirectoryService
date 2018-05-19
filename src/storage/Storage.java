package storage;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Storage {
	
	public static String getServiceURL(int imageid) {
		String identifier=FileServices.getRandomID(imageid);
		if (identifier!= null) {
			Map fileservice=FileServices.getFileService(identifier);
			String URL=fileservice.get("URL").toString();
			return "http://localhost:8080/FileService/FileServiceApi";
		}
		return "http://localhost:8080/FileService/FileServiceApi";
		
	}
	
	public static void getRandomFileServiceURL() {
		
	}
	public static String getURL(int imageid) {
		String serviceURL=getServiceURL(imageid);
		String userid="savvas";
		String validtill="now";	
		SecretKeySpec hks = new SecretKeySpec(Base64.getDecoder().decode("boubis12"), "HmacSHA256");
		Mac m;
		try {
			m = Mac.getInstance("HmacSHA256");
			m.init(hks);
			byte[] hmac = m.doFinal(Base64.getDecoder().decode(imageid+userid+validtill));
			String hmachash=Base64.getEncoder().encodeToString(hmac);
			String url=serviceURL+"?fileid="+imageid+"&userid="+userid+"&validtill="+validtill+"&hmac="+hmachash;
			return url;
		
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvalidKeyException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return null;
}
	
	
	 public static String generateString() {
	       String uuid = UUID.randomUUID().toString();
	       return "uuid = " + uuid;
	   }
	

}

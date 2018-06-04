package storage;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import images.Image;

public class Storage {
	
	public static Map getService(int imageid) {
		System.out.println("Searching service for image:"+String.valueOf(imageid));
		String identifier=FileServices.getRandomAvailableServiceID(imageid);
		System.out.println(identifier+" WAS CHOSEN");
		if (identifier!= null) {
			Map fileservice=FileServices.getFileService(identifier);
			
			return fileservice;
		}
		return null;
		
	}
	
	public static String getURL(int imageid) throws IllegalStateException, UnsupportedEncodingException {
		Map fileservice=getService(imageid);
		String serviceURL=fileservice.get("URL").toString();
		String userid=Image.getImageOwnerUsername(imageid);
		String validtill=String.valueOf((int)(System.currentTimeMillis()/1000));	
		SecretKeySpec hks = new SecretKeySpec(Base64.getDecoder().decode(fileservice.get("keybase64").toString()), "HmacSHA256");
		Mac m;
		try {
			m = Mac.getInstance("HmacSHA256");
			m.init(hks);
			byte[] hmac = m.doFinal((imageid+userid+validtill).getBytes("UTF-8"));
			String hmachash=Base64.getEncoder().encodeToString(hmac);
			hmachash=URLEncoder.encode(hmachash, "UTF-8");
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
	
	

}

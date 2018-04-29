package crypto;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Base64;

import org.json.JSONObject;

public class Token {
	public static JSONObject getDecryptedToken(String JSONString) throws GeneralSecurityException,UnsupportedEncodingException  {

		String sharedKeyBase64 = zookeeper.Zookeeper.getKey(); 
		byte[] sharedKey = Base64.getDecoder().decode(sharedKeyBase64);
		String decrypted = null;
		String cipher = "AES/CBC/PKCS5Padding";
		try {
			decrypted = Encryption.decrypt(JSONString,sharedKey,cipher);
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			throw e;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		JSONObject tokenjs= new JSONObject(decrypted);
		return tokenjs;
		
	}
	

}

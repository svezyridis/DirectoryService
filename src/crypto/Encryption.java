package crypto;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.*;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.*;
import javax.crypto.spec.*;


public class Encryption {
	public static String encrypt(String plainText, byte[] sharedKey, String cipher) throws GeneralSecurityException, java.io.UnsupportedEncodingException {
	    Cipher cipherSuite = Cipher.getInstance(cipher);
	    SecretKeySpec key = new SecretKeySpec(sharedKey, "AES");
		AlgorithmParameters params = cipherSuite.getParameters();
		byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
	    cipherSuite.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
	    byte[] ciphertextRaw = cipherSuite.doFinal(plainText.getBytes("UTF-8"));

		SecretKeySpec hks = new SecretKeySpec(sharedKey, "HmacSHA256");
		Mac m = Mac.getInstance("HmacSHA256");
		m.init(hks);
		byte[] hmac = m.doFinal(ciphertextRaw);

		return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(hmac) + ":" + Base64.getEncoder().encodeToString(ciphertextRaw);
	}


	public static String decrypt(String ciphertext, byte[] sharedKey, String cipher) throws GeneralSecurityException, java.io.UnsupportedEncodingException {
		String[] parts = ciphertext.split(":");
		byte[] iv = Base64.getDecoder().decode(parts[0]);
		byte[] hmacHash = Base64.getDecoder().decode(parts[1]);
		byte[] ciphertextRaw = Base64.getDecoder().decode(parts[2]);
		
		Cipher cipherSuite = Cipher.getInstance(cipher);
		SecretKeySpec key = new SecretKeySpec(sharedKey, "AES");

		cipherSuite.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		byte[] original_plaintextBytes = cipherSuite.doFinal(ciphertextRaw);

		SecretKeySpec hks = new SecretKeySpec(sharedKey, "HmacSHA256");
		Mac m = Mac.getInstance("HmacSHA256");
		m.init(hks);
		byte[] calcmac = m.doFinal(ciphertextRaw);
		
		if (Arrays.equals(hmacHash, calcmac))
		{
			return new String(original_plaintextBytes, "UTF-8");
		}
		return null;
	}
	
	public static String hmac(String plainText, String sharedkey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
		byte[] sharedKeyBytes=Base64.getDecoder().decode(sharedkey);
		SecretKeySpec hks = new SecretKeySpec(sharedKeyBytes, "HmacSHA256");
		Mac m = Mac.getInstance("HmacSHA256");
		m.init(hks);
		byte[] plainTextBytes=plainText.getBytes("UTF-8");
		byte[] hmac = m.doFinal(plainTextBytes);
		return Base64.getEncoder().encodeToString(hmac);
	}


}

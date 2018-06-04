package crypto;

public class Validator {
	public static boolean validatetime(int validtill) {
		if(((int)(System.currentTimeMillis()/1000)-validtill)>3600) {
			return false;
			
		}
		return true;
	}

}

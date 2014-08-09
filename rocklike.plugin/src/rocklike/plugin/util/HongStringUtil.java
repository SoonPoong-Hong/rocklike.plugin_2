package rocklike.plugin.util;

public class HongStringUtil {

	public static boolean isValidName(String n){
		if(n==null){
			return false;
		}
		if(n.length()==0){
			return false;
		}
		if(n.indexOf(" ")>-1){
			return false;
		}
		return true;
	}
}

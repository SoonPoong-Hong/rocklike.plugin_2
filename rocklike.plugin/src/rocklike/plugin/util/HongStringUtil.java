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


	public static String toCamelCase(String s){
		StringBuilder sb = new StringBuilder();
		int length = s.length();
		boolean shouldBeUpper = false;
		for(int i=0; i<length; i++){
			String thisChar = String.valueOf(s.charAt(i));
			if(thisChar.equals("_")){
				shouldBeUpper = true;
				continue;
			}

			if(shouldBeUpper){
				thisChar = thisChar.toUpperCase();
			}else{
				thisChar = thisChar.toLowerCase();
			}
			sb.append(thisChar);
			shouldBeUpper = false;
		}
		return sb.toString();
	}



//	public static void main(String[] args) {
//	    String s = null;
//
//	    s = "ab_cd";
//	    System.out.printf("%s => %s \n", s, toCamelCase(s));
//	    s = "ab_cd_e";
//	    System.out.printf("%s => %s \n", s, toCamelCase(s));
//	    s = "ab__cd";
//	    System.out.printf("%s => %s \n", s, toCamelCase(s));
//	    s = "aB_cd";
//	    System.out.printf("%s => %s \n", s, toCamelCase(s));
//	    s = "aB_Cd";
//	    System.out.printf("%s => %s \n", s, toCamelCase(s));
//	    System.out.println("====");
//
////	    s = " conts_id, conts_nm,    sers_id ";
////	    System.out.printf("%s => %s \n", s, new KtSqlColumnDecorator(s).decorateColumns());
////	    s = " conts_id, conts_nm,sers_id ";
////	    System.out.printf("%s => %s \n", s, new KtSqlColumnDecorator(s).decorateColumns());
//	    s = " conts_id, \nconts_nm,sers_id\n,ccc_xx ";
//	    System.out.printf("%s \n=> %s \n", s, new KtSqlColumnDecorator(s).decorateColumns());
//	    s = " CONTS_ID, \nCONTS_NM,SERS_ID\n,CCC_XX ";
//	    System.out.printf("%s \n=> %s \n", s, new KtSqlColumnDecorator(s).decorateColumns());
//
//    }
}

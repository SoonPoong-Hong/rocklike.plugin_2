package rocklike.plugin.util;

public class KtSqlColumnDecorator {

	private String sql;
	private boolean isColumn = false;
	private String column = null;

	public KtSqlColumnDecorator(String sql) {
	    super();
	    this.sql = sql;
    }

	public String decorateColumns(){
		int length = sql.length();
		StringBuilder sb = new StringBuilder();

		for(int i=0; i<length; i++){
			char c = sql.charAt(i);
			boolean wasColumn = isColumn;
			isColumn = isValid(c);
			if(c=='.'){
				sb.append(c);
				reset();
			}else if(isColumn){
				sb.append(c);
				appendColumnChar(c);
			}else{
				if(wasColumn){
					sb.append(toCamelCase(column));
					sb.append(c);
					reset();
				}else{
					sb.append(c);
				}
			}
		}
		if(isColumn){
			sb.append(toCamelCase(column));
		}

		return sb.toString();
	}


	private void reset(){
		isColumn = false;
		column = "";
	}

	private void appendColumnChar(char c){
		if(column==null){
			column = "";
		}
		column += c;
		isColumn = true;
	}


	private boolean isValid(char c){
		if( Character.isDigit(c) || Character.isLetter(c) || c=='_' ){
			return true;
		}else{
			return false;
		}
	}


	private String toCamelCase(String col){
		if(col==null || col.equals("")){
			return "";
		}
		return " \"" + HongStringUtil.toCamelCase(col) + "\"";
	}

//	public static void main(String[] args) {
//	    char c;
//	    c = 'a';
//	    System.out.printf("%s => %s \n", c, Character.isLetter(c));
//	    c = ',';
//	    System.out.printf("%s => %s \n", c, Character.isLetter(c));
//	    c = '.';
//	    System.out.printf("%s => %s \n", c, Character.isLetter(c));
//	    c = 'A';
//	    System.out.printf("%s => %s \n", c, Character.isLetter(c));
//	    c = '1';
//	    System.out.printf("%s => %s \n", c, Character.isLetter(c));
//	    c = '-';
//	    System.out.printf("%s => %s \n", c, Character.isLetter(c));
//	    c = '_';
//	    System.out.printf("%s => %s \n", c, Character.isLetter(c));
//    }
}

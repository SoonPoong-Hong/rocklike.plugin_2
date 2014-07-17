package rocklike.plugin.mybatis.highlight;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;

import rocklike.plugin.mybatis.highlight.scanner.HongColorProvider;

public class HongSqlScanner extends RuleBasedScanner{
	private static final String[] sql_keywords = {"select", "from", "order", "by", "asc", "des", "where"
												 , "update", "delete", "set"
												 , "insert", "null", "not"
												 , "union", "all"
												 , "inner","outer", "join"
												 , "left", "right", "connect", "prior", "like", "in"
												 , "having", "group"
												 , "and", "or", "in"
												 };
	private static final String[] oracle_keywords = {"to_date", "to_char", "to_number"};
	
	public HongSqlScanner(HongColorProvider cp){
		IToken oracleKeywords = new Token(new TextAttribute(cp.getColor(RgbConstants.KEYWORD_ORACLE)));
		IToken sqlKeywords = new Token(new TextAttribute(cp.getColor(RgbConstants.KEYWORD_ORACLE), null, SWT.BOLD));
		IToken quoatedStr = new Token(new TextAttribute(cp.getColor(RgbConstants.QUOTED_STRING)));
		IToken comment = new Token(new TextAttribute(cp.getColor(RgbConstants.COMMENT)));
		IToken defaults = new Token(new TextAttribute(cp.getColor(RgbConstants.DEFAULT)));
		
		List<IRule> rules = new ArrayList();
		
		WordRule wordRule = new WordRule(new HongWordDetector(), defaults, true);
		for(String w : sql_keywords){
			wordRule.addWord(w, sqlKeywords);
		}
		for(String w : oracle_keywords){
			wordRule.addWord(w, oracleKeywords);
		}
		rules.add(wordRule);
		
//		rules.add(new WhitespaceRule(new HongWhitespaceDetector()));
		
		rules.add(new SingleLineRule("'", "'", quoatedStr));
		rules.add(new SingleLineRule("\"", "\"", quoatedStr));
		
		rules.add(new EndOfLineRule("--", comment));
		rules.add(new MultiLineRule("/*", "*/", comment, (char)0, true ));
		
		setRules(rules.toArray(new IRule[0]));
		
//		setDefaultReturnToken(defaults);
	}

	
	static class HongWordDetector implements IWordDetector{

		@Override
		public boolean isWordStart(char c) {
			return Character.isLetter(c) || c=='_';
		}

		@Override
		public boolean isWordPart(char c) {
			return Character.isLetter(c) || c=='_';
		}
		
	}
	
	public static void main(String[] args) {
		char c ;
		c = '_';
		System.out.printf("%s  %s\n", c, Character.isLetter(c));
	}
}

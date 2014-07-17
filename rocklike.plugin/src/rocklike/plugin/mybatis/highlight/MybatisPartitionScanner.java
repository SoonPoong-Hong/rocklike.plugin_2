package rocklike.plugin.mybatis.highlight;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

public class MybatisPartitionScanner extends RuleBasedPartitionScanner {
	public static final String XML_CDATA = "__hong_xmlcdata";
	public static final String XML_COMMENT = "__hong_xmlcomment";
	public static final String XML_TAG = "__hong_xmltag";
	
	public static String[] getPartitionTypes(){
		return new String[]{XML_CDATA, XML_COMMENT, XML_TAG};
	}

	public MybatisPartitionScanner() {
		super();
		IToken multilineCdata = new Token(XML_CDATA);
		IToken multilineComment = new Token(XML_COMMENT);
		IToken multilineTag = new Token(XML_TAG);
		
		List<IPredicateRule> rules = new ArrayList();
		
		rules.add(new MultiLineRule("<![CDATA[", "]]>", multilineCdata, (char)0, true ));
		rules.add(new MultiLineRule("<!--", "-->", multilineComment, (char)0, true ));
		rules.add(new MultiLineRule("<", ">", multilineTag, (char)0, true ));
		
		setPredicateRules(rules.toArray(new IPredicateRule[0]));
	}
	
	
	
	
}

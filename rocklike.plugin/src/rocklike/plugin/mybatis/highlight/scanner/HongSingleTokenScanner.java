package rocklike.plugin.mybatis.highlight.scanner;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

public class HongSingleTokenScanner extends BufferedRuleBasedScanner {
	public HongSingleTokenScanner(TextAttribute textAttr){
		setDefaultReturnToken(new Token(textAttr));
	}
}

package rocklike.plugin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.w3c.dom.Element;

public class XmlElementBuilder {
	private IDOMDocument dom;
	private Map<String,String> attrs = new HashMap();
	private String content;
	private String tagName;

	public XmlElementBuilder(IDOMDocument dom, String tagName) {
	    this.dom = dom;
	    this.tagName = tagName;
    }

	public XmlElementBuilder addAttr(String key, String value) {
		attrs.put(key, value);
		return this;
	}

	public XmlElementBuilder setContent(String content) {
		this.content = content;
		return this;
	}

	public ElementImpl build(){
		ElementImpl elem = (ElementImpl) dom.createElement(tagName);
		Iterator<Entry<String, String>> iterator = attrs.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, String> attr = iterator.next();
			elem.setAttribute(attr.getKey(), attr.getValue());
		}
		if(content!=null){
			elem.appendChild( dom.createTextNode(content) );
		}

		return elem;
	}

}

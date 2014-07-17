package rocklike.plugin.jdt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class CachedMethodDeclarationHolder {
	// singleton
	private CachedMethodDeclarationHolder(){}
	private Map<IMethod, MethodDeclaration> cached = new HashMap();
	private static CachedMethodDeclarationHolder self = new CachedMethodDeclarationHolder();
	
	public static CachedMethodDeclarationHolder get(){
		return self;
	}
	
	public void clearCache(){
		cached.clear();
	}
	
	public void put(IMethod key, MethodDeclaration value){
		if(!cached.containsKey(key)){
			cached.put(key, value);
		}
	}
	
	public MethodDeclaration get(IMethod key){
		MethodDeclaration md = cached.get(key);
		if(md!=null){
			System.out.printf("== cache에서 가져옴. (%s) \n", key.getElementName());
			return md;
		}
		return null;
	}
}

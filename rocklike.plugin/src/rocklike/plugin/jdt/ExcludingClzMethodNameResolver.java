package rocklike.plugin.jdt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class ExcludingClzMethodNameResolver {

	private transient String excludingClz ;
	private transient List<String> excludingClzList = new ArrayList(2);
	private transient String excludingMethod;
	private transient List<String> excludingMethodList = new ArrayList(2);
	private transient boolean changed = true;
	private static ExcludingClzMethodNameResolver instance = new ExcludingClzMethodNameResolver();
	
	// singleton
	private ExcludingClzMethodNameResolver(){ }
	
	public static ExcludingClzMethodNameResolver get(){
		return instance;
	}

	public void setExcludingClz(String excludingClz) {
		this.excludingClz = excludingClz;
		this.changed = true;
	}

	public void setExcludingMethod(String excludingMethod) {
		this.excludingMethod = excludingMethod;
		this.changed = true;
	}
	
	public List<String> getExcludingClzList(){
		checkChanged();
		return excludingClzList;
	}
	
	public List<String> getExcludingMethodList(){
		checkChanged();
		return excludingMethodList;
	}

	private void checkChanged(){
		if(changed){
			changed = false;
			setList(excludingClzList, excludingClz);
			setList(excludingMethodList, excludingMethod);
		}
	}
	
	private void setList(List<String> list , String src){
		list.clear();
		if(src==null){
			return;
		}
		String[] arr = src.split(",");
		for(String e : arr){
			e = e.trim();
			list.add(e);
		}
	}
	
	public boolean isShouldBeExcluded(String clzNm, String methodNm){
//		System.out.printf("== cls,method : %s . %s \n", clzNm, methodNm);
		if(clzNm!=null && getExcludingClzList().contains(clzNm) ){
			return true;
		}
		if(methodNm!=null && getExcludingMethodList().contains(methodNm)){
			return true;
		}
		return false;
	}
	
	public boolean isShouldBeExcluded(MethodInvocation minv){
		if(minv==null){
			return true;
		}
		IMethodBinding mb = minv.resolveMethodBinding();
		if(mb==null){
			return true;
		}
		String clzNm = mb.getDeclaringClass().getName();
		String methodNm = mb.getName();
		return isShouldBeExcluded(clzNm, methodNm);
	}
}

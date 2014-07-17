package rocklike.plugin.jdt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class CacheSupportTypeHierarchyResolver {
	// singleton
	private CacheSupportTypeHierarchyResolver() {
	}
	private static CacheSupportTypeHierarchyResolver instance = new CacheSupportTypeHierarchyResolver();

	private Map<IJavaProject, Map<IType,ITypeHierarchy>> cache = new HashMap();
	
	public static CacheSupportTypeHierarchyResolver get() {
		return instance;
	}

	public void clearCache(){
		cache.clear();
	}
	
	public ITypeHierarchy resolvedCachedTypeHierarchy(IJavaProject proj, IType type) throws JavaModelException {
		if(proj==null || type==null){
			return null;
		}
		Map<IType, ITypeHierarchy> projCache = cache.get(proj);
		if(projCache!=null){
			ITypeHierarchy cachedObj = projCache.get(type);
			if(cachedObj!=null){
				return cachedObj;
			}
		}
		
		ITypeHierarchy result = type.newTypeHierarchy(proj, null);
		if(result!=null){
			Map<IType, ITypeHierarchy> map = cache.get(proj);
			if(map==null){
				map = new HashMap();
				cache.put(proj, map);
			}
			map.put(type, result);
		}
		return result;
	}
}

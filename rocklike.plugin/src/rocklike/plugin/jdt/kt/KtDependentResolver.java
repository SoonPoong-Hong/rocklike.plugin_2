package rocklike.plugin.jdt.kt;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import rocklike.plugin.util.HongJdtHelper;

public class KtDependentResolver {

	public static boolean isDaoImplClass(IType t){
		if(t==null){
			return false;
		}
		String fullName = t.getFullyQualifiedName();
		return fullName.startsWith("com.kt.cms") && fullName.indexOf(".dao.impl")>0;
	}

	public static boolean isSelectedClassIsDaoImpl() {
        try {
        	IType selType = HongJdtHelper.resolveSelectedType();
        	return isDaoImplClass(selType);
        } catch (JavaModelException e) {
	        e.printStackTrace();
	        return false;
        }
	}

	public static IType getDaoInterface(IType daoImpl) throws JavaModelException{
		if(!isDaoImplClass(daoImpl)){
			return null;
		}
		IType[] interfaces = HongJdtHelper.getInterfaces(daoImpl);
		for(IType t : interfaces){
			if(KtCorecmsResolver.isBelongsToCorecmsClass(t) && t.getFullyQualifiedName().indexOf(".dao.")>-1){
				return t;
			}
		}
		return null;
	}
}

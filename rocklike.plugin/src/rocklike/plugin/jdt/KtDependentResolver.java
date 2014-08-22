package rocklike.plugin.jdt;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import rocklike.plugin.jdt.kt.KtCorecmsResolver;
import rocklike.plugin.util.HongJdtHelper;

public class KtDependentResolver {

	public boolean isDaoImplClass(IType t){
		String fullName = t.getFullyQualifiedName();
		return fullName.startsWith("com.kt.cms") && fullName.indexOf(".dao.impl")>0;
	}

	public boolean isSelectedClassIsDaoImpl() throws JavaModelException{
		IType selType = HongJdtHelper.resolveSelectedType();
		return isDaoImplClass(selType);
	}

	public IType getDaoInterface(IType daoImpl) throws JavaModelException{
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

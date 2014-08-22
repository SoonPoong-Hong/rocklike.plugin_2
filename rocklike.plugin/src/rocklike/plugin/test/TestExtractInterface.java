package rocklike.plugin.test;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

public class TestExtractInterface {

	public void extractInterface(IType type) throws JavaModelException{
		ITypeHierarchy typeHierarchy = type.newTypeHierarchy(null);
		IType[] interfaces = typeHierarchy.getAllInterfaces();

		System.out.println(interfaces);
	}
}

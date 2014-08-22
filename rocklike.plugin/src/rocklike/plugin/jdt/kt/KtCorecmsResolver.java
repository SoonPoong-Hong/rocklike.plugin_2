package rocklike.plugin.jdt.kt;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import rocklike.plugin.util.HongEclipseUtil;
import rocklike.plugin.util.HongEditorHelper;
import rocklike.plugin.util.HongJdtHelper;

public class KtCorecmsResolver {

	public static boolean isBelongsToCorecmsClass(IType t){
		return t.getFullyQualifiedName().startsWith("com.kt.cms.")
			   || t.getFullyQualifiedName().startsWith("com.kt.bit.standard.header.v1.")
			   || t.getFullyQualifiedName().startsWith("com.extend.opencase")
				;
	}



}

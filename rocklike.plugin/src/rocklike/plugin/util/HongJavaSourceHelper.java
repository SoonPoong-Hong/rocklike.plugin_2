package rocklike.plugin.util;


import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class HongJavaSourceHelper {
	public void addImport(ICompilationUnit icu, String importStr) throws JavaModelException{
		icu.createImport(importStr, null, null);
	}


	public String addFieldIfNotExists(IType type, final String clzName, String fieldName, String contents) throws JavaModelException{
		if(clzName==null){
			return null;
		}
		ICompilationUnit icu = type.getCompilationUnit();
		
		final ObjectHolder<String> holderExistingName = new ObjectHolder();
		CompilationUnit cu = HongJdtHelper.getCompilationUnit(icu);
		cu.accept(new ASTVisitor() {
			@Override
			public boolean visit(FieldDeclaration node) {
				if(holderExistingName.get()!=null){
					return false;
				}
				String thisClzName = node.getType().resolveBinding().getName();
				if(clzName.equals(thisClzName)){
					// 기존에 존재.
					Object obj = node.fragments().get(0);
					if(obj instanceof VariableDeclarationFragment){
						VariableDeclarationFragment varDel = (VariableDeclarationFragment) obj;
						String varName = varDel.getName().toString();
						holderExistingName.put(varName) ;
					}
				}
				return true;
			}
		});
		
		if(holderExistingName.get()!=null){
			fieldName = holderExistingName.get();
		}
		
	
		type.createField(contents, null, true, null);
		
		return fieldName;
	}

	
	public boolean hasSameMethodName(IType type, String methodName) throws JavaModelException{
		IMethod[] methods = type.getMethods();
		for(IMethod m : methods){
			if(methodName.equals(m.getElementName())){
				return true;
			}
		}
		return false;
	}
	
	
	public void addMethod(IType type, String contents) throws JavaModelException{
		type.createMethod(contents, null, true, null);
	}
}

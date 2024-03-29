package rocklike.plugin.util;


import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class HongJavaSourceHelper {

	public static void addImport(ICompilationUnit icu, String fullClzName) throws JavaModelException{
		if(fullClzName.startsWith("java.lang.") || fullClzName.indexOf(".")<0){
			return;
		}
		icu.createImport(fullClzName, null, null);
	}


	public static void addImportIfNotExists(ICompilationUnit icu, final String fullClzName) throws JavaModelException{
		if(fullClzName==null || fullClzName.equals("") || fullClzName.startsWith("java.lang.") || fullClzName.indexOf(".")<0 ){
			return;
		}

		CompilationUnit cu = HongJdtHelper.getCompilationUnit(icu);
		final ObjectHolder<String> holder = new ObjectHolder();
		cu.accept(new ASTVisitor(){
			@Override
            public boolean visit(ImportDeclaration node) {
				String name = node.resolveBinding().getJavaElement().getElementName();
				if(name.equals(fullClzName)){
					holder.put("Y");
					return false;
				}
				return true;
            }
		});

		if(holder.get()==null){
			icu.createImport(fullClzName, null, null);
		}
	}


	public static String addFieldIfNotExists(IType type, final String clzName, String fieldName, String contents) throws JavaModelException{
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


	public static boolean hasSameMethodName(IType type, String methodName) throws JavaModelException{
		IMethod[] methods = type.getMethods();
		for(IMethod m : methods){
			if(methodName.equals(m.getElementName())){
				return true;
			}
		}
		return false;
	}


	public static IMethod addMethod(IType type, String contents) throws JavaModelException{
		return type.createMethod(contents, null, true, null);
	}
}

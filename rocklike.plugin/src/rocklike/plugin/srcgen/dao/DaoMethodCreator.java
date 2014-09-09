package rocklike.plugin.srcgen.dao;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IDocument;

import rocklike.plugin.jdt.kt.KtDependentResolver;
import rocklike.plugin.srcgen.freemarker.DaoMethodGenerator;
import rocklike.plugin.util.HongJavaSourceHelper;
import rocklike.plugin.util.HongMessagePopupUtil;
import rocklike.plugin.util.HongMybatisHelper;

public class DaoMethodCreator {

	public static void addMethodInDaoImpl(GenDaoMethodRequest request){
		DaoMethodVO v = new DaoMethodVO();
		v.setMethodName(request.methodName);
		v.setOneListOption(request.oneListOption.name());
		v.setParameterType(request.parameterType);
		v.setResultType(request.resultType);
		v.setSelectUpdateOption(request.selectUpdateOption.name());
		v.setXmlId(HongMybatisHelper.assumeMybatisQueryXmlPackageId(request.type)+"." + request.methodName );

		IType t = request.type;
		ICompilationUnit icu = t.getCompilationUnit();

		String method;

		method = DaoMethodGenerator.genDaoImplMethod(v);
		try {
	        HongJavaSourceHelper.addImportIfNotExists(icu, request.parameterType);
        } catch (JavaModelException e) {
	        e.printStackTrace();
        }
		try {
	        HongJavaSourceHelper.addImportIfNotExists(icu, request.resultType);
        } catch (JavaModelException e) {
	        e.printStackTrace();
        }
		try {
	        IMethod m = HongJavaSourceHelper.addMethod(t, method);
	        JavaUI.openInEditor(m);
	        icu.save(null, true);
//	        icu.commitWorkingCopy(false, null);
        } catch (Exception e) {
	        e.printStackTrace();
        }
	}


	public static void addMethodInDao(GenDaoMethodRequest request){
		DaoMethodVO v = new DaoMethodVO();
		v.setMethodName(request.methodName);
		v.setOneListOption(request.oneListOption.name());
		v.setParameterType(request.parameterType);
		v.setResultType(request.resultType);
		v.setSelectUpdateOption(request.selectUpdateOption.name());
		v.setXmlId(HongMybatisHelper.assumeMybatisQueryXmlPackageId(request.type)+"." + request.methodName );

		IType t = null;
        try {
	        t = KtDependentResolver.getDaoInterface( request.type );
        } catch (JavaModelException e) {
	        e.printStackTrace();
	        HongMessagePopupUtil.showErrMsg(e);
	        return;
        }
		ICompilationUnit icu = t.getCompilationUnit();

		String method;

		method = DaoMethodGenerator.genDaoMethod(v);
		try {
			HongJavaSourceHelper.addImportIfNotExists(icu, request.parameterType);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		try {
			HongJavaSourceHelper.addImportIfNotExists(icu, request.resultType);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		try {
			IMethod m = HongJavaSourceHelper.addMethod(t, method);
			JavaUI.openInEditor(m);
			icu.save(null, true);
//	        icu.commitWorkingCopy(false, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

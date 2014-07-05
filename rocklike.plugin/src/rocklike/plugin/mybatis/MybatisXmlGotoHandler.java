package rocklike.plugin.mybatis;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;

import rocklike.plugin.util.HongEclipseUtil;
import rocklike.plugin.util.HongJdtHelper;
import rocklike.plugin.util.HongMybatisHelper;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class MybatisXmlGotoHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public MybatisXmlGotoHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IProject proj = HongEclipseUtil.getSelectedProject();
		
		ICompilationUnit icu = HongJdtHelper.getSelectedICompilationUnit();
		CompilationUnit cu = HongJdtHelper.getCompilationUnit(icu);
		
		ISelection sel = HongEclipseUtil.getSelection();
		if(sel instanceof ITextSelection){
			ITextSelection ts = (ITextSelection)sel;
			MethodInvocation mi = HongJdtHelper.resolveMethodInvocationByPosition(cu, ts.getOffset()+ts.getLength());
			if(mi!=null){
				ITypeBinding tb = mi.resolveMethodBinding().getDeclaringClass();
				IFile interfaceFile = (IFile)tb.getJavaElement().getResource();
				IFile implFile = HongMybatisHelper.assumeDaoImplPath(proj, interfaceFile);
				if(implFile==null){
					return null;
				}
				
				CompilationUnit implCu = HongJdtHelper.getCompilationUnit(implFile);
				MethodDeclaration implMethod = HongJdtHelper.resolveMethodInImpl(implCu, mi.resolveMethodBinding());
				if(implMethod!=null){
					HongMybatisHelper.openMybatisXmlFile(proj, implMethod);
				}
			}
		}
		
		return null;
	}
	
	
	
//	private IFile assumeImplPath(IProject proj, IFile f){
//		IPath p = f.getProjectRelativePath();
//		String path = p.toString();
//		int pos = path.lastIndexOf("/");
//		path = path.substring(0, pos) + "/impl" + path.substring(pos);
//		path = path.replaceFirst("\\.java", "Impl.java");
//		return proj.getFile(path);
//	}
//	
}

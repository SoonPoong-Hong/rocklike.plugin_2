package rocklike.plugin.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ide.ResourceUtil;

public class HongJdtHelper {

	public static IJavaProject getActiveEditorsJavaProject(){
		IFile ifile = ResourceUtil.getFile(HongEclipseUtil.getActiveEditor().getEditorInput());
		IProject proj = ifile.getProject();
		if(proj!=null){
			IJavaProject javaProject = JavaCore.create(proj);
			return javaProject;
		}
		return null;
	}
	
	
	public static ICompilationUnit getSelectedICompilationUnit(){
		ISelection sel = HongEclipseUtil.getSelection();
		if(sel instanceof ITextSelection){
			IFile ifile = ResourceUtil.getFile(HongEclipseUtil.getActiveEditor().getEditorInput());
			return JavaCore.createCompilationUnitFrom(ifile);
		}else if(sel instanceof IStructuredSelection){
			IStructuredSelection ss = (IStructuredSelection)sel;
			Object selObj = ss.getFirstElement();
			if(selObj instanceof ICompilationUnit){
				return (ICompilationUnit)selObj;
			}else if(selObj instanceof IAdaptable){
				return (ICompilationUnit)((IAdaptable)selObj).getAdapter(ICompilationUnit.class);
			}
		}
		return null;
	}
	
	public static CompilationUnit getCompilationUnit(IFile f){
		return getCompilationUnit( JavaCore.createCompilationUnitFrom(f) );
		
	}
	
	public static CompilationUnit getCompilationUnit(ICompilationUnit icu){
		ASTParser p = ASTParser.newParser(AST.JLS4);
		p.setSource(icu);
		p.setKind(ASTParser.K_COMPILATION_UNIT);
		p.setResolveBindings(true);
		p.setBindingsRecovery(true);
		return (CompilationUnit)p.createAST(null);
	}
}

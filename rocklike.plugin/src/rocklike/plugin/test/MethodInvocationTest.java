package rocklike.plugin.test;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ide.ResourceUtil;

import rocklike.plugin.util.HongEclipseUtil;
import rocklike.plugin.util.HongJdtHelper;
import rocklike.plugin.util.HongMessagePopupUtil;
import rocklike.plugin.util.ObjectHolder;

public class MethodInvocationTest {

	public void getParameterFromMethodInvocation( ){
		MethodInvocation mi = HongJdtHelper.getSelectedElementOfMethodInvocation();
		if(mi==null){
			HongMessagePopupUtil.showErrMsg("Setter/Getter 메소드 부분에 커서를 두고서 하세요.");
			return ;
		}
		mi.resolveTypeBinding().getName();
		IMethodBinding mb = mi.resolveMethodBinding();
		IMethodBinding mb2 = mb.getMethodDeclaration();
		System.out.println();
	}

	public void visit(){

		ISelection sel = HongEclipseUtil.getSelection();
		final ObjectHolder<MethodInvocation> oh = new ObjectHolder();
		if(sel instanceof ITextSelection){
			final ITextSelection ts = (ITextSelection)sel;
			final int pos = ts.getOffset() + ts.getLength();
			IFile ifile = ResourceUtil.getFile(HongEclipseUtil.getActiveEditor().getEditorInput());
			ICompilationUnit icu = JavaCore.createCompilationUnitFrom(ifile);
			CompilationUnit cu = HongJdtHelper.getCompilationUnit(icu);
			cu.accept(new ASTVisitor(){

				@Override
                public boolean visit(MethodInvocation node) {
					if(node.getStartPosition()<=pos && pos<node.getStartPosition()+node.getLength()){
						oh.put(node);
					}
					return true;
                }
				
			});
		}

		MethodInvocation mi = oh.get();
//		getMethodInvocation(mi);

		System.out.printf("== caller name : %s \n", HongJdtHelper.getCallerObjectName(mi));
		ITypeBinding resolveTypeBinding = mi.getExpression().resolveTypeBinding();
		
		List<IMethodBinding> gatherDeclaredMethods = HongJdtHelper.gatherDeclaredMethods(mi);
		
		System.out.println("===");
	}

	void getMethodInvocation(MethodInvocation parent){
		parent.accept(new ASTVisitor() {
			int cnt = 0;
			@Override
            public boolean visit(MethodInvocation node) {
				System.out.println("==> " + node);
				System.out.println(" caller : " + HongJdtHelper.getCallerObjectName(node));
				getCaller(node);
	            return super.visit(node);
            }

		});
	}
	
	void getCaller(MethodInvocation parent){
		Expression exp = parent.getExpression();
		ITypeBinding tb = exp.resolveTypeBinding();
		System.out.println(exp);
	}
}

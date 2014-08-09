package rocklike.plugin.util;




import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.ResourceUtil;

import rocklike.plugin.jdt.CacheSupportCompilationUnit;
import rocklike.plugin.jdt.CacheSupportTypeHierarchyResolver;
import rocklike.plugin.jdt.CachedMethodDeclarationHolder;

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
	
	
	public static MethodInvocation getSelectedElementOfMethodInvocation(){
		ISelection sel = HongEclipseUtil.getSelection();
		final ObjectHolder<MethodInvocation> oh = new ObjectHolder();
		if(sel instanceof ITextSelection){
			final ITextSelection ts = (ITextSelection)sel;
			final int pos = ts.getOffset() + ts.getLength();
			IFile ifile = ResourceUtil.getFile(HongEclipseUtil.getActiveEditor().getEditorInput());
			ICompilationUnit icu = JavaCore.createCompilationUnitFrom(ifile);
			CompilationUnit cu = getCompilationUnit(icu);
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
		return oh.get();
	}
	
	
	

	public static MethodInvocation resolveMethodInvocationByPosition(CompilationUnit cu, int pos){
		MethodInvocationResolverByPosition v = new MethodInvocationResolverByPosition(cu, pos);
		cu.accept(v);
		return v.holder.get();
	}
	
	public static MethodInvocation resolveMethodInvocationByPosition2(CompilationUnit cu,int pos){
		 IJavaElement srcJavaElement = null;
        try {
	        srcJavaElement = ((ICompilationUnit)cu.getJavaElement()).getElementAt(pos);
        } catch (JavaModelException e) {
	        e.printStackTrace();
        }
		ASTNode srcNode = null;
		if(srcJavaElement instanceof IMethod){
			srcNode = cu.findDeclaringNode(((IMethod)srcJavaElement).getKey());
		}else if(srcJavaElement instanceof IField){
			srcNode = cu.findDeclaringNode(((IField)srcJavaElement).getKey());
		}else if(srcJavaElement instanceof IType){
			srcNode = cu.findDeclaringNode(((IType)srcJavaElement).getKey());
		}
		
		MethodInvocationResolverByPosition v;
		
		if(srcNode!=null){
			v = new MethodInvocationResolverByPosition(srcNode, pos);
		}else{
			v = new MethodInvocationResolverByPosition(cu, pos);
		}
		cu.accept(v);
		return v.holder.get();
	}

	private static class MethodInvocationResolverByPosition extends ASTVisitor{
		private ASTNode cu;
		private ObjectHolder<MethodInvocation> holder = new ObjectHolder();
		private int pos ;

		private MethodInvocationResolverByPosition(ASTNode cu, int pos) {
			super();
			this.cu = cu;
			this.pos = pos;
		}

		@Override
		public boolean visit(MethodInvocation node) {
			// [주의] 안에 있는 메소드까지 다 검사해야 하기 때문에, 발견했다고 false를 리턴하면 안됨.
			int start = node.getStartPosition();
			int end = start + node.getLength();
			if(start<=pos && pos<end){
				holder.put(node);
			}
			return true;
		}
	}
	
	

	
	public static MethodDeclaration resolveImplMethodOfDirect(IMethod interfaceMethod){
		if(interfaceMethod==null){
			return null;
		}
		IType mtype = interfaceMethod.getDeclaringType();
		try {
			ITypeHierarchy th = mtype.newTypeHierarchy(interfaceMethod.getJavaProject(), null);
			IType[] types = th.getSubtypes(mtype);
			for(IType t : types){
				MethodDeclaration md = resolveMethodInImpl(HongJdtHelper.getCompilationUnit(t.getCompilationUnit()), interfaceMethod);
				if(md!=null){
					return md;
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static MethodDeclaration resolveImplMethodOfDirectOfCached(IMethod interfaceMethod){
		if(interfaceMethod==null){
			return null;
		}
		IType mtype = interfaceMethod.getDeclaringType();
		try {
			long start = System.currentTimeMillis();
			long end = 0;
			ITypeHierarchy th =   CacheSupportTypeHierarchyResolver.get().resolvedCachedTypeHierarchy(interfaceMethod.getJavaProject(), mtype);
			IType[] types = th.getSubtypes(mtype);
			for(IType t : types){
				MethodDeclaration md = resolveMethodInImpl(HongJdtHelper.getCompilationUnit(t.getCompilationUnit()), interfaceMethod);
				if(md!=null){
					return md;
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MethodDeclaration resolveMethodInImpl(ASTNode implCu, IMethod searchMethod){
		if(implCu==null || searchMethod==null){
			return null;
		}
		MethodResolverByNameAndParameter v = new MethodResolverByNameAndParameter(implCu, searchMethod);
		implCu.accept(v);
		return v.holder.get();
	}
	
	public static MethodDeclaration resolveMethodInImpl(ASTNode implCu, IMethodBinding searchMethodBinding){
		if(implCu==null || searchMethodBinding==null){
			return null;
		}
		MethodResolverByNameAndParameter v = new MethodResolverByNameAndParameter(implCu, (IMethod)searchMethodBinding.getJavaElement());
		implCu.accept(v);
		return v.holder.get();
	}
	
	private static class MethodResolverByNameAndParameter extends ASTVisitor{
		private ObjectHolder<MethodDeclaration> holder = new ObjectHolder();
		private IMethod intfMethod;
		private boolean finished = false;
		private ASTNode cu;
		
		public MethodResolverByNameAndParameter(ASTNode cu, IMethod interfaceMethod) {
			super();
			this.intfMethod = interfaceMethod;
			this.cu = cu;
		}
		
		@Override
		public boolean visit(MethodDeclaration node) {
			if(finished){
				return false;
			}
			// 메소드명이 똑같고, 파라미터 타입도 똑같을때
			if(intfMethod.getElementName().equals(node.resolveBinding().getName())){
				String[] intfParams = intfMethod.getParameterTypes(); 
				String[] thisParams = ((IMethod)(node.resolveBinding().getJavaElement())).getParameterTypes(); 
				if(Arrays.equals(intfParams, thisParams)){
					holder.put(node);
					finished = true;
					return false;
				}
			}
			return super.visit(node);
		}
	}
	
	

	public static IFolder assumeJavaSrcFolder(IJavaProject jp) throws JavaModelException {
		IClasspathEntry[] cps = jp.getRawClasspath();
		IWorkspaceRoot root = HongEclipseFileHelper.getWorkspaceRoot();
		
		List<IFolder> candidates = new ArrayList(2);
		
		for(IClasspathEntry e : cps){
			if(e.getEntryKind()!=IClasspathEntry.CPE_SOURCE){
				continue;
			}
			IFolder f = root.getFolder(e.getPath());
			candidates.add(f);
		}
		
		if(candidates.size()==1){
			return candidates.get(0);
		}
		
		// main/java 가 있으면 그게 우선.
		for(IFolder f : candidates){
			if(f.toString().indexOf("src/main/java")!=-1){
				return f;
			}
		}
		
		// 그외는 젤 첫번째 걸로 그냥 씀
		if(candidates.size()>0){
			return candidates.get(0);
		}
		
		return null;
	}
	
	
	
	

	
	public static  MethodDeclaration getSelectedElementOfMethodDeclaration(){
		ISelection sel = HongEclipseUtil.getSelection();
		final ObjectHolder<MethodDeclaration> oh = new ObjectHolder();
		if(sel instanceof ITextSelection){
			final ITextSelection ts = (ITextSelection)sel;
			final int pos = ts.getOffset() + ts.getLength();
			IFile ifile = ResourceUtil.getFile(HongEclipseUtil.getActiveEditor().getEditorInput());
			ICompilationUnit icu = JavaCore.createCompilationUnitFrom(ifile);
			CompilationUnit cu = HongJdtHelper.getCompilationUnit(icu);
			cu.accept(new ASTVisitor(){
				@Override
                public boolean visit(MethodDeclaration node) {
					if(node.getStartPosition()<=pos && pos<node.getStartPosition()+node.getLength()){
						oh.put(node);
					}
					return true;
                }
			});
		}
		return oh.get();
	}
	
	
	/**
	 * 메소드 invocation이라면 그 invocation의 declaration을 쓰고, 아니면, 해당 메소드 declaration을 가져온다.
	 * @return
	 */
	public static  MethodDeclaration resolveSelectedMethodDeclarationRegardingMethodInvocation(){
		MethodInvocation mi = getSelectedElementOfMethodInvocation();
		if(mi!=null){
			// 메소드 호출일때
			// 먼저 impl에서 찾고
			MethodDeclaration md ;
			IMethod m = (IMethod) mi.resolveMethodBinding().getJavaElement();
			md = resolveImplMethodOfDirect(m);
			if(md!=null){
				return md;
			}
			
			// impl에 없으면 자신에게서 찾기
			CompilationUnit cu = getCompilationUnit((IFile)m.getResource());
			md = resolveMethodInImpl(cu, m );
			if(md!=null){
				return md;
			}
			
		}else{
			// 메소드 호출이 아니면, 그것을 둘러싸는 메소드 invocation을 리턴
			MethodDeclaration md = getSelectedElementOfMethodDeclaration();
			if(md!=null){
				return md;
			}
		}
		
		return null;
	}
	
	
	public static MethodDeclaration getSelectedElementOfMethodInvocationRegardingInterface(){
		MethodInvocation mi = getSelectedElementOfMethodInvocation();
		if(mi==null){
			return null;
		}
		
		// 먼저 impl에서 찾고
		MethodDeclaration md ;
		IMethod m = (IMethod) mi.resolveMethodBinding().getJavaElement();
		md = resolveImplMethodOfDirect(m);
		if(md!=null){
			return md;
		}

		// impl에 없으면 자신에게서 찾기
		CompilationUnit cu = getCompilationUnit((IFile)m.getResource());
		md = resolveMethodInImpl(cu, m );
		if(md!=null){
			return md;
		}
		
		return null;
	}
	
	
	public  MethodDeclaration getSelectedElementOfMethodDeclarationAdvanced() throws JavaModelException{
		ISelection sel = HongEclipseUtil.getSelection();
		final ObjectHolder<MethodDeclaration> oh = new ObjectHolder();
		if(sel instanceof ITextSelection){
			final ITextSelection ts = (ITextSelection)sel;
			final int pos = ts.getOffset() + ts.getLength();
			IFile ifile = ResourceUtil.getFile(HongEclipseUtil.getActiveEditor().getEditorInput());
			ICompilationUnit icu = JavaCore.createCompilationUnitFrom(ifile);
			IJavaElement je = icu.getElementAt(pos);
			if(!(je instanceof IMethod)){
				return null;
			}
			IMethod m = (IMethod)je;
			CompilationUnit cu = HongJdtHelper.getCompilationUnit(icu);
			MethodDeclaration md = (MethodDeclaration) cu.findDeclaringNode(m.getKey());
			
			if(md==null){
				return null;
			}
			
			md.accept(new ASTVisitor(){
				@Override
				public boolean visit(MethodDeclaration node) {
					if(node.getStartPosition()<=pos && pos<node.getStartPosition()+node.getLength()){
						oh.put(node);
					}
					return true;
				}
			});
		}
		return oh.get();
	}
	
	
	
	//=========== 추가할 소스
	
	public static  MethodDeclaration resolveMethodDeclaration(MethodInvocation mi){
		if(mi!=null){
			// 메소드 호출일때
			// 먼저 impl에서 찾고
			MethodDeclaration md ;
			IMethod m = (IMethod) mi.resolveMethodBinding().getJavaElement();
			md = resolveImplMethodOfDirect(m);
			if(md!=null){
				return md;
			}
			
			// impl에 없으면 자신에게서 찾기
			CompilationUnit cu = getCompilationUnit((IFile)m.getResource());
			md = resolveMethodInImpl(cu, m );
			if(md!=null){
				return md;
			}
		}
		return null;
	}
	
	
	public static  MethodDeclaration resolveMethodDeclarationCached(MethodInvocation mi){
		if(mi!=null){
			// 메소드 호출일때
			// 먼저 impl에서 찾고
			MethodDeclaration md ;
			IMethod m = (IMethod) mi.resolveMethodBinding().getJavaElement();
			
			MethodDeclaration cachedMD = CachedMethodDeclarationHolder.get().get(m);
			if(cachedMD!=null){
				return cachedMD;
			}
			
			md = resolveImplMethodOfDirectOfCached(m);
			
			if(md!=null){
				CachedMethodDeclarationHolder.get().put(m, md);
				return md;
			}
			
			// impl에 없으면 자신에게서 찾기
			CompilationUnit cu = CacheSupportCompilationUnit.get().getCompilationUnit((IFile)m.getResource());
			md = resolveMethodInImpl(cu, m );
			if(md!=null){
				CachedMethodDeclarationHolder.get().put(m, md);
				return md;
			}
		}
		return null;
	}

	
	public static void openInJavaEditor(IJavaElement javaElement) throws PartInitException, JavaModelException{
		JavaUI.openInEditor(javaElement);
	}
	
}

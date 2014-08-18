package rocklike.plugin.jdt;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ide.ResourceUtil;

import rocklike.plugin.jdt.viewer.MethodCallTreeViewer;
import rocklike.plugin.util.HongEclipseUtil;
import rocklike.plugin.util.HongJdtHelper;

public class CallingMethodExtractor {

	public static void extractCallingMethodsAndOpen() throws JavaModelException{

		// 먼저 clear cache.
		CacheSupportCompilationUnit.get().clearCache();
		CachedMethodDeclarationHolder.get().clearCache();


		MethodInvocation mi = HongJdtHelper.getSelectedElementOfMethodInvocation();
		int pos = HongEclipseUtil.getTextSelection().getOffset();
		if(mi!=null){
			// 메소드를 호출한 부
			final MethodInvocationNode rootNode = new MethodInvocationNode(null, mi);
			IFile rootFile = ResourceUtil.getFile(HongEclipseUtil.getActiveEditor().getEditorInput());
			rootNode.setRootFile(rootFile);
			rootNode.setRootFilePos(pos);

			extractCallingMethods(rootNode);
			Display.getDefault().syncExec(new Runnable(){
				@Override
                public void run() {
					final MethodCallTreeViewer v  = (MethodCallTreeViewer)HongEclipseUtil.showView(MethodCallTreeViewer.ID);
					v.refreshContents(rootNode);
                }
			});
			return;
		}

		MethodDeclaration md = HongJdtHelper.getSelectedElementOfMethodDeclaration();
		if(md!=null){
			// 메소드 구현부 전체에 대해서,
			IFile rootFile = ResourceUtil.getFile(HongEclipseUtil.getActiveEditor().getEditorInput());
			final MethodInvocationNode rootNode = new MethodInvocationNode(null, null);
			rootNode.setMethodDeclaration(md);
			rootNode.setRootFile(rootFile);
			rootNode.setRootFilePos(pos);

			List<MethodInvocation> methodInvs = MeaningfulMethodInvocationResolver.extractMeaningfulMethodInvocations(md);
			for(MethodInvocation minv : methodInvs){
				MethodInvocationNode newChild = new MethodInvocationNode(rootNode, minv);
				rootNode.addChild(newChild);
				extractCallingMethods(newChild);
			}
			Display.getDefault().syncExec(new Runnable(){
				@Override
                public void run() {
					final MethodCallTreeViewer v  = (MethodCallTreeViewer)HongEclipseUtil.showView(MethodCallTreeViewer.ID);
					v.refreshContents(rootNode);
                }
			});
			return;
		}

	}



	public static void extractCallingMethodsAndOpen(CompilationUnit cu, int pos) throws JavaModelException{

		// 먼저 clear cache.
		CacheSupportCompilationUnit.get().clearCache();
		CachedMethodDeclarationHolder.get().clearCache();

		MethodInvocation mi = HongJdtHelper.resolveMethodInvocationByPosition(cu, pos);
		if(mi!=null){
			// 메소드를 호출한 부
			final MethodInvocationNode rootNode = new MethodInvocationNode(null, mi);
			IFile rootFile = (IFile)((ICompilationUnit)cu.getJavaElement()).getResource();
			rootNode.setRootFile(rootFile);
			rootNode.setRootFilePos(pos);

			extractCallingMethods(rootNode);
			Display.getDefault().syncExec(new Runnable(){
				@Override
				public void run() {
					final MethodCallTreeViewer v  = (MethodCallTreeViewer)HongEclipseUtil.showView(MethodCallTreeViewer.ID);
					v.refreshContents(rootNode);
				}
			});
			return;
		}

		MethodDeclaration md = HongJdtHelper.resolveMethodDeclarationByPosition(cu, pos);
		if(md!=null){
			// 메소드 구현부 전체에 대해서,
			IFile rootFile = (IFile)((ICompilationUnit)cu.getJavaElement()).getResource();
			final MethodInvocationNode rootNode = new MethodInvocationNode(null, null);
			rootNode.setMethodDeclaration(md);
			rootNode.setRootFile(rootFile);
			rootNode.setRootFilePos(pos);

			List<MethodInvocation> methodInvs = MeaningfulMethodInvocationResolver.extractMeaningfulMethodInvocations(md);
			for(MethodInvocation minv : methodInvs){
				MethodInvocationNode newChild = new MethodInvocationNode(rootNode, minv);
				rootNode.addChild(newChild);
				extractCallingMethods(newChild);
			}
			Display.getDefault().syncExec(new Runnable(){
				@Override
				public void run() {
					final MethodCallTreeViewer v  = (MethodCallTreeViewer)HongEclipseUtil.showView(MethodCallTreeViewer.ID);
					v.refreshContents(rootNode);
				}
			});
			return;
		}

	}



	public static void extractCallingMethods(MethodInvocationNode methodInvNode){

		long start = System.currentTimeMillis();
		long end=0;
		if(methodInvNode==null || methodInvNode.getMethodInvocation()==null || methodInvNode.isProcessed() ){
			return;
		}
//		System.out.printf("====== 시작. (%s) \n", methodInvNode.getMethodInvocation().getName());
		MethodInvocation thisMethodInv = methodInvNode.getMethodInvocation();
		MethodDeclaration md = HongJdtHelper.resolveMethodDeclarationCached(thisMethodInv);
		methodInvNode.setMethodDeclaration(md);

		end = System.currentTimeMillis();
//		System.out.printf("== 0 (%s) [%s]  \n", (end-start), md.getName());
		List<MethodInvocation> methodInvChildren = MeaningfulMethodInvocationResolver.extractMeaningfulMethodInvocations(md);

		end = System.currentTimeMillis();
//		System.out.printf("== 1 (%s) 의미있는 메소드들 추출  \n", (end-start), md.getName());

		for(MethodInvocation mi : methodInvChildren){
//			System.out.println("  = method : " + mi.getName());
			MethodInvocationNode newChild = new MethodInvocationNode(methodInvNode, mi);

			methodInvNode.addChild(newChild);
		}
		end = System.currentTimeMillis();
//		System.out.printf("== 2 (%s) 처리끝.  \n", (end-start), md.getName());
		methodInvNode.setProcessed();
//		System.out.println();
	}



	public static void extractCallingMethodsRecursively(MethodInvocationNode methodInvNode){
		if(methodInvNode==null || methodInvNode.getMethodInvocation()==null || methodInvNode.isProcessed()){
			return;
		}

		MethodInvocation thisMethodInv = methodInvNode.getMethodInvocation();
		MethodDeclaration md = HongJdtHelper.resolveMethodDeclarationCached(thisMethodInv);
		methodInvNode.setMethodDeclaration(md);

		List<MethodInvocation> methodInvs = MeaningfulMethodInvocationResolver.extractMeaningfulMethodInvocations(md);

		System.out.println("== type bindings");
		for(MethodInvocation mi : methodInvs){
//			System.out.println("  == MI" + mi.getName());
			MethodInvocationNode newChild = new MethodInvocationNode(methodInvNode, mi);

			methodInvNode.addChild(newChild);
			if(newChild.isTooDeep() || newChild.isDuplicatedInParent()){
				continue;
			}

			extractCallingMethodsRecursively(newChild);
			newChild.setProcessed();

			IMethodBinding mb = mi.resolveMethodBinding()  ;
//			System.out.println(mb.getDeclaringClass().getPackage().getName() + "." +   mb.getDeclaringClass().getName() + " => " + mb.getName());
//			System.out.println( mb.getDeclaringClass().getName() + "." + mb.getName());
		}
		System.out.println();
	}

}

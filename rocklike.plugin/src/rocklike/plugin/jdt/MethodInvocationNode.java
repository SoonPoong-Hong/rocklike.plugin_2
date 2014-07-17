package rocklike.plugin.jdt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.ui.JavaUI;

import rocklike.plugin.util.HongEditorHelper;
import rocklike.plugin.util.HongJdtHelper;

public class MethodInvocationNode {
	private List<MethodInvocationNode> children;
	private MethodInvocation methodInvocation;
	private MethodInvocationNode parent;
	private MethodDeclaration methodDeclaration;
	private boolean processed = false;
	private IFile rootFile;
	private int rootFilePos;
	
	public MethodInvocationNode(MethodInvocationNode parent, MethodInvocation methodInvocation) {
	    super();
	    this.parent = parent;
	    this.methodInvocation = methodInvocation;
    }

	public List<MethodInvocationNode> getChildren() {
		return children;
	}

	public void setChildren(List<MethodInvocationNode> children) {
		this.children = children;
	}

	public MethodInvocationNode addChild(MethodInvocationNode node){
		if(children==null){
			children = new ArrayList();
		}
		children.add(node);
		return this;
	}
	
	public boolean hasChildren(){
		return this.children!=null && children.size()>0;
	}

	public MethodInvocation getMethodInvocation() {
		return methodInvocation;
	}

	public void setMethodInvocation(MethodInvocation methodInvocation) {
		this.methodInvocation = methodInvocation;
	}

	public MethodInvocationNode getParent() {
		return parent;
	}
	
	public boolean isDuplicatedInParent(){
		// parent에 중복되는 node가 있는지 체크
		MethodInvocationNode p = parent;
		while(p!=null){
			MethodInvocation parentMethodInv = p.getMethodInvocation();
			if(parentMethodInv!=null
					&& parentMethodInv.resolveMethodBinding().getJavaElement().equals(methodInvocation.resolveMethodBinding().getJavaElement())){
				return true;
			}
			p = p.getParent();
		}
		return false;
	}
	
	public boolean isTooDeep(){
		MethodInvocationNode p = parent;
		int cnt = 0;
		while(p!=null){
			if(cnt>10){
				return true;
			}
			p = p.getParent();
			cnt++;
		}
		return false;
	}
	
	public IMethodBinding resolveMethodBinding(){
		if(methodDeclaration!=null){
			return methodDeclaration.resolveBinding();
		}
//		if(methodInvocation!=null){
//			return methodInvocation.resolveMethodBinding();
//		}
		return null;
	}
	
	public String getName(){
		if(methodInvocation!=null){
			return methodInvocation.toString();
		}
		if(methodDeclaration!=null){
			return methodDeclaration.getName().toString();
		}
		return "null";
//		IMethodBinding mb = methodInvocation.resolveMethodBinding();
//		mb.getDeclaringClass().getPackage().getName() + "." +   mb.getDeclaringClass().getName() + " => " + mb.getName();
		
//		ITypeBinding[] parameterTypes = mb.getParameterTypes();
//		StringBuilder sb = new StringBuilder();
//		int cnt = parameterTypes==null ? 0 : parameterTypes.length;
//		for(int i=0; i<cnt; i++){
//			if(i>0){
//				sb.append(", ");
//			}
//			sb.append(parameterTypes[i].getName() );
//		}
		
		
//		String name =  mb.getDeclaringClass().getName() + "." + mb.getName() + "(" + sb + ")";
//		String name =  methodInvocation.toString();
//		return name;
	}
	
	public int getInvocationPosition(){
		if(methodInvocation!=null){
			return methodInvocation.getStartPosition() + methodInvocation.getLength();
		}
		if(methodDeclaration!=null){
			return methodDeclaration.getStartPosition() + methodDeclaration.getLength();
		}
		return 0;
	}
	
	public MethodDeclaration resolveMethodDeclaration(){
		if(methodDeclaration!=null){
			return methodDeclaration;
		}
		if(methodInvocation!=null){
			return HongJdtHelper.resolveMethodDeclarationCached(methodInvocation);
		}
		return null;
	}
	
	public String resolveDeclaringClassName(){
		IMethodBinding mb = resolveMethodBinding();
		if(mb==null){
			return "null임..";
		}
		return mb.getDeclaringClass().getName();
	}
	
	public ITypeBinding getDeclaringClass(){
		IMethodBinding mb = resolveMethodBinding();
		if(mb==null){
			return null;
		}
		return mb.getDeclaringClass();
	}
	
//	public int getDeclaringPosition(){
//		if(methodDeclaration!=null){
//			return methodDeclaration.getStartPosition();
//		}
//		if(methodInvocation!=null){
//			MethodDeclaration tmpMethodDec = HongJdtHelper.resolveMethodDeclaration(methodInvocation);
//			if(tmpMethodDec!=null){
//				return tmpMethodDec.getStartPosition();
//			}
//		}
//		return 0;
//	}
	
	public boolean isRoot(){
		return parent==null;
	}

	@Override
    public String toString() {
		return getName();
    }


	public void setMethodDeclaration(MethodDeclaration methodDeclaration) {
		this.methodDeclaration = methodDeclaration;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed() {
		this.processed = true;
	}

	
	public void setRootFilePos(int rootFilePos) {
		this.rootFilePos = rootFilePos;
	}

	public void setRootFile(IFile rootFile) {
		this.rootFile = rootFile;
	}

	public void openInvocation(){
		
		IFile srcFile = null;
		int pos = 0;
		
		if(isRoot()){
			srcFile = rootFile;
			pos = rootFilePos;
			
		}else{
			IMethodBinding mb = null;
			
			if(parent==null){
				return;
			}
			mb = parent.resolveMethodBinding();
			if(mb==null){
				return;
			}
			srcFile = (IFile)mb.getJavaElement().getResource();
			pos = methodInvocation.getStartPosition();
		}
		
		
//		//=== 밑은 테스트. 나중에 지울것
//		IMethod m = (IMethod) mb.getJavaElement();
//		MethodDeclaration md2 = HongJdtHelper.resolveImplMethodOfDirect(m);
//		mb = md2.resolveBinding();
		
		try {
			HongEditorHelper.openTextEditorAndSelect(srcFile, pos, 0);
			return;
        } catch (Exception e) {
	        e.printStackTrace();
        } 
	}
	
	public void openDeclaration(){
		MethodDeclaration methodDec = resolveMethodDeclaration();
		if(methodDec==null){
			return;
		}
		
		IMethodBinding mb = methodDec.resolveBinding();
		if(mb==null){
			return;
		}
		
		try {
			JavaUI.openInEditor(mb.getJavaElement());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

package rocklike.plugin.jdt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import rocklike.plugin.jdt.kt.KtcmsMeaningfulClassResolver;

public class MeaningfulMethodInvocationResolver{

	public static List<MethodInvocation> extractMeaningfulMethodInvocations(MethodDeclaration md){
		List<MethodInvocation> list = new ArrayList();
		MeaningfulMethodInvocationResolveVisitor v = new MeaningfulMethodInvocationResolveVisitor(list, new KtcmsMeaningfulClassResolver());
		md.accept(v);
		return list;
	}
	
	private static class MeaningfulMethodInvocationResolveVisitor extends ASTVisitor {
		final List<MethodInvocation> methodInv;
		private final MeaningfulClassResolver clzResolver;
		
		public MeaningfulMethodInvocationResolveVisitor(List<MethodInvocation> methodInv, MeaningfulClassResolver clzResolver) {
			this.methodInv = methodInv;
			this.clzResolver = clzResolver;
		}
		
		@Override
		public boolean visit(MethodInvocation node) {
			if(!ExcludingClzMethodNameResolver.get().isShouldBeExcluded(node) && clzResolver.isMeaningful(node)){
				methodInv.add(node);
			}
			return true;
		}
	}
}

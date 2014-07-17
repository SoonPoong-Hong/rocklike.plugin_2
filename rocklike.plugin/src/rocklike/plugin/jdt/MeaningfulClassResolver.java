package rocklike.plugin.jdt;

import org.eclipse.jdt.core.dom.MethodInvocation;

public interface MeaningfulClassResolver {
	public boolean isMeaningful(MethodInvocation mi);
}

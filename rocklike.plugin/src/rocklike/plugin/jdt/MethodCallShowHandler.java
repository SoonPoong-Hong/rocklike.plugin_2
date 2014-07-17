package rocklike.plugin.jdt;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jdt.core.JavaModelException;

public class MethodCallShowHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
	        CallingMethodExtractor.extractCallingMethodsAndOpen();
        } catch (JavaModelException e) {
	        e.printStackTrace();
        }
		return null;
	}

}

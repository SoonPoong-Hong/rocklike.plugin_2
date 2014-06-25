package rocklike.plugin.explorer;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import rocklike.plugin.util.HongEclipseUtil;

public class CopyFilePath extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		HongEclipseUtil.copyFilePathToClipboard();
		return null;
	}

}

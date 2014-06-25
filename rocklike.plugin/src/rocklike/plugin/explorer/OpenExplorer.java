package rocklike.plugin.explorer;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import rocklike.plugin.util.HongEclipseUtil;

/**
 * @Description : 탐색기 열기
 *
 * @Author 홍순풍 (rocklike@gmail.com)
 * @Date  2013. 9. 25.
 * @Version : 1.0
 */
public class OpenExplorer extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		HongEclipseUtil.openExplorer();
		return null;
	}
	

}

package rocklike.plugin.bookmark;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Display;

import rocklike.plugin.util.HongEclipseUtil;

public class HongGotoBookmark extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent e) throws ExecutionException {
		System.out.printf("==== HongGotoBookmarkHandler \n");
//		MessageDialogWithToggle.openInformation(Display.getDefault().getActiveShell(), "ㅌㅌ", "뭬냐..");
		try {
			String key = e.getParameter(HongBookmarkHelper.PARAM_KEY);
			System.out.printf("==== param [%s] \n", key);

			boolean isSccess = HongBookmarkHelper.gotoBookmark(key);
			if(!isSccess){
				HongEclipseUtil.setStatusLineMsg(key + "번 bookmark 존재하지 않음");
				System.out.printf("==== bookmark로 가지 못했습니다. \n");
			} // AB_CDEF
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ExecutionException(ex.getMessage(), ex);
		}
		return null;
	}

}

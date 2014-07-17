package rocklike.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Display;

import rocklike.plugin.dialog.MsgDisplayDialog;
import rocklike.plugin.util.HongMessagePopupUtil;
import rocklike.plugin.util.PreferenceStoreHelper;

public class ShowRefreshFolderList extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String folders = PreferenceStoreHelper.getString(RefreshFolderAssignHandler.PREFERENCE_REFRESH_FOLDER_KEY);
		if(folders==null || folders.length()==0){
			HongMessagePopupUtil.showInfomsg("refresh할 폴더로 지정해 놓은게 없습니다.");
			return null;
		}
		
		String[] fArr = folders.split(";");
		StringBuilder sb = new StringBuilder();
		for(String a : fArr){
			sb.append(a).append("\n");
		}
		MsgDisplayDialog dialog = new MsgDisplayDialog(Display.getDefault().getActiveShell(), null, "저장된 refresh폴더 목록");
		sb.insert(0, "=== refresh할 목록들은 아래와 같음 ===\n\n");
		dialog.setMsg(sb.toString());
		dialog.open();
		return null;
	}

}

package rocklike.plugin.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.widgets.Display;

import rocklike.plugin.dialog.MsgDisplayDialog;
import rocklike.plugin.util.PreferenceStoreHelper;
import rocklike.plugin.util.SelectedFileExtractor;

public class RefreshFolderAssignHandler extends AbstractHandler implements IHandler {

	public static final String PREFERENCE_REFRESH_FOLDER_KEY = "__hong_PREFERENCE_REFRESH_FOLDER_KEY";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("refresh 폴더 지정하기");
		
		List<IResource> selectedResources = SelectedFileExtractor.getSelectedResources();
		String selFolders = SelectedFileExtractor.toSeparatedStr(selectedResources);
		System.out.printf("== selFolders [%s] \n", selFolders);
		
		PreferenceStoreHelper.setString(PREFERENCE_REFRESH_FOLDER_KEY, selFolders);
		
		showMsg(selectedResources);
		
		return null;
	}

	
	private void showMsg(List<IResource> selectedResources){
		MsgDisplayDialog dialog = new MsgDisplayDialog(Display.getDefault().getActiveShell(), null, "refresh할 폴더들을 지정함.");
		StringBuilder sb = new StringBuilder();
		sb.append("=== 아래의 폴더들에 대해서 refresh할 것임 ===").append("\n\n");
		for(IResource r : selectedResources){
			sb.append(r.getFullPath().toOSString()).append("\n");
		}
		dialog.setMsg(sb.toString());
		dialog.open();
	}
}

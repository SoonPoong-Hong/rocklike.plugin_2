package rocklike.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;

import rocklike.plugin.dialog.MsgDisplayDialog;
import rocklike.plugin.util.HongEclipseFileHelper;
import rocklike.plugin.util.PreferenceStoreHelper;
import rocklike.plugin.util.StatusLineHelper;

public class ResourceRefreshHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("== refresh함.");
		String folders = PreferenceStoreHelper.getString(RefreshFolderAssignHandler.PREFERENCE_REFRESH_FOLDER_KEY);
		System.out.printf("== 저장된 폴더 : [%s] \n", folders);
		if(folders==null || folders.length()==0){
			return null;
		}
		
		String[] fArr = folders.split(";");
		IWorkspaceRoot root = HongEclipseFileHelper.getWorkspaceRoot();
		for(String s : fArr){
			System.out.printf("= [%s] \n", s);
			IResource res = toResource(root, s);
			if(res!=null){
				try {
	                res.refreshLocal(IResource.DEPTH_INFINITE, null);
	                System.out.printf("= refresh함. (%s) \n", s);
                } catch (CoreException e) {
	                e.printStackTrace();
                }
			}
		}
		
		StatusLineHelper.setMessage("refresh 했어요..");
		
		return null;
	}
	

	
	
	private IResource toResource(IWorkspaceRoot root, String path){
		IFolder folder = root.getFolder(Path.fromOSString(path));
		if(folder.exists()){
			return folder;
		}
		IFile file = root.getFile(Path.fromOSString(path));
		if(file.exists()){
			return file;
		}
		return null;
	}

	
	void test(){
		IFile f;
		IWorkspaceRoot root = HongEclipseFileHelper.getWorkspaceRoot();
		root.getFile(null);
	}

}

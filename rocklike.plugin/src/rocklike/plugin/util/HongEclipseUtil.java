package rocklike.plugin.util;


import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.services.IServiceLocator;

import rocklike.plugin.HongPluginActivator;


/**
 * @author Hong SoonPoong (rocklike@gmail.com)
 * @date 2014. 7. 5.
 */
public class HongEclipseUtil {

    public static IEditorPart getActiveEditor() {
    	if(isUiThread()){
    		IWorkbenchWindow window = getActiveWorkbenchWindow();
    		if (window != null) {
    			IWorkbenchPage page = window.getActivePage();
    			if (page != null) {
    				return page.getActiveEditor();
    			}
    		}
    		return null;
    		
    	}else{
    		final ObjectHolder holder = new ObjectHolder();
    		Display.getDefault().syncExec(new Runnable(){
				@Override
				public void run() {
		    		IWorkbenchWindow window = getActiveWorkbenchWindow();
		    		if (window != null) {
		    			IWorkbenchPage page = window.getActivePage();
		    			if (page != null) {
		    				holder.put(page.getActiveEditor());
		    			}
		    		}
				}
    		});
    		return (IEditorPart)holder.get();
    	}
    }

    public static IWorkbenchPage getActivePage(){
    	if(isUiThread()){
    		IWorkbenchWindow window = getActiveWorkbenchWindow();
    		if (window != null) {
    			IWorkbenchPage page = window.getActivePage();
    			return page;
    		}
    		return null;
    		
    	}else{
    		final ObjectHolder<IWorkbenchPage> holder = new ObjectHolder();
    		Display.getDefault().syncExec(new Runnable(){
				@Override
				public void run() {
					IWorkbenchWindow window = getActiveWorkbenchWindow();
					if (window != null) {
						IWorkbenchPage page = window.getActivePage();
						holder.put(page);
					}
				}
    		});
    		return holder.get();
    	}
    }

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		if(isUiThread()){
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		}else{
			final ObjectHolder holder = new ObjectHolder();
			Display.getDefault().syncExec(new Runnable(){
				@Override
				public void run() {
					holder.put(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
				}
			});
			return (IWorkbenchWindow)holder.get();
		}
	}

	public static ISelection getSelection(){
		if(isUiThread()){
			ISelectionService service = getSelectionService();
			ISelection sel = service.getSelection();
//			System.out.printf("== ISelection [%s] \n", sel);
			return sel;
			
		}else{
			final ObjectHolder<ISelection> holder = new ObjectHolder();
			Display.getDefault().syncExec(new Runnable(){
				@Override
				public void run() {
					ISelectionService service = getSelectionService();
					ISelection sel = service.getSelection();
					holder.put(sel);
				}
			});
			return holder.get();
		}
	}

	public static ISelectionService getSelectionService(){
		return getActiveWorkbenchWindow().getSelectionService();
	}


	public static void setStatusLineMsg(final String msg){
		if(isUiThread()){
			IEditorPart part = getActiveEditor();
			if(part==null){
				return;
			}
			part.getEditorSite().getActionBars().getStatusLineManager().setMessage(msg);
			
		}else{
			Display.getDefault().asyncExec(new Runnable(){
				@Override
				public void run() {
					IEditorPart part = getActiveEditor();
					if(part==null){
						return;
					}
					part.getEditorSite().getActionBars().getStatusLineManager().setMessage(msg);
				}
			});
		}
	}
	
	public static void openExplorer(File f){
		if(f==null || !f.exists()){
			System.out.printf("=== 해당 파일이 존재하지 않음. [%s] \n", f.getAbsolutePath());
			return;
		}
		
		if(f.isDirectory()){
			try {
				openExplorer(f.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}else if(f.isFile()){
			File dir = f.getParentFile();
			try {
				openExplorer(dir.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}else{
			System.out.printf("=== 해당 파일위치를 열수 없습니다. [%s] \n", f.getPath());
			setStatusLineMsg("=== 해당 파일위치를 열수 없습니다.::" + f.getPath());
		}
		
	}
	
	private static void openExplorer(String path){
		ProcessBuilder pb = new ProcessBuilder("explorer", path);
		try {
			pb.start();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static void openExplorer(){
		File f = getSelectedFile();
		if(f!=null){
			openExplorer(f);
		}else{
			setStatusLineMsg("[에러] 해당 파일위치를 열수 없습니다.");	
		}
	}
	
	public static void copyFilePathToClipboard(){
		File f = getSelectedFile();
		if(f!=null){
			try {
				String path = f.getCanonicalPath();
				Clipboard clipboard = new Clipboard(Display.getCurrent());
				TextTransfer textTransfer = TextTransfer.getInstance();
				clipboard.setContents(new Object[] { path },
				  new Transfer[] { textTransfer });
			} catch (IOException e) {
				e.printStackTrace();
				setStatusLineMsg("[에러] 파일 경로를 copy하지 못했습니다." + e.getMessage());
			}
		}else{
			setStatusLineMsg("[에러] 파일 경로를 copy하지 못했습니다.");			
		}
	}
	
	private static File getSelectedFile(){
		ISelection sel =  HongEclipseUtil.getSelection();
		
		if(sel instanceof IStructuredSelection){
			IStructuredSelection strucSel = (IStructuredSelection)sel;
			Object selObj = strucSel.getFirstElement();
			
			Object obj = Platform.getAdapterManager().getAdapter(selObj, IResource.class);
			if(obj!=null){
				IResource res = (IResource)obj;
				File f = res.getLocation().toFile();
				return f;
			}
		}
		
		return null;
	}
	
	
	public static boolean isUiThread(){
		return Display.getDefault().getThread()==Thread.currentThread();
	}

	public static IFile getActiveEditorFile(){
		IFile file = (IFile)HongEclipseUtil.getActiveEditor().getEditorInput().getAdapter(IFile.class);
		System.out.printf("==== IFile [%s] \n", file.getLocation().toString());
		return file;
	}
	
	
	public static <T> T getFromActiveEditor(Class clz){
		T t = (T)HongEclipseUtil.getActiveEditor().getEditorInput().getAdapter(clz);
		return t;
	}
	
	
	public static IServiceLocator getIServiceLocator(){
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}
	
	public static ISelectionService getISelectionService(){
		return (ISelectionService)getIServiceLocator().getService(ISelectionService.class);
	}

	public static IProject getActiveEditorProject(){
		IProject p = getActiveEditorFile().getProject();
		return p;
	}
	
	public static IProject getSelectedProject(){
		ISelection isel = getSelection();
		if(isel instanceof ITextSelection){
			return getActiveEditorProject();
		}else{
			if(isel instanceof IStructuredSelection){
				IStructuredSelection thisSel = (IStructuredSelection)isel;
				IResource res = (IResource)Platform.getAdapterManager().getAdapter(thisSel.getFirstElement(), IResource.class);
				return res.getProject();
			}
		}
		return null;
	}
}

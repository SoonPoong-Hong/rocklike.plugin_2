package rocklike.plugin.util;


import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IServiceLocator;

import rocklike.plugin.HongPluginActivator;
import de.loskutov.anyedit.ui.editor.AbstractEditor;


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
    		final ObjectHolder<IEditorPart> holder = new ObjectHolder<IEditorPart>();
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
    		return holder.get();
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
//			return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			return HongPluginActivator.getDefault().getWorkbench().getActiveWorkbenchWindow();
		}else{
			final ObjectHolder<IWorkbenchWindow> holder = new ObjectHolder();
			Display.getDefault().syncExec(new Runnable(){
				@Override
				public void run() {
//					holder.put(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
					holder.put(HongPluginActivator.getDefault().getWorkbench().getActiveWorkbenchWindow());
				}
			});
			return (IWorkbenchWindow)holder.get();
		}
	}

	public static ISelection getSelection(){
		if(isUiThread()){
			ISelectionService service = getSelectionService();
			ISelection sel = service.getSelection();
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


	public static ITextSelection getTextSelection(){
		AbstractEditor editor = new AbstractEditor(getActiveEditor());
		ITextSelection ts = (ITextSelection)editor.getSelectionProvider().getSelection();
		return ts;
//		ISelection sel = getSelection();
//		if(sel instanceof ITextSelection){
//			return (ITextSelection)sel;
//		}
//		return null;
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
			HongMessagePopupUtil.showErrMsg("해당 파일이 존재하지 않음.\n"+ f.getAbsolutePath());
			return;
		}

		try {
			openExplorer(f.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
			setStatusLineMsg("=== 해당 파일위치를 열수 없습니다.::" + f.getPath());
		}

	}

	private static void openExplorer(String path){
		ProcessBuilder pb = new ProcessBuilder("explorer", "/E,", "/select,",  path);
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
				if(thisSel instanceof IResource){
					return ((IResource)thisSel).getProject();
				}
				IResource res = (IResource)Platform.getAdapterManager().getAdapter(thisSel.getFirstElement(), IResource.class);
				return res.getProject();
			}
		}
		return null;
	}


	public static IProject getSelectedProject(ISelection isel){
		if(isel instanceof ITextSelection){
			return getActiveEditorProject();
		}else{
			if(isel instanceof IStructuredSelection){
				IStructuredSelection thisSel = (IStructuredSelection)isel;
				if(thisSel instanceof IResource){
					return ((IResource)thisSel).getProject();
				}
				if(!thisSel.isEmpty()){
					Object firstElement = thisSel.getFirstElement();
					IResource res = (IResource) Platform.getAdapterManager().getAdapter(firstElement, IResource.class);
					if(res!=null){
						return res.getProject();
					}
				}
			}
		}
		return null;
	}





	public static IViewPart showView(String id){
		try {
	        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(id);
        } catch (PartInitException e) {
	        e.printStackTrace();
	        return null;
        }
	}



	public static void main(String[] args) throws IOException {
	    ProcessBuilder pb = new ProcessBuilder("explorer", "/E,", "/select,", "D:\\tmp\\한글 막\\oracle-ds.xml");
	    pb.start();
    }
}

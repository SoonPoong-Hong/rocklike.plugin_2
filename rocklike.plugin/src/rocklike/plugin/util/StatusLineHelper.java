package rocklike.plugin.util;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;

public class StatusLineHelper {

	public static IStatusLineManager getStatusLineManager(){
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IWorkbenchPart activePart = activePage.getActivePart();
		IWorkbenchPartSite site = activePart.getSite();
		
		System.out.println( activePart instanceof IEditorPart );
		
		
		if(site instanceof IViewSite){
			IViewSite vsite = (IViewSite)site;
			IActionBars actionBars = vsite.getActionBars();
			if(actionBars!=null){
				IStatusLineManager statusLineManager = actionBars.getStatusLineManager();
				if(actionBars!=null){
					return statusLineManager;
				}
			}
		}else{
			if(activePart instanceof IEditorPart){
				IEditorSite editorSite = ((IEditorPart)activePart).getEditorSite();
				IActionBars actionBars = editorSite.getActionBars();
				if(actionBars!=null){
					IStatusLineManager statusLineManager = actionBars.getStatusLineManager();
					return statusLineManager;
				}
			}
		}
		return null;
	}
	
	public static void setMessage(String msg){
		IStatusLineManager m = getStatusLineManager();
		if(m!=null){
			m.setMessage(msg);
		}
	}
}

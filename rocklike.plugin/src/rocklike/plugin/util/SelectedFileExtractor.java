package rocklike.plugin.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

public class SelectedFileExtractor {

	public static List<IResource> getSelectedResources(){
		ISelection sel =  HongEclipseUtil.getSelection();
		
		if(sel instanceof IStructuredSelection){
			IStructuredSelection strucSel = (IStructuredSelection)sel;
			if(strucSel==null){
				return new ArrayList(0);
			}
			List<IResource> result = new ArrayList();
			Iterator iterator = strucSel.iterator();
			while(iterator.hasNext()){
				Object obj = iterator.next();
				IResource res = (IResource)Platform.getAdapterManager().getAdapter(obj, IResource.class);
				if(res!=null){
					result.add(res);
					
				}
			}
			return result;
		}	
		
		return new ArrayList(0);
	}
	
	
	public static String toSeparatedStr(List<IResource> l){
		if(l==null || l.size()==0){
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		int cnt = l.size();
		for(int i=0; i<cnt; i++){
			if(i>0){
				sb.append(";");
			}
			sb.append(l.get(i).getFullPath().toString());
		}
		return sb.toString();
	}
	
	
	public static File getSelectedFile(){
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
}

package rocklike.plugin.bookmark;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.MarkerUtilities;

import rocklike.plugin.util.HongEclipseUtil;

public class HongBookmarkHelper {

	public static final String MARKER = "rocklike.plugin.bookmark" ;
	public static final String ANNOTATION = "rocklike.plugin.bookmark.annotation";
	public static final String NUM = "NUM";
	public static final String PARAM_KEY = "rocklike.plugin.bookmark.commandParameter_number";


	public static boolean createMarker(String keyStr) throws CoreException{

		int key = -1;
		try {
			key = Integer.parseInt(keyStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		IEditorPart editorPart =  HongEclipseUtil.getActiveEditor();
		if(editorPart==null){
			return false;
		}

		IFile file = (IFile)HongEclipseUtil.getActiveEditor().getEditorInput().getAdapter(IFile.class);
//		System.out.printf("==== IFile [%s] \n", file);

		ISelection sel = HongEclipseUtil.getSelection();
//		System.out.printf("==== ISelection [%s] \n", sel);

		if(file==null){
			return false;
		}

		if(!(sel instanceof ITextSelection)){
			return false;
		}

		ITextSelection textSel = (ITextSelection)sel;
		int endLine = textSel.getEndLine();
		int pos = textSel.getOffset() + textSel.getLength();

		IMarker oldM = fetchMarker(key);

		// 기존 marker를 뒤져봐서 없으면 새로 만들고,
		if(oldM==null){
			createMarker(file, key, pos, endLine);
			HongEclipseUtil.setStatusLineMsg(String.format("bookmark 추가 (%s 번)", key));
			return true;
		}

		// 있는데 현재 Line이면 지우고,
		int oldEndLine = MarkerUtilities.getLineNumber(oldM);
		if(oldEndLine==endLine && oldM.getResource().equals(file)){
			oldM.delete();
			HongEclipseUtil.setStatusLineMsg(String.format("bookmark 삭제 (%s 번)", key));
			return true;

		}else{
			// 아니면 기존것을 지우고 현재위치로 새로 만든다.
			oldM.delete();
			createMarker(file, key, pos, endLine);
			HongEclipseUtil.setStatusLineMsg(String.format("bookmark 추가 (%s 번)", key));
			return true;
		}
	}





	private static void createMarker(IResource file, int key, int pos, int lineNum) throws CoreException{
		IMarker marker =  file.createMarker(MARKER);
		marker.setAttribute(IMarker.MESSAGE, "[hong] " + key + " 번 (alt+"+key+")");
		marker.setAttribute(NUM, key);
		MarkerUtilities.setLineNumber(marker, lineNum);
		MarkerUtilities.setCharStart(marker, pos);
		MarkerUtilities.setCharEnd(marker, pos);
	}


	public static boolean gotoBookmark(String keyStr) throws CoreException{
		int key = -1;
		try {
			key = Integer.parseInt(keyStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		IMarker m = fetchMarker(key);
		if(m==null){
			return false;
		}

		IDE.openEditor(HongEclipseUtil.getActivePage(), m);
		HongEclipseUtil.setStatusLineMsg(String.format("bookmark로 가기 (%s 번)", key));
		return true;
	}

	public static IMarker fetchMarker(int key) throws CoreException{
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(MARKER, false, IResource.DEPTH_INFINITE);

		// key에 해당하는 marker 골라내기
		List<IMarker> selectedMarkers = new ArrayList();
		for(IMarker m : markers){
			int thisNum = m.getAttribute(NUM, -999);
			if(key==thisNum){
				selectedMarkers.add(m);
			}
		}

		if(selectedMarkers.size()==0){
			return null;
		}

		// 만일 marker가 여러개이면 첫번째 것만 남기고 모두 삭제
		if(selectedMarkers.size()>1){
			for(int i=1; i<selectedMarkers.size(); i++){
				IMarker m = selectedMarkers.get(i);
				m.delete();
			}
		}

		return selectedMarkers.get(0);
	}

	private static IFile getActiveEditorFile(){
		IFile file = (IFile)HongEclipseUtil.getActiveEditor().getEditorInput().getAdapter(IFile.class);
		System.out.printf("==== IFile [%s] \n", file);
		return file;
	}


}

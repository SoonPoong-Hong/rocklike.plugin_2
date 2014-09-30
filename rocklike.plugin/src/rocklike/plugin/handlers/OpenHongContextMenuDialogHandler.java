package rocklike.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ide.ResourceUtil;

import rocklike.plugin.jdt.quickassist.HongContextMenuDialog;
import rocklike.plugin.jdt.quickassist.ICompilationUnitAndOffset;
import rocklike.plugin.srcgen.dialog.HongNoJavaContextMenuDialog;
import rocklike.plugin.util.HongEclipseUtil;
import rocklike.plugin.util.HongEditorHelper;
import rocklike.plugin.util.HongJdtHelper;
import rocklike.plugin.util.HongMessagePopupUtil;

public class OpenHongContextMenuDialogHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ITextSelection textSelection = HongEclipseUtil.getTextSelection();
		if(textSelection==null){
			HongMessagePopupUtil.showErrMsg("에디터에서 소스(java소스등)를 열어서 커서를 위치시킨후에 호출하세요.");
			return null;
		}

		IFile ifile = ResourceUtil.getFile(HongEclipseUtil.getActiveEditor().getEditorInput());
		if(ifile==null){
			HongMessagePopupUtil.showErrMsg("에디터에서 소스를 연후에 호출하세요.");
			return null;
		}

		if(ifile.getName().endsWith(".java")){
			openJavaMenu(event, textSelection);
		}else{
			openNoJavaMenu(event, textSelection);
		}


		return null;
	}


	private void openJavaMenu(ExecutionEvent event, ITextSelection textSelection){

		ICompilationUnit icu = HongJdtHelper.getSelectedICompilationUnit();
		IDocument doc = HongEditorHelper.getIDocument();
		if(icu==null || doc==null){
			HongMessagePopupUtil.showErrMsg("자바소스를 연 상태에서 호출하세요.");
		}

		int offset = 0;

		if(textSelection!=null){
			offset = textSelection.getOffset();
		}
		ICompilationUnitAndOffset param = new ICompilationUnitAndOffset(icu, doc, offset);

		HongContextMenuDialog dialog = new HongContextMenuDialog(Display.getDefault().getActiveShell());
		dialog.setICompilationUnitAndOffset(param);
		dialog.open();
	}

	private void openNoJavaMenu(ExecutionEvent event, ITextSelection textSelection){
		IDocument doc = HongEditorHelper.getIDocument();
		HongNoJavaContextMenuDialog dialog = new HongNoJavaContextMenuDialog(Display.getDefault().getActiveShell(), doc, textSelection);
		dialog.open();
	}
}

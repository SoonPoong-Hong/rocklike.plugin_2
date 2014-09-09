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
import rocklike.plugin.util.HongEclipseUtil;
import rocklike.plugin.util.HongEditorHelper;
import rocklike.plugin.util.HongJdtHelper;
import rocklike.plugin.util.HongMessagePopupUtil;

public class OpenHongContextMenuDialogHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IFile ifile = ResourceUtil.getFile(HongEclipseUtil.getActiveEditor().getEditorInput());
		if(ifile==null || !ifile.getName().endsWith(".java")){
			HongMessagePopupUtil.showErrMsg("자바소스를 연 상태에서 호출하세요.");
			return null;
		}

		ICompilationUnit icu = HongJdtHelper.getSelectedICompilationUnit();
		IDocument doc = HongEditorHelper.getIDocument();
		if(icu==null || doc==null){
			HongMessagePopupUtil.showErrMsg("자바소스를 연 상태에서 호출하세요.");
			return null;
		}
		int offset = 0;
		ITextSelection textSelection = HongEclipseUtil.getTextSelection();
		if(textSelection!=null){
			offset = textSelection.getOffset();
		}
		ICompilationUnitAndOffset param = new ICompilationUnitAndOffset(icu, doc, offset);

		HongContextMenuDialog dialog = new HongContextMenuDialog(Display.getDefault().getActiveShell());
		dialog.setICompilationUnitAndOffset(param);
		dialog.open();
		return null;
	}

}

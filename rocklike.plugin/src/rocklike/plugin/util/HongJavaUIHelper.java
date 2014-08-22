package rocklike.plugin.util;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;

public class HongJavaUIHelper {

	public static IType searchFromJavaTypeDialog(String msg, String initialValue){
		Shell shell = Display.getDefault().getActiveShell();
		SelectionDialog dialog = null;
        try {
            dialog = JavaUI.createTypeDialog(shell, new ProgressMonitorDialog(shell)
            	, SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_ALL_TYPES, false, initialValue, null );
        } catch (JavaModelException e1) {
            e1.printStackTrace();
        }

        dialog.setTitle("클래스 선택");
        dialog.setMessage(msg);
        if (dialog.open() == IDialogConstants.CANCEL_ID) {
        	return null;
        }

        Object[] result = dialog.getResult();
        if(result.length==0){
        	return null;
        }
        IType type = (IType) result[0];
        return type;
	}

}

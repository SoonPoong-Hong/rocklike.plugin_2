package rocklike.plugin.test;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.dialogs.ITypeInfoFilterExtension;
import org.eclipse.jdt.ui.dialogs.ITypeInfoRequestor;
import org.eclipse.jdt.ui.dialogs.TypeSelectionExtension;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;

public class JavaUITest {

	public void openTypeDialog() throws JavaModelException {
		Shell parent = Display.getDefault().getActiveShell();

		SelectionDialog dialog = JavaUI.createTypeDialog(parent, new ProgressMonitorDialog(parent)
				, SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_ALL_TYPES, false, "", getTypeSelectionExtensionOfCoreCms() );

		dialog.setTitle("My Dialog Title");
		dialog.setMessage("My Dialog Message");
		if (dialog.open() == IDialogConstants.CANCEL_ID) {
			return;
		}


		 Object[] result = dialog.getResult();
		 IType t = (IType) result[0];
		System.out.printf("== types : %s \n", t.getFullyQualifiedName());
	}


	TypeSelectionExtension getTypeSelectionExtensionOfCoreCms(){
		return new TypeSelectionExtension(){

			@Override
            public ITypeInfoFilterExtension getFilterExtension() {
				return new ITypeInfoFilterExtension(){
					@Override
                    public boolean select(ITypeInfoRequestor requestor) {
						return requestor.getPackageName().startsWith("com.kt.cms");
                    }
				};
            }

		};
	}

}

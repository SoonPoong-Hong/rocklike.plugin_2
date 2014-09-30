package rocklike.plugin.srcgen.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import rocklike.plugin.util.HongEditorHelper;
import rocklike.plugin.util.HongMessagePopupUtil;
import rocklike.plugin.util.KtSqlColumnDecorator;

public class HongNoJavaContextMenuDialog extends Dialog {

	enum WorkType{DecorateSqlColumn};

	private WorkType workType;
	private List<Button> radioButtons = new ArrayList();
	private IDocument doc;
	private ITextSelection textSel;

	public HongNoJavaContextMenuDialog(Shell parentShell, IDocument doc, ITextSelection textSel) {
	    super(parentShell);
	    this.doc = doc;
	    this.textSel = textSel;
    }

	public void extractWorkType(){
		for(Button b : radioButtons){
			if(b.getSelection()){
				workType = (WorkType)b.getData();
			}
		}
	}

	@Override
    protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		((GridLayout)container.getLayout()).numColumns = 1;

		new Label(container, SWT.NONE).setText("뭘 할까요..?");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL));

		addRadioButton(container, "Sql 컬럼에 CamelCase alias 붙이기" , WorkType.DecorateSqlColumn);

		return container;
	}


	private void addRadioButton(Composite parent, String name, WorkType workType){
		Button b = new Button(parent, SWT.RADIO);
		b.setText(name);
		b.setData(workType);
		radioButtons.add(b);
	}

	@Override
    protected boolean isResizable() {
		return true;
    }

	@Override
    protected void okPressed() {
		extractWorkType();

		if(workType==WorkType.DecorateSqlColumn){
			String text = textSel.getText();
			String decorateColumns = new KtSqlColumnDecorator(text).decorateColumns();

			try {
	            HongEditorHelper.replaceSelection(doc, textSel, decorateColumns);
            } catch (BadLocationException e) {
	            e.printStackTrace();
	            HongMessagePopupUtil.showErrMsg(e);
            }
		}

	    super.okPressed();
    }


	public WorkType getWorkType() {
		return workType;
	}
}

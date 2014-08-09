package rocklike.plugin.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MsgDisplayDialog extends Dialog {

	public MsgDisplayDialog(Shell parentShell, IDialogSettings settings, String title) {
		super(parentShell);
		this.settings = settings;
		// modeless
		this.setShellStyle(getShellStyle()  ^ SWT.APPLICATION_MODAL | SWT.MODELESS);
		this.title = title;
	}

	protected IDialogSettings settings;
	private String msg ;
	private String title;
	
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		Text t = new Text(container, SWT.H_SCROLL|SWT.V_SCROLL);
		t.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		t.setText(msg==null ? "null" : msg);
		t.setEditable(true);
		if(title!=null){
			this.getShell().setText(title);
		}
		return container;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(500, 400);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	public static void staticOpen(){
		
	}
	
	synchronized private void sustainOnlyOne(){
	}
	
	@Override
	public boolean close() {
		System.out.printf("=== close 호출전. \n");
		return super.close();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

//	@Override
//	protected IDialogSettings getDialogBoundsSettings() {
//		return settings;
//	}
	
	

}
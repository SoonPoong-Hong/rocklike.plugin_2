package rocklike.plugin.jdt.quickassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import rocklike.plugin.util.ScrolledCompositeCreater;

public class GetterSelectionDialog extends Dialog {
	private List<Button> checkBoxes = new ArrayList();
	private List<IMethodBinding> selectedSetterNames = new ArrayList();
	private ITypeBinding typeBinding;

	public GetterSelectionDialog(Shell parentShell) {
	    super(parentShell);
    }

	public void extractSelectedGetters(){
		for(Button b : checkBoxes){
			if(b.getSelection()){
				selectedSetterNames.add((IMethodBinding)b.getData());
			}
		}
	}

	public List<IMethodBinding> getSelectedGetters() {
		return selectedSetterNames;
	}

	private boolean isValidGetterMethod(String name){
		return name!=null && name.length()>3 && name.startsWith("get")
				&& Character.isUpperCase(name.charAt(3))
				&& !name.equals("getClass")
				;
	}

	@Override
    protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		Composite inside = new ScrolledCompositeCreater().createScrolledComposite(container);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(inside);

		final Button allChecked = new Button(inside, SWT.CHECK);
		allChecked.setText("전체 선택/해제");
		allChecked.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent e) {
				boolean isSelected = allChecked.getSelection();
				for(Button b : checkBoxes){
					b.setSelection(isSelected);
				}
            }
		});

		new Label(inside, SWT.SEPARATOR | SWT.HORIZONTAL);
		new Label(inside, SWT.SEPARATOR | SWT.HORIZONTAL);



		ColumnDescVO descVo = null;
		try {
            descVo = ColumnDescriptionStore.readFromLocal();
        } catch (Exception e1) {
            e1.printStackTrace();
        }



		if(typeBinding!=null){
			IMethodBinding[] methods = typeBinding.getDeclaredMethods();

			for(IMethodBinding mb : methods){
				String name = mb.getName();
				if(isValidGetterMethod(name)){
					addCheckBox(descVo, inside, mb, name);
				}
			}
		}


		inside.setSize(inside.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		inside.layout();


		return container;
	}

	private void addCheckBox(ColumnDescVO descVo, Composite parent, IMethodBinding mb, String name){
		Button b = new Button(parent, SWT.CHECK);
		String column = name.substring(3,4).toLowerCase() + name.substring(4);
		String desc = ColumnDescriptionStore.getColumnDescriptionApproximately(column, descVo);;
		b.setText( column + (desc==null ? "" : " ("+desc+")") );
		b.setData(mb);
		checkBoxes.add(b);
	}

	@Override
    protected boolean isResizable() {
		return true;
    }

	public void setTypeBinding(ITypeBinding tb) {
		this.typeBinding = tb;
	}

	@Override
    protected void okPressed() {
		extractSelectedGetters();
	    super.okPressed();
    }


}

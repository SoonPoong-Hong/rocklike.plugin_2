package rocklike.plugin.jdt.quickassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import rocklike.plugin.util.HongJdtHelper;
import rocklike.plugin.util.ScrolledCompositeCreater;

public class SetterCallSelectionDialog extends Dialog {
	private List<Button> checkBoxes = new ArrayList();
	private List<String> selectedSetterNames = new ArrayList();
	private List<String> setterNames = new ArrayList();
	private MethodInvocation methodInvocation;

	public SetterCallSelectionDialog(Shell parentShell) {
	    super(parentShell);
    }

	public void setSetterNames(List<String> setterNames) {
		this.setterNames = setterNames;
	}

	public void extractSelectedSetters(){
		for(Button b : checkBoxes){
			if(b.getSelection()){
				selectedSetterNames.add((String)b.getData());
			}
		}
	}

	public List<String> getSelectedSetterNames() {
		return selectedSetterNames;
	}

	private boolean isValidSetterMethod(String name){
		return name!=null && name.length()>3 && name.startsWith("set") && Character.isUpperCase(name.charAt(3)) && !Arrays.asList(excludingSetterNames).contains(name);
	}

	private String[] excludingSetterNames = {"setFirstCretDt", "setFirstCretTrtrId", "setFirstCretPgmId", "setLastChgDt", "setLastChgTrtrId", "setLastChgPgmId", "setCcmsLoginId"};

	@Override
    protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		Composite inside = ScrolledCompositeCreater.createScrolledComposite(container);
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


		ColumnDescVO descVo = ColumnDescriptionStore.readFromLocal();
		
		if(methodInvocation!=null){
			List<IMethodBinding> methods = HongJdtHelper.gatherDeclaredMethods(methodInvocation);
			
			List<String> validNames = new ArrayList();

			for(IMethodBinding thisMb : methods){
				String name = thisMb.getName();
				if(isValidSetterMethod(name)){
					validNames.add(name);
				}
			}

			for(String name : validNames){
				addCheckBox(descVo, inside, name);
			}

		}else{
			for(String name : setterNames){
				addCheckBox(descVo, inside, name);
			}
		}


		ScrolledCompositeCreater.setInsideCompositeSize(inside);

		return container;
	}



	private void addCheckBox(ColumnDescVO descVo, Composite parent, String name){
		Button b = new Button(parent, SWT.CHECK);
		String column = name.substring(3,4).toLowerCase() + name.substring(4);
		String desc = ColumnDescriptionStore.getColumnDescriptionApproximately(column, descVo);
		b.setText( column + (desc==null ? "" : " ("+desc+")") );
		b.setData(name);
		checkBoxes.add(b);
	}



	@Override
    protected boolean isResizable() {
		return true;
    }

	@Override
    protected void okPressed() {
		extractSelectedSetters();
	    super.okPressed();
    }

	public MethodInvocation getMethodInvocation() {
		return methodInvocation;
	}

	public void setMethodInvocation(MethodInvocation methodInvocation) {
		this.methodInvocation = methodInvocation;
	}
}

package rocklike.plugin.srcgen.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import rocklike.plugin.srcgen.wizards.GenAllModelPage.AllModel.GenTarget;
import rocklike.plugin.util.HongStringUtil;

public class GenAllModelPage extends WizardPage {

	AllModel model = new AllModel();
	Text pkgText;
	Text classText;
	
	List<Checkbox> checkBoxes = new ArrayList();
	Text descriptionText;
	
	
	protected GenAllModelPage(String pageName) {
	    super(pageName);
	    setTitle("한세트(model,java,xml등) 생성");
    }

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridDataFactory.fillDefaults().grab(true,true).applyTo(composite);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);
		
		Label label;
		label = new Label(composite, SWT.NULL);
		label.setText("패키지");
		GridDataFactory.fillDefaults().span(1,2).applyTo(label);
		
		pkgText = new Text(composite, SWT.BORDER);
		pkgText.setText(DefaultGenPkgAdvisor.getDefaultPackage());
		GridDataFactory.fillDefaults().align(SWT.FILL,SWT.BEGINNING).grab(true,false).applyTo(pkgText);
		
		label = new Label(composite, SWT.NULL);
		label.setText("※ controller,domain,dao,service등의 마지막 package는 제외하고 쓰세요. 마지막 패키지는 알아서 정해집니다.\n(예) com.kt.cms.cuc.dst.dm");
		GridDataFactory.fillDefaults().applyTo(label);
		
		label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).span(2,1).grab(true, false).applyTo(label);		
		
		label = new Label(composite, SWT.NULL);
		label.setText("클래스명");
		GridDataFactory.fillDefaults().span(1,2).align(SWT.BEGINNING,SWT.BEGINNING).applyTo(label);
		
		classText = new Text(composite, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(classText);
		
		label = new Label(composite, SWT.NULL);
		label.setText("(예) AdditionalDeploy ");
		GridDataFactory.fillDefaults().applyTo(label);

		label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).span(2,1).grab(true, false).applyTo(label);

		
		label = new Label(composite, SWT.NULL);
		label.setText("description");
		GridDataFactory.fillDefaults().align(SWT.BEGINNING,SWT.BEGINNING).applyTo(label);
		
		descriptionText = new Text(composite, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(descriptionText);
		
		label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).span(2,1).grab(true, false).applyTo(label);

		
		label = new Label(composite, SWT.NULL);
		label.setText("선택");
		GridDataFactory.fillDefaults().span(1,10).applyTo(label);
		
		checkBoxes.add(createCheckbox(composite, "input model", GenTarget.InputModel));
		checkBoxes.add(createCheckbox(composite, "output List model", GenTarget.OutputListModel));
		checkBoxes.add(createCheckbox(composite, "output Row model", GenTarget.OutputRowModel));
		checkBoxes.add(createCheckbox(composite, "Controller", GenTarget.Controller));
		checkBoxes.add(createCheckbox(composite, "Service, ServiceImpl", GenTarget.Service));
		checkBoxes.add(createCheckbox(composite, "Dao, DaoImpl, query xml", GenTarget.Dao));
		
		setControl(composite);
		
		addListeners();
	}
	

	private Checkbox createCheckbox(Composite composite, String s, GenTarget target){
		Button b = new Button(composite, SWT.CHECK);
		b.setText(s);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(b);
		Checkbox cb = new Checkbox(b, target );
		return cb;
	}
	
	
	
	public AllModel updateModel(){
		model.setPkgName(pkgText.getText());
		model.setClassName(classText.getText());
		model.setDescription(descriptionText.getText());
		
		model.targets.clear();
		for(Checkbox cb : checkBoxes){
			if(cb.button.getSelection()){
				model.targets.add(cb.target);
			}
		}
		return model;
	}
	
	
	@Override
    public IWizardPage getNextPage() {
		return null;
    }

	
	
	private KeyListener buttonUpdaterByKey = new KeyAdapter(){
		@Override
        public void keyReleased(KeyEvent e) {
			updateModel();
			getWizard().getContainer().updateButtons();
        }
	};
	
	
	private SelectionListener buttonUpdaterBySelection = new SelectionAdapter(){
		@Override
        public void widgetSelected(SelectionEvent e) {
			updateModel();
			getWizard().getContainer().updateButtons();
        }
	};
		
	
	private void addListeners(){
		pkgText.addKeyListener(buttonUpdaterByKey);
		classText.addKeyListener(buttonUpdaterByKey);
		for(Checkbox b : checkBoxes){
			b.button.addSelectionListener(buttonUpdaterBySelection);
		}
	}
	
	public AllModel getModel() {
		return model;
	}

	class Checkbox{
		public Button button;
		public GenTarget target;
		public Checkbox(Button button, GenTarget target) {
	        super();
	        this.button = button;
	        this.target = target;
        }
	}
	
	public static class AllModel{
		public enum GenTarget { Model, InputModel, OutputListModel, OutputRowModel, Controller, Service, Dao};
		
		String pkgName;
		String className;
		String description;
		List<GenTarget> targets = new ArrayList();
		
		public boolean isValid(){
			 if(!HongStringUtil.isValidName(pkgName)){
				 return false;
			 }
			 if(!HongStringUtil.isValidName(className)){
				 return false;
			 }
			 if(targets.size()==0){
				 return false;
			 }
			 return true;
		}
		

		public String getPkgName() {
			return pkgName;
		}
		public void setPkgName(String pkgName) {
			this.pkgName = pkgName;
		}
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		public List<GenTarget> getTargets() {
			return targets;
		}
		public void setTargets(List<GenTarget> targets) {
			this.targets = targets;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		
	}
	
	
}

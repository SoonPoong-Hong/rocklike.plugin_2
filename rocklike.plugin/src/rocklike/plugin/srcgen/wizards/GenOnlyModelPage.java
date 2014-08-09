package rocklike.plugin.srcgen.wizards;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import rocklike.plugin.util.HongStringUtil;

public class GenOnlyModelPage extends WizardPage {
	Text pkgText;
	Text classText;
	Text descriptionText;
	OnlyModel model = new OnlyModel();

	protected GenOnlyModelPage(String pageName) {
	    super(pageName);
	    setTitle("모델만 생성");	    
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
		label.setText("※ 마지막의 domain은 제외하고 쓰세요.\n(예) com.kt.cms.cuc.dst.dm");
		GridDataFactory.fillDefaults().applyTo(label);
		
		label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).span(2,1).grab(true, false).applyTo(label);
		
		label = new Label(composite, SWT.NULL);
		label.setText("클래스명");
		GridDataFactory.fillDefaults().span(1,2).align(SWT.BEGINNING,SWT.BEGINNING).applyTo(label);
		
		classText = new Text(composite, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(classText);
		
		label = new Label(composite, SWT.NULL);
		label.setText("(예)\ninput : DeployWorkViewCondition, output : DeployWorkViewList");
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

		setControl(composite);
		
		addListeners();
	}
	

	@Override
    public IWizardPage getNextPage() {
		return null;
    }
	

	public OnlyModel getModel() {
		return model;
	}
	
	
	public OnlyModel updateModel(){
		model.setPkgName(pkgText.getText());
		model.setClassName(classText.getText());
		model.setDescription(descriptionText.getText());
		return model;
	}
	
	
	private KeyListener buttonUpdaterByKey = new KeyAdapter(){
		@Override
        public void keyReleased(KeyEvent e) {
			updateModel();
			getWizard().getContainer().updateButtons();
        }
	};
	
	
	private void addListeners(){
		pkgText.addKeyListener(buttonUpdaterByKey);
		classText.addKeyListener(buttonUpdaterByKey);
	}

	
	public static class OnlyModel{
		String pkgName;
		String className;
		String description;
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
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public boolean isValid(){
			return HongStringUtil.isValidName(pkgName) && HongStringUtil.isValidName(className);
		}
	}
}

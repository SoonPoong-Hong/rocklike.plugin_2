package rocklike.plugin.srcgen.wizards;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class SelectGenScopePage extends WizardPage {
	
	public enum GenScope {OnlyModel, Fullset, NotYet};

	protected SelectGenScopePage(String pageName) {
	    super(pageName);
	    setTitle("선택하세요.");
	    setDescription("model만 생성할지, 아님 한 세트를 생성할지 선택");
    }
	
	Button[] btns = new Button[2];

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridDataFactory.fillDefaults().grab(true,true).applyTo(composite);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(composite);
		
		btns[0] = new Button(composite, SWT.RADIO);
		btns[0].setText("model만 생성하겠습니다.");
		btns[0].addSelectionListener(buttonUpdater);
//		btns[0].setToolTipText("model만 생성할때는, 패키지와 클래스명을 naming rule에 구애받지 않고 자유롭게 생성할수 있습니다.");
		
		btns[1] = new Button(composite, SWT.RADIO);
		btns[1].setText("한 세트(model,controller,service,dao등)을 생성하겠습니다.");
		btns[1].addSelectionListener(buttonUpdater);
//		btns[1].setToolTipText("모델을 한세트를 생성할때는 패키지와 클래스명이 naming rule에 맞게 변환되서 생성됩니다.");
		
		btns[1].setFocus();
		
		setControl(composite);
	}
	
	
	public GenScope getGenScope(){
		if(btns[0]==null){
			return GenScope.NotYet;
		}else if(btns[0].getSelection()){
			return GenScope.OnlyModel;
		}else if(btns[1].getSelection()){
			return GenScope.Fullset;
		}else{
			return GenScope.NotYet;
		}
	}

	
	@Override
    public IWizardPage getNextPage() {
		GenScope s = getGenScope();
		if(s==GenScope.OnlyModel){
			return ((HongNewFileCreateWizard)getWizard()).genOnlyModelPage;
		}else if(s==GenScope.Fullset){
			return ((HongNewFileCreateWizard)getWizard()).genAllModelPage;
		}
		return null;
    }


	@Override
    public boolean isPageComplete() {
		return getGenScope()!=GenScope.NotYet;
    }

	
	private SelectionListener buttonUpdater = new SelectionAdapter(){
		@Override
        public void widgetSelected(SelectionEvent e) {
			Object o = e.widget;
			getWizard().getContainer().updateButtons();
			if(o==btns[0]){
				setDescription("클래스명을 naming rule에 구애받지 않고 맘대로 생성할수 있습니다.");
			}else if(o==btns[1]){
				setDescription("패키지와 클래스명이 naming rule에 맞게 변환되서 생성됩니다.");
			}
        }
	};
}
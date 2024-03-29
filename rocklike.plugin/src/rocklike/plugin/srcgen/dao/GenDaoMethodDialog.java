package rocklike.plugin.srcgen.dao;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

import rocklike.plugin.util.HongDialogSettings;
import rocklike.plugin.util.HongEditorHelper;
import rocklike.plugin.util.HongEventHelper;
import rocklike.plugin.util.HongJavaUIHelper;
import rocklike.plugin.util.HongMessagePopupUtil;
import rocklike.plugin.util.HongMybatisHelper;

public class GenDaoMethodDialog extends Dialog{
	public GenDaoMethodDialog(IType type) {
	    super(Display.getDefault().getActiveShell());
	    this.type = type;
    }

	IType type;

	private static HongDialogSettings dialogSettings = new HongDialogSettings("rocklike.plugin.srcgen.GenDaoMethodDialog");
	List<Button> crudOptions = new ArrayList(2);
	List<Button> oneListOptions = new ArrayList(2);

	Text methodNameText;
	Text parameterTypeText;
	Text resultTypeText ;

	Button searchParameterTypeButton;
	Button searchResultTypeButton;

	GenDaoMethodRequest genDaoMethodRequest;

	@Override
    protected Control createDialogArea(Composite parent) {
	    Composite container = (Composite) super.createDialogArea(parent);
	    GridLayoutFactory.fillDefaults().numColumns(3).margins(8,1).applyTo(container);

	    Group group;

	    group = new Group(container, SWT.NULL);
	    group.setText("select/update");
	    GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).span(3,1).grab(true, false).applyTo(group);
	    GridLayoutFactory.fillDefaults().margins(2,2).numColumns(1).applyTo(group);

	    createRadioButtonOfSelectOrUpdate(group, "select");
	    createRadioButtonOfSelectOrUpdate(group, "update류(update/insert/delete)");

	    group = new Group(container, SWT.NULL);
	    group.setText("selectOne/selectList");
	    GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).span(3,1).grab(true, false).applyTo(group);
	    GridLayoutFactory.fillDefaults().margins(2,2).numColumns(1).applyTo(group);

	    createRadioButtonOfSelectOneOrList(group, "selectOne");
	    createRadioButtonOfSelectOneOrList(group, "selectList");

	    oneListOptions.get(0).setSelection(true);

	    new Label(container, SWT.NULL).setText("메소드명");
	    methodNameText = new Text(container, SWT.BORDER);
	    GridDataFactory.fillDefaults().span(2,1).grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(methodNameText);

	    new Label(container, SWT.NULL).setText("parameterType");
	    parameterTypeText = new Text(container, SWT.BORDER);
	    GridDataFactory.fillDefaults().span(1,1).grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(parameterTypeText);
	    searchParameterTypeButton = new Button(container, SWT.PUSH);
	    searchParameterTypeButton.setText("클래스 찾기");

	    new Label(container, SWT.NULL).setText("resultType");
	    resultTypeText = new Text(container, SWT.BORDER);
	    GridDataFactory.fillDefaults().span(1,1).grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(resultTypeText);
	    searchResultTypeButton = new Button(container, SWT.PUSH);
	    searchResultTypeButton.setText("클래스 찾기");

	    addSearchClzEvent();

	    return container;
    }

	Button createRadioButtonOfSelectOrUpdate(Composite composite, String text){
		Button b = new Button(composite, SWT.RADIO);
		b.setText(text);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(b);
		crudOptions.add(b);
		return b;
	}


	Button createRadioButtonOfSelectOneOrList(Composite composite, String text){
		Button b = new Button(composite, SWT.RADIO);
		b.setText(text);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(b);
		oneListOptions.add(b);
		return b;
	}


	void addSearchClzEvent(){
		searchParameterTypeButton.addSelectionListener(new SelectionAdapter(){
			@Override
            public void widgetSelected(SelectionEvent e) {
				IType type = HongJavaUIHelper.searchFromJavaTypeDialog("클래스를 선택하세요.", parameterTypeText.getText());
				if(type!=null){
					parameterTypeText.setText(type.getFullyQualifiedName());
				}
			}
		});
		searchResultTypeButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				IType type = HongJavaUIHelper.searchFromJavaTypeDialog("클래스를 선택하세요.", resultTypeText.getText());
				if(type!=null){
					resultTypeText.setText(type.getFullyQualifiedName());
				}
			}
		});

		HongEventHelper.addEventOfSelectAllWhenGetFocused(methodNameText);
		HongEventHelper.addEventOfSelectAllWhenGetFocused(parameterTypeText);
		HongEventHelper.addEventOfSelectAllWhenGetFocused(resultTypeText);


		for(Button thisB : crudOptions){
			thisB.addSelectionListener(new SelectionAdapter() {
				@Override
                public void widgetSelected(SelectionEvent e) {
					updateButtonEnableStatus();
                }
			});
		}

		for(Button thisB : oneListOptions){
			thisB.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateButtonEnableStatus();
				}
			});
		}

	}


	void updateButtonEnableStatus(){
		if(crudOptions.get(1).getSelection()){
			for(Button b : oneListOptions){
				b.setEnabled(false);
				resultTypeText.setEnabled(false);
			}
		}else{
			for(Button b : oneListOptions){
				b.setEnabled(true);
			}
			resultTypeText.setEnabled(true);
		}

	}


	@Override
    protected boolean isResizable() {
		return true;
    }

	@Override
    protected Point getInitialSize() {
		if(dialogSettings.isFirstOpen()){
			return new Point(600, 350);
		}else{
			return super.getInitialSize();
		}
    }

	@Override
    protected IDialogSettings getDialogBoundsSettings() {
		return dialogSettings;
    }

	@Override
    protected void okPressed() {
		genDaoMethodRequest = new GenDaoMethodRequest();
		genDaoMethodRequest.methodName = methodNameText.getText();
		genDaoMethodRequest.parameterType = parameterTypeText.getText();
		genDaoMethodRequest.resultType = resultTypeText.getText();
		genDaoMethodRequest.type = this.type;

		if(this.crudOptions.get(0).getSelection()){
			genDaoMethodRequest.selectUpdateOption = GenDaoMethodRequest.SelectUpdateOption.select;
		}else{
			genDaoMethodRequest.selectUpdateOption = GenDaoMethodRequest.SelectUpdateOption.update;
		}

		if(this.oneListOptions.get(0).getSelection()){
			genDaoMethodRequest.oneListOption = GenDaoMethodRequest.OneListOption.one;
		}else{
			genDaoMethodRequest.oneListOption = GenDaoMethodRequest.OneListOption.list;
		}

		if(!canOkProcess(genDaoMethodRequest)){
			return ;
		}

		DaoMethodCreator.addMethodInDaoImpl(genDaoMethodRequest);
		DaoMethodCreator.addMethodInDao(genDaoMethodRequest);
		processMybatisXml(genDaoMethodRequest);

	    super.okPressed();
    }




	boolean canOkProcess(GenDaoMethodRequest r){
		if(isBlank(r.methodName)){
			HongMessagePopupUtil.showErrMsg("메소드명 비었음.");
			methodNameText.setFocus();
			return false;

		}else if(isBlank(r.parameterType)){
			HongMessagePopupUtil.showErrMsg("parameterType 비었음.");
			parameterTypeText.setFocus();
			return false;

		}else if(r.selectUpdateOption==GenDaoMethodRequest.SelectUpdateOption.select && isBlank(r.resultType)){
			HongMessagePopupUtil.showErrMsg("resultType 비었음.");
			resultTypeText.setFocus();
			return false;

		}

		return true;
	}


	boolean isBlank(String str){
		return str==null || str.trim().length()==0;
	}

	void processMybatisXml(GenDaoMethodRequest request){
		String xmlId = HongMybatisHelper.assumeMybatisQueryXmlPackageId(request.type)+"." + request.methodName ;
		IFile xmlFile = HongMybatisHelper.assumeMybatisQueryXmlFilePath(request.type.getJavaProject().getProject(), xmlId);
		try {
	        IDOMModel domModel = HongMybatisHelper.appendToLastInMybatisXml(xmlFile, request.selectUpdateOption.name(), request.methodName, request.parameterType, request.resultType, "\n\n");
	        HongMybatisHelper.saveXml(domModel);
	        HongEditorHelper.openXmlEditorAndSelectById(xmlFile, request.methodName);
        } catch (Exception e) {
	        e.printStackTrace();
	        HongMessagePopupUtil.showErrMsg("Mybatis xml을 처리하다가 에러 ("+e.toString()+")");
        }
	}



	public GenDaoMethodRequest getGenDaoMethodResult() {
		return genDaoMethodRequest;
	}

	public void setType(IType type) {
		this.type = type;
	}

}

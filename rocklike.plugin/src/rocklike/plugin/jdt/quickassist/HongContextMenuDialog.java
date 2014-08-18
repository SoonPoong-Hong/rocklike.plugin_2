package rocklike.plugin.jdt.quickassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import rocklike.plugin.jdt.CallingMethodExtractor;
import rocklike.plugin.util.HongEclipseUtil;
import rocklike.plugin.util.HongEditorHelper;
import rocklike.plugin.util.HongJdtHelper;
import rocklike.plugin.util.HongMessagePopupUtil;

public class HongContextMenuDialog extends Dialog {

	enum WorkType{GenSetterCalling, GenVarFromGetter, MethodCallingTree};

	private WorkType workType;
	private List<Button> radioButtons = new ArrayList();
	private ICompilationUnitAndOffset icompilationunitAndOffset;

	public HongContextMenuDialog(Shell parentShell) {
	    super(parentShell);
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

		addRadioButton(container, "setter 호출 만들기." , WorkType.GenSetterCalling);
		addRadioButton(container, "getter로부터 받는 변수 만들기" , WorkType.GenVarFromGetter);
		addRadioButton(container, "메소드 호출 추적하기" , WorkType.MethodCallingTree);

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

		if(workType==WorkType.GenSetterCalling){
			CompilationUnit cu = HongJdtHelper.getCompilationUnit(icompilationunitAndOffset.icu) ;
			MethodInvocation mi = HongJdtHelper.resolveMethodInvocationByPosition(cu, icompilationunitAndOffset.offset);
			if(mi==null){
				HongMessagePopupUtil.showErrMsg("Setter/Getter 메소드 부분에 커서를 두고서 하세요.");
				close();
				return ;
			}
			mi.resolveTypeBinding().getName();
			IMethodBinding mb = mi.resolveMethodBinding();

			SetterCallSelectionDialog dialog = new SetterCallSelectionDialog(Display.getDefault().getActiveShell());
			dialog.setTypeBinding(mb.getDeclaringClass());
			if(dialog.open()==Window.OK){
				List<String> selectedSetterNames = dialog.getSelectedSetterNames();
				StringBuilder sb = new StringBuilder();
				ColumnDescVO columnDescVO = null;
				try {
	                columnDescVO = ColumnDescriptionStore.readFromLocal();
                } catch (Exception e) {
	                e.printStackTrace();
                }
				if(columnDescVO==null){
					columnDescVO = new ColumnDescVO();
				}

				for(String s : selectedSetterNames){
					String desc = ColumnDescriptionStore.getColumnDescriptionApproximately(s.substring(3), columnDescVO);
					sb.append(String.format("%s.%s(); %s\n", HongJdtHelper.getCallerObjectName(mi), s, (desc==null?"":"// " + desc)) );
				}
				if(sb.length()>0){
					try {
						int lineNum = icompilationunitAndOffset.doc.getLineOfOffset(icompilationunitAndOffset.offset);
						HongEditorHelper.writeToNextLine(icompilationunitAndOffset.doc , lineNum, sb.toString() );
					} catch (BadLocationException e) {
						e.printStackTrace();
						HongMessagePopupUtil.showErrMsg(e);
					}
				}
			}



		}else if(workType==WorkType.GenVarFromGetter){
			CompilationUnit cu = HongJdtHelper.getCompilationUnit(icompilationunitAndOffset.icu) ;
			MethodInvocation mi = HongJdtHelper.resolveMethodInvocationByPosition(cu, icompilationunitAndOffset.offset);
			if(mi==null){
				HongMessagePopupUtil.showErrMsg("Setter/Getter 메소드 부분에 커서를 두고서 하세요.");
				close();
				return ;
			}
			mi.resolveTypeBinding().getName();
			IMethodBinding mb = mi.resolveMethodBinding();

			GetterSelectionDialog dialog = new GetterSelectionDialog(Display.getDefault().getActiveShell());
			dialog.setTypeBinding(mb.getDeclaringClass());
			if(dialog.open()==Window.OK){
				List<IMethodBinding> selGetters = dialog.getSelectedGetters();
				StringBuilder sb = new StringBuilder();
				ColumnDescVO columnDescVO = null;
				try {
	                columnDescVO = ColumnDescriptionStore.readFromLocal();
                } catch (Exception e) {
	                e.printStackTrace();
                }
				if(columnDescVO==null){
					columnDescVO = new ColumnDescVO();
				}

				for(IMethodBinding thisMb : selGetters){
					String getterMethodName = thisMb.getName();
					String returnType = thisMb.getReturnType().getName();
					String var = getterMethodName.substring(3,4).toLowerCase() + getterMethodName.substring(4);
					String callerObj = HongJdtHelper.getCallerObjectName(mi);

					String desc = ColumnDescriptionStore.getColumnDescriptionApproximately(var, columnDescVO);
					// String contsId = vo.getContsId();
					sb.append(String.format("%s %s = %s.%s(); %s\n", returnType, var, callerObj, getterMethodName, (desc==null?"":"// " + desc)) );
				}
				if(sb.length()>0){
					try {
						int lineNum = icompilationunitAndOffset.doc.getLineOfOffset(icompilationunitAndOffset.offset);
						HongEditorHelper.writeToNextLine(icompilationunitAndOffset.doc , lineNum, sb.toString() );
					} catch (BadLocationException e) {
						e.printStackTrace();
						HongMessagePopupUtil.showErrMsg(e);
					}
				}
			}



		}else if(workType==WorkType.MethodCallingTree){
			System.out.println("== 메소드 호출 추적");
			CompilationUnit cu = HongJdtHelper.getCompilationUnit(icompilationunitAndOffset.icu) ;
			ITextSelection textSel = HongEclipseUtil.getTextSelection();
			int offset = textSel.getOffset();
			try {
	            CallingMethodExtractor.extractCallingMethodsAndOpen(cu, offset);
            } catch (JavaModelException e) {
	            e.printStackTrace();
	            HongMessagePopupUtil.showErrMsg(e);
            }
		}

	    super.okPressed();
    }

	public WorkType getWorkType() {
		return workType;
	}

	public void setICompilationUnitAndOffset(ICompilationUnitAndOffset icompilationunitAndOffset) {
		this.icompilationunitAndOffset = icompilationunitAndOffset;
	}
}

package rocklike.plugin.srcgen.wizards;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import rocklike.plugin.dialog.MsgDisplayDialog;
import rocklike.plugin.srcgen.freemarker.SourceGenerator;
import rocklike.plugin.srcgen.freemarker.SourceGenerator.SrcGenModel;
import rocklike.plugin.srcgen.wizards.GenAllModelPage.AllModel;
import rocklike.plugin.srcgen.wizards.GenAllModelPage.AllModel.GenTarget;
import rocklike.plugin.srcgen.wizards.GenOnlyModelPage.OnlyModel;
import rocklike.plugin.srcgen.wizards.SelectGenScopePage.GenScope;
import rocklike.plugin.util.HongEclipseUtil;
import rocklike.plugin.util.HongEditorHelper;
import rocklike.plugin.util.HongMessagePopupUtil;
import rocklike.plugin.util.StaticListHolder;

public class HongNewFileCreateWizard extends Wizard implements INewWizard {

	protected SelectGenScopePage selectGenScopePage;
	protected GenOnlyModelPage  genOnlyModelPage;
	protected GenAllModelPage  genAllModelPage;

	IProject project;

	public HongNewFileCreateWizard() {
		setWindowTitle("파일(java,xml)들을 생성.");
	}


	@Override
    public void addPages() {
		if(this.project==null){
			HongMessagePopupUtil.showErrMsg("프로젝트를 선택한후에 진행하세요.");
			return;
		}

		selectGenScopePage = new SelectGenScopePage("시작");
		addPage(selectGenScopePage);
		genOnlyModelPage = new GenOnlyModelPage("모델만");
		addPage(genOnlyModelPage);
		genAllModelPage = new GenAllModelPage("한 세트");
		addPage(genAllModelPage);
    }


	@Override
    public boolean canFinish() {
		GenScope genScope = selectGenScopePage.getGenScope();
		if(genScope==GenScope.NotYet){
			return false;
		}

		if(genScope==GenScope.OnlyModel){
			OnlyModel model = genOnlyModelPage.updateModel();
			return model!=null && model.isValid();

		}else if(genScope==GenScope.Fullset){
			AllModel model = genAllModelPage.updateModel();
			return model!=null && model.isValid();
		}

		return false;

    }


	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.project = HongEclipseUtil.getSelectedProject(selection);
	}


	@Override
	public boolean performFinish() {
		System.out.println("=== finish...");

		GenScope scope = selectGenScopePage.getGenScope();

		if(scope==GenScope.OnlyModel){
			//===== 모델만

			OnlyModel model = genOnlyModelPage.getModel();
			if(model==null || !model.isValid()){
				HongMessagePopupUtil.showErrMsg("값이 다 입력되지 않았습니다.");
				return false;
			}

			DefaultGenPkgAdvisor.setDefaultPackage(model.pkgName);

			SrcGenModel srcGenModel = new SrcGenModel();
			srcGenModel.setClassName(model.getClassName());
			srcGenModel.setPkgName(model.getPkgName());
			srcGenModel.setDescription(model.getDescription());

			// 동일한 파일이 존재하는지 체크.
			if(new SourceGenerator(project).isExists(srcGenModel, GenTarget.Model)){
				HongMessagePopupUtil.showErrMsg(String.format("파일이 이미 존재합니다.\n(%s)", GenTarget.Model));
				return false;
			}

			try {
				StaticListHolder.instance().reset();
	            new SourceGenerator(project).genModelFile(srcGenModel);
	            final IFile ifile = (IFile) StaticListHolder.instance().getList().get(0);
	            BusyIndicator.showWhile(Display.getDefault(), new Runnable(){
					@Override
                    public void run() {
						try {
	                        project.refreshLocal(ifile.DEPTH_INFINITE, new NullProgressMonitor());
                        } catch (CoreException e) {
	                        e.printStackTrace();
                        }
						HongEditorHelper.openTextEditorAndSelect(ifile);
                    }
	            });
            } catch (Exception e) {
	            e.printStackTrace();
	            HongMessagePopupUtil.showErrMsg(e, "소스 생성중 에러 발생..");
            }
			StaticListHolder.instance().reset();


		}else if(scope==GenScope.Fullset){
			//===== 한세트

			AllModel model = genAllModelPage.getModel();
			if(model==null || !model.isValid()){
				HongMessagePopupUtil.showErrMsg("값이 다 입력되지 않았습니다.");
				return false;
			}

			DefaultGenPkgAdvisor.setDefaultPackage(model.pkgName);

			try {
				SrcGenModel srcGenModel = new SrcGenModel();
				srcGenModel.setClassName(model.getClassName());
				srcGenModel.setPkgName(model.getPkgName());
				srcGenModel.setDescription(model.getDescription());

				// 동일한 파일이 존재하는지 체크
				for(GenTarget t : model.getTargets()){
					if(new SourceGenerator(project).isExists(srcGenModel, t)){
						HongMessagePopupUtil.showErrMsg(String.format("파일이 이미 존재합니다.\n(%s)", t));
						return false;
					}
				}

				StaticListHolder.instance().reset();

				for(GenTarget t : model.getTargets()){
					try {
	                    new SourceGenerator(project).genNewFile(srcGenModel, t);

                    } catch (Exception e) {
	                    e.printStackTrace();
//	                    HongMessagePopupUtil.showErrMsg(e, "파일 생성에 실패했습니다. ("+t+")");
                    }
				}

				//== 생성된 파일 목록 보여주기
				final List<IFile> list = StaticListHolder.instance().getList();
				StringBuilder sb = new StringBuilder();
				for(IFile f : list){
					sb.append(f.getName()).append("\n");
				}
				MsgDisplayDialog msgDialog = new MsgDisplayDialog(Display.getDefault().getActiveShell(), null, "파일 생성함.");
				msgDialog.setMsg("=== 아래의 파일들을 만들었습니다. ===\n\n" + sb.toString() + "\n프로젝트 refresh후, 새로 만든 파일을 열것입니다." );
				msgDialog.open();

				BusyIndicator.showWhile(Display.getDefault(), new Runnable(){
					@Override
                    public void run() {
						// 프로젝트 refresh()
						try {
	                        project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
                        } catch (CoreException e) {
	                        e.printStackTrace();
                        }

						// 만든 파일 open
						for(IFile f : list){
							HongEditorHelper.openTextEditorAndSelect(f);
						}
                    }
				});

            } catch (Exception e) {
	            e.printStackTrace();
	            HongMessagePopupUtil.showErrMsg(e, "소스 생성중 에러 발생..");
            }
			StaticListHolder.instance().reset();

		}

		StaticListHolder.instance().reset();

		return true;
	}


	public void setProject(IProject project) {
		this.project = project;
	}

}

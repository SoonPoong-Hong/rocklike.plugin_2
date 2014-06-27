package rocklike.plugin.mybatis;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import rocklike.plugin.mybatis.highlight.MybatisSourceViewerConfiguration;
import rocklike.plugin.mybatis.highlight.scanner.HongColorProvider;
import rocklike.plugin.util.HongEditorHelper;
import rocklike.plugin.util.HongJdtHelper;
import rocklike.plugin.util.HongMybatisHelper;
import rocklike.plugin.util.HongMybatisHelper.IFileAndId;

public class JavaEditorListeningView extends ViewPart {

	Text t;
	Button linkCheckbox;
	Button gotoBtn;
	boolean isHidden = false;
	SourceViewer sourceViewer;
	HongColorProvider colorProvider;
	ExecutorService executor;
	IProject selectedProj;

	
	private final String javaEditorId = "org.eclipse.jdt.ui.CompilationUnitEditor";
	
	public JavaEditorListeningView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		System.out.println("== 만들어짐.");
		
		executor = new ThreadPoolExecutor(
				  1
				, 5
				, 10L
				, TimeUnit.SECONDS
				, new LinkedBlockingQueue<Runnable>()
				, new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						t.setDaemon(true);
						return t;
				 }
		});
		
		colorProvider = new HongColorProvider();
		GridLayoutFactory.fillDefaults().numColumns(1).margins(3, 3).applyTo(parent);
		
		Composite linkingComposite = new Composite(parent, SWT.NULL);
		GridDataFactory.fillDefaults().align(GridData.FILL, GridData.FILL).grab(true, false).applyTo(linkingComposite);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(linkingComposite);
		
		linkCheckbox = new Button(linkingComposite, SWT.CHECK);
		linkCheckbox.setText("연결");
		linkCheckbox.setSelection(false);
		GridDataFactory.fillDefaults().align(GridData.BEGINNING, GridData.FILL).grab(false, true).applyTo(linkCheckbox);
		
		t = new Text(linkingComposite, SWT.NORMAL | SWT.BORDER);
		GridDataFactory.fillDefaults().align(GridData.FILL, GridData.FILL).grab(true, true).applyTo(t);
		
		gotoBtn = new Button(linkingComposite, SWT.PUSH);
		gotoBtn.setText("open");
		gotoBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String sqlId = t.getText();
				if(selectedProj!=null && sqlId.length()>0 ){
					HongMybatisHelper.openMybatisXmlFile(selectedProj, sqlId);
				}
			}
		});
		
		
		
		sourceViewer = new ProjectionViewer(parent, new VerticalRuler(12), null, false
				,  SWT.FULL_SELECTION  | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		GridDataFactory.fillDefaults().align(GridData.FILL, GridData.FILL).grab(true, true).applyTo(sourceViewer.getControl());
		
		new MybatisSourceViewerConfiguration(colorProvider, sourceViewer).setup();
		
		ISelectionService ss = getSite().getWorkbenchWindow().getSelectionService();
		ss.addPostSelectionListener( sl);
	}

	@Override
	public void setFocus() {
		t.setFocus();
	}

	@Override
	public void dispose() {
		ISelectionService ss = getSite().getWorkbenchWindow().getSelectionService();
		System.out.printf("== (%s) disposed됨 \n", this.getClass().getName());
		ss.removePostSelectionListener( sl);
		colorProvider.dispose();
		try {
			executor.shutdownNow();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.dispose();
	}

	private ISelectionListener sl = new ISelectionListener() {
		@Override
		public void selectionChanged(final IWorkbenchPart part, final ISelection sel) {
			if(!javaEditorId.equals( part.getSite().getId())){  
				return;
			}
			if(!linkCheckbox.getSelection()){
				return;
			}

			executor.execute( new Runnable(){
				@Override
				public void run() {
					if(sel instanceof ITextSelection){
						ITextSelection ts = (ITextSelection)sel;

						CompilationUnitEditor cud = (CompilationUnitEditor)part;
						IFile ifile = (IFile)cud.getEditorInput().getAdapter(IFile.class);
						CompilationUnit cu = HongJdtHelper.getCompilationUnit(ifile);
						MethodInvocation mi = HongMybatisHelper.MethodInvocationResolverByPosition.resolve(cu, ts.getOffset()+ts.getLength());
						if(mi!=null){
							final IMethodBinding mb = mi.resolveMethodBinding();
							final ITypeBinding tb = mb.getDeclaringClass();
							if(tb.getPackage().getName().endsWith(".dao")){
								IFile implFile = HongMybatisHelper.assumeDaoImplPath(ifile.getProject(), (IFile)tb.getJavaElement().getResource());
								if(implFile==null){
									Display.getDefault().syncExec(new Runnable(){
										@Override
										public void run() {
											t.setText("");
											sourceViewer.getTextWidget().setText("");
										}
									});									
									return;
								}
								CompilationUnit implCu = HongJdtHelper.getCompilationUnit(implFile);
//								MethodDeclaration implMethod = MethodDeclarationResolverByName.resolve(implCu, mb.getName());
								MethodDeclaration implMethod = HongMybatisHelper.resolveImplMethod(implCu, mb);
								final IFileAndId ifileAndId = HongMybatisHelper.ExtractMybatisXmlFileAndId.execute(implFile.getProject(), implMethod);
								
								if(ifileAndId!=null && ifileAndId.inputParam!=null && ifileAndId.ifile!=null && ifileAndId.id==null){
									Display.getDefault().syncExec(new Runnable(){
										@Override
										public void run() {
											t.setText(ifileAndId.inputParam); 
											sourceViewer.getTextWidget().setText("");
										}
									});
									
								}else if(ifileAndId!=null && ifileAndId.ifile!=null && ifileAndId.id!=null){
									selectedProj = implFile.getProject();
									IFile xmlFile = ifileAndId.ifile;
									String id = ifileAndId.id;
									try {
										final String contents = HongEditorHelper.getXmlContentsIncludingRef(xmlFile, id);
										Display.getDefault().syncExec(new Runnable(){
											@Override
											public void run() {
												t.setText(ifileAndId.inputParam); 
												sourceViewer.getTextWidget().setText(contents);
											}
										});
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								
							}
						}
					}
				}
			});
		}
	};
	
	
	

}

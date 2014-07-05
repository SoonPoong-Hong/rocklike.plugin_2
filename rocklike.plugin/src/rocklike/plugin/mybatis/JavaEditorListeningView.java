package rocklike.plugin.mybatis;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
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
import rocklike.plugin.util.HongEclipseUtil;
import rocklike.plugin.util.HongEditorHelper;
import rocklike.plugin.util.HongJdtHelper;
import rocklike.plugin.util.HongMybatisHelper;
import rocklike.plugin.util.HongMybatisHelper.IFileAndId;

/**
 * @author Hong SoonPoong (rocklike@gmail.com)
 * @date 2014. 7. 5.
 */
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
				, 3
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

			executor.execute( new LinkJobRunnable(sel, part) );
		}
	};
	
	
	
	private class LinkJobRunnable implements Runnable{
		private ISelection sel;
		private IWorkbenchPart part;

		public LinkJobRunnable(ISelection sel, IWorkbenchPart part) {
	        super();
	        this.sel = sel;
	        this.part = part;
        }

		@Override
        public void run() {
			if(sel instanceof ITextSelection){
				ITextSelection ts = (ITextSelection)sel;
				CompilationUnitEditor cud = (CompilationUnitEditor)part;
				IFile ifile = (IFile)cud.getEditorInput().getAdapter(IFile.class);
				selectedProj = ifile.getProject();
				ICompilationUnit icu = JavaCore.createCompilationUnitFrom(ifile);
				int pos = ts.getOffset() + ts.getLength();
				
				try {
	                IJavaElement je = icu.getElementAt(pos);
	                boolean isProcessed = false;
	                if(je instanceof IMethod){
	    				IMethod m = (IMethod) je;
	    				IType type = (IType) m.getParent();
	    				String name = type.getFullyQualifiedName();
//	    				System.out.printf("== full type name : %s \n", name);
	    				
	    				if(name.indexOf(".dao.")>-1){
	    					isProcessed = true;
	    					runInDao();
	    				}
	    				
	                }
	                if(!isProcessed){
	                	runInService();
	                }
	                
                } catch (JavaModelException e) {
	                e.printStackTrace();
                }
				
			}
		} // end run
		
		
		void runInDao() throws JavaModelException{
			ITextSelection ts = (ITextSelection)sel;
			
			CompilationUnitEditor cud = (CompilationUnitEditor)part;
			IFile ifile = (IFile)cud.getEditorInput().getAdapter(IFile.class);
			ICompilationUnit icu = JavaCore.createCompilationUnitFrom(ifile);
			int pos = ts.getOffset() + ts.getLength();
			
			IMethod m = (IMethod) icu.getElementAt(pos);
			IType type = (IType) m.getParent();
			String name = type.getFullyQualifiedName();
//			System.out.printf("== full type name : %s \n", name);
			
			if(name.indexOf(".dao.")==-1){
				return;
			}
			
			CompilationUnit cu = HongJdtHelper.getCompilationUnit(icu);
			if(!type.isInterface()){
				CompilationUnit implCu = cu;
				MethodDeclaration implMethod = HongJdtHelper.resolveMethodInImpl(implCu, m);
				final IFileAndId ifileAndId = HongMybatisHelper.extractMybatisXmlFileAndId(icu.getResource().getProject(), implMethod);
//				System.out.printf("== (impl) %s , %s , %s \n", ifileAndId.id , ifileAndId.inputParam , ifileAndId.ifile);
				exeIFileAndId(ifileAndId);
			}else{
				IFile implFile = HongMybatisHelper.assumeDaoImplPath(icu.getResource().getProject(), (IFile) icu.getResource().getAdapter(IFile.class));
				CompilationUnit implCu = HongJdtHelper.getCompilationUnit(implFile);
				MethodDeclaration implMethod = HongJdtHelper.resolveMethodInImpl(implCu, m);
				final IFileAndId ifileAndId = HongMybatisHelper.extractMybatisXmlFileAndId(implFile.getProject(), implMethod);
//				System.out.printf("== (interface) %s , %s , %s \n", ifileAndId.id , ifileAndId.inputParam , ifileAndId.ifile);
				exeIFileAndId(ifileAndId);
			}
		}
		
		
		
		void runInService(){
			ITextSelection ts = (ITextSelection)sel;

			CompilationUnitEditor cud = (CompilationUnitEditor)part;
			IFile ifile = (IFile)cud.getEditorInput().getAdapter(IFile.class);
			CompilationUnit cu = HongJdtHelper.getCompilationUnit(ifile);
			int pos = ts.getOffset() + ts.getLength();
			MethodInvocation mi = HongJdtHelper.resolveMethodInvocationByPosition(cu, pos);
			
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
					MethodDeclaration implMethod = HongJdtHelper.resolveMethodInImpl(implCu, mb);
					final IFileAndId ifileAndId = HongMybatisHelper.extractMybatisXmlFileAndId(implFile.getProject(), implMethod);
					
					exeIFileAndId(ifileAndId);
				}
			}
		}
		
		
		private void exeIFileAndId(final IFileAndId ifileAndId){
			if(ifileAndId!=null && ifileAndId.inputParam!=null && ifileAndId.ifile!=null && ifileAndId.id==null){
				Display.getDefault().syncExec(new Runnable(){
					@Override
					public void run() {
						t.setText(ifileAndId.inputParam); 
						sourceViewer.getTextWidget().setText("");
					}
				});
				
			}else if(ifileAndId!=null && ifileAndId.ifile!=null && ifileAndId.id!=null){
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
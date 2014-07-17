package rocklike.plugin.jdt.viewer;


import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

import rocklike.plugin.jdt.CacheSupportCompilationUnit;
import rocklike.plugin.jdt.CacheSupportTypeHierarchyResolver;
import rocklike.plugin.jdt.CachedMethodDeclarationHolder;
import rocklike.plugin.jdt.CallingMethodExtractor;
import rocklike.plugin.jdt.ExcludingClzMethodNameResolver;
import rocklike.plugin.jdt.MethodInvocationNode;
import rocklike.plugin.util.PreferenceStoreHelper;
import rocklike.plugin.util.RowHeightAdjustor;
import rocklike.plugin.util.TreeTableCellIndexResolver;

public class MethodCallTreeViewer extends ViewPart {
	public static final String ID = "rocklike.jdt.methodCall.viewer";
	private TreeViewer treeViewer;
	private Text excludeClassText;
	private Text excludeMethodText;
	private Label notiLabel;
	private Button clearCacheBtn;
	
	private final String EXCLUDE_CLASS_TEXT = "__hong_EXCLUDE_CLASS_TEXT__";
	private final String EXCLUDE_METHOD_TEXT = "__hong_EXCLUDE_METHOD_TEXT__";
	
	private final String DEFAULT_NOTI_MSG = "[단축키] 자바에디터에서 ctrl+alt+shitft+H , 더블클릭하면 해당 소스로 이동";
	
	public MethodCallTreeViewer() {
		super();
	}

	public void createPartControl(Composite parent) {
		GridLayoutFactory.fillDefaults().spacing(1,1).numColumns(1).equalWidth(false).applyTo(parent);
		
		Group excludesConditionGroup = new Group(parent, SWT.NULL);
		excludesConditionGroup.setText("제외시킬 클래스/메소드명 (코마로 구분)");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(excludesConditionGroup);
		GridLayoutFactory.fillDefaults().spacing(0,0).margins(2,2).numColumns(2).applyTo(excludesConditionGroup);
		
		Label label;
		label = new Label(excludesConditionGroup, SWT.NULL);
		label.setText("클래스명들");
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.FILL).grab(false, false).applyTo(label);
		excludeClassText = new Text(excludesConditionGroup, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(excludeClassText);
		label = new Label(excludesConditionGroup, SWT.NULL);
		label.setText("메소드명들");
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.FILL).grab(false, false).applyTo(label);
		excludeMethodText = new Text(excludesConditionGroup, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(excludeMethodText);
		
		Composite msgComposite = new Composite(parent, SWT.NULL);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(msgComposite);
		GridLayoutFactory.fillDefaults().spacing(0,0).margins(0,0).numColumns(2).applyTo(msgComposite);
		notiLabel = new Label(msgComposite, SWT.NULL);
		notiLabel.setText(DEFAULT_NOTI_MSG);
		notiLabel.setToolTipText(DEFAULT_NOTI_MSG );
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(notiLabel);
		clearCacheBtn = new Button(msgComposite, SWT.PUSH);
		clearCacheBtn.setText("cache clear");
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).applyTo(clearCacheBtn);
		
		
		Tree addressTree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION );
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(addressTree);
		
		addressTree.setHeaderVisible(true);
		treeViewer = new TreeViewer(addressTree);
		createTreeColumns(addressTree);
		treeViewer.setContentProvider(new MethodNodeProvider());
		treeViewer.setLabelProvider(new MethodLabelProvider());
		treeViewer.expandAll();
		
		new RowHeightAdjustor(treeViewer.getTree());
		
		addEvent();
		
		// preference
		excludeClassText.setText(PreferenceStoreHelper.getString(EXCLUDE_CLASS_TEXT));
		excludeMethodText.setText(PreferenceStoreHelper.getString(EXCLUDE_METHOD_TEXT));
	}

	

	private void createTreeColumns(Tree addressTree) {
		TreeColumn column1 = new TreeColumn(addressTree, SWT.LEFT);
		addressTree.setLinesVisible(true);
		column1.setAlignment(SWT.LEFT);
		column1.setText("메소드 호출부");
		column1.setWidth(500);
		TreeColumn column2 = new TreeColumn(addressTree, SWT.LEFT);
		column2.setAlignment(SWT.LEFT);
		column2.setText("메소드 구현부");
		column2.setWidth(500);
	}

	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	public void refreshContents(MethodInvocationNode node){

		// 이렇게 가상으로 root를 만들어주지 않으면, tree에서 원래의 root가 보이지 않는다.
		MethodInvocationNode psuedoRoot = new MethodInvocationNode(null, null);
		psuedoRoot.addChild(node);
		
		treeViewer.setInput(psuedoRoot);
		treeViewer.expandToLevel(2);
		showBusy(false);
	}
	
	
	
	public void clearContents(){
		treeViewer.setInput(null);
	}
	
	
	
	
	
	void addEvent(){
		excludeClassText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
//				System.out.printf("== modified : %s \n", e.data);
				ExcludingClzMethodNameResolver.get().setExcludingClz(excludeClassText.getText());
			}
		});
		excludeMethodText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
//				System.out.printf("== modified : %s \n", e.data);
				ExcludingClzMethodNameResolver.get().setExcludingMethod(excludeMethodText.getText());
			}
		});
		
		excludeClassText.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				System.out.printf("== viewer disposed : exclude class text \n" );
				PreferenceStoreHelper.setString(EXCLUDE_CLASS_TEXT, excludeClassText.getText());				
			}
		});
		excludeMethodText.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				System.out.printf("== viewer disposed : excluded method text \n");
				PreferenceStoreHelper.setString(EXCLUDE_METHOD_TEXT, excludeMethodText.getText());
			}
		});
		
		
		
		final Tree tree = treeViewer.getTree();
		
		tree.addMouseListener(new MouseListener(){
			@Override
            public void mouseDoubleClick(MouseEvent e) {
				System.out.printf("== double click (%s, %s) \n", e.x, e.y);
				System.out.printf("== item count : %s \n", tree.getItems().length);
				System.out.printf("== row count : %s \n", tree.getColumns().length);
				
				Point p = new Point(e.x, e.y);
				ViewerCell c = treeViewer.getCell(p);
				Object obj = c.getElement();
				
				if(!(obj instanceof MethodInvocationNode)){
					return;
				}
				
				int idx = TreeTableCellIndexResolver.resolveTreeColumnIndex(tree, new Point(e.x, e.y));
				System.out.printf("== tree idx : %s \n", idx);
				MethodInvocationNode node = (MethodInvocationNode)obj;
				System.out.printf("== double click : %s \n", node.getName());
			
				if(idx==0){
					node.openInvocation();
				}else if(idx==1){
					node.openDeclaration();
				}
            }
			@Override
            public void mouseDown(MouseEvent e) {
            }
			@Override
            public void mouseUp(MouseEvent e) {
            }
		});
		
		
		clearCacheBtn.addSelectionListener(new SelectionAdapter() {

			@Override
            public void widgetSelected(SelectionEvent e) {
				System.out.println("=== cache clear시킴.");
				CacheSupportTypeHierarchyResolver.get().clearCache();
				CacheSupportCompilationUnit.get().clearCache();
				CachedMethodDeclarationHolder.get().clearCache();
            }
			
		});
	}
	
	
	
	
	class MethodNodeProvider implements ITreeContentProvider {
		@Override
		public Object[] getChildren(Object e) {
			if(!(e instanceof MethodInvocationNode)){
				return new Object[0];
			}
			MethodInvocationNode node = (MethodInvocationNode)e;
			
			if(!node.isProcessed()){
				CallingMethodExtractor.extractCallingMethods(node);
			}
			
			if(!node.hasChildren()){
				return new Object[0];
			}
			return node.getChildren().toArray();
		}

		@Override
		public Object getParent(Object e) {
			if(!(e instanceof MethodInvocationNode)){
				return null;
			}
			
			MethodInvocationNode node = (MethodInvocationNode)e;
			return node.getParent();
		}

		@Override
		public boolean hasChildren(Object e) {
			if(!(e instanceof MethodInvocationNode)){
				return false;
			}
			
			MethodInvocationNode node = (MethodInvocationNode)e;
			if(!node.isProcessed()){
				CallingMethodExtractor.extractCallingMethods(node);
			}
			return node.hasChildren();
		}

		@Override
		public Object[] getElements(Object e) {
			if(!(e instanceof MethodInvocationNode)){
				return new Object[0];
			}
			
			MethodInvocationNode node = (MethodInvocationNode)e;
			return node.getChildren().toArray();
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	
	

	class MethodLabelProvider implements ITableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object e, int columnIndex) {
			if(!(e instanceof MethodInvocationNode)){
				return "[이상함]";
			}
			MethodInvocationNode node = (MethodInvocationNode)e;
			switch (columnIndex) {
			case 0:
				if(node.isRoot()){
					
				}
				return node.getName();
			case 1:
				MethodDeclaration tmpMethodDeclaration = node.resolveMethodDeclaration();
				if(tmpMethodDeclaration==null){
					return null;
				}
				IMethodBinding mb = tmpMethodDeclaration.resolveBinding();
				if(mb!=null){
					ITypeBinding[] parameterTypes = mb.getParameterTypes();
					StringBuilder sb = new StringBuilder();
					int cnt = parameterTypes==null ? 0 : parameterTypes.length;
					for(int i=0; i<cnt; i++){
						if(i>0){
							sb.append(", ");
						}
						sb.append(parameterTypes[i].getName() );
					}
					return mb.getDeclaringClass().getName() + " :: " + mb.getName() + " ("+sb + ")";
				}
				break;
			case 2 : 
				return null;
			default:
				break;
			}

			return null;
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}
	}




	@Override
    public void dispose() {
		// 최소한 이 viewer가 종료하면 TypeHierarchy를 clear시킨다.
		CacheSupportTypeHierarchyResolver.get().clearCache();
		CacheSupportCompilationUnit.get().clearCache();
		CachedMethodDeclarationHolder.get().clearCache();
	    super.dispose();
    }

	

	
	
}

package rocklike.plugin.util;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


/**
 * 
 * @author 홍순풍 (rocklike@gmail.com)
 * @created 2009. 9. 4.
 */
public class HongMessagePopupUtil {

	static public void showErrMsg(final String msg) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "에러", msg);
			}
		});
	}

	static public void showErrMsg(final Throwable th) {
		th.printStackTrace();
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "에러", th.toString());
			}
		});
	}

	static public void showErrMsg(final Throwable th, final String msg) {
		th.printStackTrace();
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "에러", msg + "\n\n"
				        + "에러내용 : " + th.toString());
			}
		});
	}

	static public void showInfomsg(final String msg) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), "홍", msg);
			}
		});
	}

	static public IWorkbenchPage getActivePage() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		return page;
	}

	static public IWorkbenchPart getActivePart() {
		IWorkbenchPart part = getActivePage().getActivePart();
		return part;
	}

	/**
	 * 해당 Point에 해당하는 Cell 을 구함.
	 * 
	 * @param table
	 * @param p
	 * @return
	 */
	static public Cell computeCellIdx(Table table, Point p) {

		TableItem item = table.getItem(p);
		if (item == null) {
			return null;
		}

		int count = table.getColumnCount();
		for (int i = 0; i < count; i++) {
			Rectangle rect = item.getBounds(i);
			if (rect.contains(p)) {
				Cell cell = new Cell();
				cell.column = i;
				cell.row = table.indexOf(item);
				return cell;
			}
		}

		return null;
	}

	static public class Cell {
		public Cell() {
		}

		public Cell(int row, int column) {
			super();
			this.row = row;
			this.column = column;
		}

		public int row;
		public int column;
		@Override
		public String toString() {
			return "Cell [row=" + row + ", column=" + column + "]";
		}
		
	}
	
	public static IStatusLineManager getStatusBarManager(ViewPart viewpart){
	    IStatusLineManager statusBarMgr = ((IViewSite) viewpart.getSite())
	    .getActionBars().getStatusLineManager();
	    return statusBarMgr;
	}

	public static IStatusLineManager getStatusBarManager(){
        IStatusLineManager statusBarMgr = ((IViewSite) PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage().getActivePart().getSite())
                .getActionBars().getStatusLineManager();
        return statusBarMgr;
	}
	
	

    private static int num = 0;
    
    /**
     * 특정 View를, 특정 perspective에서 연다.
     * 
     * @param event
     * @return
     * @throws ExecutionException
     * @throws Exception 
     */
    public static void openPerspectiveView(String perspectiveId, String viewId) throws Exception {
        IWorkbench wb = PlatformUI.getWorkbench();
        wb.showPerspective(perspectiveId, wb.getActiveWorkbenchWindow());
        wb.getActiveWorkbenchWindow().getActivePage().showView(viewId, null, 
                IWorkbenchPage.VIEW_ACTIVATE);
    }
    
    public static void showView(String viewId) throws PartInitException{
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .showView(viewId);
    }
    
    
    /**
     * 특정 View를, 특정 perspective에서 새로운 View로 연다.
     * (기존에 열려있어도 새로 연다.)
     * @param event
     * @return
     * @throws ExecutionException
     * @throws Exception 
     */
    public static IViewPart openAnotherView(String perspectiveId, String viewId) throws Exception {
        IWorkbench wb = PlatformUI.getWorkbench();
        wb.showPerspective(perspectiveId, wb.getActiveWorkbenchWindow());
        return wb.getActiveWorkbenchWindow().getActivePage()
        .showView(viewId, String.valueOf(++num), 
                IWorkbenchPage.VIEW_ACTIVATE);
    }
    
    
    /**
     * 현재 perspective에서 새로운 view를 연다. (기존에 열려 있어도 연다.)
     * @param viewId
     * @return
     * @throws Exception
     */
	public static IViewPart openActivePageAnotherView(String viewId, String SecondaryId) throws Exception {
		IWorkbench wb = PlatformUI.getWorkbench();
		return wb.getActiveWorkbenchWindow().getActivePage().showView(viewId, SecondaryId, IWorkbenchPage.VIEW_ACTIVATE);
	}
    
    
    /**
     * <pre>
     * 새로운 Thread안에서, 안전하게 window control들을 처리할수 있도록 해주는 기능.
     * [사용법]
     * 
     
     class{
             // 특정 메소드 안에서 다음과 같이 Job을 실행
             Job job = new MyJob("홍");
                 job.schedule();
         
             ~~~
             
            // inner class
            public class MyJob extends Job{
        
                public MyJob(String name) {
                    super(name);
                }
                
        
                @Override
                protected void canceling() {
                    super.canceling();
                    System.out.println("Job을 interrupt합니다.");
                    this.getThread().interrupt();
                }
        
        
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    try {
                        for(int i=0; i<100; i++){
                            System.out.printf("[run] %s\n", i);
                            this.getThread().sleep(1000);
                            MyUtil.runSafe(new SafeRunImpl(), false, txt1, i+"");
                        }
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    return null;
                }
            }
            
            // inner class
            public class SafeRunImpl implements SafeRun{
                public void run(Object... obj) throws Exception {            
                    ((Text)obj[0]).setText(obj[1].toString());
                }
            }
            ~~~
     }// class 끝.
 
     * </pre>
     * @param safeRun
     * @param ignoreException
     * @param obj
     * @throws Exception
     */
    public static void runSafe(final SafeRun safeRun, final boolean ignoreException, final Object... obj) throws Exception{
        Display.getDefault().syncExec(new Runnable(){
            public void run() {
                try {
                    safeRun.run(obj);
                } catch (Exception e) {                    
                    e.printStackTrace();
                    if(!ignoreException){
                        MessageDialog.openError(Display.getDefault().getActiveShell(), "Exception", e.toString());
                    }
                }
            }
        });
    }
    
    /**
     * runSafe를 하기 위해서 구현해야할 interface.
     * @author 홍순풍(rocklike@gmail.com)
     * @created 2010. 3. 28.
     */
    public static interface SafeRun{
        public void run(Object... obj) throws Exception;
    }

    
    public static IViewPart getView(String viewId){
    	IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    	IViewPart view = page.findView(viewId);
    	return view;
    }
    
    public static boolean isBlank(String str){
    	return str==null || str.trim().length()==0;
    }
    
    public static boolean isNotBlank(String str){
    	return !isBlank(str);
    }
}

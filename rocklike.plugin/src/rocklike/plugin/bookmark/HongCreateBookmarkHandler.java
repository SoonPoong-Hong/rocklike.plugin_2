package rocklike.plugin.bookmark;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;

import rocklike.plugin.util.HongEclipseUtil;

/**
 * @author Hong SoonPoong (rocklike@gmail.com)
 * @date 2013.
 */
public class HongCreateBookmarkHandler extends AbstractHandler {


	@Override
	public Object execute(ExecutionEvent e) throws ExecutionException {
//		MessageDialog.openInformation(Display.getDefault().getActiveShell(), "테스트", "테스트");
		System.out.printf("==== CreateHongBookmarkHandler \n");
		try {
			final String key = e.getParameter(HongBookmarkHelper.PARAM_KEY);
			System.out.printf("==== param [%s] \n", key);
			
			new Thread(new Runnable(){
				@Override
				public void run() {
					boolean isSccess;
					try {
						isSccess = HongBookmarkHelper.createMarker(key);
						if(!isSccess){
							HongEclipseUtil.setStatusLineMsg(key + " 번 bookmark를 만들지 못함.");
							System.out.printf("==== bookmark를 만들지 못했습니다. \n");
						}
					} catch (CoreException ex) {
						ex.printStackTrace();
						throw new RuntimeException(ex.getMessage(), ex);
					}
				}
			}).start();
			
			
//			Display.getDefault().asyncExec(new Runnable(){
//			new Thread( new Runnable(){
//				@Override
//				public void run() {
//					boolean isSccess;
//					try {
//						isSccess = HongBookmarkHelper.createMarker(key);
//						if(!isSccess){
//							HongEclipseUtil.setStatusLineMsg(key + " 번 bookmark를 만들지 못함.");
//							System.out.printf("==== bookmark를 만들지 못했습니다. \n");
//						}
//					} catch (CoreException e) {
//						e.printStackTrace();
//						throw new RuntimeException(e.getMessage(), e);
//					}
//				}
//			}).start();
//		});
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ExecutionException(ex.getMessage(), ex);
		}
		return null;
	}



}

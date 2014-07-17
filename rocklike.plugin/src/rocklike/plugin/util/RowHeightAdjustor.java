package rocklike.plugin.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class RowHeightAdjustor {

	private int rowHeight = -1;
	Control control;
	
	public RowHeightAdjustor(Control control) {
	    super();
	    this.control = control;
		control.addListener(SWT.MeasureItem, new Listener(){
			public void handleEvent(Event e) {
				if(rowHeight<0){
					rowHeight = e.gc.stringExtent("1ABgy국").y + 6;
				}
				e.height = rowHeight;
            }
		});
    }
	
	
}

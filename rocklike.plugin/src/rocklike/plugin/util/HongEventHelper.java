package rocklike.plugin.util;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Text;

public class HongEventHelper {

	public static void addEventOfSelectAllWhenGetFocused(final Text text){
		text.addFocusListener(new FocusAdapter(){
			@Override
            public void focusGained(FocusEvent e) {
				text.selectAll();
            }
		});
	}
}

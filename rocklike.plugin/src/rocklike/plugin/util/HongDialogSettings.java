package rocklike.plugin.util;

import org.eclipse.jface.dialogs.DialogSettings;

public class HongDialogSettings extends DialogSettings {
	private final String IS_EVER_OPENED = "IS_EVER_OPENED";
	public HongDialogSettings(String sectionName) {
	    super(sectionName);
    }

	public boolean isFirstOpen(){
		if("Y".equals(this.get(IS_EVER_OPENED))){
			return false;
		}else{
			this.put(IS_EVER_OPENED, "Y");
			return true;
		}
	}
}

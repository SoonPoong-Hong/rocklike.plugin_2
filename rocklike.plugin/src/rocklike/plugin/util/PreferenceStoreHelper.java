package rocklike.plugin.util;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

public class PreferenceStoreHelper {

	public static void setString(String key, String value){
		if(key==null || value==null){
			return;
		}
		getStore().setValue(key, value);
	}
	
	public static String getString(String key){
		return getStore().getString(key);
	}
	
	private static IPreferenceStore getStore(){
		IPreferenceStore store = IDEWorkbenchPlugin.getDefault().getPreferenceStore();
		return store;
	}
}

package rocklike.plugin.srcgen.wizards;

import rocklike.plugin.srcgen.SourceGenConstants;
import rocklike.plugin.util.PreferenceStoreHelper;

public class DefaultGenPkgAdvisor {
	public static String getDefaultPackage(){
		String val = PreferenceStoreHelper.getString(SourceGenConstants.LAST_STORED_PACKAGE_NAME);
		if(val==null || val.length()==0){
			val = "com.kt.cms.cuc";
		}
		return val;
	}
	
	public static void setDefaultPackage(String pkg){
		if(pkg==null || pkg.length()==0){
			return;
		}
		PreferenceStoreHelper.setString(SourceGenConstants.LAST_STORED_PACKAGE_NAME, pkg);
	}
}

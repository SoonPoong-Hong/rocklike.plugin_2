package rocklike.plugin.util;

import java.util.ArrayList;

import org.eclipse.core.runtime.Platform;

public class HongAdapterHelper {

	public static <T extends Class> T getAdapter(Object obj, T clz){
		String s = null;
		return (T)Platform.getAdapterManager().getAdapter(obj, clz);
	}
	
	public static void main(String[] args) {
	    getAdapter(new Object(), String.class);
	    getAdapter(new Object(), ArrayList.class);
    }
	
	<T> void test(){
		T o = null;
		o.getClass();
	}
	
}

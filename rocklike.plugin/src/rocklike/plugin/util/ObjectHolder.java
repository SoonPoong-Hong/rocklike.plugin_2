package rocklike.plugin.util;

import java.util.HashMap;
import java.util.Map;

public class ObjectHolder<T> {
	private Map<String, T> map = new HashMap();
	private final String KEY = "_KEY_";
	
	public Object put(T obj){
		return map.put(KEY, obj);
	}
	
	public T get(){
		return map.get(KEY);
	}
}

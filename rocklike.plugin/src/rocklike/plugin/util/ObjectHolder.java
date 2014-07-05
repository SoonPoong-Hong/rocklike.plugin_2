package rocklike.plugin.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hong SoonPoong (rocklike@gmail.com)
 * @date 2014. 7. 5.
 * @param <T>
 */
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

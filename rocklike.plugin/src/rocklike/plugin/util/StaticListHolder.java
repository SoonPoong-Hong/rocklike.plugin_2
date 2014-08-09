package rocklike.plugin.util;

import java.util.ArrayList;
import java.util.List;

public class StaticListHolder<T> {
	// singleton
	private StaticListHolder(){ }
	private static StaticListHolder self = new StaticListHolder();
	
	public static StaticListHolder instance(){
		return self;
	}
	
	private transient List<T> list = new ArrayList();
	
	public StaticListHolder<T> reset(){
		list.clear();
		return this;
	}
	
	public StaticListHolder<T> add(T t){
		list.add(t);
		return this;
	}
	
	public List<T> getList(){
		return list;
	}
	
}

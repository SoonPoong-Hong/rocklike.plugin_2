package rocklike.plugin.util;

import java.lang.reflect.Method;

import rocklike.plugin.test.JavaUITest;

public class HongClassUtil {

	public static Object callMethod(Object obj, String methodName) throws Exception{
		Method m = obj.getClass().getDeclaredMethod(methodName);
		m.setAccessible(true);
		return m.invoke(obj);
	}

	public static Object callMethod(Object obj, String methodName, Class paramClz, Object param) throws Exception{
		Method m = obj.getClass().getDeclaredMethod(methodName, paramClz);
		m.setAccessible(true);
		return m.invoke(obj, param);
	}

	public static Object callMethod(Object obj, String methodName, Class[] paramClz, Object[] params) throws Exception{
		Method m = obj.getClass().getDeclaredMethod(methodName, paramClz);
		m.setAccessible(true);
		return m.invoke(obj, params);
	}

}

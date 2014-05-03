package com.espressif.iot.util;

import java.lang.reflect.Method;

/**
 * some method about reflection is here to make code more readable
 * @author afunx
 *
 */
public class Reflect {
	/**
	 * call back method, 
	 * the method must return boolean.
	 * if the method is static, obj should be null
	 * else obj should be the instance which invoke the method
	 *  
	 * @param tryMax					the max time for retry
	 * @param canonicalClassName		the canonicalClassName, including package name
	 * @param obj						the object of the instance
	 * @param methodName				the static method to be called
	 * @param sleepMilliSeconds			sleep milliseconds if it fail
	 * @return							whether it succeed in the try max time
	 */
	public static boolean Retry(int tryMax , String canonicalClassName, Object obj, String methodName
			,int sleepMilliSeconds){
		Method method = null;
		try {
			method = Class.forName(canonicalClassName).getDeclaredMethod(methodName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		method.setAccessible(true);
		for(int tryTime=0;tryTime<tryMax;tryTime++){
			try {
				boolean result = ((Boolean) method.invoke(obj));
				if(result)
					return true;
				Thread.sleep(sleepMilliSeconds);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
}

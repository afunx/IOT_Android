package com.espressif.iot.mediator.json;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.json.JSONObject;


/**
 * jsonObject <==> object
 * json is the data type of REST(POST,GET)
 * JsonParser could parse jsonObject to object(instance of some class),and vice versa
 * 
 * Usage:
 * the class to support "GET", please refer to Info.java
 * the class to support "POST", please refer to WifiConfigureSoftAP.java
 * the class to support "GET" and "POST", please refer to WifiConfigure.java
 * 
 * in the json model classes, 
 * 		getXyz is used by other classes to access the value of it(json model class)
 * 		setxyz is used by JsonParser' unparse()
 * why "x" in "getXyz" Upper-case while in "setxyz" Lower-case?
 * it is just to distinguish from JsonPaser and other classes
 * "getXyz" is used by other classes
 * "setxyz" is used by JsonParser
 * usually, "getXyz" and "setXyz" are used as the function name.
 * therefore, "setxyz" is used to make it different from usual use.
 * (we could use "setXyz" in JsonParser, but i don't think this is
 * a good idea. if you think differently, just do as you like)
 * 
 * @author afunx
 *
 */
public class JsonParser {

	/**
	 * !!!NOTE: if there's more primitive type in the object's field,
	 * 			please add here.
	 * 
	 * check whether the field's type is primitive
	 * @param f		the filed to be checked
	 * @return		whether the field of f is primitive
	 */
	private static boolean isPrimitive(Field f) {
		return f.getType() == String.class 
				|| f.getType() == Integer.class
				|| f.getType() == Boolean.class
				|| f.getType() == Long.class;
	}

	/**
	 * Parse an object to jsonObject
	 * @param o		the object to be transferred to jsonObject
	 * @return		the jsonObject
	 */
	public static JSONObject parse(Object o) {
		Class<?> clazz = o.getClass();
		JSONObject jsonObject = new JSONObject();
		try {
			// traverse the all fields of the clazz
			for (Field f : clazz.getDeclaredFields()) {
				f.setAccessible(true);

				if (isPrimitive(f)) {
					// if f is Integer,String..., put it into jsonObject
					jsonObject.put(f.getName(), f.get(o));
				} else {
					// else call parse() recursively and put the jsonObject into its parent
					JSONObject temp = parse(f.get(o));
					jsonObject.put(f.getName(), temp);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	/**
	 * extract the object's property from jsonObject
	 * @param jsonObject	the jsonObject to be extracted from
	 * @param o				the object to be extracted to
	 */
	public static void unparse(JSONObject jsonObject, Object o) {
		Class<?> clazz = o.getClass();
		try {
			// traverse the all fields of the clazz
			for (Field f : clazz.getDeclaredFields()) {
				f.setAccessible(true);
				if (isPrimitive(f)) {
					// if f is Integer,String..., call set+xxx dynamically
					Method m;
					m = clazz.getDeclaredMethod("set" + f.getName(),
							f.getType());
					m.setAccessible(true);
					m.invoke(o, jsonObject.get(f.getName()));
				} else {
					// else call unparse() recursively to set its children's property
					unparse(jsonObject.getJSONObject(f.getName()), f.get(o));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

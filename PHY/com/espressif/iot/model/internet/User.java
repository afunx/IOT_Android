package com.espressif.iot.model.internet;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Each User has a user key, which indicate the user's identity
 * 
 * 
 * @author afunx
 *
 */
public class User {
	
	public static String token;
	public static long id;
	
	private static List<String> deviceKeyList = new CopyOnWriteArrayList<String>();
	public static void clearDeviceKeyList(){
		deviceKeyList.clear();
	}
	public static void addDeviceKey(String deviceKey){
		deviceKeyList.add(deviceKey);
	}
	public static List<String> getDeviceKeyList(){
		return deviceKeyList;
	}
}

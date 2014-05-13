package com.espressif.iot.util;

public class Util {
	public static void Sleep(long milliSeconds){
		try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

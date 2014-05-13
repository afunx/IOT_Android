package com.espressif.iot.tasknet.discover.wan;

import android.content.Context;


public interface IntWifiSnifferWAN extends IntSnifferWAN {
	/**
	 * sniffer whether the wifi is accessible to the Internet
	 * 
	 * if network(AP) is accessible and the Internet is accessible, return true
	 * else return false
	 * 
	 * @param context	the context
	 * 
	 * @return
	 */
	boolean sniffer(Context context);
}

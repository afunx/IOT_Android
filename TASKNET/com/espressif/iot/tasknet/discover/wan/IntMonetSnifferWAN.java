package com.espressif.iot.tasknet.discover.wan;

import android.content.Context;

public interface IntMonetSnifferWAN extends IntSnifferWAN {
	/**
	 * sniffer whether the mobile information flow is accessible to the Internet
	 * 
	 * if network(AP) is not accessible and the mobile information flow is accessible 
	 * to the Internet, return true
	 * else return false
	 * 
	 * @param context	the context
	 * 
	 * @return
	 */
	public boolean sniffer(Context context);
}

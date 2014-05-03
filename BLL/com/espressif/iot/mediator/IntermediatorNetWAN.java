package com.espressif.iot.mediator;

import android.content.Context;

import com.espressif.iot.tasknet.discover.Discoverer;

/**
 * it is used to intermediate something about the net of WAN
 * @author afunx
 *
 */
public class IntermediatorNetWAN {
	public static IntermediatorNetWAN instance = new IntermediatorNetWAN();
	private Discoverer discoverer = Discoverer.getInstance();

	// Singleton Pattern
	private IntermediatorNetWAN() {
	}

	public static IntermediatorNetWAN getInstance() {
		return instance;
	}
	
	public boolean isInternetAccessedMonetWANSyn(Context context){
		return discoverer.isInternetAccessedMonetWANSyn(context);
	}
	
	public boolean isInternetAccessedWifiWANSyn(Context context){
		return discoverer.isInternetAccessedWifiWANSyn(context);
	}
}

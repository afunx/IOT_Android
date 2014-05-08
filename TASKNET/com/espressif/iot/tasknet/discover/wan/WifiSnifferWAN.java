package com.espressif.iot.tasknet.discover.wan;

import android.content.Context;
import android.util.Log;

import com.espressif.iot.constants.CONSTANTS;
import com.espressif.iot.net.wan.NetChecker;
import com.espressif.iot.tasknet.net.wan.InternetAvailableHelper;
import com.espressif.iot.util.Logger;

public class WifiSnifferWAN implements IntWifiSnifferWAN {

	private static final String TAG = "WifiSnifferWAN";
//	private static final int RETRY_TIMES = 1;

	private NetChecker netChecker = NetChecker.getInstance();

	// Singleton Pattern
	private static WifiSnifferWAN instance = new WifiSnifferWAN();

	private WifiSnifferWAN() {
	}

	public static WifiSnifferWAN getInstance() {
		return instance;
	}

	@Override
	public boolean sniffer(Context context) {
		// TODO Auto-generated method stub
		boolean result = false;
		for (int i = 0; i < CONSTANTS.INTERNET_AVAILABLE_RETRY && !result; i++) {
			if (netChecker.isWifiAvailable(context)) {
				Logger.d(TAG, "wifi has connected to some AP(network).");
//				if (netChecker.isInternetAvailable("www.baidu.com")) {
				if (InternetAvailableHelper.isInternetAvailableSyn()) {
					Logger.d(TAG,
							"wifi is accessed to the Internet, wifi sniffer = true");
					// return true;
					result = true;
				} else {
					Logger.d(TAG,
							"wifi isn't accessed to the Internet, wifi sniffer = false");
					// return false;
					result = false;
				}
			} else {
				Logger.d(TAG,
						"wifi hasn't connected to any AP(network), wifi sniffer = false");
				// return false;
				result = false;
				// if wifi is not connected to any AP, there's no need to retry
				break;
			}
		}
		return result;
	}

}

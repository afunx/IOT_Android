package com.espressif.iot.tasknet.discover.wan;

import com.espressif.iot.constants.CONSTANTS;
import com.espressif.iot.net.wan.NetChecker;
import com.espressif.iot.tasknet.net.wan.InternetAvailableHelper;

import android.content.Context;
import android.util.Log;

public class MonetSnifferWAN implements IntMonetSnifferWAN {

	private NetChecker netChecker = NetChecker.getInstance();

	private static final String TAG = "MonetSnifferWAN";
//	private static final int RETRY_TIMES = 1;

	private static MonetSnifferWAN instance = new MonetSnifferWAN();

	// Singleton Pattern
	private MonetSnifferWAN() {
	}

	public static MonetSnifferWAN getInstance() {
		return instance;
	}

	@Override
	public boolean sniffer(Context context) {
		// TODO Auto-generated method stub
		boolean result = false;
		for (int i = 0; i < CONSTANTS.INTERNET_AVAILABLE_RETRY; i++) {
			if (netChecker.isWifiAvailable(context)) {
				Log.d(TAG,
						"wifi has connected to some AP(network), monet sniffer = false");
				// return false;
				result = false;
				// if wifi is connected to any AP, there's no need to retry
				break;
			} else {
				Log.d(TAG, "wifi hasn't connected to any AP(network).");
				if (netChecker.isMonetAvailable(context)) {
					Log.d(TAG, "monet is available");
//					if (netChecker.isInternetAvailable("www.baidu.com")) {
					if (InternetAvailableHelper.isInternetAvailableSyn()) {
						Log.d(TAG,
								"monet is accessed to the Internet, monet sniffer = true");
						// return true;
						result = true;
						break;
					} else {
						Log.d(TAG,
								"monet isn't accessed to the Internet, monet sniffer = false");
						// return false;
						result = false;
					}

				} else {
					Log.d(TAG, "monet is not available, monet sniffer = false");
					// return false;
					result = false;
				}
			}
		}
		return result;
	}
}

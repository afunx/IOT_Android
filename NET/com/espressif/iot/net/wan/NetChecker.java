package com.espressif.iot.net.wan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.espressif.iot.util.Logger;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * this class is used to check whether the android could connect to the
 * Internet, and which way is used, wifi or monet(e.g. 3G)
 * 
 * @author afunx
 * 
 */
public class NetChecker {

	private static final String TAG = "NetChecker";

	private static NetChecker instance = new NetChecker();

	private static double PING_TIMEOUT_SECOND = 1;
	private static int PING_TIMEOUT_TIME = 1;

	// Singleton Pattern
	private NetChecker() {
	}

	public static NetChecker getInstance() {
		return instance;
	}

	// private boolean isWifiOpened(Context context){
	// WifiManager wifiManager = (WifiManager)
	// context.getSystemService(Context.WIFI_SERVICE);
	// return wifiManager.getWifiState()==WifiManager.WIFI_STATE_ENABLED;
	// }

	/**
	 * Indicates whether monet(mobile net,e.g. 2G,3G,4G) is available !!!NOTE:
	 * if wifi is open and connect to one AP, no matter whether the wifi is
	 * available to the Internet, isMonetAvailable() will return false forever
	 * 
	 * @param context
	 * @return
	 */
	public boolean isMonetAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkINfo = cm.getActiveNetworkInfo();
		if (networkINfo != null
				&& networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}

	/**
	 * Indicates whether wifi network connectivity is possible. It is to say
	 * whether the wifi is connected to some network, whether the network is
	 * accessed to the Internet
	 * 
	 * @see NetworkInfo#isAvailable()
	 * 
	 * @param context
	 * @return
	 */
	public boolean isWifiAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkINfo = cm.getActiveNetworkInfo();
		if (networkINfo != null
				&& networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * the code here is so ugly!!!(if you have better solution, please fix it)
	 * 
	 * for p = Runtime.getRuntime().exec(), p.waitFor();
	 * will block when wifi is connected to some AP, which can't be
	 * accessed to the Internet.
	 * 
	 * @param pingAddress
	 *            the address used to ping
	 * @param process
	 * 			  the process[] is just like a hook
	 * 
	 * @return whether the Internet is available
	 */
	public boolean isInternetAvailable(String pingAddress, Process process[]) {
		int status = -1;
		try {
			String command = "/system/bin/ping -c 1 -w 2 " + pingAddress;
			Logger.d("NetChecker", "command =" + command);
			process[0] = Runtime.getRuntime().exec(command);
			status = process[0].waitFor();
			if (status == 0) {
				Logger.d(TAG, "isInternetAvailable() = true");
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.d(TAG, "isInternetAvailable() = false");
		return false;
	}
}

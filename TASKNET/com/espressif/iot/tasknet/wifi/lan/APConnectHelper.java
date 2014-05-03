package com.espressif.iot.tasknet.wifi.lan;

import java.util.concurrent.TimeUnit;

import com.espressif.iot.constants.WIFI_ENUM;
import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.thread.AbsTaskAsyn;
import com.espressif.iot.thread.AbsTaskSyn;
import com.espressif.iot.thread.FixedThreadPool;

public class APConnectHelper {

	private static FixedThreadPool mThreadPool = FixedThreadPool.getInstance();

	private static final TimeUnit TimeoutUnit = TimeUnit.SECONDS;

	/**
	 * check whether the wifi is connected
	 * 
	 * @param wifiAdmin
	 *            instance of wifiAdmin
	 * @param timeoutSeconds
	 *            the seconds of timeout
	 */
	public static boolean isConnectedSyn(WifiAdmin wifiAdmin, int timeoutSeconds) {
		AbsTaskSyn<Boolean> checkWifiConnectTask = new CheckAPConnectedTask(
				"check wifi connect task", wifiAdmin);

		mThreadPool.executeSyn(checkWifiConnectTask, timeoutSeconds,
				TimeoutUnit);

		return checkWifiConnectTask.getResult();
	}

	/**
	 * connect wifi
	 * 
	 * @param wifiAdmin
	 *            the instance of wifiadmin
	 * @param SSID
	 *            the AP's SSID, which to be connected
	 * @param isNoPassword
	 * 			  whether the AP is no password
	 */
	public static void connectAsyn(WifiAdmin wifiAdmin, String SSID, boolean isNoPassword) {

		AbsTaskAsyn connectWifiConnectTask = new ConnectAPTask(
				"connectWifiConnectTask", wifiAdmin, SSID, isNoPassword);

		mThreadPool.executeAsyn(connectWifiConnectTask);

	}

	/**
	 * connect wifi
	 * 
	 * @param wifiAdmin
	 *            the instance of wifiadmin
	 * @param SSID
	 *            the AP's SSID, which to be connected
	 * @param password
	 *            the AP's password, which to be connected
	 * @param type
	 *            the AP's cipher type, which to be connected
	 */
	public static void connectAsyn(WifiAdmin wifiAdmin, String SSID,
			String password, WIFI_ENUM.WifiCipherType type) {

		AbsTaskAsyn connectWifiConnectTask = new ConnectAPTask(
				"connectWifiConnectTask", wifiAdmin, SSID, password, type);

		mThreadPool.executeAsyn(connectWifiConnectTask);
	}
	
}

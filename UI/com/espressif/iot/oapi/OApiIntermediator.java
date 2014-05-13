package com.espressif.iot.oapi;

import java.util.List;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.os.Handler;

import com.espressif.iot.constants.WIFI_ENUM;
import com.espressif.iot.model.device.IOTAddress;
import com.espressif.iot.model.manager.Administrator;
import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.net.lan.wifi.WifiScanResult;

import com.espressif.iot.tasknet.discover.lan.APSnifferLAN;
import com.espressif.iot.net.lan.wifi.WifiScanResultListCreater;
import com.espressif.iot.tasknet.discover.wan.WifiSnifferWAN;

/**
 * 
 * it is used for secondary developing in the future
 * 
 * @author afunx
 * 
 */
public class OApiIntermediator {

	private Administrator administrator = Administrator.getInstance();

	private static OApiIntermediator instance = new OApiIntermediator();

	// Singleton Pattern
	private OApiIntermediator() {
	}

	public static OApiIntermediator getInstance() {
		return instance;
	}

	/**
	 * whether the Internet is accessible by 2g/3g/4g
	 * 
	 * @param context
	 *            the Application Context
	 * @return whether the Internet is accesssible by 2g/3g/4g
	 */
	public boolean isInternetAccessedMonetWANSyn(Context context) {
		return administrator.isInternetAccessedMonetWANSyn(context);
	}

	/**
	 * whether the Internet is accessible by wifi WAN
	 * 
	 * @param context
	 *            the Application Context
	 * @return whether the Internet is accessible by wifi WAN
	 * 
	 * @see WifiSnifferWAN#sniffer(Context)
	 */
	public boolean isInternetAccessedWifiWANSyn(Context context) {
		return administrator.isInternetAccessedWifiWANSyn(context);
	}

	/**
	 * get current AP(which is connected now)'s BSSID
	 * 
	 * @param wifiAdmin
	 *            the WifiAdmin
	 * @return the current AP's BSSID
	 * 
	 * @see WifiAdmin#getBSSID()
	 */
	public String getBSSIDSyn(WifiAdmin wifiAdmin) {
		return administrator.getBSSIDSyn(wifiAdmin);
	}

	/**
	 * scan APs by wifi synchronously
	 * 
	 * @param wifiAdmin
	 *            the WifiAdmin
	 * @param isEspDevice
	 *            whether the AP must ESP's devices
	 * @return the WifiScanResult list
	 * 
	 * @see WifiScanResult
	 * @see WifiAdmin
	 * @see APSnifferLAN#sniffSyn(WifiAdmin, boolean)
	 * @see WifiScanResultListCreater#createWifiScanResultList(List, boolean)
	 */
	public List<WifiScanResult> scanAPsLANSyn(WifiAdmin wifiAdmin,
			boolean isEspDevice) {
		return administrator.scanAPsLANSyn(wifiAdmin, isEspDevice);
	}

	/**
	 * scan STAs in LAN(the AP is connected) synchronously
	 * 
	 * @return the list of the IOTAddress
	 */
	public List<IOTAddress> scanSTAsLANSyn() {
		return administrator.scanSTAsLANSyn();
	}

	/**
	 * @deprecated check whether there's some STA exist the AP connected
	 * 
	 * @param iotAddress
	 *            the IOTAddress
	 * @param timeoutSeconds
	 *            seconds of timeout
	 * @return whether there's some STA exist in the AP
	 */
	public boolean isSTAExistLANSyn(IOTAddress iotAddress, int timeoutSeconds) {
		return administrator.isSTAExistLANSyn(iotAddress, timeoutSeconds);
	}

	/**
	 * disconnect from the current AP synchronously
	 * 
	 * @param wifiAdmin
	 *            the wifiAdmin
	 * 
	 * @see WifiAdmin#disconnectSyn()
	 */
	public void disconnectWifiSyn(WifiAdmin wifiAdmin) {
		administrator.disconnectWifiSyn(wifiAdmin);
	}

	/**
	 * get the AP's status, whose SSID is the specified one
	 * 
	 * @param wifiAdmin
	 *            the wifiAdmin
	 * @param SSID
	 *            the AP's SSID
	 * @return WIFI_ENUM.WifiStatus
	 * 
	 * @see WifiAdmin#getStatus(String)
	 * @see WIFI_ENUM
	 */
	public WIFI_ENUM.WifiStatus getWifiStatusSyn(WifiAdmin wifiAdmin,
			String SSID) {
		return administrator.getWifiStatusSyn(wifiAdmin, SSID);
	}

	/**
	 * get the connecting wifi's info, it hasn't been used now
	 * 
	 * @param wifiAdmin
	 *            the wifiAdmin
	 * @return the WifiInfo which is connected now
	 * @see WifiAdmin#getConnectionInfo
	 */
	public WifiInfo getConnectionInfoSyn(WifiAdmin wifiAdmin) {
		return administrator.getConnectionInfoSyn(wifiAdmin);
	}

	/**
	 * connect to the AP asynchronously, using Android's configuration or
	 * nopassword
	 * 
	 * @param wifiAdmin
	 *            the wifiAdmin
	 * @param SSID
	 *            the AP's SSID
	 * @param isNoPassword
	 *            the AP's password, if it has password
	 */
	public void connectAPAsyn(WifiAdmin wifiAdmin, String SSID,
			boolean isNoPassword) {
		administrator.connectAPAsyn(wifiAdmin, SSID, isNoPassword);
	}

	/**
	 * connect to the AP asynchronously, using user's input
	 * 
	 * @param wifiAdmin
	 *            the wifiAdmin
	 * @param SSID
	 *            the AP's SSID
	 * @param password
	 *            the AP's password, if it has password
	 * @param type
	 *            the AP's TYPE
	 * @see WifiAdmin
	 * @see WIFI_ENUM
	 */
	public void connectAPAsyn(WifiAdmin wifiAdmin, String SSID,
			String password, WIFI_ENUM.WifiCipherType type) {
		administrator.connectAPAsyn(wifiAdmin, SSID, password, type);
	}

	/**
	 * check whether the Some AP is connected by the android device
	 * 
	 * @param wifiAdmin
	 *            the wifiAdmin
	 * @return whether it is connected in the timeout
	 * @see WifiAdmin
	 */
	public boolean isAPConnectedSyn(WifiAdmin wifiAdmin, int timeoutSeconds) {
		return administrator.isAPConnectedSyn(wifiAdmin, timeoutSeconds);
	}

	@Deprecated
	public void reconnectAsyn(Handler handler, WifiAdmin wifiAdmin,
			String SSID, String BSSID, WIFI_ENUM.WifiCipherType type) {
		administrator.reconnectAsyn(handler, wifiAdmin, SSID, BSSID, type);
	}

	@Deprecated
	public void checkAPConnectedAsyn(Handler handler, WifiAdmin wifiAdmin,
			String BSSID) {
		administrator.checkAPConnectedAsyn(handler, wifiAdmin, BSSID);
	}

}

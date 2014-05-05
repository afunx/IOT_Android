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

// test master branch

/**
 *
 * it is used for secondary developing in the future
 * @author afunx
 *
 */
public class OApiIntermediator {
	
	private Administrator administrator = Administrator.getInstance();
	
	private static OApiIntermediator instance = new OApiIntermediator();
	
	// Singleton Pattern
	private OApiIntermediator(){
	}
	public static OApiIntermediator getInstance(){
		return instance;
	}
	
	public boolean isInternetAccessedMonetWANSyn(Context context){
		return administrator.isInternetAccessedMonetWANSyn(context);
	}
	
	public boolean isInternetAccessedWifiWANSyn(Context context){
		return administrator.isInternetAccessedWifiWANSyn(context);
	}
	
	public String getBSSIDSyn(WifiAdmin wifiAdmin){
		return administrator.getBSSIDSyn(wifiAdmin);
	}
	
	public List<WifiScanResult> scanAPsLANSyn(WifiAdmin wifiAdmin, boolean isEspDevice) {
		return administrator.scanAPsLANSyn(wifiAdmin , isEspDevice);
	}
	
	public List<IOTAddress> scanSTAsLANSyn(){
		return administrator.scanSTAsLANSyn();
	}
	
	public boolean isSTAExistLANSyn(IOTAddress iotAddress, int timeoutSeconds){
		return administrator.isSTAExistLANSyn(iotAddress, timeoutSeconds);
	}
	
	public void disconnectWifiSyn(WifiAdmin wifiAdmin) {
		administrator.disconnectWifiSyn(wifiAdmin);
	}

	public WIFI_ENUM.WifiStatus getWifiStatusSyn(WifiAdmin wifiAdmin, String SSID) {
		return administrator.getWifiStatusSyn(wifiAdmin, SSID);
	}

	public WifiInfo getConnectionInfoSyn(WifiAdmin wifiAdmin){
		return administrator.getConnectionInfoSyn(wifiAdmin);
	}
	
	public void connectAPAsyn(WifiAdmin wifiAdmin, String SSID,
			boolean isNoPassword) {
		administrator.connectAPAsyn(wifiAdmin, SSID, isNoPassword);
	}

	public void connectAPAsyn(WifiAdmin wifiAdmin, String SSID,
			String password, WIFI_ENUM.WifiCipherType type) {
		administrator.connectAPAsyn(wifiAdmin, SSID, password, type);
	}

	public boolean isAPConnectedSyn(WifiAdmin wifiAdmin){
		return administrator.isAPConnectedSyn(wifiAdmin);
	}
	
	public void reconnectAsyn(Handler handler, WifiAdmin wifiAdmin,
			String SSID, String BSSID, WIFI_ENUM.WifiCipherType type) {
		administrator.reconnectAsyn(handler, wifiAdmin, SSID, BSSID, type);
	}

	public void checkAPConnectedAsyn(Handler handler, WifiAdmin wifiAdmin, String BSSID) {
		administrator.checkAPConnectedAsyn(handler, wifiAdmin, BSSID);
	}
	
}

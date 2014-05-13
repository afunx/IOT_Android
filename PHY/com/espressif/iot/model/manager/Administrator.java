package com.espressif.iot.model.manager;

import java.util.List;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.os.Handler;

import com.espressif.iot.constants.CONSTANTS;
import com.espressif.iot.constants.WIFI_ENUM;
import com.espressif.iot.mediator.IntermediatorNetWAN;
import com.espressif.iot.mediator.IntermediatorWifiLAN;
import com.espressif.iot.model.device.IOTAddress;
import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.net.lan.wifi.WifiScanResult;
import com.espressif.iot.taskphy.lan.CheckAPConnectedAsynTask;
import com.espressif.iot.taskphy.lan.ReconnectAPAsynTask;
import com.espressif.iot.thread.FixedThreadPool;

/**
 * it is the administrator of the apk
 * 
 * @author afunx
 * 
 */
public class Administrator {
	
	private IntermediatorWifiLAN intermediatorWifiLan = IntermediatorWifiLAN.getInstance();
	private IntermediatorNetWAN intermediatorNetWAN = IntermediatorNetWAN.getInstance();
	private FixedThreadPool threadPool = FixedThreadPool.getInstance();

	private static Administrator instance = new Administrator();

	// Singleton Pattern
	private Administrator() {
	}

	public static Administrator getInstance() {
		return instance;
	}

	public boolean isInternetAccessedMonetWANSyn(Context context){
		return intermediatorNetWAN.isInternetAccessedMonetWANSyn(context);
	}
	
	public boolean isInternetAccessedWifiWANSyn(Context context){
		return intermediatorNetWAN.isInternetAccessedWifiWANSyn(context);
	}
	
	public String getBSSIDSyn(WifiAdmin wifiAdmin){
		return intermediatorWifiLan.getBSSID(wifiAdmin);
	}
	
	public List<WifiScanResult> scanAPsLANSyn(WifiAdmin wifiAdmin,boolean isEspDevice) {
		return intermediatorWifiLan.scanAPsLANSyn(wifiAdmin, isEspDevice);
	}
	
	public List<IOTAddress> scanSTAsLANSyn(){
		return intermediatorWifiLan.scanSTAsLANSyn();
	}
	
	public boolean isSTAExistLANSyn(IOTAddress iotAddress, int timeoutSeconds){
		return intermediatorWifiLan.isSTAExistLANSyn(iotAddress, timeoutSeconds);
	}
	
	public void disconnectWifiSyn(WifiAdmin wifiAdmin) {
		intermediatorWifiLan.wifiDisconnectSyn(wifiAdmin);
	}

	public WIFI_ENUM.WifiStatus getWifiStatusSyn(WifiAdmin wifiAdmin, String SSID) {
		return intermediatorWifiLan.getWifiStatusSyn(wifiAdmin, SSID);
	}
	
	public WifiInfo getConnectionInfoSyn(WifiAdmin wifiAdmin){
		return intermediatorWifiLan.getConnectionInfoSyn(wifiAdmin);
	}

	public void connectAPAsyn(WifiAdmin wifiAdmin, String SSID,
			boolean isNoPassword) {
		intermediatorWifiLan.connectAPAsyn(wifiAdmin, SSID, isNoPassword);
	}

	public void connectAPAsyn(WifiAdmin wifiAdmin, String SSID,
			String password, WIFI_ENUM.WifiCipherType type) {
		intermediatorWifiLan.connectAPAsyn(wifiAdmin, SSID, password, type);
	}
	
	public boolean isAPConnectedSyn(WifiAdmin wifiAdmin, int timeoutSeconds){
		return intermediatorWifiLan.isAPConnectedSyn(wifiAdmin, timeoutSeconds);
	}

	public void reconnectAsyn(Handler handler, WifiAdmin wifiAdmin,
			String SSID, String BSSID, WIFI_ENUM.WifiCipherType type) {
		ReconnectAPAsynTask task = new ReconnectAPAsynTask("reconnectAPTaskAsyn:"
				+ SSID, handler, wifiAdmin, SSID, BSSID,type);
		threadPool.executeAsyn(task);
	}

	public void checkAPConnectedAsyn(Handler handler, WifiAdmin wifiAdmin,String BSSID) {
		CheckAPConnectedAsynTask task = new CheckAPConnectedAsynTask(
				"checkAPConnectedTaskAsyn", handler, wifiAdmin, BSSID);
		threadPool.executeAsyn(task);
	}
}

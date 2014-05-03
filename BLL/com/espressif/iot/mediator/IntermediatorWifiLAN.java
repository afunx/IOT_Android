package com.espressif.iot.mediator;

import java.util.List;

import android.net.wifi.WifiInfo;

import com.espressif.iot.constants.WIFI_ENUM;
import com.espressif.iot.model.device.IOTAddress;
import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.net.lan.wifi.WifiScanResult;
import com.espressif.iot.tasknet.discover.Discoverer;
import com.espressif.iot.tasknet.wifi.lan.APConnectHelper;


/**
 * it is used to intermediate something about Wifi in LAN
 * @author afunx
 *
 */
public class IntermediatorWifiLAN {
	public static IntermediatorWifiLAN instance = new IntermediatorWifiLAN();
	private Discoverer discoverer = Discoverer.getInstance();

	// Singleton Pattern
	private IntermediatorWifiLAN() {
	}

	public static IntermediatorWifiLAN getInstance() {
		return instance;
	}

	public String getBSSID(WifiAdmin wifiAdmin){
		return discoverer.getBSSIDSyn(wifiAdmin);
	}
	
	public List<WifiScanResult> scanAPsLANSyn(WifiAdmin wifiAdmin, boolean isEspDevice) {
		return discoverer.findAPsLANSyn(wifiAdmin, isEspDevice);
	}

	public List<IOTAddress> scanSTAsLANSyn(){
		return discoverer.findSTAsLANSyn();
	}
	
	public boolean isSTAExistLANSyn(IOTAddress iotAddress, int timeoutSeconds){
		return discoverer.isSTAExistLANSyn(iotAddress, timeoutSeconds);
	}
	
	public void connectAPAsyn(WifiAdmin wifiAdmin, String SSID,
			boolean isNoPassword) {
		APConnectHelper.connectAsyn(wifiAdmin, SSID, isNoPassword);
	}

	public void connectAPAsyn(WifiAdmin wifiAdmin, String SSID,
			String password, WIFI_ENUM.WifiCipherType type) {
		APConnectHelper.connectAsyn(wifiAdmin, SSID, password, type);
	}

	public void wifiDisconnectSyn(WifiAdmin wifiAdmin) {
		wifiAdmin.disconnect();
	}

	public WIFI_ENUM.WifiStatus getWifiStatusSyn(WifiAdmin wifiAdmin, String SSID) {
		return wifiAdmin.getStatus(SSID);
	}

	public boolean isAPConnectedSyn(WifiAdmin wifiAdmin, int timeoutSeconds) {
		return APConnectHelper.isConnectedSyn(wifiAdmin, timeoutSeconds);
	}
	
	public WifiInfo getConnectionInfoSyn(WifiAdmin wifiAdmin){
		return wifiAdmin.getConnectionInfo();
	}

}

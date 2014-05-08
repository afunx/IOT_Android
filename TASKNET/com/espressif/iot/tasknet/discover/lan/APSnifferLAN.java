package com.espressif.iot.tasknet.discover.lan;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.util.Log;

import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.net.lan.wifi.WifiScanResult;
import com.espressif.iot.net.lan.wifi.WifiScanResultListCreater;
import com.espressif.iot.net.lan.wifi.WifiScanResultManager;
import com.espressif.iot.util.Logger;

public class APSnifferLAN implements IntAPSnifferLAN<WifiScanResult> {

	private WifiScanResultManager wifiScanResultManager = WifiScanResultManager
			.getInstance();

	private static APSnifferLAN instance = new APSnifferLAN();
	// Singleton Pattern
	private APSnifferLAN(){
	}
	public static APSnifferLAN getInstance(){
		return instance;
	}

	@Override
	public List<WifiScanResult> sniffSyn(WifiAdmin wifiAdmin, boolean isEspDevice) {
		// TODO Auto-generated method stub
		List<ScanResult> scanResultList = wifiAdmin.scan();
		//!NOTE modify roughly
		if(scanResultList==null){
			return new CopyOnWriteArrayList<WifiScanResult>();
		}
		for(ScanResult scan : scanResultList){
			if(scan.SSID.equals(""))
				Logger.e("APSnifferLAN", "scan SSID is null");
		}
		// succeed
		if(scanResultList!=null){
			List<WifiScanResult> wifiScanResultList = WifiScanResultListCreater
					.createWifiScanResultList(scanResultList, isEspDevice);
			/*wifiScanResultManager.checkAddWifiScanResultList(wifiScanResultList);
			wifiScanResultManager.sortRssiDescend();
			return wifiScanResultManager.getWifiScanResultList();*/
			return wifiScanResultList;
		}
		return new CopyOnWriteArrayList<WifiScanResult>();
	}
}

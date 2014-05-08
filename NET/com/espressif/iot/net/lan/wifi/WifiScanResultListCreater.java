package com.espressif.iot.net.lan.wifi;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.espressif.iot.util.Logger;

import android.net.wifi.ScanResult;
import android.util.Log;

/*
 * it is used to store wifiScanResult, 
 * the filter is implemented here, SSID: ESP_******
 */
public class WifiScanResultListCreater {
	
	private static final String TAG = "WifiScanResultListCreater";
	private static final String PREFIX_FILTER = "ESP_";
	
	// it is used to judge whether the wifi scan result is ESP's IOT Device
	private static boolean isESPDevice(String SSID,String prefixFilter){
		for(int i=0;i<prefixFilter.length();i++){
			if(i>=SSID.length()||SSID.charAt(i)!=prefixFilter.charAt(i))
				return false;
		}
		return true;
	}
	
	// "ESP_" + MAC address's 6 places
	private static boolean isESPDevice(String SSID){
		// "ESP_"'s length = 4, 4+6=10
		if(SSID.length()!=10)
			return false;
		for(int i=0;i<PREFIX_FILTER.length();i++){
			if(i>=SSID.length()||SSID.charAt(i)!=PREFIX_FILTER.charAt(i))
				return false;
		}
		return true;
	}
	
	
	/**
	 * create the List<WifiScanResult> according to List<ScanResult>,
	 * which is the result of wifi scan
	 * 
	 * @param scanResultList	the source list(scan from the wifiManager)
	 * @param isEspDevice		whether the scan list is EspDevice or not
	 * @return					List<WifiScanResult>
	 */
	
	public static List<WifiScanResult> createWifiScanResultList(
			List<ScanResult> scanResultList, boolean isEspDevice) {
		List<WifiScanResult> returnList = new CopyOnWriteArrayList<WifiScanResult>();
		if(scanResultList==null){
			Logger.e(TAG, "scanResultList=null");
		}
		for(int i=0;i<scanResultList.size();i++){
			ScanResult elementSrc = scanResultList.get(i);
			// filter
			boolean resultIsEspDevice = isESPDevice(elementSrc.SSID);
			// require EspDevice, but the result isn't
			if(isEspDevice&&!resultIsEspDevice
					// require not EspDevice, but the result is
					||!isEspDevice&&resultIsEspDevice)
				continue;
			WifiScanResult elementDest = new WifiScanResult(elementSrc);
			returnList.add(elementDest);
		}
		// sort by RSSI
		return returnList;
	}
}

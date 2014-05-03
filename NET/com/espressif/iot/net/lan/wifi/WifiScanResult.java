package com.espressif.iot.net.lan.wifi;


import com.espressif.iot.constants.WIFI_ENUM;

import android.net.wifi.ScanResult;
import android.util.Log;

/*
 * it is a wrapper class
 * 
 * primary key: mScanResult.BSSID
 * 
 */
public class WifiScanResult {
	
	private static final String TAG = "WifiScanResult";
	
	protected ScanResult mScanResult;
	
	private int missTime;
	
	WifiScanResult(ScanResult scanResult){
		this.mScanResult = scanResult;
	}
	public void missOnce(){
		missTime++;
		Log.i(TAG, "BSSID:"+mScanResult.BSSID+",miss time="+missTime);
	}
	public int getMissTime(){
		return missTime;
	}
	public void clearMissTime(){
		missTime = 0;
	}
	public void setScanResult(ScanResult scanResult){
		this.mScanResult = scanResult;
	}
	public ScanResult getScanResult(){
		return mScanResult;
	}
	public WIFI_ENUM.WifiCipherType getWifiCipherType(){
		if(mScanResult.capabilities.contains("WEP"))
			return WIFI_ENUM.WifiCipherType.WIFICIPHER_WEP;
		else if(mScanResult.capabilities.contains("PSK"))
			return WIFI_ENUM.WifiCipherType.WIFICIPHER_WPA;
		return WIFI_ENUM.WifiCipherType.WIFICIPHER_NOPASS;
	}
}

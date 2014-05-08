package com.espressif.iot.ui.esp.scan;


import com.espressif.iot.constants.WIFI_ENUM;
import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.net.lan.wifi.WifiScanResult;
import com.espressif.iot.oapi.OApiIntermediator;
import com.espressif.iot.thread.single.SingleTaskWifiConnectAsyn;
import com.espressif.iot.util.Logger;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

public class EspScanButton extends Button{
	
	private static final String TAG = "EspScanButton";
	
	private WifiScanResult mWifiScanResult;
//	private WIFI_ENUM.WifiStatus mWifiStatus;
	private static OApiIntermediator oApiIntermediator = OApiIntermediator.getInstance();
	
	public static WifiAdmin wifiAdmin;
	private SingleTaskWifiConnectAsyn wifiConnect = SingleTaskWifiConnectAsyn
			.getInstance(wifiAdmin);

//	private SingleTaskWifiConnectChecker singleTaskWifiConnectChecker;// = SingleTaskWifiConnectChecker.getInstance();
	
	/**
	 * !!!NOTE: it must be called after EspScanButton is created
	 * @param wifiStatus
	 */
//	public void setEspWifiStatus(WIFI_ENUM.WifiStatus wifiStatus){
//		this.mWifiStatus = wifiStatus;
//		Logger.d(TAG, mWifiScanResult.getScanResult().SSID + "'s wifiStatus is "
//				+ wifiStatus);
//	}
//	public WIFI_ENUM.WifiStatus getEspWifiStatus(){
//		return this.mWifiStatus;
//	}
	
	/**
	 * !!!NOTE: it must be called after EspScanButton is created
	 * @param wifiScanResult
	 */
	public void setEspWifiScanResult(WifiScanResult wifiScanResult){
		this.mWifiScanResult = wifiScanResult;
	}
	public WifiScanResult getEspWifiScanResult(){
		return this.mWifiScanResult;
	}
	
	private void init(){
//		try {
//			singleTaskWifiConnectChecker = SingleTaskWifiConnectChecker.getInstance();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public EspScanButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public EspScanButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}	

	public EspScanButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}
	
	public boolean connect(){
		String SSID = mWifiScanResult.getScanResult().SSID;
		String BSSID = mWifiScanResult.getScanResult().BSSID;
		WIFI_ENUM.WifiCipherType type = mWifiScanResult.getWifiCipherType();
		WIFI_ENUM.WifiStatus wifiStatus = oApiIntermediator.getWifiStatusSyn(wifiAdmin, SSID);
		
		switch(wifiStatus){
		case WIFISTATUS_CURRENT:
			// do nothing
			Logger.d(TAG, "wifi is the current, do nothing");
			return true;
//			break;
		case WIFISTATUS_DISABLE:
		case WIFISTATUS_ENABLE:
			switch(mWifiScanResult.getWifiCipherType()){
			case WIFICIPHER_WPA:
				Logger.d(TAG, "WIFICIPHER_WPA");
				wifiConnect.connect(SSID, false, WIFI_ENUM.WifiCipherType.WIFICIPHER_WPA);
				break;
			case WIFICIPHER_NOPASS:
				Logger.d(TAG, "WIFICIPHER_NOPASS");
				wifiConnect.connect(SSID, true, WIFI_ENUM.WifiCipherType.WIFICIPHER_NOPASS);
				break;
			case WIFICIPHER_WEP:
				Logger.d(TAG, "WIFICIPHER_WEP");
				wifiConnect.connect(SSID, false,WIFI_ENUM.WifiCipherType.WIFICIPHER_WEP);
				break;
			default:
				Logger.e(TAG, "WIFICIPHER_INVALID");
			}
			break;
		}
		return false;
	}

}

package com.espressif.iot.ui.android;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.espressif.iot.constants.WIFI_ENUM;
import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.util.Logger;

/**
 * this class is used to store information,which should be removed. it exist
 * here is just to accomplish the apk quickly
 * 
 * @author afunx
 * 
 */
public class MessageStatic {
	
	private static final String TAG = "MessageStatic";
	/**
	 * wifi scan list, except ESP_XXXXXX
	 */
	public static ArrayList<String> wifiScanSSIDList = new ArrayList<String>();
	public static ArrayList<WIFI_ENUM.WifiCipherType> wifiScanTypeList 
	= new ArrayList<WIFI_ENUM.WifiCipherType>();
	
	public static String settingDeviceName;
	public static String device_ap_connected_ssid;
	public static String device_ap_connected_password;
	public static WIFI_ENUM.WifiCipherType device_ap_type;
	public static IOTDevice currentIOTDevice;
	
	// Iterator Pattern
	private static List<IOTDevice> IOTDeviceList = new ArrayList<IOTDevice>();
	private static int IOTDeviceListIndex = 0;
	private static void displayAll(){
		Logger.i(TAG, "displayALL() start");
		for(IOTDevice iotDevice:IOTDeviceList){
			Logger.i(TAG, "iotDevice's bssid:" + iotDevice.getIOTAddress().getBSSID());
		}
		Logger.i(TAG, "displayALL() end");
	}
	public static void clearIOTDeviceList(){
		Logger.i(TAG, "clearIOTDeviceList()");
		IOTDeviceList.clear();
		displayAll();
	}
	public static void addIOTDevice(IOTDevice iotDevice){
		Logger.i(TAG, "addIOTDevice:" + iotDevice.getIOTAddress().getBSSID());
		IOTDeviceList.add(iotDevice);
		displayAll();
	}
	public static void clearIOTDeviceListIndex(){
		Logger.i(TAG, "clearIOTDeviceListIndex()");
		IOTDeviceListIndex = 0;
		displayAll();
	}
	public static int getIOTDeviceListSize(){
		return IOTDeviceList.size();
	}
	public static IOTDevice nextIOTDevice(){
		Logger.i(TAG, "nextIOTDevice()");
		displayAll();
		if(IOTDeviceListIndex<IOTDeviceList.size()){
			IOTDevice iotDevice = IOTDeviceList.get(IOTDeviceListIndex++);
			Logger.i(TAG, "iotDevice's bssid:" + iotDevice.getIOTAddress().getBSSID());
			return iotDevice;
		}
		return null;
	}
}

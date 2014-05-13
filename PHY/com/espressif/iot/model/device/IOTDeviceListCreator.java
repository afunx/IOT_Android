package com.espressif.iot.model.device;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.net.wifi.ScanResult;
import android.util.Log;

import com.espressif.iot.net.lan.wifi.WifiScanResult;
import com.espressif.iot.util.Logger;

public class IOTDeviceListCreator {

	private static final String TAG = "IOTDeviceListCreator";

	/**
	 * create the List<IOTDevice> according to List<IOTAddress>, which is
	 * the result of udp broadcast
	 * 
	 * @param iotAddressList
	 *            the iotAddress list
	 * @return List<IOTDevice>
	 */

	public static List<IOTDevice> createIOTDeviceList(
			List<IOTAddress> iotAddressList) {
		List<IOTDevice> returnList = new CopyOnWriteArrayList<IOTDevice>();
		if (iotAddressList == null) {
			Logger.e(TAG, "iotDeviceList=null");
		}
		for (int i = 0; i < iotAddressList.size(); i++) {
			IOTAddress elementSrc = iotAddressList.get(i);
			IOTDevice elementDest = IOTDevice.createIOTDevice(elementSrc);
			returnList.add(elementDest);
		}
		return returnList;
	}
}

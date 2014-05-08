package com.espressif.iot.model.device;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.util.Log;

import com.espressif.iot.constants.CONSTANTS;
import com.espressif.iot.util.Logger;

public class IOTDevicesManager {

	private static final String TAG = "IOTDevicesManager";

	// ID, AP's BSSID
	private String mBSSID_AP;

	/**
	 * create a new manager of IOT devices if necessary
	 * 
	 * @param BSSID_AP
	 *            BSSID of the AP now
	 * @param oldIOTDevicesManager
	 *            old IOTDevicesManager
	 * @return if BSSID_AP==oldIOTDevicesManager.mBSSID_AP return the old one
	 *         else create a new IOTDevicesManager according to BSSID_AP
	 */
	public static IOTDevicesManager createOrReplace(String BSSID_AP,
			IOTDevicesManager oldIOTDevicesManager) {

		if (BSSID_AP == null || oldIOTDevicesManager == null
				|| !(BSSID_AP.equals(oldIOTDevicesManager.mBSSID_AP)))
			return new IOTDevicesManager(BSSID_AP);
		else
			return oldIOTDevicesManager;
	}

	private IOTDevicesManager(String BSSID) {
	}

	public synchronized List<IOTDevice> getIOTDeviceList() {
		return mList;
	}

	private List<IOTDevice> mList = new CopyOnWriteArrayList<IOTDevice>();

	/**
	 * check and add the list's element (IOTDevice's primay key is BSSID) if
	 * mList contains and list doesn't contain, the element's miss time +1 if
	 * miss time is bigger than STA_MAX_MISS_TIME, remove the element from mList
	 * if mList contains and list contains too, the element's miss time =0 and
	 * mList will update the new list(may be on statistics in the future) if
	 * mList doesn't contain while list contains, the element will be add into
	 * mList
	 * 
	 * @param list
	 *            it should be CopyOnWriteArrayList to avoid
	 *            ConcurrentModificationException
	 */
	public synchronized void checkAddIOTDeviceList(List<IOTDevice> list) {
		// mList contains
		for (IOTDevice element : mList) {
			// whether element is contained in list
			boolean isContained = false;
			// traverse
			for (IOTDevice elementOther : list) {
				// list contains
				if ((elementOther.getIOTAddress().getBSSID()).equals(element
						.getIOTAddress().getBSSID())) {
					isContained = true;
					Logger.d(TAG, "list contains,BSSID:"
							+ element.getIOTAddress().getBSSID());
					// !!!NOTE: update(may be on statistics in the future)
					// Because at this moment, we think the IOT device will not
					// change
					// unless we update it on purpose
					element.clearMissTime();
					// don't forget remove it
					list.remove(elementOther);
					break;
				}
			}
			// list doesn't contain
			if (!isContained) {
				element.missOnce();
				if (element.getMissTime() >= CONSTANTS.STA_MAX_MISS_TIME) {
					Logger.d(TAG, "list remove,BSSID:"
							+ element.getIOTAddress().getBSSID());
					mList.remove(element);
				}
			}
		}
		// mList doesn't contain
		for (IOTDevice elementOther : list) {
			Logger.d(TAG, "list add,BSSID:"
					+ elementOther.getIOTAddress().getBSSID());
			mList.add(elementOther);
			elementOther.clearMissTime();
		}
		Logger.d(TAG, "mList.size()=" + mList.size());
	}

	
	

}

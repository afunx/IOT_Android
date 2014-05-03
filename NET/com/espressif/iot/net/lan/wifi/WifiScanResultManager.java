package com.espressif.iot.net.lan.wifi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.espressif.iot.constants.CONSTANTS;

import android.net.wifi.WifiInfo;
import android.util.Log;

public class WifiScanResultManager {
	
	private List<WifiScanResult> mList = new CopyOnWriteArrayList<WifiScanResult>();
	
	private WifiInfo mWifiInfo;
	
	private static final String TAG = "WifiScanResultManager";
	
	private static WifiScanResultManager instance = new WifiScanResultManager();
	
	private static WifiScanResultComparator comparator = WifiScanResultComparator.getInstance();
	
	// Singleton Pattern
	private WifiScanResultManager(){
	}
	public static WifiScanResultManager getInstance(){
		return instance;
	}
	
	public void setWifiInfo(WifiInfo wifiInfo){
		this.mWifiInfo = wifiInfo;
	}
	
	public WifiInfo getWifiInfo(){
		return mWifiInfo;
	}
	
	public synchronized List<WifiScanResult> getWifiScanResultList(){
		return mList;
	}
	
	/**
	 * sort the mlist(List<WifiScanResult>) by RSSI Descending
	 */
	public synchronized void sortRssiDescend(){
		// for CopyOnWriteArrayList is thread safe, don't support sort
		// we have to use tempList	
		List<WifiScanResult> tempList = new ArrayList<WifiScanResult>(mList);
		Collections.sort(tempList, comparator);
		mList.clear();
		mList.addAll(tempList);
	}
	
	/**
	 * check and add the list's element
	 * (WifiScanResult's primay key is BSSID)
	 * if mList contains and list doesn't contain, the element's miss time +1
	 * 	if miss time is bigger than AP_MAX_MISS_TIME, remove the element from mList
	 * if mList contains and list contains too, the element's miss time =0
	 * 	and mList will update the new list(may be on statistics in the future)
	 * if mList doesn't contain while list contains, the element will be add into mList 
	 * 
	 * @param list	it should be CopyOnWriteArrayList 
	 * 				to avoid ConcurrentModificationException
	 */
	public synchronized void checkAddWifiScanResultList(List<WifiScanResult> list){
		// mList contains
		for(WifiScanResult element:mList){
			// whether element is contained in list
			boolean isContained = false;
			// traverse
			for(WifiScanResult elementOther:list){
				// list contains
				if((elementOther.getScanResult().BSSID).
						equals(element.getScanResult().BSSID)){
					isContained = true;
					Log.d(TAG, "list contains,BSSID:"+element.getScanResult().BSSID);
					// update(may be on statistics in the future)
					element.setScanResult(elementOther.getScanResult());
					element.clearMissTime();
					// don't forget remove it
					list.remove(elementOther);
					break;
				}
			}
			// list doesn't contain
			if(!isContained){
				element.missOnce();
				if(element.getMissTime() >= CONSTANTS.AP_MAX_MISS_TIME){
					Log.d(TAG, "list remove,BSSID:"+element.getScanResult().BSSID);
					mList.remove(element);
				}
			}
		}
		// mList doesn't contain
		for(WifiScanResult elementOther:list){
			Log.d(TAG, "list add,BSSID:"+elementOther.getScanResult().BSSID);
			mList.add(elementOther);
			elementOther.clearMissTime();
		}
		Log.d(TAG, "mList.size()="+mList.size());
	}
	
}

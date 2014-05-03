package com.espressif.iot.net.lan.wifi;

import java.util.Comparator;

public class WifiScanResultComparator implements Comparator<WifiScanResult> {

	private static WifiScanResultComparator singleton = new WifiScanResultComparator();

	// Singleton Pattern
	private WifiScanResultComparator() {
	}
	public static WifiScanResultComparator getInstance(){
		return singleton;
	}

	@Override
	public int compare(WifiScanResult lhs, WifiScanResult rhs) {
		// TODO Auto-generated method stub
		int lValue = lhs.mScanResult.level;
		int rValue = rhs.mScanResult.level;
		// to make Collection.sort() use rssi descending order
		if (lValue < rValue)
			return 1;
		else if (lValue == rValue)
			return 0;
		else
			return -1;
	}
}

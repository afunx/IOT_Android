package com.espressif.iot.thread.single;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Message;
import android.util.Log;

import com.espressif.iot.constants.WIFI_ENUM;
import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.ui.android.MessageCenter;
import com.espressif.iot.ui.android.MyFragmentsActivity;
import com.espressif.iot.util.Logger;

public class SingleTaskWifiConnectAsyn {

	private static ExecutorService singleExecutor = Executors
			.newSingleThreadExecutor();

	private final String TAG = "SingleTaskWifiConnectAsyn";

	private WifiAdmin mWifiAdmin;

	private SingleTaskWifiConnectChecker singleTaskWifiConnectChecker;
	// Singleton Pattern
	private static SingleTaskWifiConnectAsyn instance;

	public static SingleTaskWifiConnectAsyn getInstance(
			WifiAdmin wifiAdmin) {
		if (instance != null) {
			return instance;
		} else {
			instance = new SingleTaskWifiConnectAsyn(wifiAdmin);
			try {
				instance.singleTaskWifiConnectChecker = SingleTaskWifiConnectChecker.getInstance();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return instance;
		}
	}

	private SingleTaskWifiConnectAsyn(WifiAdmin wifiAdmin) {
		this.mWifiAdmin = wifiAdmin;
	}

//	public static boolean cancel(@SuppressWarnings("rawtypes") Future future) {
//		return future.cancel(true);
//	}

	public void connect(final String SSID, final boolean isNoPassword
			,final WIFI_ENUM.WifiCipherType type) {
		singleExecutor.execute(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
//				mWifiAdmin.disconnect();
				
				// NOPASS will not overtime
				singleTaskWifiConnectChecker.submitTargetSSID(SSID,type);
				// do something when the AP isn't in mWificpmfogirations
				boolean result = mWifiAdmin.connect(SSID, isNoPassword);
				if(!result){
					Logger.d(TAG, "pop wifi password setting");
					try {
						SingleTaskWifiConnectChecker.getInstance().clearTimeout();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Message msg= MessageCenter.genPopMessage(SSID, type);
					MyFragmentsActivity.leakHandler.sendMessage(msg);
				}
			}
		});
	}

	@SuppressWarnings("rawtypes")
	public void connect(final String SSID, final String password,
			final WIFI_ENUM.WifiCipherType type) {
		singleExecutor.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
//				mWifiAdmin.disconnect();
//				singleTaskWifiConnectChecker.submitTargetSSID(SSID, BSSID, type);
				mWifiAdmin.connect( SSID, password, type);
			}

		});
	}
}

package com.espressif.iot.taskphy.lan;

import android.os.Handler;

import com.espressif.iot.constants.CONSTANTS;
import com.espressif.iot.constants.WIFI_ENUM;
import com.espressif.iot.mediator.IntermediatorWifiLAN;
import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.thread.AbsTaskAsyn;
//import com.espressif.iot.ui.android.MyFragmentsActivity;
//import com.espressif.iot.ui.android.MessageCenter;

public class ReconnectAPAsynTask extends AbsTaskAsyn {

	private Handler mHandler;
	private String mSSID;
	private String mBSSID;
	private WIFI_ENUM.WifiCipherType mType;
	private static IntermediatorWifiLAN intermediatorWifi = IntermediatorWifiLAN
			.getInstance();
	private WifiAdmin mWifiAdmin;

	protected ReconnectAPAsynTask(String taskName) {
		super(taskName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * reconnect AP task, used after connect the AP(WifiStatus(Enum) is ENABLE)
	 * for the AP's password may be changed sometime, but the android's device
	 * treat it ENABLE for the AP could be connected to last time
	 * 
	 * @param taskName
	 *            the task name
	 * @param handler
	 *            the activity's handler
	 * @param wifiAdmin
	 *            the admin of wifi
	 * @param SSID
	 *            the AP's SSID
	 * @param BSSID
	 *            the AP's BSSID
	 * @param type
	 *            the AP's wificipher type
	 */
	public ReconnectAPAsynTask(String taskName, Handler handler,
			WifiAdmin wifiAdmin, String SSID, String BSSID,
			WIFI_ENUM.WifiCipherType type) {
		this(taskName);
		this.mHandler = handler;
		this.mWifiAdmin = wifiAdmin;
		this.mSSID = SSID;
		this.mBSSID = BSSID;
		this.mType = type;
	}

	private void sendPopMessage() {
//		mHandler.sendMessage(MessageCenter.Config.genPopMessage(mSSID,mBSSID,mType));
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean isConnect = false;
		isConnect = intermediatorWifi.isAPConnectedSyn(mWifiAdmin,
				CONSTANTS.IS_AP_CONNECTED_TIMEOUT_SECONDS);
		if (!isConnect) {
			sendPopMessage();
		}
	}
}

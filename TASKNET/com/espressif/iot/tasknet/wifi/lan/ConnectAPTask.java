package com.espressif.iot.tasknet.wifi.lan;


import com.espressif.iot.constants.WIFI_ENUM;
import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.thread.AbsTaskAsyn;



public class ConnectAPTask extends AbsTaskAsyn {

	private WifiAdmin mWifiAdmin;

	private String mSSID;

	private String mPassword;

	private WIFI_ENUM.WifiCipherType mType;
	
	private boolean mIsNoPassword;

	public ConnectAPTask(String taskName, WifiAdmin wifiAdmin, String SSID, boolean isNoPassword) {
		super(taskName);
		// TODO Auto-generated constructor stub
		this.mWifiAdmin = wifiAdmin;
		this.mSSID = SSID;
		this.mIsNoPassword = isNoPassword;
		this.mType = null;
	}

	public ConnectAPTask(String taskName, WifiAdmin wifiAdmin, String SSID,
			String password, WIFI_ENUM.WifiCipherType type) {
		super(taskName);
		// TODO Auto-generated constructor stub
		this.mWifiAdmin = wifiAdmin;
		this.mSSID = SSID;
		this.mPassword = password;
		this.mType = type;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// use the saved info from wifi setting by android
		if (mType == null) {
			mWifiAdmin.connect(mSSID,mIsNoPassword);
		}
		// use the user's input info
		else {
			mWifiAdmin.connect(mSSID, mPassword, mType);
		}
	}

}

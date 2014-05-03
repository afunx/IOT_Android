package com.espressif.iot.taskphy.lan;

import android.os.Handler;

import com.espressif.iot.constants.CONSTANTS;
import com.espressif.iot.mediator.IntermediatorWifiLAN;
import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.thread.AbsTaskAsyn;
//import com.espressif.iot.ui.android.MyFragmentsActivity;
//import com.espressif.iot.ui.android.MessageCenter;

public class CheckAPConnectedAsynTask extends AbsTaskAsyn{

	private static IntermediatorWifiLAN intermediatorWifi = IntermediatorWifiLAN
			.getInstance();
	private WifiAdmin mWifiAdmin;
	private Handler mHandler;
	private String mBSSID;
	
	protected CheckAPConnectedAsynTask(String taskName) {
		super(taskName);
		// TODO Auto-generated constructor stub
	}
	
	public CheckAPConnectedAsynTask(String taskName, Handler handler,WifiAdmin wifiAdmin,String BSSID){
		this(taskName);
		this.mHandler = handler;
		this.mWifiAdmin = wifiAdmin;
		this.mBSSID = BSSID;
	}

	private void sendCheckAPMessage(boolean isSucceed){
//		mHandler.sendMessage(MessageCenter.Config.genCheckAPMessage(isSucceed,mBSSID));
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		// sleep some time, avoid race condition
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean isConnect = false;
		isConnect = intermediatorWifi.isAPConnectedSyn(mWifiAdmin,
				CONSTANTS.CHECK_AP_CONNECTED_TIMEOUT_SECONDS-1);
		sendCheckAPMessage(isConnect);
	}

}

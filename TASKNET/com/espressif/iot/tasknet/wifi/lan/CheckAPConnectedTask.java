package com.espressif.iot.tasknet.wifi.lan;

import android.util.Log;

import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.thread.AbsTaskSyn;
import com.espressif.iot.util.Logger;

/**
 * it is used to check whether the wifi is connected, it will poll until the
 * wifi is connected or timeout
 * 
 * @author afunx
 * 
 */
public class CheckAPConnectedTask extends AbsTaskSyn<Boolean> {

	private WifiAdmin mWifiAdmin;

	private static final long SLEEP_MILLISECONDS = 100;

	private boolean mIsConnected;

	private static final String TAG = "CheckWifiConnectTask";

	public CheckAPConnectedTask(String taskName, WifiAdmin wifiAdmin) {
		super(taskName);
		this.mWifiAdmin = wifiAdmin;
		// TODO Auto-generated constructor stub
	}

	private void waitWifiConnected() {
		while (true) {
			if (mWifiAdmin.isConnect()){
				mIsConnected = true;
				break;
			}
			try {
				Thread.sleep(SLEEP_MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public Boolean call() {
		// TODO Auto-generated method stub
		mIsConnected = false;
		waitWifiConnected();
		return mIsConnected;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return taskName;
	}

	@Override
	protected void doAfterFailed() {
		// TODO Auto-generated method stub
		Logger.d(TAG, "fail");
	}

	@Override
	public Boolean getResult() {
		// TODO Auto-generated method stub
		return mIsConnected;
	}

}

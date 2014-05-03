package com.espressif.iot.thread.single;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Message;
import android.util.Log;

import com.espressif.iot.constants.WIFI_ENUM;
import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.ui.android.MessageCenter;
import com.espressif.iot.ui.android.MyFragmentsActivity;

public class SingleTaskWifiConnectChecker {

	private static final String TAG = "SingleTaskWifiConnectChecker";
	
	// Singleton Pattern
	private static SingleTaskWifiConnectChecker instance;
	public static SingleTaskWifiConnectChecker createSingleton(WifiAdmin wifiAdmin){
		if(instance!=null){
			return instance;
		}
		else{
			instance = new SingleTaskWifiConnectChecker(wifiAdmin);
			return instance;
		}
	}
	public static SingleTaskWifiConnectChecker getInstance() throws Exception{
		if(instance!=null)
			return instance;
		else
			throw new Exception("SingleTaskWifiConnectChecker is null");
	}
	private SingleTaskWifiConnectChecker(WifiAdmin wifiAdmin){
		this.mWifiAdmin = wifiAdmin;
	}
	
	private static ExecutorService singleExecutor = Executors.newSingleThreadExecutor();
	
	private WifiAdmin mWifiAdmin;
	private WifiChecker mWifiChecker = new WifiChecker();
	
	public void startTask(){
		Log.d(TAG,"startTask()");
		mWifiChecker.isStop = false;
		// !!!NOTE mWifiAdmin must be injected before execute(mWifiChecker)
		mWifiChecker.setWifiAdmin(mWifiAdmin);
		singleExecutor.execute(mWifiChecker);
	}
	public void pauseTask(){
		Log.d(TAG,"pauseTask()");
		mWifiChecker.isStop = true;
	}
	public String getTargetSSID(){
		return mWifiChecker.targetSSID;
	}
	
	public void clearTimeout(){
		while(true){
			if(mWifiChecker.isTimeoutRunning){
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				mWifiChecker.mTimeoutChecker.clearTimeout();
				break;
			}
		}
		
	}
	
	/**
	 * submit the target AP's info to WifiChecker, when it is not running 
	 * @param targetAPSSID
	 * @param targetAPtype
	 */
	public void submitTargetSSID(String targetAPSSID,
			WIFI_ENUM.WifiCipherType targetAPType){
		while(true){
			if(mWifiChecker.isRunning){
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				mWifiChecker.targetSSID = targetAPSSID;
				mWifiChecker.targetType = targetAPType;
				// set timeout for connecting, if no password, it won't overtime
				if(targetAPType != WIFI_ENUM.WifiCipherType.WIFICIPHER_NOPASS)
					mWifiChecker.setTimeout();
				else
					mWifiChecker.clearTimeout();
				break;
			}
		}
	}
	
	// this class is used to check after connecting command is executed,
	// whether the connect is succeed in the timeout.
	// if new submit occur, it will update the timeout stamp
	class TimeoutChecker{
		private long mTimeout;
		private boolean mIsSet;
		private static final int TIMEOUT_MILLISECONDS = 15000;
		private static final String TAG = "TimeoutChecker";
		
		// Singleton
		private TimeoutChecker(){
		}
		
		public void setTimeout(){
			mTimeout = System.currentTimeMillis() + TIMEOUT_MILLISECONDS;
			mIsSet = true;
		}
		public boolean isTimeout(){
			Log.d(TAG, "isTimeout()");
			return System.currentTimeMillis() > mTimeout;
		}
		public void clearTimeout(){
			mIsSet = false;
		}
		public boolean isSet(){
			return mIsSet;
		}
	}
	
	class WifiChecker implements Runnable{
		
		private final String TAG = "SingleTaskWifiConnectChecker$WifiChecker";
		private boolean isStop = false;
		private boolean isRunning = false;
		private boolean isConnectedNow = false;
		private boolean isConnectedPrev = false;
		private boolean isTimeoutRunning = false;
		private volatile String targetSSID = null;
		private String preSSID = null;
		private String curSSID = null;
		private WIFI_ENUM.WifiCipherType targetType;
		private WifiAdmin mWifiAdmin;
		private TimeoutChecker mTimeoutChecker = new TimeoutChecker();
		
		private void setWifiAdmin(WifiAdmin wifiAdmin){
			this.mWifiAdmin = wifiAdmin;
		}
		
		private void setTimeout(){
			this.mTimeoutChecker.setTimeout();
		}
		
		private void clearTimeout(){
			this.mTimeoutChecker.clearTimeout();
		}
		
		private void sendPopMessage(){
			Message msg= MessageCenter.genPopMessage(targetSSID, targetType);
			MyFragmentsActivity.leakHandler.sendMessage(msg);
		}
		private void sendWifiConnectSucMessage(){
			Message msg= MessageCenter.genPopMessage(targetSSID);
			MyFragmentsActivity.leakHandler.sendMessage(msg);
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
//				if(isStop){
//					try {
//						Thread.sleep(2000);
//						continue;
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
				
				isTimeoutRunning = true;
				// check whether connect execution is timeout
				if(mTimeoutChecker.isSet()&&mTimeoutChecker.isTimeout()){
					Log.w(TAG, "connect is timeout");
					sendPopMessage();
					mTimeoutChecker.clearTimeout();
				}
				isTimeoutRunning = false;
				
				// it is running, and the targetSSID couldn't be changed during this time
				isRunning = true;
				
				isConnectedNow = mWifiAdmin.isConnect();
				if(isConnectedNow!=isConnectedPrev){
					// disconnected(previous)-> connected(now)
					if(isConnectedNow){
						Log.d(TAG, "wifi is connected now.");
						
						if(mWifiAdmin.getStatus(targetSSID)==WIFI_ENUM.WifiStatus.WIFISTATUS_CURRENT){
							Log.d(TAG, "targetSSID " + targetSSID + " connect succeed");
							mTimeoutChecker.clearTimeout();
							sendWifiConnectSucMessage();
						}
						else{
							Log.d(TAG, "targetSSID " + targetSSID + " connect fail");
						}
						
					}
					// connected(previous) -> disconnected(now)
					else{
						Log.d(TAG, "wifi is disconnected now.");
					}
					
					isConnectedPrev = isConnectedNow;
				}
				
				// check whether the connected AP is changed
//				if(mWifiAdmin.getConnectionInfo()!=null){
//					String tempSSID = mWifiAdmin.getConnectionInfo().getSSID();
//					String SSID = tempSSID.substring(1, tempSSID.length()-1);
//					preSSID = curSSID;
//					curSSID = SSID;
//					if(preSSID==null&&curSSID!=null
//							||preSSID!=null&&!preSSID.equals(curSSID)){
//						Log.d(TAG, "SSID:"+SSID+" is connected");
//						sendWifiConnectSucMessage();
//					}
//					
//				}
				
				// it is not running, and the targetSSID could be changed during this time 
				isRunning = false;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
}

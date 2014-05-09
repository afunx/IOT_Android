package com.espressif.iot.ui.android.device;



// baihua2
// user key "token": "43f10e8ae92a4c621f7e45baeaa8a735fcb7aa67"

// device tempt token: "1234567890123456789012345678901234567890"
// master key:fd9f4417ceec5f9cd33f2feabb0d329b99a111f1 // can't see
// owner key:12bf17008ab32477d8785c63397fc82e3f06d665


import java.util.List;

import com.espressif.iot.R;
import com.espressif.iot.cipher.RandomUtil;
import com.espressif.iot.constants.CONSTANTS;
import com.espressif.iot.constants.CONSTANTS_DYNAMIC;
import com.espressif.iot.db.device.IOTDeviceDBManager;
import com.espressif.iot.model.device.IOTActionEnum;
import com.espressif.iot.model.device.IOTAddress;
import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.model.device.IOTDevice.STATUS;
import com.espressif.iot.model.device.IOTDevice.TYPE;
import com.espressif.iot.model.internet.IOTDeviceHelper;
import com.espressif.iot.model.internet.User;
import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.oapi.OApiIntermediator;
import com.espressif.iot.ui.android.MessageStatic;
import com.espressif.iot.util.BSSIDUtil;
import com.espressif.iot.util.Logger;
import com.espressif.iot.util.MathUtil;
import com.espressif.iot.util.Reflect;
import com.espressif.iot.util.Util;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DeviceSettingProgressActivity extends Activity {

	
	private static final int START = 0;
	private static final int NEXT = 1;
	private static final int STOP = 2;
	
	private ProgressBar mProgressBar;
	private TextView mTvTitle;
	private int mProgressBarCount = 0;
	private boolean mLocked = true;
	private static final String TAG = "DeviceSettingProgressActivity";
	private Thread mClockThread = null;
	private Thread mWorkThread = null;
	private static final int ONE_DEVICE_SETTING_TIME_SECONDS = 120;
	private static final int PROGRESS_BAR_MAX = 100;
	private IOTDevice mIotDeviceCurrent;
	private IOTDeviceDBManager mIOTDeviceDBManager;
	private WifiAdmin mWifiAdmin;
	private static OApiIntermediator oApiIntermediator = OApiIntermediator.getInstance();
	private static String CLASS_NAME = DeviceSettingProgressActivity.class.getCanonicalName();
	
	private static boolean isDebug = true;
	
	private String getProgressTitle(){
		if(ConfigState.isIOTDeviceInternetFinished){
			if(ConfigState.isIOTDeviceInternetSucceed){
				return "网络授权成功";
			}
			else{
				return "网络授权失败";
			}
		}
		if(ConfigState.isIOTDeviceLocalFinished){
			if(ConfigState.isIOTDeviceLocalSucceed){
				return "本地认证成功";
			}
			else{
				return "本地认证失败";
			}
		}
		if(ConfigState.isConnectAPFinished){
			if(ConfigState.isConnectAPSucceed){
				return "AP连接成功";
			}
			else{
				return "AP连接失败";
			}
		}
		if(ConfigState.isConnectIOTDeviceFinished){
			if(ConfigState.isConnectIOTDeviceSucceed){
				return "设备连接成功";
			}
			else{
				return "设备连接失败";
			}
		}
		return("开始配置");
	}
	/**
	 * it is exist here, just for some reason,
	 * it should be removed instantly
	 */
//	private static long userId;
	/*
	 * this handler is used to show the progress of ProgressBar
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String deviceName = mIotDeviceCurrent.getName();
			
			switch (msg.what) {
			case START:
				mTvTitle.setText("正在配置"+deviceName+" ...");
				mProgressBar.setProgress(0);
				break;
			case NEXT:
				if (!mClockThread.isInterrupted())
					mProgressBar.setProgress(mProgressBarCount);
				break;
			case STOP:
				mClockThread.interrupt();
				mLocked = false;
				if(!ConfigState.isFail){
					// config succeed
					if(!isDebug)
					mTvTitle.setText(deviceName+" 配置成功");
				}
				else{
					if(!isDebug)
					// config fail
					mTvTitle.setText(deviceName+" 配置失败");
				}
				// let user see the result
//				Util.Sleep(2000);
				mClockThread = null;
				mWorkThread = null;
				mIotDeviceCurrent = MessageStatic.nextIOTDevice();
				// whether there's more devices need configuring
				if(mIotDeviceCurrent!=null){
					Logger.e(TAG, "*******************next iotdevice config*******************");
					config();
				}
				return;
//				break;
			}
			
			/** set the status bar for debugging*/
			if(isDebug){
				String title = getProgressTitle();
				mTvTitle.setText(deviceName+" "+title);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progressbar);
		init();
	}

	/**
	 * lock the screen, it is not thoroughly presently
	 * it need improving later
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mLocked)
			return true;
		else
			return super.onTouchEvent(event);
	}

	protected void onResume() {
		super.onResume();
		MessageStatic.clearIOTDeviceListIndex();
//		MessageStatic.clearIOTDeviceList();
		mIotDeviceCurrent = MessageStatic.nextIOTDevice();
		if(mIotDeviceCurrent==null){
			mTvTitle.setText("无可配置设备");
		}
		else{
			config();
		}
	}
	
	private void config(){
//		MessageStatic.clearIOTDeviceListIndex();
		mLocked = true;
		startTiming();
		startConfiging();
	}

	private void init() {
		mWifiAdmin = new WifiAdmin(this);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mTvTitle = (TextView) findViewById(R.id.tv_progressbar_title);
		mIOTDeviceDBManager = IOTDeviceDBManager.getInstance(this);
	}

	/**
	 * configure the mIotDeviceCurrent to the AP(according to
	 * DeviceSettingActivity) selected by user
	 * 
	 * although the post action timeout is set 20s now,
	 * it should finish in 1s for the iotdevice do less thing
	 * at this moment
	 * it need modifying later
	 */
	private void configDevice() {
		
		String staGateWay = mIotDeviceCurrent.getIOTSta().getGateWay();
		String staIp = mIotDeviceCurrent.getIOTSta().getIP();
		String staMask = mIotDeviceCurrent.getIOTSta().getMask();
		String staPassword = MessageStatic.device_ap_connected_password;
		String staSSID = MessageStatic.device_ap_connected_ssid;

		mIotDeviceCurrent.getIOTSta().setGateWay(staGateWay);
		mIotDeviceCurrent.getIOTSta().setIP(staIp);
		mIotDeviceCurrent.getIOTSta().setMask(staMask);
		
		mIotDeviceCurrent.getIOTSta().setPassword(staPassword);
		mIotDeviceCurrent.getIOTSta().setSSID(staSSID);
		
		String token = RandomUtil.random40();
		Logger.d(TAG, "random token:" + token);
		mIotDeviceCurrent.getIOTSta().setToken(token);
		mIotDeviceCurrent.executeAction(IOTActionEnum.IOT_ACTION_SET_STA_CONFIGURE);
	}
	
	/**
	 * connect to the AP connected by IOTDevice
	 * it will take 22s at most
	 * @return		whether it is succeed
	 */
	@SuppressWarnings("unused")// it used by reflection
	private boolean connectAPSyn(){
		// 2s at most estimated by me
		oApiIntermediator.disconnectWifiSyn(mWifiAdmin);
//		Logger.e(TAG, "connect should be false, result is " + oApiIntermediator.isAPConnectedSyn(mWifiAdmin));
		oApiIntermediator.connectAPAsyn(mWifiAdmin, MessageStatic.device_ap_connected_ssid, 
				MessageStatic.device_ap_connected_password, MessageStatic.device_ap_type);
		// 20s at most set by me
		if(oApiIntermediator.isAPConnectedSyn(mWifiAdmin,CONSTANTS.CHECK_AP_CONNECTED_TIMEOUT_SECONDS_2))
				return true;
		return false;
	}
	
	/**
	 * connect to the IOTDevice which is configured
	 * it will take 22s at most
	 * @return		whether connect to the IOTDevice succeed
	 */
	private boolean connectIOTDevice(){
		String SSID = mIotDeviceCurrent.getIOTSoftAP().getSSID();
		// 2s at most
		oApiIntermediator.disconnectWifiSyn(mWifiAdmin);
		oApiIntermediator.connectAPAsyn(mWifiAdmin, SSID, true);
		// 20s at most
		if (oApiIntermediator.isAPConnectedSyn(mWifiAdmin,CONSTANTS.CHECK_AP_CONNECTED_TIMEOUT_SECONDS_2))
			return true;
		return false;
	}
	
	/**
	 * check whether IOTDevice is on Internet
	 * @return
	 */
	@SuppressWarnings("unused")// it used by reflection
	private boolean checkIOTDeviceInternet(){
//		String userToken = IOTDeviceHelper.getUserKey("baihua", "espressif");
//		User.token = userToken;
		String userToken = User.token;
		if(userToken==null){
			Logger.w(TAG, "checkIOTDeviceInternet() fail");
			return false;
		}
//		userId = mIOTDeviceDBManager.addUserIfNotExist(userToken);
		String tempToken = mIotDeviceCurrent.getIOTSta().getToken();
		Logger.d(TAG, "userToken:" + userToken);
		Logger.d(TAG,"tempToken:" + tempToken);
		String deviceKey = null;
		deviceKey = IOTDeviceHelper.authorize(mIotDeviceCurrent,userToken, tempToken);
		if(deviceKey!=null){
			mIotDeviceCurrent.setDeviceKey(deviceKey);
			Logger.d(TAG, "deviceKey:" + deviceKey);
			Logger.d(TAG, "checkIOTDeviceInternet() suc");
			return true;
		}
		else{
			Logger.w(TAG, "checkIOTDeviceInternet() fail");
			return false;
		}
	}
	
	/**
	 * check whether the mIotDeviceCurrent is configured successfully
	 * it takes 2s
	 * @return	whether the mIotDeviceCurrent is configured successfully
	 */
	@SuppressWarnings("unused")// it used by reflection
	private boolean checkIOTDeviceLocal(){
		// connect to the AP the mIotDeviceCurrent is connected to
		// check whether the device is exist on the AP
		List<IOTAddress> iotAddressList = oApiIntermediator.scanSTAsLANSyn();
		Logger.e(TAG, "iotAddressList.size = " + iotAddressList.size());
		Logger.i(TAG, "device's bssid is: " + mIotDeviceCurrent.getIOTAddress().getBSSID());
		for(IOTAddress iotAddress : iotAddressList){
			// iotAddress's BSSID is the same as mIotDeviceCurrent
			// there's something wrong for BSSID, so we use SSID at present
			String BSSID = BSSIDUtil.restoreRealBSSID(iotAddress.getBSSID());
			if(BSSID.equals(mIotDeviceCurrent.getIOTAddress().getBSSID())
					||
					/* */
					(iotAddress.getBSSID().equals(mIotDeviceCurrent.getIOTAddress().getBSSID()))){
				Logger.e(TAG, "checkIOTDeviceLocal():  type:" + iotAddress.getType().toString());
				mIotDeviceCurrent.getIOTAddress().setBSSID(iotAddress.getBSSID());
				mIotDeviceCurrent.setType(iotAddress.getType());
				return true;
			}
		}
		return false;
	}
	
	static class ConfigState{
		
		static boolean isConnectIOTDeviceFinished;
		static boolean isConnectIOTDeviceSucceed;
		static boolean isConnectAPFinished;
		static boolean isConnectAPSucceed;
		static boolean isIOTDeviceLocalFinished;
		static boolean isIOTDeviceLocalSucceed;
		static boolean isIOTDeviceInternetFinished;
		static boolean isIOTDeviceInternetSucceed;
		static boolean isFail;
		
		// the current progress, which should be showed in UI
		static int progressCurrent;
		// the last progress when the state of ConfigState changed
		// e.g. from isConnectIOTDeviceFinished to isConnectAPFinished
		static int progressPrevious;
		// last step of config, refer to constrain()
		static int stepPrevious;
		
		/**
		 * the sum of portion should be equal 1.0
		 */
		private static float[] portion = new float[]{
			//isConnectIOTDevice,  isConnectAP,  isIOTDeviceLocal,  isIOTDeviceInternet
			0.2f,				   0.3f,	     0.2f,	            0.3f
		};
		private static int transPortionInt(float[] portion,int number){
			int sum = 0;
			for(int i=0;i<number;i++){
				sum += portion[i]*PROGRESS_BAR_MAX;
			}
			return sum;
		}
		static int[] progressPortion = new int[]{
			transPortionInt(portion,1),
			transPortionInt(portion,2),
			transPortionInt(portion,3),
			PROGRESS_BAR_MAX// sometimes transPortionInt(portion,3) maybe less than PROGRESS_BAR_MAX
		};
		
		static void clearAll(){
			isConnectIOTDeviceFinished = false;
			isConnectIOTDeviceSucceed = false;
			isConnectAPFinished = false;
			isConnectAPSucceed = false;
			isIOTDeviceLocalFinished = false;
			isIOTDeviceLocalSucceed = false;
			isIOTDeviceInternetFinished = false;
			isIOTDeviceInternetSucceed = false;
			isFail = false;
			progressCurrent = 0;
			progressPrevious = 0;
			stepPrevious = 0;
		}
	}
	
	private void startConfiging(){
		
		Logger.d(TAG, "startConfiging()");
		
		ConfigState.clearAll();
		
		if (mWorkThread == null) {
			mWorkThread = new Thread(new Runnable() {
				public void run() {
					Logger.d(TAG, "ssid:"+MessageStatic.device_ap_connected_ssid);
					Logger.d(TAG, "password:"+MessageStatic.device_ap_connected_password);
//					mIotDeviceCurrent = MessageStatic.nextIOTDevice();
					ConfigState.isConnectIOTDeviceSucceed = connectIOTDevice();
					ConfigState.isConnectIOTDeviceFinished = true;
					
					if(ConfigState.isConnectIOTDeviceSucceed){
						
						Logger.e(TAG, "connectIOTDevice() suc");
						
						configDevice();
						
						ConfigState.isConnectAPSucceed = Reflect.Retry(1, CLASS_NAME,
								DeviceSettingProgressActivity.this,
								"connectAPSyn", 0);
						ConfigState.isConnectAPFinished = true;
						
						if(ConfigState.isConnectAPSucceed){
							
							Logger.e(TAG, "connectAPSyn() suc");
							
//							Util.Sleep(5000);
							
							/*
							ConfigState.isIOTDeviceLocalSucceed = Reflect.Retry(5, CLASS_NAME,
									DeviceSettingProgressActivity.this,
									"checkIOTDeviceLocal", 0);
							*/
							for (int tryTime = 0; tryTime < 5; tryTime++) {
								Logger.e(TAG, "tryTime="+tryTime);
								switch (tryTime) {
								case 0:
									CONSTANTS_DYNAMIC.UDP_BROADCAST_TIMEOUT_DYNAMIC = CONSTANTS.UDP_BROADCAST_TIMEOUT / 24;
									break;
								case 1:
									CONSTANTS_DYNAMIC.UDP_BROADCAST_TIMEOUT_DYNAMIC = CONSTANTS.UDP_BROADCAST_TIMEOUT / 12;
									break;
								case 2:
									CONSTANTS_DYNAMIC.UDP_BROADCAST_TIMEOUT_DYNAMIC = CONSTANTS.UDP_BROADCAST_TIMEOUT / 4;
									break;
								case 3:
									CONSTANTS_DYNAMIC.UDP_BROADCAST_TIMEOUT_DYNAMIC = CONSTANTS.UDP_BROADCAST_TIMEOUT / 2;
									break;
								case 4:
									CONSTANTS_DYNAMIC.UDP_BROADCAST_TIMEOUT_DYNAMIC = CONSTANTS.UDP_BROADCAST_TIMEOUT ;
								}
								ConfigState.isIOTDeviceLocalSucceed = 
										DeviceSettingProgressActivity.this.checkIOTDeviceLocal();
								if(ConfigState.isIOTDeviceLocalSucceed){
									break;
								}
							}
							CONSTANTS_DYNAMIC.UDP_BROADCAST_TIMEOUT_DYNAMIC = CONSTANTS.UDP_BROADCAST_TIMEOUT;
							
//							ConfigState.isIOTDeviceLocalSucceed = true;
							ConfigState.isIOTDeviceLocalFinished = true;
							
							Logger.e(TAG, "localSuc=" + ConfigState.isIOTDeviceLocalSucceed);
							
							// local succeed
							if(ConfigState.isIOTDeviceLocalSucceed){
								ConfigState.isIOTDeviceInternetSucceed = Reflect.Retry(10, CLASS_NAME,
										DeviceSettingProgressActivity.this,
										"checkIOTDeviceInternet", 2000);
								ConfigState.isIOTDeviceInternetFinished = true;
								if(ConfigState.isIOTDeviceInternetSucceed){
									Logger.e(TAG, "internet config suc");
									String deviceKey = mIotDeviceCurrent.getDeviceKey();
									String BSSID = mIotDeviceCurrent.getIOTAddress().getBSSID();
//									BSSID = BSSIDUtil.restoreRealBSSID(BSSID);
									/**
									 * !NOTE: we should get the TYPE from the server later
									 * for the moment, we just assume all of them are PLUG
									 */
//									String type = IOTDevice.getIOTDeviceType(TYPE.PLUG);
									TYPE type = mIotDeviceCurrent.getType();
									String typeStr = IOTDevice.getIOTDeviceType(type);
									String status = IOTDevice.getIOTDeviceStatus(STATUS.INTERNET);
									// add by afunx
									boolean isOwner= mIotDeviceCurrent.getIsOwner();
									long deviceId = mIotDeviceCurrent.getDeviceId();
									
									/**
									 * put the bssid and device type via device's metadata on Server
									 * !NOTE: we assume it should suc in 10 times,
									 * although i think there're many potential problems here,
									 * it is just used temporarily
									 */
									boolean suc = IOTDeviceHelper.putMetadata(deviceKey, BSSID, type);
									for(int retry = 0;retry<9;retry++){
										if(suc)
											break;
										suc = IOTDeviceHelper.putMetadata(deviceKey, BSSID, type);
									}
									
									// add the device to local db
									mIOTDeviceDBManager.
									addDevice(BSSID, typeStr, status, isOwner, deviceKey, true, deviceId
											,User.id );
									
									
								}
								else{
									Logger.e(TAG, "checkIOTDeviceInternet() fail");
									ConfigState.isFail = true;
								}
							}
							// only local
							if(!ConfigState.isIOTDeviceInternetSucceed){
								ConfigState.isFail = true;
								Logger.d(TAG, "local fail or internet fail");
							}
						}
						else{
							ConfigState.isFail = true;
							Logger.e(TAG, "connectAPSyn() fail");
						}
						
					}
					else{
						ConfigState.isFail = true;
						Logger.e(TAG, "connectIOTDevice() fail");
					}
				}
			});
			mWorkThread.start();
		}
	}
	
	/**
	 * calculate the progress, make user think the program is running all the time
	 * @param progress		the progress get from the ClockThread
	 * @param min			the min value
	 * @param max			the max value
	 * @param step			the step
	 * @return				the progress value displayed on UI
	 */
	private int calProgress(int progress,int min,int max,int step){
		int org = 0;
		switch(step){
		case 1:
			ConfigState.stepPrevious = 1;
			ConfigState.progressCurrent = MathUtil.constrain(progress, min, max);
			break;
		case 2:
			if(ConfigState.stepPrevious==1){
				ConfigState.progressPrevious = progress;
			}
			// progress - ConfigState.progressPrevious is the progress since last step changed
			org = min + progress - ConfigState.progressPrevious;
			ConfigState.progressCurrent = MathUtil.constrain(org, min, max);
			ConfigState.stepPrevious = 2;
			break;
		case 3:
			if(ConfigState.stepPrevious==2){
				ConfigState.progressPrevious = progress;
			}
			org = min + progress - ConfigState.progressPrevious;
			ConfigState.progressCurrent = MathUtil.constrain(org, min, max);
			ConfigState.stepPrevious = 3;
			break;
		case 4:
			if(ConfigState.stepPrevious==3){
				ConfigState.progressPrevious = progress;
			}
			org = min + progress - ConfigState.progressPrevious;
			ConfigState.progressCurrent = MathUtil.constrain(org, min, max);
			ConfigState.stepPrevious = 4;
			break;
		}
		return ConfigState.progressCurrent;
	}
	
	/**
	 * transfer the progress of the progressing bar
	 * @param progressBarCount		progressBarCount to be constrained
	 * @return						the result value
	 */
	private int transferProgres(int progressBarCount){
		int min,max;
		// step 0. check whether it is fail
		if(ConfigState.isFail){
			return PROGRESS_BAR_MAX;
		}
		// step 1. connect IOT device
		if(!ConfigState.isConnectIOTDeviceFinished){
			min = 0;
			max = ConfigState.progressPortion[0];
			return calProgress(progressBarCount,min,max,1);
		}
		// step 2. connect AP configured by user
		if(!ConfigState.isConnectAPFinished){
			min = ConfigState.progressPortion[0];
			max = ConfigState.progressPortion[1];
			return calProgress(progressBarCount,min,max,2);
		}
		// step 3. check whether local is ok
		if(!ConfigState.isIOTDeviceLocalFinished){
			min = ConfigState.progressPortion[1];
			max = ConfigState.progressPortion[2];
			return calProgress(progressBarCount,min,max,3);
		}
		// step 4. check whether internet is ok
		if(!ConfigState.isIOTDeviceInternetFinished){
			min = ConfigState.progressPortion[2];
			max = ConfigState.progressPortion[3];
			return calProgress(progressBarCount,min,max,4);
		}
		// step 5. no matter whether it succeed, it should return PROGRESS_BAR_MAX
		else{
			return PROGRESS_BAR_MAX;
		}
	}
	
	private void sendMessage(int what){
		Message msg = Message.obtain();
		msg.what = what;
		mHandler.sendMessage(msg);
	}
	
	/**
	 * start time IOT Device
	 */
	private void startTiming() {
		Logger.d(TAG, "startTiming()");
		mProgressBar.setMax(PROGRESS_BAR_MAX);
		mProgressBar.setProgress(0);
		mProgressBar.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
		if (mClockThread == null) {
			mClockThread = new Thread(new Runnable() {
				public void run() {
					// let user see the result configured last
					Util.Sleep(2000);
					
					sendMessage(START);
					for (int i = 1; i <= PROGRESS_BAR_MAX; i++) {
						if (!mLocked)
							break;
							mProgressBarCount = i ;
							mProgressBarCount = transferProgres(mProgressBarCount);
							Logger.d(TAG, "progressCount = " + mProgressBarCount);
							Util.Sleep((long)(1.0 * ONE_DEVICE_SETTING_TIME_SECONDS
									/ PROGRESS_BAR_MAX * 1000));
							if (mProgressBarCount == PROGRESS_BAR_MAX) {
								sendMessage(STOP);
								break;
							} else {
								sendMessage(NEXT);
							}
					}
				}
			});
			mClockThread.start();
		}
	}
}

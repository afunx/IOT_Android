package com.espressif.iot.ui.android.device;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;


import com.espressif.iot.R;
import com.espressif.iot.constants.CONSTANTS;
import com.espressif.iot.constants.CONSTANTS_DYNAMIC;
import com.espressif.iot.db.device.IOTDeviceDBManager;
import com.espressif.iot.db.device.model.TokenIsOwner;
import com.espressif.iot.db.greenrobot.daoDevice.DeviceDB;
import com.espressif.iot.model.device.IOTActionEnum;
import com.espressif.iot.model.device.IOTAddress;
import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.model.device.IOTDevice.STATUS;
import com.espressif.iot.model.device.IOTDevice.TYPE;
import com.espressif.iot.model.internet.User;
import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.net.lan.wifi.WifiScanResult;
import com.espressif.iot.oapi.OApiIntermediator;
import com.espressif.iot.ui.android.AbsFragment;
import com.espressif.iot.ui.android.MessageStatic;
import com.espressif.iot.ui.android.UtilActivity;
import com.espressif.iot.ui.android.share.ShareCaptureActivity;
import com.espressif.iot.ui.esp.iotdevice.EspUIDevice;
import com.espressif.iot.util.BSSIDUtil;
import com.espressif.iot.util.Logger;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

public class FragmentDevice extends AbsFragment {

	private static final String TAG = "FragmentDevice";
	
	// add by afunx 2014-04-18
	private List<WifiScanResult> mWifiScanResultList;
	private List<DeviceDB> mIOTDeviceDBList;
	
	private List<IOTDevice> mIOTDeviceNewList = new CopyOnWriteArrayList<IOTDevice>();
	// the position of where the IOTDeviceNew belong to List<WifiScanResult>
	private List<Integer> mIOTDeviceNewPosList = new CopyOnWriteArrayList<Integer>();
	
	private List<IOTDevice> mIOTDeviceLocalList = new CopyOnWriteArrayList<IOTDevice>();
	// the position of where the IOTDeviceLocal belong to List<IOTDeviceDB>
	private List<Integer> mIOTDeviceLocalPosList = new CopyOnWriteArrayList<Integer>();
	
	private List<IOTDevice> mIOTDeviceInternetList = new CopyOnWriteArrayList<IOTDevice>();
	// the position of where the IOTDeviceInternet belong to List<IOTDeviceDB>
	private List<Integer> mIOTDeviceInternetPosList = new CopyOnWriteArrayList<Integer>();
	
	private List<IOTDevice> mIOTDeviceOfflineList = new CopyOnWriteArrayList<IOTDevice>();
	// the position of where the IOTDeviceOffline belong to List<IOTDeviceDB>
	private List<Integer> mIOTDeviceOfflinePosList = new CopyOnWriteArrayList<Integer>();
	
	private volatile List<IOTDevice> mIOTDeviceConnectingList = new CopyOnWriteArrayList<IOTDevice>();
	// the position of where the IOTDeviceConnecting belong to List<IOTDeviceDB>
	private volatile List<Integer> mIOTDeviceConnectingPosList = new CopyOnWriteArrayList<Integer>();
//	private EspCopyOnWriteArrayList<IOTDevice> mIOTDeviceConnectingList = new EspCopyOnWriteArrayList<IOTDevice>();
//	private EspCopyOnWriteArrayList<Integer> mIOTDeviceConnectingPosList = new EspCopyOnWriteArrayList<Integer>();
	
	private final ReentrantLock lock = new ReentrantLock();
//	private final Object lock = new Object();
	
	private LinearLayout mLayout;

	public static LinearLayout mLayoutLeak;
	
    private PullToRefreshScrollView mEspUIRefreshableView;
    
    private static FragmentDevice leakThis;
    
    private static IOTDeviceDBManager sIOTDeviceDBManager;// = IOTDeviceDBManager.getInstance(getActivity());
    
    private OApiIntermediator oApiIntermediator;
    
	private WifiAdmin mWifiAdmin;
	
	/**
	 * !NOTE: 
	 * there's some concurrent bugs exist,
	 * for the moment, we use mIsStop to avoid it in most time,
	 * we will fix it thoroughly in the future
	 */
	private boolean mIsStop;
	
//	private volatile boolean isGetData2Finished;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			scanUI();
		}
	};
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Logger.d(TAG, "onCreate");
		oApiIntermediator = OApiIntermediator.getInstance();
		mWifiAdmin = new WifiAdmin(getActivity());
		View view = inflater
				.inflate(R.layout.activity_device_list, container, false);
		init(view);
		
		mEspUIRefreshableView = (PullToRefreshScrollView) view
				.findViewById(R.id.refreshable_device_view);
		// Set a listener to be invoked when the list should be refreshed.
		mEspUIRefreshableView.setOnRefreshListener(new OnRefreshListener<ScrollView>(){
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				// TODO Auto-generated method stub
//				if(isAllThreadFinished())
//				if(isGetData2Finished){
//					isGetData2Finished = false;
					new GetDataTask2().execute();
//				}else{
//					mEspUIRefreshableView.onRefreshComplete();
//				}
//				else
//					mEspUIRefreshableView.onRefreshComplete();
			}
			
		});
		
//		getDataTask1Exc();
		
		leakThis = this;
		
		sIOTDeviceDBManager = IOTDeviceDBManager.getInstance(getActivity());
		
//		isGetData2Finished = true;
		// add from db
//		mIOTDeviceDBList = sIOTDeviceDBManager.getIOTDeviceDBList(User.id);
		
		return view;
	}
	
	
	/**
	 * for the reason that fragment's onResume() is not like what we think it
	 */
	public static void onResumeAlike(){
//		leakThis.getDataTask1Exc();
	}
	
	private void getDataTask1Exc(){
//		if(isAllThreadFinished())
		new GetDataTask1().execute();
	}
	
	// used by onCreateView
	private class GetDataTask1 extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			Logger.d(TAG, "GetDataTask1");
			while(sIOTDeviceDBManager==null){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			scanAction();
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// Do some stuff here
			// Call onRefreshComplete when the list has been refreshed.
			scanUI();
			mEspUIRefreshableView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}
	
	// used by refreshableView
	private class GetDataTask2 extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			scanAction();
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// Do some stuff here
			// Call onRefreshComplete when the list has been refreshed.
			scanUI();
			mEspUIRefreshableView.onRefreshComplete();
//			isGetData2Finished = true;
			super.onPostExecute(result);
		}
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		this.mIsStop = false;
		getDataTask1Exc();
//		Log.e(TAG, "onResume");
	}
	
	@Override
	public void onStop(){
		super.onStop();
		this.mIsStop = true;
//		Log.e(TAG, "onStop");
	}
	
	
//	private IOTDevicesManager mIOTDevicesManager;
	
	/**
	 * deviceToken : be83e4661db6f548ecac67f14a9560f6111bb5d5
	 * 
	 * it remianed to be implemented later
	 * @return
	 */
	private boolean checkIOTDeviceInternet(IOTDevice device){
//		String userToken = User.userToken;
		boolean isOnline = false;
		String type = device.getType().toString().toLowerCase();
		String token = device.getDeviceKey();
		if(type.equals(IOTDevice.getIOTDeviceType(TYPE.PLUG))){
//			isOnline = IOTDeviceHelper.plugSwitch(false, token, false);
			isOnline = device.executeAction(IOTActionEnum.IOT_ACTION_GET_SWITCH_INTERNET);
			Logger.d(TAG,"checkIOTDeviceInternet() PLUG is " + isOnline);
//			return isOnline;
		}
		else if(type.equals(IOTDevice.getIOTDeviceType(TYPE.TEMPERATURE))){
//			isOnline = IOTDeviceHelper.getTemHumDataList(token)!=null;
			isOnline = device.executeAction(IOTActionEnum.IOT_ACTION_GET_TEM_HUM_INTERNET);
//			isOnline = IOTDeviceHelper.getTemHumData(token)!=null;
			Logger.d(TAG,"checkIOTDeviceInternet() TEMPERATURE is " + isOnline);
//			return isOnline;
		}
		return isOnline;
	}

	/**
	 * when it comes to local device, the IOTAddress will
	 * be set the current IOTAddress
	 */
	private IOTAddress mLocalIOTAddress = null;
	/**
	 * !NOTE it require modifying later
	 * @param currentSSID
	 * @return
	 */
	private boolean checkIOTDeviceLocal(String currentBSSID){
		// connect to the AP the mIotDeviceCurrent is connected to
		// check whether the device is exist on the AP
		List<IOTAddress> iotAddressList = oApiIntermediator.scanSTAsLANSyn();
		Logger.e(TAG, "iotAddressList.size = " + iotAddressList.size());
		for(IOTAddress iotAddress : iotAddressList){
			// iotAddress's BSSID is the same as mIotDeviceCurrent
			// there's something wrong for BSSID, so we use SSID at present
//			String BSSID = BSSIDUtil.restoreRealBSSID(iotAddress.getBSSID());
			String BSSID = iotAddress.getBSSID();
			Logger.i(TAG, "currentBSSID(device):"+currentBSSID+", BSSID(receive):"+BSSID);
			if(BSSID.equals(currentBSSID)
					||currentBSSID.equals(BSSIDUtil.restoreRealBSSID(BSSID))){
				mLocalIOTAddress = iotAddress;
				Logger.i(TAG, "checkIOTDeviceLocal() is true");
				return true;
			}
		}
		return false;
	}
	
	private void clearList(){
		mIOTDeviceNewList.clear();
		mIOTDeviceNewPosList.clear();
		mIOTDeviceLocalList.clear();
		mIOTDeviceLocalPosList.clear();
		mIOTDeviceOfflineList.clear();
		mIOTDeviceOfflinePosList.clear();
		mIOTDeviceInternetList.clear();
		mIOTDeviceInternetPosList.clear();
		mIOTDeviceConnectingList.clear();
		mIOTDeviceConnectingPosList.clear();
		
//		threadFinishedList.clear();
	}
	
	private void classifyStatus(int index, IOTDevice targetDevice, Integer targetIndex){
		/**
		 * add by afunx, to modify the concurrent error
		 */
		if(mIsStop){
			return;
		}
//		IOTDevice toRemovedDevice = mIOTDeviceConnectingList.get(index);
//		Integer toRemovedDevicePos = mIOTDeviceConnectingPosList.get(index);
		IOTDevice toRemovedDevice = targetDevice;
		Integer toRemovedDevicePos = targetIndex;
		
		DeviceDB iotDeviceDB = mIOTDeviceDBList.get(index);
		String BSSID = iotDeviceDB.getBssid();
//		String deviceKey = iotDeviceDB.getKey();
		long deviceId = iotDeviceDB.getId();
		TokenIsOwner tokenIsOwner = sIOTDeviceDBManager.getTokenIsOwner(deviceId, User.id);
		String deviceKey = tokenIsOwner.getToken();
		boolean isOwner = tokenIsOwner.getIsOwner();
		IOTDevice device = null;
		
		/**
		 * !NOTE: we nearly all is PLUG for the moment,
		 * it need modifying later
		 */
		String type = iotDeviceDB.getType();
//		device.setTypeStr(type);
		// check whether it is local
		boolean isLocal = false;
		
		/**
		 * !NOTE: temperature shouldn't have local status
		 */
		if (!type.equals(IOTDevice.getIOTDeviceType(TYPE.TEMPERATURE))) {
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
				if (checkIOTDeviceLocal(BSSID)) {
					isLocal = true;
					break;
				}
			}
			CONSTANTS_DYNAMIC.UDP_BROADCAST_TIMEOUT_DYNAMIC = CONSTANTS.UDP_BROADCAST_TIMEOUT;
		}
		
		/**
		 * JUST FOR TEST
		 */
//		isLocal = false;
		IOTAddress iotAddress = new IOTAddress(BSSID,null);
		device = IOTDevice.createIOTDevice(iotAddress);
		device.setTypeStr(type);
		device.setDeviceKey(deviceKey);
		device.setIsOwner(isOwner);
		
//		lock.lock();
		
		if(isLocal){
//			device = IOTDevice.createIOTDevice(mLocalIOTAddress);
			device.setIOTAddress(mLocalIOTAddress);
			device.setStatus(STATUS.LOCAL);
//			device.setTypeStr(type);
			lock.lock();
			mIOTDeviceLocalList.add(device);
			mIOTDeviceLocalPosList.add(index);
			lock.unlock();
		}
		// check whether it is Internet
		else{
			if(mIsStop){
				return;
			}
			boolean isInternetAccessable = oApiIntermediator.isInternetAccessedWifiWANSyn(getActivity())
					|| oApiIntermediator.isInternetAccessedMonetWANSyn(getActivity());
//			boolean isInternetConnected = checkIOTDeviceInternet(deviceKey,type);
			boolean isInternetConnected = checkIOTDeviceInternet(device);
			if(isInternetAccessable){
				if(isInternetConnected){
//					IOTAddress iotAddress = new IOTAddress(BSSID,null);
//					device = IOTDevice.createIOTDevice(iotAddress);
					device.setStatus(STATUS.INTERNET);
//					device.setTypeStr(type);
					lock.lock();
					mIOTDeviceInternetList.add(device);
					mIOTDeviceInternetPosList.add(index);
					lock.unlock();
//					mHandler.sendEmptyMessage(0);
//					return;
				}
			}
//			else{
			if(!isInternetAccessable||!isInternetConnected){
				// it is offline
//				IOTAddress iotAddress = new IOTAddress(BSSID,null);
//				device = IOTDevice.createIOTDevice(iotAddress);
				device.setStatus(STATUS.OFFLINE);
//				device.setTypeStr(type);
				lock.lock();
				mIOTDeviceOfflineList.add(device);
				mIOTDeviceOfflinePosList.add(index);
				lock.unlock();
			}
//			}
		}
		
//		device.setDeviceKey(tokenIsOwner.getToken());
//		device.setIsOwner(tokenIsOwner.getIsOwner());
		
		lock.lock();
		
//		synchronized(lock){
//		mIOTDeviceConnectingList.remove(index);
		
		//boolean deviceDelete = mIOTDeviceConnectingList.remove(toRemovedDevice);
		boolean deviceDelete = false;
		for(IOTDevice iotdevice: mIOTDeviceConnectingList){
			String iotdeviceBssid = iotdevice.getIOTAddress().getBSSID();
			String toRemoveBssid = toRemovedDevice.getIOTAddress().getBSSID();
			if(iotdeviceBssid.equals(toRemoveBssid)){
				deviceDelete = mIOTDeviceConnectingList.remove(iotdevice);
				break;
			}
		}
		if(!deviceDelete){
			Logger.t(TAG, "device delete fail");
		}
//		mIOTDeviceConnectingPosList.remove(index);
		boolean posDelete = mIOTDeviceConnectingPosList.remove(toRemovedDevicePos);
		if(!posDelete){
			Logger.t(TAG, "pos delete fail");
		}
		lock.unlock();
//		}
		
		mHandler.sendEmptyMessage(0);
	}
	
//	private volatile int mThreadNumber = 0;
//	private boolean isAllThreadFinished(){
//		return mThreadNumber == 0;
//	}
	private volatile List<Boolean> threadFinishedList = new CopyOnWriteArrayList<Boolean>();
	
	/*
	private IOTDevice mIOTDeviceSimulate;
	// add a simulate device
	private void addScanActionSimulate(){
		String BSSID = "12:34:56:8c:7b:00";
		IOTAddress iotAddress = new IOTAddress(BSSID,null);
		mIOTDeviceSimulate = IOTDevice.createIOTDevice(iotAddress);
		mIOTDeviceSimulate.setStatus(STATUS.INTERNET);
		mIOTDeviceSimulate.setType(TYPE.TEMPERATURE);
	}
	// add a simulate device
	private void addScanUISimulate(){
		String BSSID = "12:34:56:8c:7b:00";
		String key = "20dd316acf9c3f0f9347c27fab14d77bd98458ac";
		boolean isActive = true;
		boolean isOwner = true;
		mIOTDeviceSimulate.setDeviceKey(key);
		mIOTDeviceSimulate.setIsActive(isActive);
		mIOTDeviceSimulate.setIsOwner(isOwner);
		EspUIDevice espUIDevice = new EspUIDevice(this.getActivity(), mIOTDeviceSimulate, getActivity());
		espUIDevice.setEspDeviceName(BSSIDUtil.genDeviceNameByBSSID(BSSID));
		espUIDevice.setEspStatusInternet();
		mLayout.addView(espUIDevice);
	}
	*/
	private boolean isDeviceInDB(String BSSID){
		for(DeviceDB deviceDB: mIOTDeviceDBList){
			String bssid = BSSIDUtil.restoreRealBSSID(deviceDB.getBssid());
			if(bssid.equals(BSSID))
				return true;
		}
		return false;
	}
	
	private void scanAction(){
		lock.lock();
//		synchronized(lock){
		clearList();
//		}
//		lock.unlock();
//		// add from db
		mIOTDeviceDBList = sIOTDeviceDBManager.getIOTDeviceDBList(User.id);
		// add from scan
		mWifiScanResultList = oApiIntermediator.scanAPsLANSyn(mWifiAdmin,true);
		for(int index=0;index<mWifiScanResultList.size();index++){
			WifiScanResult wifiScanResult = mWifiScanResultList.get(index);
			String BSSID = wifiScanResult.getScanResult().BSSID;
			
			// if the device is in db, we ignore it
			if(isDeviceInDB(BSSID)){
				continue;
			}
			
			String SSID = wifiScanResult.getScanResult().SSID;
			InetAddress inetAddress = null;
			try {
				inetAddress = InetAddress.getByName("192.168.4.1");
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			IOTAddress iotAddress = new IOTAddress(BSSID,inetAddress);
			Logger.e(TAG, "SSID:"+SSID+",BSSID:"+BSSID);
			IOTDevice device = IOTDevice.createIOTDevice(iotAddress);
			// only set the SoftAP's SSID for connecting to the device later
			device.getIOTSoftAP().setSSID(SSID);
			
			// it is not in the db, which means it is new device found
			if(!sIOTDeviceDBManager.isDeviceExistByBSSID(BSSID)){
//			if(true){
				// device new
				device.setStatus(STATUS.NEW);
				mIOTDeviceNewList.add(device);
				mIOTDeviceNewPosList.add(index);
			}
			/**
			 * add by afunx 2014-05-09
			 */
			/*
			for(DeviceDB deviceDB: mIOTDeviceDBList){
				Log.e(TAG, "afunx");
				String deviceDBBssid = deviceDB.getBssid();
				String realDeviceDBBssid = BSSIDUtil.restoreRealBSSID(deviceDBBssid);
				String deviceBssid = iotAddress.getBSSID();
				String realDeviceBssid = BSSIDUtil.restoreRealBSSID(deviceBssid);
				if(deviceDBBssid.equals(deviceBssid)
						||deviceDBBssid.equals(realDeviceBssid)
						|| realDeviceDBBssid.equals(deviceBssid)
						|| realDeviceDBBssid.equals(realDeviceBssid))
				{
					mIOTDeviceDBList.remove(deviceDB);
				}
//				sIOTDeviceDBManager.deleteDeviceByBSSID(deviceDBBssid, User.id);
			}
			*/
		}
//		// add from db
//		mIOTDeviceDBList = sIOTDeviceDBManager.getIOTDeviceDBList(User.id);
		
		/**
		 * add new element to threadFinishedList, if new mIOTDeviceDBList element exist
		 */
		for(int i=threadFinishedList.size();i<mIOTDeviceDBList.size();i++){
			threadFinishedList.add(true);
		}
		
		
		/**
		 * add the simulating device, it should be removed later
		 */
//		addScanActionSimulate();
		
//		mThreadNumber = 0;
		for(int index=0;index<mIOTDeviceDBList.size();index++){
//			classifyStatus(index);
			final int indexFinal = index;
			DeviceDB iotDeviceDB = mIOTDeviceDBList.get(index);
			String BSSID = iotDeviceDB.getBssid();
			IOTAddress iotAddress = new IOTAddress(BSSID,null);
			String type = iotDeviceDB.getType();
			IOTDevice device = IOTDevice.createIOTDevice(iotAddress);
			device.setStatus(STATUS.CONNECTING);
			/**
			 * !NOTE: we got it from db local
			 * at the moment, when activating device, we store all
			 * the device as type of PLUG, refer to DeviceSettingProgressActivity
			 */
			device.setTypeStr(type);
			/**
			 * !NOTE: we should store the info while activate the device
			 * for the moment, we just assume it is PLUG
			 */
//			lock.lock();
//			synchronized(lock){
			mIOTDeviceConnectingList.add(device);
			mIOTDeviceConnectingPosList.add(index);
//			}
//			lock.unlock();
			new Thread(){
				public void run(){
//					mThreadNumber++;
					if(threadFinishedList.get(indexFinal)){
						threadFinishedList.set(indexFinal, false);
						lock.lock();
						
//						mIOTDeviceConnectingList.add(device);
//						mIOTDeviceConnectingPosList.add(index);
						if(indexFinal<mIOTDeviceConnectingList.size()&&
								indexFinal<mIOTDeviceConnectingPosList.size()){
						IOTDevice targetDevice = mIOTDeviceConnectingList.get(indexFinal);
						Integer targetPos = mIOTDeviceConnectingPosList.get(indexFinal);
						
						lock.unlock();
						classifyStatus(indexFinal,targetDevice,targetPos);
						}
						else{
							lock.unlock();
						}
//						lock.unlock();
						threadFinishedList.set(indexFinal, true);
					}
//					mThreadNumber--;
				}
			}.start();
		}
		lock.unlock();
	}
	
	private void scanUI(){
		Logger.d(TAG, "scanUI()");
		if(mIsStop){
			return;
		}
		mLayout.removeAllViews();
		MessageStatic.clearIOTDeviceList();
		
		lock.lock();
		// Device new
		for(int index=0;index<mIOTDeviceNewList.size();index++){
			WifiScanResult wifiScanResult = mWifiScanResultList.get(mIOTDeviceNewPosList.get(index));
			String SSID = wifiScanResult.getScanResult().SSID;
			IOTDevice device = mIOTDeviceNewList.get(index);
			EspUIDevice espUIDevice = new EspUIDevice(this.getActivity(), device, getActivity());
			espUIDevice.setEspDeviceName(SSID);
			espUIDevice.setEspStatusNew();
			mLayout.addView(espUIDevice);
		}
//		lock.lock();
//		synchronized(lock){
		// Device connecting
		for(int index=0;index<mIOTDeviceConnectingList.size()
				&& index < mIOTDeviceConnectingPosList.size();index++){
//			if(mIOTDeviceConnectingPosList.isEmpty()||mIOTDeviceConnectingPosList.size()<index)
			/*
			if(!(mIOTDeviceConnectingPosList.size()>index))
				break;
			*/
			if(mIOTDeviceConnectingList.size()!=mIOTDeviceConnectingPosList.size()){
				Logger.w(TAG,"NOT EQUAL");
				Logger.w(TAG, "device size is " + mIOTDeviceConnectingList.size()
						+",pos size is " + mIOTDeviceConnectingPosList.size());
			}
			
			int position = mIOTDeviceConnectingPosList.get(index);
			
			DeviceDB iotDeviceDB = mIOTDeviceDBList.get(position);
			String BSSID = iotDeviceDB.getBssid();
			IOTDevice device = mIOTDeviceConnectingList.get(index);
			EspUIDevice espUIDevice = new EspUIDevice(getActivity(), device, getActivity());
			espUIDevice.setEspDeviceName(BSSIDUtil.genDeviceNameByBSSID(BSSID));
			espUIDevice.setEspStatusConnecting();
			mLayout.addView(espUIDevice);
		}
//		}
//		lock.unlock();
		// Device local
		for(int index=0;index<mIOTDeviceLocalList.size();index++){
			DeviceDB iotDeviceDB = mIOTDeviceDBList.get(mIOTDeviceLocalPosList.get(index));
			String BSSID = iotDeviceDB.getBssid();
			IOTDevice device = mIOTDeviceLocalList.get(index);
			EspUIDevice espUIDevice = new EspUIDevice(this.getActivity(), device, getActivity());
			espUIDevice.setEspDeviceName(BSSIDUtil.genDeviceNameByBSSID(BSSID));
			espUIDevice.setEspStatusLocal();
			mLayout.addView(espUIDevice);
		}
		// Device Internet
		for(int index=0;index<mIOTDeviceInternetList.size();index++){
			DeviceDB iotDeviceDB = mIOTDeviceDBList.get(mIOTDeviceInternetPosList.get(index));
			String BSSID = iotDeviceDB.getBssid();
//			String key = iotDeviceDB.getKey();
//			boolean isActive = iotDeviceDB.getIsActive();
//			boolean isOwner = iotDeviceDB.getIsOwner();
			long deviceId = iotDeviceDB.getId();
			TokenIsOwner tokenIsOwner = sIOTDeviceDBManager.getTokenIsOwner(deviceId, User.id);
			boolean isActive = true;
			String key = tokenIsOwner.getToken();
			boolean isOwner = tokenIsOwner.getIsOwner();
			
			IOTDevice device = mIOTDeviceInternetList.get(index);
			device.setDeviceKey(key);
			device.setIsActive(isActive);
			device.setIsOwner(isOwner);
			EspUIDevice espUIDevice = new EspUIDevice(this.getActivity(), device, getActivity());
			espUIDevice.setEspDeviceName(BSSIDUtil.genDeviceNameByBSSID(BSSID));
			espUIDevice.setEspStatusInternet();
			mLayout.addView(espUIDevice);
		}
		// Device Offline
		for(int index=0;index<mIOTDeviceOfflineList.size();index++){
			DeviceDB iotDeviceDB = mIOTDeviceDBList.get(mIOTDeviceOfflinePosList.get(index));
			String BSSID = iotDeviceDB.getBssid();
			IOTDevice device = mIOTDeviceOfflineList.get(index);
			EspUIDevice espUIDevice = new EspUIDevice(this.getActivity(), device, getActivity());
			espUIDevice.setEspDeviceName(BSSIDUtil.genDeviceNameByBSSID(BSSID));
			espUIDevice.setEspStatusOffline();
			mLayout.addView(espUIDevice);
		}
		lock.unlock();
		/**
		 * add the simulating device, it should be removed later
		 */
//		addScanUISimulate();
	}
	
	@Override
	protected void init(View view) {
		// TODO Auto-generated method stub
		mLayout = (LinearLayout) view.findViewById(R.id.linearlayout_device);
		mLayoutLeak = mLayout;
		
		initShareButton(view);
	}
	
	private void initShareButton(View view){
		btnShare = (Button) view.findViewById(R.id.btn_device_list_share);
		btnShare.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UtilActivity.transferActivity(
						FragmentDevice.this.getActivity(),
						ShareCaptureActivity.class, false);
				}
			
		});
	}
	private Button btnShare;
	
}

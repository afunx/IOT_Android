package com.espressif.iot.model.device;

import android.util.Log;

import com.espressif.iot.model.container.IOTGroup;
import com.espressif.iot.model.device.softap.IOTActionSetSoftApConfigure;
import com.espressif.iot.model.device.softap.IOTSoftAP;
import com.espressif.iot.model.device.sta.IOTActionSetStaConfigure;
import com.espressif.iot.model.device.sta.IOTSta;
import com.espressif.iot.model.device.sta_softap.IOTActionGetInfo;
import com.espressif.iot.model.device.sta_softap.IOTActionGetStaSoftApConfigure;
import com.espressif.iot.model.device.sta_softap.IOTActionGetSwitch;
import com.espressif.iot.model.device.sta_softap.IOTActionGetWifiConfigure;
import com.espressif.iot.model.device.sta_softap.IOTActionSetWifiConfigure;
import com.espressif.iot.model.device.sta_softap.IOTActionSwitchOff;
import com.espressif.iot.model.device.sta_softap.IOTActionSwitchOn;
import com.espressif.iot.model.device.sta_softap.IOTCommonStatus;
import com.espressif.iot.model.internet.IOTActionTemHumGet100Internet;
import com.espressif.iot.model.internet.IOTActionSwitchGetInternet;
import com.espressif.iot.model.internet.IOTActionSwitchOffInternet;
import com.espressif.iot.model.internet.IOTActionSwitchOnInternet;
import com.espressif.iot.util.BSSIDUtil;
import com.espressif.iot.util.Logger;

public class IOTDevice {
	
	private static final String TAG = "IOTDevice";

	public enum TYPE{
		PLUG,
		LIGHT,
		TEMPERATURE
	}
	private static final String[] TypeStr = new String[]{"plug","light","temperature"};
	public static String getIOTDeviceType(IOTDevice.TYPE type){
		return TypeStr[type.ordinal()];
	}
	public static TYPE getIOTDeviceType(String typeStr){
		if(typeStr.equals(TypeStr[0])){
			return TYPE.PLUG;
		}
		else if(typeStr.equals(TypeStr[1])){
			return TYPE.LIGHT;
		}
		else if(typeStr.endsWith(TypeStr[2])){
			return TYPE.TEMPERATURE;
		}
		return null;
	}
	private TYPE mType;
	public void setTypeStr(String typeStr){
		for(int i=0;i<TypeStr.length;i++){
			if(typeStr.equals(TypeStr[i])){
				switch(i){
				case 0:
					mType = TYPE.PLUG;
					break;
				case 1:
					mType = TYPE.LIGHT;
					break;
				case 2:
					mType = TYPE.TEMPERATURE;
					break;
				}
			}
		}
	}
	public void setType(TYPE type){
		this.mType = type;
	}
	public TYPE getType(){
		return this.mType;
	}
	public String getTypeStr(){
		return TypeStr[mType.ordinal()];
	}
	
	public enum STATUS{
		NEW,
		LOCAL,
		INTERNET,
		OFFLINE,
		CONNECTING
	}
	private static final String[] StatusStr = new String[]{"new","local","internet","offline","connecting"};
	public static String getIOTDeviceStatus(IOTDevice.STATUS status){
		return StatusStr[status.ordinal()];
	}

	private STATUS mStatus;
	public void setStatus(STATUS status){
		this.mStatus = status;
	}
	public STATUS getStatus(){
		return this.mStatus;
	}
	public String getStatusStr(){
		return StatusStr[mStatus.ordinal()];
	}
	
	private boolean mIsOwner;
	public void setIsOwner(boolean isOwner){
		this.mIsOwner = isOwner;
	}
	public boolean getIsOwner(){
		return this.mIsOwner;
	}
	
	private boolean mIsActive;
	public void setIsActive(boolean isActive){
		this.mIsActive = isActive;
	}
	public boolean getIsActive(){
		return this.mIsActive;
	}
	
	/**
	 * maybe owner key or guest key
	 */
	private String mDeviceKey;
	public void setDeviceKey(String deviceKey){
		this.mDeviceKey = deviceKey;
	}
	public String getDeviceKey(){
		return this.mDeviceKey;
	}
	
	//****************************************************************
	//****************************************************************
	
	/**
	 * the fixed length of device name is to
	 * make the UI not change for the change
	 * of the device name's length
	 */
	private static final int DEVICE_NAME_LEN = 32;
	
	// it is
	private IOTAddress mIotAddress;
	private IOTSoftAP mIotSoftAP;
	private IOTSta mIotSta;
	private IOTCommonStatus mIotCommonStatus;

	private int missTime;
	
	private String mIOTName;

	public static long deviceIdAllocator = 0;
	private long mDeviceId;
	public long getDeviceId(){
		return mDeviceId;
	}
	public void setDeviceId(long deviceId){
		this.mDeviceId = deviceId;
	}
	private IOTGroup mGroup;
	/**
	 * whether the device is the master in the group
	 */
	private boolean mIsMaster;
	
	public void setIsMaster(){
		this.mIsMaster = true;
	}
	public void clearIsMaster(){
		this.mIsMaster = false;
	}
	public boolean isMaster(){
		return mIsMaster;
	}
	
	private boolean mIsOnline = false;
	public void setOnline(boolean isOnline){
		this.mIsOnline = isOnline;
	}
	public boolean isOnline(){
		return mIsOnline;
	}
	
	/**
	 * whether the device is the same device( same BSSID )
	 * @param device	whether the device is the same BSSID
	 * @return			whether they are the same iotDevice
	 */
	public boolean isSameDevice(IOTDevice device){
		if(device!=null){
			return this.mIotAddress.getBSSID().equals(device.getIOTAddress().getBSSID());
		}
		return false;
	}
	
	public void setGroup(IOTGroup group){
		this.mGroup = group;
	}
	public IOTGroup getGroup(){
		return mGroup;
	}


	
	/**
	 * TODO	it is just used to clear the IOTDevice
	 * 		it will be implemented later
	 */
	public void clear(){
		
	}
	
	public void missOnce(){
		missTime++;
		Logger.i(TAG, "BSSID:"+mIotAddress.getBSSID()+",miss time="+missTime);
	}
	public int getMissTime(){
		return missTime;
	}
	public void clearMissTime(){
		missTime = 0;
	}
	
	public String getName(){
		int len = mIOTName.length();
		if(len<=DEVICE_NAME_LEN)
			return mIOTName;
		else
			return mIOTName.substring(0, DEVICE_NAME_LEN);
	}
	public void setName(String iotName){
		this.mIOTName =  iotName;
	}
	
	// factory pattern
	public static IOTDevice createIOTDevice(IOTAddress iotAddress) {
		return new IOTDevice(iotAddress);
	}

	private IOTDevice(IOTAddress iotAddress) {
		this.mIotAddress = iotAddress;
		/**
		 * sometimes iotAddress is null, for the reason is that 
		 * the class is not designed well enough, so it remains
		 * to be modified later
		 */
		if(iotAddress!=null){
//			this.mIOTName = "IP:"+iotAddress.getInetAddress();
			this.mIOTName = BSSIDUtil.genDeviceNameByBSSID(iotAddress.getBSSID());
		}
		mIotSoftAP = new IOTSoftAP();
		mIotSta = new IOTSta();
		mIotCommonStatus = new IOTCommonStatus();
//		this.mDeviceId = deviceIdAllocator++;
	}

	public IOTAddress getIOTAddress() {
		return this.mIotAddress;
	}
	
	public void setIOTAddress(IOTAddress iotAddress){
		this.mIotAddress = iotAddress;
	}

	public IOTSoftAP getIOTSoftAP() {
		return this.mIotSoftAP;
	}

	public IOTSta getIOTSta() {
		return this.mIotSta;
	}

	public IOTCommonStatus getIOTCommonStatus() {
		return this.mIotCommonStatus;
	}

	private IOTAction<?> mAction;

	public boolean executeAction(IOTActionEnum actionEnum) {
		switch (actionEnum) {
		case IOT_ACTION_SWITCH_ON:
			mAction = new IOTActionSwitchOn(this);
			break;
		case IOT_ACTION_SWITCH_OFF:
			mAction = new IOTActionSwitchOff(this);
			break;
		case IOT_ACTION_GET_SWITCH:
			mAction = new IOTActionGetSwitch(this);
			break;
		case IOT_ACTION_GET_INFO:
			mAction = new IOTActionGetInfo(this);
			break;
		case IOT_ACTION_SET_WIFI_CONFIGURE:
			mAction = new IOTActionSetWifiConfigure(this);
			break;
		case IOT_ACTION_GET_WIFI_CONFIGURE:
			mAction = new IOTActionGetWifiConfigure(this);
			break;
		case IOT_ACTION_SET_STA_CONFIGURE:
			mAction = new IOTActionSetStaConfigure(this);
			break;
		case IOT_ACTION_SET_SOFTAP_CONFIGURE:
			mAction = new IOTActionSetSoftApConfigure(this);
			break;
		case IOT_ACTION_GET_STA_SOFTAP_CONFIGURE:
			mAction = new IOTActionGetStaSoftApConfigure(this);
			break;
	
		case IOT_ACTION_GET_TEM_HUM_100_INTERNET:
			mAction = new IOTActionTemHumGet100Internet(this);
			break;
		
		case IOT_ACTION_GET_SWITCH_INTERNET:
			mAction = new IOTActionSwitchGetInternet(this);
			
		case IOT_ACTION_SWITCH_ON_INTERNET:
			mAction = new IOTActionSwitchOnInternet(this);
			break;
			
		case IOT_ACTION_SWITCH_OFF_INTERNET:
			mAction = new IOTActionSwitchOffInternet(this);
			break;
			
		default:
			Logger.e(TAG,
					"it should never happen: the actionEnum is not supported.");
			return false;

		}
		return mAction.executeAction();
	}
	
	public Object getActionResult(){
		return mAction.getResult();
	}
	
	@Override
	public String toString(){
		return	"IOTDevice:"+
				"  Id:"+mDeviceId+
				"  IOTAddress:"+mIotAddress;
	}

}

package com.espressif.iot.model.container;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.util.Log;

import com.espressif.iot.model.device.IOTDevice;

/**
 * each IOTGroup is empty or has a master device,
 * if the IOTGroup is empty, its groupId is -1
 * else the groupId of it is the master device's Id
 * 
 * a master device can't be removed except clear(),
 * which would removed all devices
 * 
 * @author afunx
 *
 */
public class IOTGroup {
	
	private static final String TAG = "IOTGroup";
	/**
	 * mGroupId is the master device's Id
	 */
	private long mGroupId = -1;
	
	private String mGroupName;
	
	private List<IOTDevice> mIOTDeviceList = new CopyOnWriteArrayList<IOTDevice>();
	
	public void setGroupName(String groupName){
		this.mGroupName = groupName;
	}
	public String getGroupName(){
		if(mGroupName==null)
			return "Group " + mGroupId;
		else
			return mGroupName;
	}
	
	public IOTGroup(){
	}
	
	public void setGroupId(long groupId){
		this.mGroupId = groupId;
	}
	
	public long getGroupId(){
		return mGroupId;
	}
	
	public int getSize(){
		return mIOTDeviceList.size();
	}
	
	public void clear(){
		Log.d(TAG, "clear()");
		for(IOTDevice device: mIOTDeviceList){
			device.clear();
		}
		mIOTDeviceList.clear();
		mGroupId = -1;
	}
	
	public IOTDevice getIndex(int location){
		return mIOTDeviceList.get(location);
	}
	
	private boolean isContained(IOTDevice device) {
		Log.e(TAG, "isContained(): list size = " + mIOTDeviceList.size());
		for (IOTDevice deviceInList : mIOTDeviceList) {
			if (device.isSameDevice(deviceInList)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * add IOT Device if not exist
	 * @param device		the IOT Device to be added
	 */
	public void addIOTDevice(IOTDevice device){
		if(isContained(device)){
			Log.w(TAG, "addIOTDevice() : device is exist already");
		}
		if(!isContained(device)){
			Log.d(TAG, device + "device:("+device+") is added in the group:("+this+")");
			mIOTDeviceList.add(device);
			device.setGroup(this);
			/**
			 * if the group is empty, the first device will be the master
			 */
			if(mGroupId==-1){
				device.setIsMaster();
				this.mGroupId = device.getDeviceId();
			}
		}
	}
	
	/**
	 * get the same IOTDevice in the Group, which BSSID is the same
	 * @param device
	 * @return
	 */
	private IOTDevice getSameIOTDevice(IOTDevice device){
		for (IOTDevice deviceInList : mIOTDeviceList) {
			if (device.isSameDevice(deviceInList)) {
				return deviceInList;
			}
		}
		return null;
	}
	
	/**
	 * remove IOT Device from IOTGroup
	 * @param device		the IOT Device to be removed
	 * @return				whether the device is removed
	 * 						,master device couldn't be removed except clear()
	 */
	public boolean removeIOTDevice(IOTDevice device){
//		if(device.isMaster()){
//			Log.d(TAG, "device is master, remove fail");
//			return false;
//		}
//		else{
			IOTDevice deviceInGroup = getSameIOTDevice(device);
			boolean result = mIOTDeviceList.remove(deviceInGroup);
			if(result){
				Log.d(TAG, "device remove succeed");
				return true;
			}
			else{
				Log.d(TAG, "device remove fail");
				return false;
			}
//		}	
	}
	
	@Override
	public String toString(){
		String result = "IOTGroup:"+
				"  Id:"+mGroupId+
				"  size:"+mIOTDeviceList.size()+
				"\n";
		for(IOTDevice iotDevice : mIOTDeviceList){
			result += iotDevice;
		}
		return	result;
	}
}

package com.espressif.iot.model.container;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.util.Log;

import com.espressif.iot.model.device.IOTDevice;

/**
 * this class is a Singleton class, which is used to store the IOTGroup
 * IOTGroup store the IOTDevice of the same group
 * 
 * @author afunx
 *
 */
public class IOTContainer {
	
	private static final String TAG = "IOTContainer";
	// Singleton Pattern
	private static IOTContainer instance = new IOTContainer();
	private IOTContainer(){
		mGroupList = new ArrayList<IOTGroup> ();
		
//		IOTGroup group = new IOTGroup();
//		group.setGroupName("Accessible device");
//		mGroupList.add(group);
	}
	public static IOTContainer getInstance(){
		return instance;
	}

	private List<IOTGroup> mGroupList;
	
	public void add(IOTGroup group){
		mGroupList.add(group);
	}
	
	public void remove(IOTGroup group){
		mGroupList.remove(group);
	}
	
	public List<IOTGroup> getIOTGroupList(){
		return mGroupList;
	}
	
	public IOTGroup get(String groupName){
		if(mGroupList!=null){
			for(IOTGroup group: mGroupList){
				if(group.getGroupName().equals(groupName))
					return group;
			}
		}
		return null;
	}
	
	public IOTGroup get(int location){
		return mGroupList.get(location);
	}
	
	public int getSize(){
		return mGroupList.size();
	}
	
	public void clear(){
		Log.d(TAG,"clear()");
		for(IOTGroup group: mGroupList){
			group.clear();
		}
		IOTDevice.deviceIdAllocator = 0;
		mGroupList.clear();
	}
	
	@Override
	public String toString(){
		String result = "IOTContainer:\n"+
						"  size:"+mGroupList.size()+"\n";
		for(IOTGroup group: mGroupList){
			result += group;
		}
		return result;
	}
}

package com.espressif.iot.model.device.sta_softap;

import android.util.Log;

import com.espressif.iot.model.device.IOTAction;
import com.espressif.iot.model.device.IOTActionEnum;
import com.espressif.iot.model.device.IOTDevice;

public class IOTActionGetSwitch extends IOTAction<Boolean>{

	private static final String TAG = "IOTActionGetSwitch";
	
	public IOTActionGetSwitch(IOTDevice iotDevice) {
		super(iotDevice);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void actionFailed() {
		// TODO Auto-generated method stub
		Log.e(TAG, "action fail");
	}

	@Override
	protected boolean action() {
		// TODO Auto-generated method stub
		return result = intermediator.executeIOTAction(mIOTDevice,
				IOTActionEnum.IOT_ACTION_GET_SWITCH);
	}

	@Override
	protected Boolean getResult() {
		// TODO Auto-generated method stub
		return result;
	}

}

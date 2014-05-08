package com.espressif.iot.model.device.sta;

import android.util.Log;

import com.espressif.iot.model.device.IOTAction;
import com.espressif.iot.model.device.IOTActionEnum;
import com.espressif.iot.model.device.IOTDevice;

public class IOTActionSetStaConfigure extends IOTAction<Boolean>{

	private final String TAG = "IOTActionSetStaConfigure";
	
	public IOTActionSetStaConfigure(IOTDevice iotDevice) {
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
				IOTActionEnum.IOT_ACTION_SET_STA_CONFIGURE);
	}

	@Override
	protected Boolean getResult() {
		// TODO Auto-generated method stub
		return result;
	}

}

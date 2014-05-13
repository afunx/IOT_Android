package com.espressif.iot.model.device.sta_softap;

import android.util.Log;

import com.espressif.iot.model.device.IOTAction;
import com.espressif.iot.model.device.IOTActionEnum;
import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.util.Logger;

public class IOTActionGetInfo extends IOTAction<Boolean> {

	private static final String TAG = "IOTActionGetInfo";

	public IOTActionGetInfo(IOTDevice iotDevice) {
		super(iotDevice);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void actionFailed() {
		// TODO Auto-generated method stub
		Logger.e(TAG, "action fail");
	}

	@Override
	protected boolean action() {
		// TODO Auto-generated method stub
		return result = intermediator.executeIOTAction(mIOTDevice,
				IOTActionEnum.IOT_ACTION_GET_INFO);
	}

	@Override
	protected Boolean getResult() {
		// TODO Auto-generated method stub
		return result;
	}

}

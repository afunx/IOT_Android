package com.espressif.iot.model.internet;

import android.util.Log;

import com.espressif.iot.model.device.IOTAction;
import com.espressif.iot.model.device.IOTDevice;

public class IOTActionSwitchOnInternet extends IOTAction<Boolean> {

	private static final String TAG = "IOTActionSwitchOnInternet";
	
	public IOTActionSwitchOnInternet(IOTDevice iotDevice) {
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
		return false;
	}

	@Override
	protected Boolean getResult() {
		// TODO Auto-generated method stub
		return null;
	}

}

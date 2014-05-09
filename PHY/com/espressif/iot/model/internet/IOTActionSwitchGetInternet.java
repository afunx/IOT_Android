package com.espressif.iot.model.internet;


import com.espressif.iot.model.device.IOTAction;
import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.util.Logger;

public class IOTActionSwitchGetInternet extends IOTAction<Boolean> {

	private static final String TAG = "IOTActionSwitchGetInternet";
	
	public IOTActionSwitchGetInternet(IOTDevice iotDevice) {
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
		String token = mIOTDevice.getDeviceKey();
		return result = IOTDeviceHelper.plugSwitch(false, token, false);
	}

	@Override
	protected Boolean getResult() {
		// TODO Auto-generated method stub
		return result;
	}

}

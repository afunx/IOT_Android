package com.espressif.iot.model.internet;


import com.espressif.iot.model.device.IOTAction;
import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.util.Logger;

public class IOTActionTemHumGetInternet extends IOTAction<Boolean>{

	private static final String TAG = "IOTActionTemHumGetInternet";
	
	public IOTActionTemHumGetInternet(IOTDevice iotDevice) {
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
		return result = IOTDeviceHelper.getTemHumData(token,null);
	}

	@Override
	protected Boolean getResult() {
		// TODO Auto-generated method stub
		return result;
	}

}

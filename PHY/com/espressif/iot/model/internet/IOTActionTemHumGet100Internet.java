package com.espressif.iot.model.internet;

import java.util.List;

import android.util.Log;

import com.espressif.iot.model.device.IOTAction;
import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.util.Logger;

public class IOTActionTemHumGet100Internet extends IOTAction<List<TemHumData>>{

	private static final String TAG = "IOTActionGetTemHum";
	
	public IOTActionTemHumGet100Internet(IOTDevice iotDevice) {
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
		result = IOTDeviceHelper.getTemHumDataList(token);
		return !result.isEmpty();
	}

	@Override
	protected List<TemHumData> getResult() {
		// TODO Auto-generated method stub
		return result;
	}

}

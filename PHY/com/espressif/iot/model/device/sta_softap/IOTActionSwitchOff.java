package com.espressif.iot.model.device.sta_softap;

import com.espressif.iot.model.device.IOTAction;
import com.espressif.iot.model.device.IOTActionEnum;
import com.espressif.iot.model.device.IOTDevice;

public class IOTActionSwitchOff extends IOTAction{

	public IOTActionSwitchOff(IOTDevice iotDevice) {
		super(iotDevice);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void actionFailed() {
		// TODO Auto-generated method stub
		IOTCommonStatus commonStatus = mIOTDevice.getIOTCommonStatus();
		commonStatus.setStatus(1);
	}

	@Override
	protected boolean action() {
		// TODO Auto-generated method stub
		
		IOTCommonStatus commonStatus = mIOTDevice.getIOTCommonStatus();
		commonStatus.setStatus(0);
		
		boolean success = intermediator.executeIOTAction(mIOTDevice, IOTActionEnum.IOT_ACTION_SWITCH_OFF);
		
		return success;
	}	
}

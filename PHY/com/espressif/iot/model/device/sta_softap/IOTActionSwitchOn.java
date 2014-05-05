package com.espressif.iot.model.device.sta_softap;

import com.espressif.iot.model.device.IOTAction;
import com.espressif.iot.model.device.IOTActionEnum;
import com.espressif.iot.model.device.IOTDevice;

public class IOTActionSwitchOn extends IOTAction{

	public IOTActionSwitchOn(IOTDevice iotDevice) {
		super(iotDevice);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void actionFailed() {
		// TODO Auto-generated method stub
		IOTCommonStatus commonStatus = mIOTDevice.getIOTCommonStatus();
		commonStatus.setStatus(0);
	}

	@Override
	protected boolean action() {
		// TODO Auto-generated method stub
		
		IOTCommonStatus commonStatus = mIOTDevice.getIOTCommonStatus();
		commonStatus.setStatus(1);
		
		boolean success = intermediator.executeIOTAction(mIOTDevice, IOTActionEnum.IOT_ACTION_SWITCH_ON);
		
		return success;
	}	
}

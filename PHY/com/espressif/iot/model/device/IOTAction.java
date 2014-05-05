package com.espressif.iot.model.device;

import com.espressif.iot.mediator.IntermediatorIOT;

public abstract class IOTAction {

	protected static IntermediatorIOT intermediator = IntermediatorIOT.getInstance();
	
	// when action failed, what to do to restore the IOT device's status
	protected abstract void actionFailed();

	// the action to be performed
	protected abstract boolean action();

	// which IOT device the IOTAction belongs to
	protected IOTDevice mIOTDevice;
	
	public IOTAction(IOTDevice iotDevice){
		this.mIOTDevice = iotDevice;
	}
	
	/**
	 * the framework for IOTAction
	 * @return		whether the action executed successed or not
	 */
	public boolean executeAction() {
		if(action()){
			return true;
		}
		else{
			actionFailed();
			return false;
		}
	}
}

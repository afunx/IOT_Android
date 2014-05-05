package com.espressif.iot.ui.esp;

public abstract class EspUIModelAbs implements EspUIModel {
	
	void receiveUpdateRequest(EspUIControlAbs espUIControl, EspMessage request){
		EspMessage response = processRequest(request);
		if(response!=null)
			sendUpdateResponse(espUIControl, response);
	}
	
	private void sendUpdateResponse(EspUIControlAbs espUIControl,EspMessage response){
		espUIControl.receiveUpdateResponse(response);
	}
	
	/**
	 * send the response to the EspUIControl
	 * @param request	the request from the EspUIControl
	 * @return			the response(if the response=null, it won't be sent)
	 */
	protected abstract EspMessage processRequest(EspMessage request);
	
}

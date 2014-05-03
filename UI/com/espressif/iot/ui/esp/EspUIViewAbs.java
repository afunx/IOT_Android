package com.espressif.iot.ui.esp;

public abstract class EspUIViewAbs implements EspUIView{

	private EspUIControlAbs mEspUIControl;
	
	// inject the EspUIControl
	public void setEspUIControl(EspUIControlAbs espUIControl){
		this.mEspUIControl = espUIControl;
	}
//	public EspUIControlAbs getEspUIControl(){
//		return mEspUIControl;
//	}
	
	void receiveActionResponse(EspMessage response){
		procResponse(response);
	}
	
	public void sendActionRequest(EspMessage request){
		mEspUIControl.receiveActionRequest(request , this);
	}
	protected abstract void procResponse(EspMessage response);
	
}

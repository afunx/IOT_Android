package com.espressif.iot.ui.esp;

public abstract class EspUIControlAbs implements EspUIControl{
	
	private EspUIModelAbs mEspUIModel;

	// inject the EspUIModel
	public void setEspUIModel(EspUIModelAbs espUIModel) {
		this.mEspUIModel = espUIModel;
	}
//	public EspUIModelAbs getEspUIModel() {
//		return mEspUIModel;
//	}
	
	private EspUIViewAbs espUIView;
	
	void receiveActionRequest(EspMessage request, EspUIViewAbs espUIView){
		sendUpdateRequest(mEspUIModel, request);
		this.espUIView = espUIView;
	}
	
	private void sendUpdateRequest(EspUIModelAbs espUIModel, EspMessage request){
		mEspUIModel.receiveUpdateRequest(this, request);
	}
	
	void receiveUpdateResponse(EspMessage response){
		sendActionResponse(response);
	}
	
	private void sendActionResponse(EspMessage response){
		espUIView.receiveActionResponse(response);
	}
	
}

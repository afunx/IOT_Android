package com.espressif.iot.model.device.softap;

public class IOTSoftAP {
	private String mAuthmode;
	private int mChannel;
	private String mSSID;
	private String mPassword;
	
	private String mIP;
	private String mMask;
	private String mGateWay;
	
	public String getIP(){
		return this.mIP;
	}
	public void setIP(String IP){
		this.mIP = IP;
	}
	
	public String getMask(){
		return this.mMask;
	}
	public void setMask(String mask){
		this.mMask = mask;
	}
	
	public String getGateWay(){
		return this.mGateWay;
	}
	public void setGateWay(String gateWay){
		this.mGateWay = gateWay;
	}
	
	public String getAuthmode(){
		return this.mAuthmode;
	}
	public void setAuthmode(String authmode){
		this.mAuthmode = authmode;
	}
	
	public int getChannel(){
		return this.mChannel;
	}
	public void setChannel(int channel){
		this.mChannel = channel;
	}
	
	public String getSSID(){
		return this.mSSID;
	}
	public void setSSID(String SSID){
		this.mSSID = SSID;
	}
	
	public String getPassword(){
		return this.mPassword;
	}
	public void setPassword(String password){
		this.mPassword = password;
	}
}

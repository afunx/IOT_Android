package com.espressif.iot.model.device.sta;

public class IOTSta {
	private String mSSID;
	private String mPassword;
	private String mToken;
	
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
	
	public String getToken(){
		return this.mToken;
	}
	public void setToken(String token){
		this.mToken = token;
	}
}

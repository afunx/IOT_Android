package com.espressif.iot.model.device.sta_softap;

public class IOTCommonStatus {
	private int mVersionMajor;
	private int mVersionMinor;
	private String mManufacturer;
	// 1 means on while 0 means off
	private int mStatus;
	private String mSSID;
	private String mPassword;
	
	public int getVersionMajor(){
		return this.mVersionMajor;
	}
	public void setVersionMajor(int versionMajor){
		this.mVersionMajor = versionMajor;
	}
	
	public int getVersionMinor(){
		return this.mVersionMinor;
	}
	public void setVersionMinor(int versionMinor){
		this.mVersionMinor = versionMinor;
	}
	
	public String getManufacturer(){
		return this.mManufacturer;
	}
	public void setManufacturer(String manufacturer){
		this.mManufacturer = manufacturer;
	}
	
	public int getStatus(){
		return this.mStatus;
	}
	public void setStatus(int status){
		this.mStatus = status;
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

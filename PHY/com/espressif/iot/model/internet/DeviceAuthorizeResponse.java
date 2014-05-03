package com.espressif.iot.model.internet;

public class DeviceAuthorizeResponse {
	private long mDeviceId;
	private long mProductId;
	private boolean mIsOwner;
	private String mToken;
	
	public long getDeviceId(){
		return mDeviceId;
	}
	public void setDeviceId(long deviceId){
		this.mDeviceId = deviceId;
	}
	
	public long getProductId(){
		return mProductId;
	}
	public void setProductId(long productId){
		this.mProductId = productId;
	}
	
	public boolean getIsOwner(){
		return mIsOwner;
	}
	public void setIsOwner(boolean isOwner){
		this.mIsOwner = isOwner;
	}
	
	public String getToekn(){
		return mToken;
	}
	public void setToken(String token){
		this.mToken = token;
	}
	
}

package com.espressif.iot.db.device.model;


/**
 * token and isOwner is stored here
 * @author afunx
 *
 */
public class TokenIsOwner {
	private boolean mIsOwner;
	private String mToken;
	public void setIsOwner(boolean isOwner){
		this.mIsOwner = isOwner;
	}
	public boolean getIsOwner(){
		return this.mIsOwner;
	}
	public void setToken(String token){
		this.mToken = token;
	}
	public String getToken(){
		return this.mToken;
	}
}

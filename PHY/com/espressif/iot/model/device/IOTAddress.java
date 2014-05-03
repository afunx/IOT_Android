package com.espressif.iot.model.device;

import java.net.InetAddress;

import com.espressif.iot.model.device.IOTDevice.TYPE;

// it is used to process the message got from the IOT Device
// "I'm Plug.98:fe:34:77:ce:00 192.168.4.1"
public class IOTAddress {
	private String mBSSID;
	// IP address
	private InetAddress mInetAddress;
	
	/**
	 * add by afunx IOT_0.3.7_beta
	 * to differ the type of the device
	 */
	private TYPE mType;
	public TYPE getType(){
		return mType;
	}
	public void setType(TYPE type){
		this.mType = type;
	}
	
	public IOTAddress(String BSSID,InetAddress inetAddress){
		this.mBSSID = BSSID;
		this.mInetAddress = inetAddress;
	}
	
	public void setBSSID(String BSSID){
		this.mBSSID = BSSID;
	}
	public String getBSSID(){
		return mBSSID;
	}
	
	public void setInetAddr(InetAddress inetAddress){
		this.mInetAddress = inetAddress;
	}
	public InetAddress getInetAddress(){
		return mInetAddress;
	}
	
	@Override
	public String toString(){
		return 	"IOTAddress:\n"+
				"  BSSID:" + mBSSID + ",InetAddress:" + mInetAddress;
	}
}

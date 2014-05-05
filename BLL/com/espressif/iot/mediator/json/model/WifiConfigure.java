package com.espressif.iot.mediator.json.model;

import com.espressif.iot.mediator.json.model.interfaces.REST_FUNCTION_WIFI;
import com.espressif.iot.mediator.json.model.interfaces.REST_MOTION_GET;
import com.espressif.iot.mediator.json.model.interfaces.REST_MOTION_POST;

public class WifiConfigure implements REST_FUNCTION_WIFI,REST_MOTION_GET,REST_MOTION_POST{
	Configure Configure = new Configure();
	
	public void setSSID(String SSID){
		Configure.Connect.ssid = SSID;
	}
	public void setPassword(String password){
		Configure.Connect.password = password;
	}
	
	public String getSSID(){
		return Configure.Connect.ssid;
	}
	public String getPassword(){
		return Configure.Connect.password;
	}
}


class Configure{
	Connect Connect = new Connect();
}

class Connect{
	String ssid;
	String password;
	
	public void setssid(String ssid){
		this.ssid = ssid;
	}
	public void setpassword(String password){
		this.password = password;
	}
}
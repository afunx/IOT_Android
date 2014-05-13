package com.espressif.iot.mediator.json.model.softap;

import com.espressif.iot.mediator.json.model.interfaces.REST_FUNCTION_WIFI_SOFTAP;
import com.espressif.iot.mediator.json.model.interfaces.REST_MOTION_POST;


public class WifiConfigureSoftAP implements REST_FUNCTION_WIFI_SOFTAP, REST_MOTION_POST{
	private Request Request = new Request();
	
	// set method must be defined here to make the caller could set the value
	public void setAuthmode(String authmode){
		Request.Softap.Connect_Softap.authmode = authmode;
	}
	public void setChannel(Integer channel){
		Request.Softap.Connect_Softap.channel = channel;
	}
	public void setSSID(String SSID){
		Request.Softap.Connect_Softap.ssid = SSID;
	}
	public void setPassword(String password){
		Request.Softap.Connect_Softap.password = password;
	}
}

class Request{
	Softap Softap = new Softap();
}

class Softap{
	Connect_Softap Connect_Softap = new Connect_Softap();
}

class Connect_Softap{
	String 	authmode;
	Integer channel;
	String ssid;
	String password;
}
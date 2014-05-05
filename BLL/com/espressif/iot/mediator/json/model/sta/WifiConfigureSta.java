package com.espressif.iot.mediator.json.model.sta;

import com.espressif.iot.mediator.json.model.interfaces.REST_FUNCTION_WIFI_STA;
import com.espressif.iot.mediator.json.model.interfaces.REST_MOTION_POST;


public class WifiConfigureSta implements REST_FUNCTION_WIFI_STA,REST_MOTION_POST{
	Request Request = new Request();
	public void setSSID(String SSID){
		Request.Station.Connect_Station.ssid = SSID;
	}
	public void setPassword(String password){
		Request.Station.Connect_Station.password = password;
	}
	public void setToken(String token){
		Request.Station.Connect_Station.token = token;
	}
}

class Request{
	Station Station = new Station();
}

class Station{
	Connect_Station Connect_Station = new Connect_Station();
}

class Connect_Station{
	String ssid;
	String password;
	String token;
}


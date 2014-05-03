package com.espressif.iot.mediator.json.model.stasoftap;

import com.espressif.iot.mediator.json.model.interfaces.REST_FUNCTION_WIFI_STA_SOFTAP;
import com.espressif.iot.mediator.json.model.interfaces.REST_MOTION_GET;


public class WifiConfigureStaSoftAP implements REST_FUNCTION_WIFI_STA_SOFTAP, REST_MOTION_GET{
	
	Response Response = new Response();
	
	public String getStaSSID(){ 
		return Response.Station.Connect_Station.ssid;
	}
	public String getStaPassword(){
		return Response.Station.Connect_Station.password;
	}
	public String getStaIP(){
		return Response.Station.Ipinfo_Station.ip;
	}
	public String getStaMask(){
		return Response.Station.Ipinfo_Station.mask;
	}
	public String getStaGetway(){
		return Response.Station.Ipinfo_Station.gw;
	}
	
	public String getSoftAPSSID(){
		return Response.Softap.Connect_Softap.ssid;
	}
	public String getSoftAPPassword(){
		return Response.Softap.Connect_Softap.password;
	}
	public Integer getSoftAPChannel(){
		return Response.Softap.Connect_Softap.channel;
	}
	public String getSoftAPAuthmode(){
		return Response.Softap.Connect_Softap.authmode;
	}
	public String getSoftAPIP(){
		return Response.Softap.Ipinfo_Softap.ip;
	}
	public String getSoftAPMask(){
		return Response.Softap.Ipinfo_Softap.mask;
	}
	public String getSoftAPGetway(){
		return Response.Softap.Ipinfo_Softap.gw;
	}
	
}


class Response{
	Station Station = new Station();
	Softap Softap = new Softap();
}

class Station{
	Connect_Station Connect_Station = new Connect_Station();
	Ipinfo_Station Ipinfo_Station = new Ipinfo_Station();
}

class Connect_Station{
	String ssid;
	String password;
	
	void setssid(String ssid){
		this.ssid = ssid;
	}
	void setpassword(String password){
		this.password = password;
	}
}

class Ipinfo_Station{
	String ip;
	String mask;
	String gw;
	
	void setip(String ip){
		this.ip = ip;
	}
	void setmask(String mask){
		this.mask = mask;
	}
	void setgw(String gw){
		this.gw = gw;
	}
}


class Softap{
	Connect_Softap Connect_Softap = new Connect_Softap();
	Ipinfo_Softap Ipinfo_Softap = new Ipinfo_Softap();
}

class Connect_Softap{
	String ssid;
	String password;
	Integer channel;
	String authmode;
	
	void setssid(String ssid){
		this.ssid = ssid;
	}
	void setpassword(String password){
		this.password = password;
	}
	void setchannel(Integer channel){
		this.channel = channel;
	}
	void setauthmode(String authmode){
		this.authmode = authmode;
	}
	
}

class Ipinfo_Softap{
	String ip;
	String mask;
	String gw;
	
	void setip(String ip){
		this.ip = ip;
	}
	void setmask(String mask){
		this.mask = mask;
	}
	void setgw(String gw){
		this.gw = gw;
	}
}

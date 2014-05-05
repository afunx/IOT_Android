package com.espressif.iot.mediator.json.model;

import com.espressif.iot.mediator.json.model.interfaces.REST_FUNCTION_INFO;
import com.espressif.iot.mediator.json.model.interfaces.REST_MOTION_GET;

public class Info implements REST_FUNCTION_INFO, REST_MOTION_GET{
	private version version = new version();
	private device device = new device();
	
	// get method must be defined here to make the caller could get the value
	public Integer getVersionMajor(){
		return version.major;
	}
	public Integer getVersionMinor(){
		return version.minor;
	}
	public String getManufacturer(){
		return device.manufacturer;
	}
}

class version{
	// primitive type
	Integer major ;
	Integer minor ;
	// primitive type must has set method named "set"+instance's name
	// e.g. major : setmajor(!NOTE: "setmajor" instead of "setMajor")
	void setmajor(Integer major){
		this.major = major;
	}
	void setminor(Integer minor){
		this.minor = minor;
	}
}

class device{
	String manufacturer;
	void setmanufacturer(String manufacturer){
		this.manufacturer = manufacturer;
	}
}
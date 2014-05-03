package com.espressif.iot.mediator.json.model;

import com.espressif.iot.mediator.json.model.interfaces.REST_FUNCTION_SWITCH;
import com.espressif.iot.mediator.json.model.interfaces.REST_MOTION_GET;
import com.espressif.iot.mediator.json.model.interfaces.REST_MOTION_POST;

public class IOTSwitch implements REST_FUNCTION_SWITCH,REST_MOTION_GET,REST_MOTION_POST{
	private Response Response = new Response();
	public Integer getStatus(){
		return Response.status;
	}
	public void setStatus(int status){
		Response.status = status;
	}
}

class Response{
	Integer status;
	void setstatus(Integer status){
		this.status = status;
	}
}

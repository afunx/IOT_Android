package com.espressif.iot.model.internet;

/**
 * the Response is used only for logining and registering at the moment,
 * it will be used other ways later
 * 
 * @author afunx
 *
 */
public class LoginResponse {
	private String message;
	private int status;
	
	public LoginResponse(){
		// the default state of the response is fail
		message = "请打开wifi或3g确保能连上Internet";
		status = -1;
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	public String getMessage(){
		return this.message;
	}
	
	public void setStatus(int status){
		this.status = status;
	}
	public int getStatus(){
		return this.status;
	}
}

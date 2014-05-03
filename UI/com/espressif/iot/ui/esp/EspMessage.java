package com.espressif.iot.ui.esp;


public class EspMessage {
	
	public int what;
	public int arg1;
	public int arg2;
	private String msg;
//	private T mObject;
//	
//	public void setObject(T t){
//		this.mObject = t;
//	}
//	public T getObject(T t){
//		return mObject;
//	}
	
	public void setMessage(String msg){
		this.msg = msg;
	}
	public String getMessage(){
		return msg;
	}
	
	public EspMessage(int what){
		this.what = what;
	}
	public EspMessage(int what, int arg1){
		this.what = what;
		this.arg1 = arg1;
	}
	public EspMessage(int what, int arg1, int arg2){
		this.what = what;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}
	
}

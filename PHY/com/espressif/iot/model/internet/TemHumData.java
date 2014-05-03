package com.espressif.iot.model.internet;

/**
 * temperature and humidity data
 * @author afunx
 *
 */
public class TemHumData {
//	private String datapoint;
//	private String created;
//	private String visibly;
//	private String datastream_id;
	private String at;
	// humidity
	private int y;
	// temperature
	private int x;
//	private String id;
	
	private String message;
	
	public void setMessage(String message){
		this.message = message;
	}
	public String getMessage(){
		return this.message;
	}
	
	public void setAt(String at){
		this.at = at;
	}
	public String getAt(){
		return this.at;
	}
	
	public void setY(int y){
		this.y = y;
	}
	public int getY(){
		return this.y;
	}
	
	public void setX(int x){
		this.x = x;
	}
	public int getX(){
		return this.x;
	}
}

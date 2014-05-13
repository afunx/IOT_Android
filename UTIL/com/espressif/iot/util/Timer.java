package com.espressif.iot.util;

public class Timer {
	
	private static final String TAG = "Timer";
	
	private long startTime;
	private long stopTime;
	
	/**
	 * the min time the task should consume, if the task spend less time, it
	 * will sleep some time to make up min time
	 */
	private long minMilliSeconds;
	
	public void setMinTime(long minMilliSeconds){
		this.minMilliSeconds = minMilliSeconds;
	}
	public void start(){
		this.startTime = System.currentTimeMillis();
	}
	public void stop(){
		this.stopTime = System.currentTimeMillis();
		long sleepTime = minMilliSeconds-(stopTime-startTime);
		Logger.t(TAG, "sleepTime is " + sleepTime + " ms");
		if(sleepTime>0){
			Util.Sleep(sleepTime);
		}
	}
	public void spend(){
		this.stopTime = System.currentTimeMillis();
		Logger.t(TAG, "=====*****spend " + ((stopTime-startTime)/1000) + " s*****=====");
	}
}

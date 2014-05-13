package com.espressif.iot.thread;

public abstract class AbsTaskAsyn implements Runnable{
	
	protected final String taskName;
	
	public AbsTaskAsyn(String taskName){
		this.taskName = new String(taskName);
	}
	
	public String getTaskName(){
		return taskName;
	}
}

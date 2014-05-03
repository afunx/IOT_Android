package com.espressif.iot.thread;

import java.util.concurrent.ExecutorService;

interface CallBack{
	// After the AsynTask finished, it will be called
	public void callback();
}

public abstract class AbsTaskAsynCallBack implements Runnable,CallBack{
	
	protected final String taskName;
	
	private static ExecutorService exec = FixedThreadPool.getInstance().getExecutorService();
	
	public AbsTaskAsynCallBack(String taskName){
		this.taskName = taskName+" AsynCallBack";
	}
	
	public void executeAsynCallback(){
		exec.execute(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				AbsTaskAsynCallBack.this.run();
				AbsTaskAsynCallBack.this.callback();
			}
		});
	}
	
}

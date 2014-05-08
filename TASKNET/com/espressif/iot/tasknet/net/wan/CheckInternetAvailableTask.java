package com.espressif.iot.tasknet.net.wan;

import android.util.Log;

import com.espressif.iot.net.wan.NetChecker;
import com.espressif.iot.thread.AbsTaskSyn;
import com.espressif.iot.util.Logger;

/**
 * 
 * this task is used to check whether the Internet is available
 * 
 * @author afunx
 *
 */
public class CheckInternetAvailableTask extends AbsTaskSyn<Boolean>{

	private NetChecker netChecker = NetChecker.getInstance();

	private static final String TAG	= "CheckInternetAvailableTask";
	
	private boolean mIsInternetAvailable;
	private String mPingAddress;
	
	private Process process[] = new Process[1];
	
	public CheckInternetAvailableTask(String taskName, String pingAddress){
		this(taskName);
		this.mPingAddress = pingAddress;
	}
	
	protected CheckInternetAvailableTask(String taskName) {
		super(taskName);
		// TODO Auto-generated constructor stub
	}

	private boolean isInternetAvailable(){
		return netChecker.isInternetAvailable(mPingAddress,process);
	}
	
	@Override
	public Boolean call() {
		// TODO Auto-generated method stub
		mIsInternetAvailable = false;
		mIsInternetAvailable = isInternetAvailable();
		return mIsInternetAvailable;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return taskName;
	}

	@Override
	protected void doAfterFailed() {
		// TODO Auto-generated method stub
		process[0].destroy();
		Logger.d(TAG, "fail");
	}

	@Override
	public Boolean getResult() {
		// TODO Auto-generated method stub
		return mIsInternetAvailable;
	}

}

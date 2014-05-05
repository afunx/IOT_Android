package com.espressif.iot.tasknet.rest;


import org.json.JSONObject;

import android.util.Log;

import com.espressif.iot.net.rest.RestGet;
import com.espressif.iot.thread.AbsTaskSyn;

public class RestGetJsonTask extends AbsTaskSyn<JSONObject>{

	private static final String TAG = "RestGetTask";
	
	private String mUriString;
	private String mHeaderKey; 
	private String mHeaderValue;
	private JSONObject mJsonResult;
	
	protected RestGetJsonTask(String taskName) {
		super(taskName);
		// TODO Auto-generated constructor stub
	}
	
	public RestGetJsonTask(String taskName, String uriString){
		this(taskName);
		this.mUriString = uriString;
		this.mHeaderKey = null;
		this.mHeaderValue = null;
	}
	
	public RestGetJsonTask(String taskName, String uriString, String headerKey, String headerValue){
		this(taskName);
		this.mUriString = uriString;
		this.mHeaderKey = headerKey;
		this.mHeaderValue = headerValue;
	}

	@Override
	public Boolean call() {
		// TODO Auto-generated method stub
		mJsonResult = null;
		if(mHeaderKey==null||mHeaderValue==null)
			mJsonResult = RestGet.getInstance().restGetJson(mUriString);
		else
			mJsonResult = RestGet.getInstance().restGetJson(mUriString, mHeaderKey, mHeaderValue);
		return true;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return taskName;
	}

	@Override
	protected void doAfterFailed() {
		// TODO Auto-generated method stub
		Log.d(TAG, "fail");
	}

	@Override
	public JSONObject getResult() {
		// TODO Auto-generated method stub
		return mJsonResult;
	}

}

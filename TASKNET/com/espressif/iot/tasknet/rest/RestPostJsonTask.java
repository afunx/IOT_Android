package com.espressif.iot.tasknet.rest;

import org.json.JSONObject;

import android.util.Log;

import com.espressif.iot.net.rest.RestPost;
import com.espressif.iot.thread.AbsTaskSyn;

public class RestPostJsonTask extends AbsTaskSyn<JSONObject>{

	private static final String TAG = "RestPostTask";
	private JSONObject mResult;
	private String mUriString;
	private JSONObject mJsonObject;
	private String mHeaderKey;
	private String mHeaderValue;
	
	protected RestPostJsonTask(String taskName) {
		super(taskName);
		// TODO Auto-generated constructor stub
	}
	
	public RestPostJsonTask(String taskName, String uriString,
			JSONObject jsonObject, String headerKey, String headerValue) {
		this(taskName);
		this.mUriString = uriString;
		this.mJsonObject = jsonObject;
		this.mHeaderKey = headerKey;
		this.mHeaderValue = headerValue;
	}

	@Override
	public Boolean call() {
		// TODO Auto-generated method stub
//		mResult = false;
		if(mHeaderKey==null||mHeaderValue==null)
			mResult = RestPost.getInstance().restPost(mUriString, mJsonObject);
		else
			mResult = RestPost.getInstance().restPost(mUriString, mJsonObject, mHeaderKey, mHeaderValue);
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
		return mResult;
	}
	
}


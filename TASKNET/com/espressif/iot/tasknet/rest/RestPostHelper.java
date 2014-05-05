package com.espressif.iot.tasknet.rest;

import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.espressif.iot.constants.CONSTANTS;
import com.espressif.iot.thread.AbsTaskSyn;
import com.espressif.iot.thread.FixedThreadPool;

public class RestPostHelper {
	
	private static RestPostHelper instance = new RestPostHelper();
	
	private static FixedThreadPool threadPool = FixedThreadPool.getInstance();
	
	// Singleton Pattern
	private RestPostHelper(){
	}
	public static RestPostHelper getInstance(){
		return instance;
	}
	
	public JSONObject restPostJSONSyn(String uriString, JSONObject jsonObject){
		AbsTaskSyn<JSONObject> restPostJSONTask = new RestPostJsonTask(
				"rest post json task", uriString, jsonObject, null, null);
		threadPool.executeSyn(restPostJSONTask,
				CONSTANTS.REST_POST_JSON_TIMEOUT_MILLISECONDS,
				TimeUnit.MILLISECONDS);
		return restPostJSONTask.getResult();
	}
	
	public JSONObject restPostJSONSyn(String uriString, JSONObject jsonObject, String headerKey, String headerValue) {
		AbsTaskSyn<JSONObject> restPostJSONTask = new RestPostJsonTask(
				"rest post json task", uriString, jsonObject, headerKey, headerValue);
		threadPool.executeSyn(restPostJSONTask,
				CONSTANTS.REST_POST_JSON_TIMEOUT_MILLISECONDS,
				TimeUnit.MILLISECONDS);
		return restPostJSONTask.getResult();
	}
	
}

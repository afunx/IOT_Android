package com.espressif.iot.net.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.espressif.iot.util.Logger;

import android.util.Log;

/*
 * should it use timeout mechanism?
 * it is used to send HttpGet and extract the json object
 */
public class RestGet {
	
	private static final String TAG = "RestGet";
	private static RestGet instance = new RestGet();
	
	// Singleton pattern
	private RestGet(){
	}
	public static RestGet getInstance(){
		return instance;
	}

	
	
	/**
	 * NOTE: Must deal with return null
	 * Get the Json by HttpGet
	 * 
	 * (It is hard code here, remains to be optimized in the future)
	 * 
	 * @param uriString		String of URI
	 * @param headerKey		the key of header
	 * @param headerValue	the value of header
	 * @return				the Json object get by the response,
	 * 						it will be null if no response
	 */
	public JSONObject restGetJson(String uriString, String headerKey, String headerValue){
		try {
			URI uri = new URI(uriString);
			Logger.d(TAG, "restGetJson entrance");
			Logger.d(TAG, "uri:"+uri);
			Logger.d(TAG, "headerKey:"+headerKey+";headerValue:"+headerValue);
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet  = new HttpGet();
			HttpResponse response;
			HttpEntity httpEntity;
			BufferedReader bufferedReader;
			StringBuilder builder = new StringBuilder();
			InputStream inputStream;
			// set uri
			httpGet.setURI(uri);
			// add header
			httpGet.addHeader(headerKey, headerValue);
//			httpGet.addHeader(headerKey, headerValue);
			// send Get request and get the response
			response = httpClient.execute(httpGet);
			// process the response
			httpEntity = response.getEntity();
			
			if(httpEntity!=null){
				builder = new StringBuilder();
				inputStream = httpEntity.getContent();
	            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	            for (String line = null; (line = bufferedReader.readLine()) != null;) {
	                builder.append(line).append("\n");
	            }
	            // get the Json object
	            JSONObject jsonObject = new JSONObject(builder.toString());
	            
	            Logger.d(TAG, "jsonObject:" + jsonObject);
	            
	            Logger.d(TAG, "restGetJson exit normally");
	            return jsonObject;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.d(TAG, "restGetJson exit abnormally with null return");
		return null;
	}
	
	/**
	 * NOTE: Must deal with return null
	 * Get the Json by HttpGet
	 * @param uriString		String of URI
	 * @return				the Json object get by the response,
	 * 						it will be null if no response
	 */
	public JSONObject restGetJson(String uriString){
		try {
			URI uri = new URI(uriString);
			Logger.d(TAG, "restGetJson entrance");
			Logger.d(TAG, "uri:"+uri);
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet  = new HttpGet();
			HttpResponse response;
			HttpEntity httpEntity;
			BufferedReader bufferedReader;
			StringBuilder builder = new StringBuilder();
			InputStream inputStream;
			// set uri
			httpGet.setURI(uri);
			
			// send Get request and get the response
			response = httpClient.execute(httpGet);
			
			// process the response
			httpEntity = response.getEntity();
			
			if(httpEntity!=null){
				builder = new StringBuilder();
				inputStream = httpEntity.getContent();
	            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	            for (String line = null; (line = bufferedReader.readLine()) != null;) {
	                builder.append(line).append("\n");
	            }
	            // get the Json object
	            JSONObject jsonObject = new JSONObject(builder.toString());
	            
	            Logger.d(TAG, "jsonObject:" + jsonObject);
	            
	            Logger.d(TAG, "restGetJson exit normally");
	            return jsonObject;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.d(TAG, "restGetJson exit abnormally with null return");
		return null;
	}
}

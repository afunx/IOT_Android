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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.espressif.iot.util.Logger;

import android.util.Log;

/*
 * should it use timeout mechanisem?
 * it is used to send HttpPost according to the json object
 */
public class RestPost {

	private static final String TAG = "RestPost";

	// private static final int RESPONSE_STATUS_OK = 200;

	private static RestPost instance = new RestPost();

	// Singleton Pattern
	private RestPost() {
	}

	public static RestPost getInstance() {
		// singleton or not?
		return instance;
	}

	/**
	 * NOTE: Must deal with return null adapter pattern
	 * 
	 * @param uriString
	 *            uri String
	 * @return restGetJson(URI uri)
	 */
	public JSONObject restPost(String uriString, JSONObject jsonObject,
			String headerKey, String headerValue) {
		try {
			URI uri = new URI(uriString);
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost();
			HttpResponse response;
			HttpEntity httpEntity;
			BufferedReader bufferedReader;
			StringBuilder builder = new StringBuilder();
			InputStream inputStream;

			Logger.d(TAG, "restPostJson entrance");
			Logger.d(TAG, "uri:" + uri);

			// set uri
			httpPost.setURI(uri);
			// add header
			httpPost.addHeader(headerKey, headerValue);
			Logger.d(TAG, "headerKey:"+headerKey+",headerValue:"+headerValue);
			// System.out.println(jsonObject);
			Logger.d(TAG, "jsonObject:" + jsonObject);
			// inject the json object(Only POST support, GET don't support)
			if(jsonObject!=null){
				StringEntity se = new StringEntity(jsonObject.toString());
				httpPost.setEntity(se);
			}

			// send Get request and get the response
			response = httpClient.execute(httpPost);

			// process the response
			httpEntity = response.getEntity();

			StatusLine statusLine = response.getStatusLine();

			// !!!!!!!!!!!!!!!!it is need to check the response!!!!!!

			// if(httpEntity!=null&&statusLine.getStatusCode()==RESPONSE_STATUS_OK){

			// 2xx is good status
			boolean isOK = (statusLine.getStatusCode() / 100 == 2);
//			if (httpEntity != null && isOK) {
			if (httpEntity != null){
				builder = new StringBuilder();
				inputStream = httpEntity.getContent();
				bufferedReader = new BufferedReader(new InputStreamReader(
						inputStream));
				for (String line = null; (line = bufferedReader.readLine()) != null;) {
					builder.append(line).append("\n");
				}
				// get the Json object
				JSONObject jsonObjectResult = new JSONObject(builder.toString());

				Logger.d(TAG, "jsonObjectResult:" + jsonObjectResult);

				Logger.d(TAG, "restPostJson exit normally");
				return jsonObjectResult;
			} else {
				Logger.e(TAG,
						"the fail reason is: " + statusLine.getReasonPhrase());
				return null;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.d(TAG, "restPostJson exit abnormally with null return");
		return null;
	}

	/**
	 * NOTE: Must deal with return null adapter pattern
	 * 
	 * @param uriString
	 *            uri String
	 * @return restGetJson(URI uri)
	 */
	public JSONObject restPost(String uriString, JSONObject jsonObject) {
		try {
			URI uri = new URI(uriString);
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost();
			HttpResponse response;
			HttpEntity httpEntity;
			BufferedReader bufferedReader;
			StringBuilder builder = new StringBuilder();
			InputStream inputStream;

			Logger.d(TAG, "restPostJson entrance");
			Logger.d(TAG, "uri:" + uri);

			// set uri
			httpPost.setURI(uri);

			// System.out.println(jsonObject);
			Logger.d(TAG, "jsonObject:" + jsonObject);

			// inject the json object(Only POST support, GET don't support)
			if(jsonObject!=null){
				StringEntity se = new StringEntity(jsonObject.toString());
				httpPost.setEntity(se);
			}

			// send Get request and get the response
			response = httpClient.execute(httpPost);

			// process the response
			httpEntity = response.getEntity();

			StatusLine statusLine = response.getStatusLine();

			// !!!!!!!!!!!!!!!!it is need to check the response!!!!!!

			// if(httpEntity!=null&&statusLine.getStatusCode()==RESPONSE_STATUS_OK){
			// 2xx is good status
			boolean isOK = (statusLine.getStatusCode() / 100 == 2);
//			if (httpEntity != null && isOK) {
			if(httpEntity != null ) {
				builder = new StringBuilder();
				inputStream = httpEntity.getContent();
				bufferedReader = new BufferedReader(new InputStreamReader(
						inputStream));
				for (String line = null; (line = bufferedReader.readLine()) != null;) {
					builder.append(line).append("\n");
				}
				JSONObject jsonObjectResult = null;
				if(builder.length()>0)
					jsonObjectResult = new JSONObject(builder.toString());

				Logger.d(TAG, "jsonObjectResult:" + jsonObjectResult);

				Logger.d(TAG, "restPostJson exit normally");
				return jsonObjectResult;
			} else {
				Logger.e(TAG,
						"the fail reason is: " + statusLine.getReasonPhrase());
				return null;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.d(TAG, "restPostJson exit abnormally with null return");
		return null;
	}
	
	
}

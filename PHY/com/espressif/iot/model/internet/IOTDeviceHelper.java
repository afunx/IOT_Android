package com.espressif.iot.model.internet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.model.device.IOTDevice.TYPE;
import com.espressif.iot.tasknet.rest.RestGetHelper;
import com.espressif.iot.tasknet.rest.RestPostHelper;


/**
 * help iotDevice to do something related with Internet
 * 
 * in the moment, we assemble all the thing related to server here,
 * i don't think it is a good solution. it need refactoring later
 * 
 * @author afunx
 *
 */
public class IOTDeviceHelper{
	
	private static final String TAG = "IOTDeviceHelper";
	private static RestPostHelper restPostHelper = RestPostHelper.getInstance();
	private static RestGetHelper restGetHelper = RestGetHelper.getInstance();
	private static final String Authorization = "Authorization";
	private static final String Encrypt_method = "encrypt_method";
	private static final String Plain = "PLAIN";
	private static final String Username = "username";
	private static final String Password = "password";
	private static final String Token = "token";
	private static final String Key = "key";
	private static final String Datapoint = "datapoint";
	private static final String UrlAuthorize = "http://114.215.177.97/v1/key/authorize/";
	private static final String UrlGetUserKey = "http://114.215.177.97/v1/keys";
	private static final String UrlJoin = "http://114.215.177.97/v1/user/join/";
	private static final String UrlValidate = "http://114.215.177.97/v1/user/join/validate/";
	private static final String UrlPlugStatus = "http://114.215.177.97/v1/datastreams/plug-status/datapoint/";
	private static final String UrlTemHumDataPoint = "http://114.215.177.97/v1/datastreams/tem_hum/datapoint/";
	private static final String UrlTemHumDataPoints = "http://114.215.177.97/v1/datastreams/tem_hum/datapoints/";
	
	private static boolean isStatusOK(int status){
		if(status/100==2)
			return true;
		else
			return false;
	}
	
	public static String Test = "test";
	
	/**
	 * !NOTE: if fail, it will return null
	 * 
	 * POST
	 * url:http://114.215.177.97/v1/key/authorize/
	 * header:Authorization : token 114d3e35c2a3c0fa5eef339b2ac70dad77313ba9
	 * 
	 * authorize the device's owner to the user
	 * @param userToken		the user's userToken
	 * @param temptToken	the tempt token
	 * @return				the device's key, owner key or guest key
	 */
	public static String authorize(IOTDevice iotDevice, String userToken, String temptToken){
		String headerKey = Authorization;
		String headerValue = "token " + userToken;
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Token, temptToken);
			JSONObject result = restPostHelper.restPostJSONSyn(UrlAuthorize, jsonObject, headerKey, headerValue);
			if(result==null){
				Log.w(TAG, "authorize() fail");
				return null;
			}
			int status = Integer.parseInt(result.getString("status"));
			if(isStatusOK(status)){
				JSONObject key = result.getJSONObject(Key);
				String token = key.getString(Token);
				boolean is_owner_key = false;
				if(Integer.parseInt(key.getString("is_owner_key"))==1)
					is_owner_key = true;
				Log.d(TAG, "is_owner_key:" + is_owner_key);
				iotDevice.setIsOwner(is_owner_key);
				long id = Long.parseLong(key.getString("id"));
				iotDevice.setDeviceId(id);
//				iotDevice.setType(TYPE.PLUG);
				Log.d(TAG, "deviceId:" + id);
				Log.d(TAG, "authorized() suc");
				return token;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.w(TAG, "authorize() fail");
		return null;
	}
	
	/**
	 * !NOTE: if fail, it will return null
	 * 
	 * get the user key
	 * @param username		the username
	 * @param password		the password
	 * @return				the response from the server
	 */
	public static LoginResponse getUserKey(String username,String password){
		LoginResponse result = new LoginResponse();
		JSONObject jsonObject = new JSONObject();
		JSONObject temp = new JSONObject();
		try {
			jsonObject = new JSONObject();
			jsonObject.put(Username, username);
			jsonObject.put(Password, password);
			temp.put(Authorization, jsonObject);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject jsonObjectResult = restPostHelper.restPostJSONSyn(UrlGetUserKey, temp);
		if(jsonObjectResult!=null){
			try {
//				JSONObject key = (JSONObject) jsonObjectResult.getJSONArray("keys").get(0);
//				String userKey = key.getString(Token);
//				User.token = userKey;
				int status = Integer.parseInt(jsonObjectResult.getString("status"));
				if(status==HttpStatus.SC_OK){
					Log.d(TAG,"getUserKey() suc");
					JSONObject key = (JSONObject) jsonObjectResult.getJSONArray("keys").get(0);
					String token = key.getString(Token);
					long id = Long.parseLong(key.getString("user_id"));
					User.token = token;
					User.id = id;
					result.setStatus(status);
					result.setMessage("登陆成功");
					return result;
				}
				else{
					Log.d(TAG, "getUserKey() fail");
					String message = jsonObjectResult.getString("message");
					result.setMessage(message);
					result.setStatus(status);
					return result;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.d(TAG, "getUserKey() fail");
//		result.setMessage("用户名或密码错误");
		return result;
	}
	
	/**
	 * hard coding here for the time is urgent.
	 * 
	 * POST, url: http://114.215.177.97/v1/user/join/
	 * ValuePair: 
	 * 			username	:	baihua
	 * 			email		:	aa@aa.com
	 * 			password	:	espressif
	 * 
	 * @param	username	username
	 * @param	email		email address
	 * @param	password	the password for the user
	 * 
	 * @return	the response from the server
	 */
	public static LoginResponse join(String username, String email, String password){
		LoginResponse result = new LoginResponse();
		try {
			URI uri = new URI(UrlJoin);
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost();
			HttpResponse response;
			HttpEntity httpEntity;
			BufferedReader bufferedReader;
			StringBuilder builder = new StringBuilder();
			InputStream inputStream;

			List<NameValuePair> formData = new ArrayList<NameValuePair>();
			formData.add(new BasicNameValuePair("username", username));
			formData.add(new BasicNameValuePair("email", email));
			formData.add(new BasicNameValuePair("password", password));

			// Creating UrlEncodedFormEntity that will use the URL encoding to encode form parameters 
			// in a form like param1=value1&param2=value2
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formData , HTTP.UTF_8);
			
			// set uri
			httpPost.setURI(uri);

			// set pair value
			httpPost.setEntity(entity);
			
			// send Get request and get the response
			response = httpClient.execute(httpPost);

			// process the response
			httpEntity = response.getEntity();

			StatusLine statusLine = response.getStatusLine();

			// !!!!!!!!!!!!!!!!it is need to check the response!!!!!!

			if (httpEntity != null) {
				builder = new StringBuilder();
				inputStream = httpEntity.getContent();
				bufferedReader = new BufferedReader(new InputStreamReader(
						inputStream));
				for (String line = null; (line = bufferedReader.readLine()) != null;) {
					builder.append(line).append("\n");
				}
				JSONObject jsonObjectResult = null;
				String message = null;
				int status = -1;
				if(builder.length()>0){
					jsonObjectResult = new JSONObject(builder.toString());
					message = jsonObjectResult.getString("message");
					status = Integer.parseInt(jsonObjectResult.getString("status"));
				}
				Log.d(TAG, "jsonObject:" + jsonObjectResult);
				Log.d(TAG, "message is: " + message);
				Log.d(TAG, "status:" + status);
				result.setMessage(message);
				result.setStatus(status);
				return result;
			} else {
				Log.e(TAG,
						"the fail reason is: " + statusLine.getReasonPhrase());
//				return "register fail";
				return result;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG, "restPostJson exit abnormally with null return");
//		return "register fail";
		return result;
	}
	
	/**
	 * 
	 * POST
	 * url: http://114.215.177.97/v1/user/join/validate/702c5f8aa11862fecd3bf2700040db2be2662dba
	 * 
	 * {'status': 404, 'result': 'failed', 'message': '验证码失效，验证用户 None 失败'}
	 * 
	 * 
	 * validate by the email
	 * @param temptToken		the tempt token got from email
	 * @return					the message from the user
	 */
	public static String validate(String temptToken){
		JSONObject jsonObjectResult = restPostHelper.restPostJSONSyn(UrlValidate + temptToken, null);
		String message = null;
		if(jsonObjectResult!=null){
			try {
				message = jsonObjectResult.getString("message");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(message!=null){
				return message;
			}
		}
		Log.w(TAG, "validate is fail");
		return null;
	}
	
	/**
	 * POST url: http://114.215.177.97/v1/datastreams/plug-status/datapoint/
	 * 
	 * header: Authorization : token b7f86f0581a84a71abe662a730966c61d83223cd
	 * {"datapoint":{"x":1}}
	 * 
	 * switch the plug
	 * 
	 * @param switchOn
	 *            switchOn or switchOff
	 * @param token
	 *            owner key or user key are both in the token
	 * @param post
	 *            whether it is post or get
	 * 
	 */
	public static boolean plugSwitch(boolean switchOn, String token,
			boolean post) {
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonObjectX = new JSONObject();
		String headerKey = Authorization;
		String headerValue = "token " + token;
		
		JSONObject result = null;
		if (post) {
			try {
				if (switchOn)
					jsonObjectX.put("x", 1);
				else
					jsonObjectX.put("x", 0);
				jsonObject.put(Datapoint, jsonObjectX);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			result = restPostHelper.restPostJSONSyn(UrlPlugStatus, jsonObject,
					headerKey, headerValue);
		} else {
			result = restGetHelper.restGetJSONSyn(UrlPlugStatus, headerKey,
					headerValue);
		}
		int status = -1;
		try {
			if(result!=null)
				status = Integer.parseInt(result.getString("status"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (isStatusOK(status)) {
			Log.d(TAG, "plugSwitch() ok");
			return true;
		} else {
			Log.w(TAG, "plugSwitch() err");
			return false;
		}

	}
	
	/**
	 * GET url: 114.215.177.97/v1/datastreams/tem_hum/datapoint/
	 * 
	 * header: Authorization : token 20dd316acf9c3f0f9347c27fab14d77bd98458ac
	 * 
	 * {"status": 200, "datapoint": {"updated": "2014-04-28 09:40:00",
	 * "created": "2014-04-28 09:40:00", "visibly": 1, "datastream_id": 3, "at":
	 * "2014-04-28 09:40:00", "y": 93, "x": 104, "id": 1871}}
	 * 
	 * @param token
	 *            owner key or user key are both in the token
	 * 
	 */
	
	public static TemHumData getTemHumData(String token){
		/**
		 * for the moment the token is used fixed
		 */
//		token = "20dd316acf9c3f0f9347c27fab14d77bd98458ac";
		String headerKey = Authorization;
		String headerValue = "token " + token;
		TemHumData temHumData = new TemHumData();
		JSONObject jsonObjectResult = restGetHelper.restGetJSONSyn(UrlTemHumDataPoint, headerKey,
				headerValue);
		if(jsonObjectResult!=null){
			try {
				int status = Integer.parseInt(jsonObjectResult
						.getString("status"));
				if (status == HttpStatus.SC_OK) {
					JSONObject datapoint = jsonObjectResult
							.getJSONObject("datapoint");
					String at = datapoint.getString("at");
					int x = Integer.parseInt(datapoint.getString("x"));
					int y = Integer.parseInt(datapoint.getString("y"));
					temHumData.setAt(at);
					temHumData.setX(x);
					temHumData.setY(y);
					Log.d(TAG, "getTemHumData() suc");
					return temHumData;
				} else {
					Log.e(TAG, "getTemHumData() fail");
					return null;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.e(TAG, "getTemHumData() fail"); 
		return null;
	}
	
	/**
	 * GET url: 114.215.177.97/v1/datastreams/tem_hum/datapoints/
	 * 
	 * header: Authorization : token 20dd316acf9c3f0f9347c27fab14d77bd98458ac
	 * 
	 * {"status": 200, "datapoints": [{"updated": "2014-04-28 09:40:00",
	 * "created": "2014-04-28 09:40:00", "visibly": 1, "datastream_id": 3, "at":
	 * "2014-04-28 09:40:00", "y": 93, "x": 104, "id": 1871}, {"updated":
	 * "2014-04-28 09:39:50", ...
	 * 
	 * @param token
	 *            owner key or user key are both in the token
	 * 
	 */
	public static List<TemHumData> getTemHumDataList(String token){
		/**
		 * for the moment the token is used fixed
		 */
//		token = "20dd316acf9c3f0f9347c27fab14d77bd98458ac";
		String headerKey = Authorization;
		String headerValue = "token " + token;
		List<TemHumData> resultList = new CopyOnWriteArrayList<TemHumData>();
		JSONObject jsonObjectResult = restGetHelper.restGetJSONSyn(UrlTemHumDataPoints, headerKey,
				headerValue);
		if(jsonObjectResult!=null){
			try {
				int status = Integer.parseInt(jsonObjectResult
						.getString("status"));
				if (status == HttpStatus.SC_OK) {
					JSONArray jsonArray = jsonObjectResult.getJSONArray("datapoints");
					for(int i=0;i<jsonArray.length();i++){
						TemHumData temHumData = new TemHumData();
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						String at = jsonObject.getString("at");
						int x = Integer.parseInt(jsonObject.getString("x"));
						int y = Integer.parseInt(jsonObject.getString("y"));
						temHumData.setAt(at);
						temHumData.setX(x);
						temHumData.setY(y);
						resultList.add(temHumData);
					}
				} else {
					Log.e(TAG, "getTemHumData() fail");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return resultList;
	}
	
}

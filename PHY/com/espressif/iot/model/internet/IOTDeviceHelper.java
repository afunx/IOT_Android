package com.espressif.iot.model.internet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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



import com.espressif.iot.model.device.IOTAddress;
import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.model.device.IOTDevice.TYPE;
import com.espressif.iot.tasknet.rest.RestGetHelper;
import com.espressif.iot.tasknet.rest.RestPostHelper;
import com.espressif.iot.util.Logger;


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
	private static final String Email = "email";
	private static final String Username = "username";
	private static final String Password = "password";
	private static final String Token = "token";
	private static final String Key = "key";
	private static final String Keys = "keys";
	private static final String Metadata = "metadata";
	private static final String Device = "device";
	private static final String Datapoint = "datapoint";
	private static final String Name = "name";
	private static final String Scope = "scope";
	private static final String Message = "message";
	// for User is a class name, to avoid collapse, use "USER" instead of "User"
	private static final String USER = "user";
	private static final String UrlAuthorize = "http://114.215.177.97/v1/key/authorize/";
	private static final String UrlGetUserKey = "http://114.215.177.97/v1/keys/";
	private static final String UrlJoin = "http://114.215.177.97/v1/user/join/";
	private static final String UrlValidate = "http://114.215.177.97/v1/user/join/validate/";
	private static final String UrlPlugStatus = "http://114.215.177.97/v1/datastreams/plug-status/datapoint/";
	private static final String UrlTemHumDataPoint = "http://114.215.177.97/v1/datastreams/tem_hum/datapoint/";
	private static final String UrlTemHumDataPoints = "http://114.215.177.97/v1/datastreams/tem_hum/datapoints/";
	
	private static final String UrlDeviceMetadataPut = "http://114.215.177.97/v1/device/?method=PUT";
	private static final String UrlDeviceMetadataGet = "http://114.215.177.97/v1/device/";
	private static final String UrlDeviceShare = "http://114.215.177.97/v1/key/share/";
	private static final String UrlDeviceShareAuthorize = "http://114.215.177.97/v1/key/authorize/";
	
	private static boolean isStatusOK(int status){
		if(status/100==2)
			return true;
		else
			return false;
	}
	
	private static class JSONResponse{
		public JSONResponse(boolean suc,Map<String,JSONObject> jsonObjectMap){
			this.suc = suc;
			this.jsonObjectMap = jsonObjectMap;
		}
		public JSONResponse(boolean suc,JSONArray jsonArray){
			this.suc = suc;
			this.jsonArray = jsonArray;
		}
		/**whether the action is succeed*/
		public boolean suc;
		/**the jsonObject*/
		public Map<String,JSONObject> jsonObjectMap;
		public List<String> jsonObjectList;
		public JSONArray jsonArray;
		public String message;
		public int status;
	}
	
	/** Flyweight Pattern */
	private static JSONResponse JSON_RESPONSE_FAIL = 
			new JSONResponse(false,new HashMap<String,JSONObject>());
	private static String JSON_RESPONSE_FAIL_MESSAGE = "请打开wifi或3g确保能连上Internet";
	
	/**
	 * check whether the response is OK
	 * @param response	the Json response get from server
	 * @param jsonKey	the result JsonObject's key
	 * @param isJsonObject whether it is JsonObject or JsonArray
	 * @return	the JSONResponse
	 * 
	 * @see JSONResponse
	 */
	private static JSONResponse checkRestResponse(JSONObject response,
			List<String> jsonKeyList, boolean isJsonObject) {
		JSON_RESPONSE_FAIL.message = JSON_RESPONSE_FAIL_MESSAGE;
		if (response == null)
			return JSON_RESPONSE_FAIL;
		int status = -1;
		JSONResponse resultJsonResponse = null;
		JSONObject resultJsonObject = null;
		JSONArray resultJsonArray = null;
		Map<String, JSONObject> resultJsonObjectMap = new HashMap<String, JSONObject>();
		// List<JSONObject> resulJsonObjectList = new ArrayList<JSONObject>();
		try {
			status = Integer.parseInt(response.getString("status"));
			JSON_RESPONSE_FAIL.status = status;
			if (status == HttpStatus.SC_OK) {
				if (isJsonObject) {
					for (String jsonKey : jsonKeyList) {
						resultJsonObject = response.getJSONObject(jsonKey);
						resultJsonObjectMap.put(jsonKey, resultJsonObject);
					}
					resultJsonResponse = new JSONResponse(false,
							resultJsonObjectMap);
				} else {
					resultJsonArray = response.getJSONArray(jsonKeyList.get(0));
					resultJsonResponse = new JSONResponse(false,
							resultJsonArray);
				} 
				resultJsonResponse.suc = true;
				resultJsonResponse.status = status;
				return resultJsonResponse;
			}
			else{
				JSON_RESPONSE_FAIL.message = response.getString(Message);
				return JSON_RESPONSE_FAIL;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return JSON_RESPONSE_FAIL;
	}
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
			List<String> keyList = new ArrayList<String>();
			keyList.add(Key);
			JSONResponse jsonResponse = checkRestResponse(result,keyList,true);
			if(jsonResponse.suc){
				Logger.d(TAG, "authorized() suc");
				JSONObject key = jsonResponse.jsonObjectMap.get(Key);
				String token = key.getString(Token);
				boolean is_owner_key = false;
				if(Integer.parseInt(key.getString("is_owner_key"))==1)
					is_owner_key = true;
				iotDevice.setIsOwner(is_owner_key);
				long id = Long.parseLong(key.getString("device_id"));
				iotDevice.setDeviceId(id);
				return token;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.w(TAG, "authorize() fail");
		return null;
	}
	
	/**
	 * !NOTE: if fail, it will return null
	 * 
	 * curl -d
	 * '{"email":"cloudzhou@163.com","password":"espressif","scope":"user"}'
	 * http://114.215.177.97/v1/keys/
	 * 
	 * get the user key
	 * 
	 * @param email
	 *            the email
	 * @param password
	 *            the password
	 * @return the response from the server
	 */
	public static LoginResponse getUserKey(String email,String password){
		LoginResponse result = new LoginResponse();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject();
			jsonObject.put(Email, email);
			jsonObject.put(Password, password);
			jsonObject.put(Scope, USER);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jsonObjectResult = restPostHelper.restPostJSONSyn(UrlGetUserKey, jsonObject);
		List<String> keyList = new ArrayList<String>();
		keyList.add(Keys);
		JSONResponse jsonResponse = checkRestResponse(jsonObjectResult,keyList,false);
		if(jsonResponse.suc){
			Logger.d(TAG,"getUserKey() suc");
			try {
				JSONObject key = (JSONObject) jsonResponse.jsonArray.get(0);
				String token = key.getString(Token);
				long id = Long.parseLong(key.getString("user_id"));
				User.token = token;
				User.id = id;
				result.setStatus(HttpStatus.SC_OK);
				result.setMessage("登陆成功");
				return result;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			Logger.d(TAG, "getUserKey() fail");
			String message = jsonResponse.message;
			result.setMessage(message);
			result.setStatus(jsonResponse.status);
			return result;
		}
		result.setMessage("用户名或密码错误");
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
				Logger.d(TAG, "jsonObject:" + jsonObjectResult);
				Logger.d(TAG, "message is: " + message);
				Logger.d(TAG, "status:" + status);
				result.setMessage(message);
				result.setStatus(status);
				return result;
			} else {
				Logger.e(TAG,
						"the fail reason is: " + statusLine.getReasonPhrase());
				return result;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.d(TAG, "restPostJson exit abnormally with null return");
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
		Logger.w(TAG, "validate is fail");
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
	static boolean plugSwitch(boolean switchOn, String token,
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
			Logger.d(TAG, "plugSwitch() ok");
			return true;
		} else {
			Logger.w(TAG, "plugSwitch() err");
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
					Logger.d(TAG, "getTemHumData() suc");
					return temHumData;
				} else {
					Logger.e(TAG, "getTemHumData() fail");
					return null;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Logger.e(TAG, "getTemHumData() fail"); 
		return null;
	}
	
	/**
	   * 获取现在时间
	   * 
	   * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
	   */
	private static Date getNowDate(String dateStr) {
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dt2 = null;
		try {
			dt2 = sdf.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dt2;
	}
	
	// tempt method
	public static boolean getTemHumData(String token,String empty){
		/**
		 * for the moment the token is used fixed
		 */
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
					for(TemHumData result: resultList){
						String dateStr = result.getAt();
						Date date = getNowDate(dateStr);
						Date now = new Date();
						long dateLong = date.getTime();
						long nowLong = now.getTime();
//						Log.e(TAG, "dateLong-nowLong="+(dateLong-nowLong));
						if(Math.abs(dateLong-nowLong)<10000){
//						if(dateLong-nowLong<30000||nowLong-dateLong<30000){
							return true;
						}
					}
				} else {
					Logger.e(TAG, "getTemHumData() fail");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return false;
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
	static List<TemHumData> getTemHumDataList(String token){
		/**
		 * for the moment the token is used fixed
		 */
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
					Logger.e(TAG, "getTemHumData() fail");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return resultList;
	}
	
	/**
	 * (using post command to do PUT action)
	 * POST url: 114.215.177.97/v1/device/?method=PUT
	 * 
	 * header: Authorization : token 20dd316acf9c3f0f9347c27fab14d77bd98458ac
	 * 
	 * {"device": {"metadata":  "7c:80:88:33:44:66 plug/light/temperature"}}
	 * "plug/light/temperature" means "plug" or "light" or "temperature"
	 * 
	 * put meta data to the device on the Internet Server,
	 * for the reason, it don's support bssid's store and device's type storation
	 * @param token		device's token
	 * @param bssid		device's bssid
	 * @param type		device's type
	 * @return			whether the action is succeed
	 */
	public static boolean putMetadata(String token, String bssid, TYPE type){
		
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonObjectMetadata = new JSONObject();
		String headerKey = Authorization;
		String headerValue = "token " + token;
		String typeStr = IOTDevice.getIOTDeviceType(type);
		String metadata = bssid+" "+typeStr;
		
		JSONObject result = null;
		
		try {
			jsonObjectMetadata.put( Metadata, metadata);
			jsonObject.put(Device, jsonObjectMetadata);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		result = restPostHelper.restPostJSONSyn(UrlDeviceMetadataPut, jsonObject,
				headerKey, headerValue);
		
		int status = -1;
		try {
			if(result!=null)
				status = Integer.parseInt(result.getString("status"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (HttpStatus.SC_OK==status) {
			Logger.d(TAG, "putMetadata() ok");
			return true;
		} else {
			Logger.w(TAG, "putMetadata() err");
			return false;
		}
	}
	
	/**
	 * GET url: 114.215.177.97/v1/device/?method=GET
	 * 
	 * {
    "device": {
        "metadata": "ff:ff:ff:77:85:00 plug"
    },
    "status": 200
}

	 * 
	 * header: Authorization : token 20dd316acf9c3f0f9347c27fab14d77bd98458ac
	 * 
	 * @param token
	 * @param device
	 * @return
	 */
	public static boolean getMetadata(String token, IOTDevice device){
		String headerKey = Authorization;
		String headerValue = "token " + token;
		JSONObject result = null;
		result = restGetHelper.restGetJSONSyn(UrlDeviceMetadataGet, headerKey, headerValue);
		if(result!=null){
			int status = -1;
			try {
				if(result!=null)
					status = Integer.parseInt(result.getString("status"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (HttpStatus.SC_OK==status) {
				Logger.d(TAG, "getMetadata() ok");
				String metadata = null;
				try {
					metadata = result.getJSONObject(Device).getString(Metadata);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Logger.d(TAG, "metadata is: " + metadata);
				// "ff:ff:ff:77:85:00" 's length() is 17
				String bssid = metadata.substring(0, 18);
				String typeStr = metadata.substring(18);
				IOTAddress iotAddress = new IOTAddress(bssid,null);
				device.setIOTAddress(iotAddress);
				device.setTypeStr(typeStr);
				return true;
			} else {
				Logger.w(TAG, "getMetadata() err");
				return false;
			}
		}
		
		return false;
	}

	/**
	 * for share, there are three steps:
	 * 1.	genShareKey : get the device key
	 * 2.	shareDeviceAuthorize : get the device_id, the user_id ( who is shared to)
	 * 3.	synchronize from server
	 */
	
	/**
	 * POST: url: http://114.215.177.97/v1/key/share/
	 * 
	 * header: Authorization : token ab2819caf9a87f61c2004097c251c8a010cca277
	 * {"name": "share test name", "scope": "device"}
	 * 
	 * { "status": 200, "message": "shared token", "result": "success", "token":
	 * "2ae05df31ad0b909b3f6e4e290d9b7235a8ee893" }
	 * 
	 * share device to other users, gennerate the share key
	 * 
	 * @param ownerKey
	 *            the ownerKey, only the owner could share the device
	 * @return shareDey, if suc; null , if fail
	 */
	public static String genShareKey(String ownerKey){
//		UrlDeviceSharePost
		String headerKey = Authorization;
		String headerValue = "token " + ownerKey;
		JSONObject jsonObject = new JSONObject();
		JSONObject result = null;
		try {
			jsonObject.put(Name, "share test name");
			jsonObject.put(Scope, Device);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = restPostHelper.restPostJSONSyn(UrlDeviceShare, jsonObject,
				headerKey, headerValue);
		int status = -1;
		try {
			if(result!=null)
				status = Integer.parseInt(result.getString("status"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (HttpStatus.SC_OK==status) {
			Logger.d(TAG, "shareDevice() ok");
			String shareKey = null;
			try {
				shareKey = result.getString(Token);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return shareKey;
		} else {
			Logger.w(TAG, "shareDevice() err");
			return null;
		}
	}
	
	
	/**
	 * 
	 * curl -H "Authorization: token 3c84ebe6a9eb554b2810ade3f381d9c75fbf9204"
	 * -d '{"token": "90436cf90322d39de51565b1df9c6989e9ace6bf"}'
	 * http://114.215.177.97/v1/key/authorize/
	 * 
	 * @param userKey		the user's userkey, who's shared
	 * @param shareKey		the shareKey, get from genShareKey
	 * @return
	 */
	public static boolean shareDeviceAuthorize(String userKey, String shareKey){
		String headerKey = Authorization;
		String headerValue = "token " + userKey;
		JSONObject jsonObject = new JSONObject();
		JSONObject result = null;
		try {
			jsonObject.put(Token, shareKey);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = restPostHelper.restPostJSONSyn(UrlDeviceShareAuthorize, jsonObject,
				headerKey, headerValue);
		int status = -1;
		try {
			if(result!=null)
				status = Integer.parseInt(result.getString("status"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (HttpStatus.SC_OK==status) {
			Logger.d(TAG, "shareDeviceAuthorize() ok");
			return true;
		} else {
			Logger.w(TAG, "shareDeviceAuthorize() err");
			return false;
		}
	}
}

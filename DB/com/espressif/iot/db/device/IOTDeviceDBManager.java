package com.espressif.iot.db.device;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.espressif.iot.db.device.model.TokenIsOwner;
import com.espressif.iot.db.greenrobot.daoDevice.DaoMaster;
import com.espressif.iot.db.greenrobot.daoDevice.DaoMaster.DevOpenHelper;
import com.espressif.iot.db.greenrobot.daoDevice.DaoSession;
import com.espressif.iot.db.greenrobot.daoDevice.DeviceDB;
import com.espressif.iot.db.greenrobot.daoDevice.DeviceDBDao;
import com.espressif.iot.db.greenrobot.daoDevice.user__device;
import com.espressif.iot.db.greenrobot.daoDevice.user__deviceDao;
//import com.espressif.iot.db.greenrobot.daoDevice.DeviceDBDao.Properties;
//import com.espressif.iot.db.greenrobot.daoDevice.UserDBDao.Properties;
import com.espressif.iot.db.greenrobot.daoDevice.UserDB;
import com.espressif.iot.db.greenrobot.daoDevice.UserDBDao;

import de.greenrobot.dao.query.Query;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;



/**
 * the database structure:
 * 
 * device_table
 * id,key,etc...
 * 
 * (Maybe user_table is useless for the moment)
 * user_table
 * id,key,etc...
 * 
 * user_device_table
 * devId,userId,isOwner,token
 * 
 * @author afunx
 *
 */
public class IOTDeviceDBManager {
	private static final String TAG = "DBDeviceManager";
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private DeviceDBDao deviceDao;
	private UserDBDao userDao;
	private user__deviceDao user_device_dao;
	
	private static final long NOT_EXIST = -1;
	// Singleton Pattern
	private static IOTDeviceDBManager instance  = null;
	private IOTDeviceDBManager(Context context){
		DevOpenHelper helper = new DaoMaster.DevOpenHelper( context , "device-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        deviceDao = daoSession.getDeviceDBDao();
        userDao = daoSession.getUserDBDao();
        user_device_dao = daoSession.getUser__deviceDao();
	}
	public static IOTDeviceDBManager getInstance(Context context){
		if(instance == null)
			instance = new IOTDeviceDBManager(context);
		return instance;
	}
	
	/**
	 * get userId by user's key.
	 * for the reason that user key is unique,
	 * so we just return userList.get(0).getId()
	 * 
	 * @param key		the user's key
	 * @return			the user's Id, if not exist, return NOT_EXIST(-1)
	 */
	/**
	private long getUserIdByKey(String key){
		Query<UserDB> query = userDao.queryBuilder().where(
				com.espressif.iot.db.greenrobot.daoDevice.UserDBDao.Properties.Key.eq(key)).build();
		List<UserDB> userList = query.list();
		if(userList.isEmpty())
			return NOT_EXIST;
		else
			return userList.get(0).getId();
	}
	*/
	
	private boolean isUserExist(long userId){
		Query<UserDB> query = userDao.queryBuilder().where(
				com.espressif.iot.db.greenrobot.daoDevice.UserDBDao.Properties.Id.eq(userId)).build();
		List<UserDB> userList = query.list();
		if(userList.isEmpty())
			return false;
		else
			return true;
	}
	
	public long addUserIfNotExist(long userId, String key){
		boolean isUserExist = isUserExist(userId);
		if(!isUserExist){
			UserDB user = new UserDB(userId , key);
			Log.e(TAG, "User [key="+key+"] is added in the DB");
			return userDao.insert(user);
		}
		else{
			Log.e(TAG, "User [key="+key+"] is already exist in the DB");
			return userId;
		}
	}
	
	public TokenIsOwner getTokenIsOwner(long deviceId,long userId){
		Query<user__device> query = user_device_dao
				.queryBuilder()
				.where(com.espressif.iot.db.greenrobot.daoDevice.user__deviceDao.Properties.UserId
						.eq(userId),
						com.espressif.iot.db.greenrobot.daoDevice.user__deviceDao.Properties.DeviceId
								.eq(deviceId)).build();
//		List<user__device> result = query.list();
		user__device result = query.unique();
//		if(result.isEmpty()){
		if(result==null){
			Log.e(TAG, "getTokenIsOwner: deviceId:"+deviceId+",userId:"+userId+" fail");
			return null;
		}
		else{
//			String token = result.get(0).getToken();
//			boolean isOwner = result.get(0).getIsOwner();
			String token = result.getToken();
			boolean isOwner = result.getIsOwner();
			TokenIsOwner tokenIsOwner = new TokenIsOwner();
			tokenIsOwner.setIsOwner(isOwner);
			tokenIsOwner.setToken(token);
			Log.d(TAG, "getTokenIsOwner: deviceId:"+deviceId+",userId:"+userId+" suc");
			return tokenIsOwner;
		}
	}
	
	
	/**
	public void addUser(long userId,String key){
		UserDB user = new UserDB(userId,key);
		Log.d(TAG, "User [userId="+userId+"][key="+key+"] is added in the UserDB.");
		userDao.insert(user);
	}
	*/
	public void addDevice(String BSSID, String type, String status,
			boolean isOwner, String key, boolean isActive, long deviceId,
			long userId) {
		DeviceDB device = new DeviceDB(deviceId, BSSID, type, status);
		user__device user_device = new user__device(null,userId, deviceId, isOwner,
				key);
		// device exist, update it
		if (isDeviceExistByBSSID(BSSID)) {
			deviceDao.update(device);
			Log.i(TAG, "IOTDevice [deviceId=" + deviceId + "][BSSID=" + BSSID
					+ "][type=" + type + "][status=" + status
					+ "] is updated in the DeviceDB.");
			Query<user__device> query = user_device_dao
					.queryBuilder()
					.where(com.espressif.iot.db.greenrobot.daoDevice.user__deviceDao.Properties.UserId
							.eq(userId),
							com.espressif.iot.db.greenrobot.daoDevice.user__deviceDao.Properties.DeviceId
									.eq(deviceId)).build();
			user_device_dao.delete(query.uniqueOrThrow());
//			user_device_dao.delete(user_device);
			
			user_device_dao.insert(user_device);
			Log.i(TAG, "user_device [userId=" + userId + "][deviceId="
					+ deviceId + "][isOwner=" + isOwner + "][key=" + key
					+ "] is updated in the user__deviceDB.");
		}
		// device doesn't exist, add it
		else {
			
			deviceDao.insert(device);
			Log.i(TAG, "IOTDevice [deviceId=" + deviceId + "][BSSID=" + BSSID
					+ "][type=" + type + "][status=" + status
					+ "] is added in the DeviceDB.");
			user_device_dao.insert(user_device);
			Log.i(TAG, "user_device [userId=" + userId + "][deviceId="
					+ deviceId + "][isOwner=" + isOwner + "][key=" + key
					+ "] is added in the user__deviceDB.");
		}
	}
	
	private user__device getUserDevice(long deviceId,long userId){
		Query<user__device> query = user_device_dao
				.queryBuilder()
				.where(com.espressif.iot.db.greenrobot.daoDevice.user__deviceDao.Properties.UserId
						.eq(userId),
						com.espressif.iot.db.greenrobot.daoDevice.user__deviceDao.Properties.DeviceId
								.eq(deviceId)).build();
		return query.uniqueOrThrow();
	}
	
	// NOTE: it should only delete one item each time
	public void deleteDeviceByBSSID(String BSSID, long userId){
		Log.i(TAG, "deleteDeviceByBSSID(): BSSID:" + BSSID);
		Query<DeviceDB> query = deviceDao.queryBuilder().where(
				com.espressif.iot.db.greenrobot.daoDevice.DeviceDBDao.Properties.Bssid.eq(BSSID)).build();
//		List<DeviceDB> deviceList = query.list();
//		Log.i(TAG, deviceList.size()+" devices are deleted");
//		for(DeviceDB device: deviceList){
//			deviceDao.deleteByKey(device.getId());
//		}
		DeviceDB device = query.uniqueOrThrow();
		deviceDao.delete(device);
		//*****delete the user_device
		long deviceId = device.getId();
		user__device user_device = getUserDevice(deviceId, userId);
		user_device_dao.delete(user_device);
	}
	
	public boolean isDeviceExistByBSSID(String BSSID){
		Query<DeviceDB> query = deviceDao.queryBuilder().where(
				com.espressif.iot.db.greenrobot.daoDevice.DeviceDBDao.Properties.Bssid.eq(BSSID)).build();
		List<DeviceDB> deviceList = query.list();
		if(!deviceList.isEmpty())
			return true;
		else
			return false;
	}
	
	private DeviceDB getDeviceDB(long deviceId){
		Query<DeviceDB> query = deviceDao.queryBuilder().where(
				com.espressif.iot.db.greenrobot.daoDevice.DeviceDBDao.Properties.Id.eq(deviceId)).build();
		return query.uniqueOrThrow();
	}
	
	/**
	 * get IOTDeviceDBList according to the userId,
	 * it will return the userId's IOTDeviceDBList
	 * 
	 * @param userId		the userId
	 * @return
	 */
	public List<DeviceDB> getIOTDeviceDBList(long userId){
		List<DeviceDB> result = new CopyOnWriteArrayList<DeviceDB>();
		Query<user__device> query_user_device = user_device_dao
				.queryBuilder()
				.where(com.espressif.iot.db.greenrobot.daoDevice.user__deviceDao.Properties.UserId
						.eq(userId)).build();
		List<user__device> user_device_list = query_user_device.list();
		for(user__device user_device:user_device_list){
			long deviceId = user_device.getDeviceId();
			result.add(getDeviceDB(deviceId));
		}
		return result;
	}
	public void deleteAllUsers(){
		userDao.deleteAll();
	}
	public void deleteAllDevices(){
		deviceDao.deleteAll();
	}
}

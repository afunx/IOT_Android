package com.espressif.iot.test;


import com.espressif.iot.R;
import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.model.device.IOTDevice.TYPE;
import com.espressif.iot.model.internet.IOTDeviceHelper;
import com.espressif.iot.model.internet.TemHumData;
import com.espressif.iot.util.Logger;

import android.app.Activity;
import android.os.Bundle;

public class TestActivity extends Activity {
	
	private static final String TAG = "TestActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_plug_control);
		test();
	}
	
	private void test(){
		System.out.println("test:"+IOTDeviceHelper.Test );
		new Thread(){
			public void run(){
//				while(true){
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("hello");
					testShareDevice();
//					testPutMetadata();
//					testGetMetadata();
//					testPutMetadata();
//					testGetTemHumDataList();
//					testGetTemHumData();
//					testPlugSwitch();
//				}
			}
		}.start();
	}
	private void testPlugSwitch(){
//		IOTDeviceHelper.plugSwitch(true, "be83e4661db6f548ecac67f14a9560f6111bb5d5" , false);
//		IOTDeviceHelper.plugSwitch(false, "be83e4661db6f548ecac67f14a9560f6111bb5d5", false);

	}
	private void testGetTemHumData(){
		TemHumData result = IOTDeviceHelper.getTemHumData("20dd316acf9c3f0f9347c27fab14d77bd98458ac");
		Logger.d(TAG,"result: at="+result.getAt() + "x="+result.getX()+"y="+result.getY());
	}
	private void testGetTemHumDataList(){
		/*
		List<TemHumData> result = IOTDeviceHelper.getTemHumDataList("20dd316acf9c3f0f9347c27fab14d77bd98458ac");
		if(result.isEmpty()){
			Logger.d(TAG, "testGetTemHumDataList() fail");
		}
		else{
			Logger.d(TAG, "testGetTemHumDataList() suc");
		}
		*/
	}
	private void testPutMetadata(){
		String bssid = "ff:ff:ff:77:85:00";
//		String type = IOTDevice.getIOTDeviceType(TYPE.PLUG);
		String token = "ab2819caf9a87f61c2004097c251c8a010cca277";
		IOTDeviceHelper.putMetadata(token, bssid, TYPE.PLUG);
	}
	
	private void testGetMetadata(){
		IOTDevice device = IOTDevice.createIOTDevice(null);
		String token = "ab2819caf9a87f61c2004097c251c8a010cca277";
		IOTDeviceHelper.getMetadata(token, device);
	}
	
	private void testShareDeviceGenShareKey(){
		String shareKey = null;
		String ownerKey = "ab2819caf9a87f61c2004097c251c8a010cca277";
		shareKey = IOTDeviceHelper.genShareKey(ownerKey);
		Logger.d(TAG, "shareKey = " + shareKey);
	}
	
	private void testShareDevice(){
		String shareKey = null;
		String ownerKey = "ab2819caf9a87f61c2004097c251c8a010cca277";
		shareKey = IOTDeviceHelper.genShareKey(ownerKey);
		Logger.d(TAG, "shareKey = " + shareKey);
		
		// test010
		String userKey = "ae66aff6ffbd4c5569d37f2fb22d5bd9992a8cf4";
		boolean suc = IOTDeviceHelper.shareDeviceAuthorize(userKey, shareKey);
		Logger.d(TAG, "share is suc = " + suc);
	}
}

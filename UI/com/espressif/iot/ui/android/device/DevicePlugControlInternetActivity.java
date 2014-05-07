package com.espressif.iot.ui.android.device;

import com.espressif.iot.R;
import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.model.internet.IOTDeviceHelper;
import com.espressif.iot.ui.android.MessageStatic;
import com.espressif.iot.ui.android.UtilActivity;
import com.espressif.iot.ui.android.share.CreateQRImageActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class DevicePlugControlInternetActivity extends Activity {
	
	private static final String TAG = "DevicePlugControlLocalActivity";
	
	private Button btnBack;
	private ImageView imgStatus;
	private Button btnOp;
	private boolean isBtnOpPressed;
	private IOTDevice iotDevice;
	private String token;
	private Button btnShare;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		iotDevice = MessageStatic.currentIOTDevice;
		setContentView(R.layout.activity_device_plug_control);
		token = iotDevice.getDeviceKey();
		init();
	}

	private void share(){
		CreateQRImageActivity.shareKey = IOTDeviceHelper.genShareKey(iotDevice.getDeviceKey());
		if(CreateQRImageActivity.shareKey!=null){
			Log.d(TAG, "shareKey is: " + CreateQRImageActivity.shareKey);
		UtilActivity.transferActivity(
				DevicePlugControlInternetActivity.this,
				CreateQRImageActivity.class, false);
		}
	}
	
	private void initBtnShare(){
		btnShare = (Button) findViewById(R.id.btn_device_plug_control_share);
		if (iotDevice.getIsOwner()) {
			btnShare.setVisibility(View.VISIBLE);
			btnShare.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					share();
				}

			});
		}
		else{
			btnShare.setVisibility(View.GONE);
		}
	}
	private void init(){
		initBtnShare();
		btnBack = (Button) findViewById(R.id.btn_device_plug_control_back);
		btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DevicePlugControlInternetActivity.this.finish();
			}
		});
		imgStatus = (ImageView) findViewById(R.id.img_device_plug_control_status);
		isBtnOpPressed = false;
		btnOp = (Button) findViewById(R.id.btn_device_plug_control_op);
		btnOp.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isBtnOpPressed){
					isBtnOpPressed = false;
					Log.d(TAG, "Switch off");
					if(IOTDeviceHelper.plugSwitch(false, token, true)){
						btnOp.setBackgroundResource(R.drawable.key_off);
						imgStatus.setBackgroundResource(R.drawable.img_off);
					}
				}
				else{
					isBtnOpPressed = true;
					Log.d(TAG, "Switch on");
					if(IOTDeviceHelper.plugSwitch(true, token, true)){
					btnOp.setBackgroundResource(R.drawable.key_on);
					imgStatus.setBackgroundResource(R.drawable.img_on);
				}
				}
			}
			
		});
	}
	
	
}

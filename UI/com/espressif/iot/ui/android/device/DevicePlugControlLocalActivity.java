package com.espressif.iot.ui.android.device;

import com.espressif.iot.R;
import com.espressif.iot.model.device.IOTActionEnum;
import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.ui.android.MessageStatic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class DevicePlugControlLocalActivity extends Activity {
	
	private static final String TAG = "DevicePlugControlLocalActivity";
	
	private Button btnBack;
	private ImageView imgStatus;
	private Button btnOp;
	private boolean isBtnOpPressed;
	private IOTDevice iotDevice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		iotDevice = MessageStatic.currentIOTDevice;
		setContentView(R.layout.activity_device_plug_control);
		init();
	}

	private void init(){
		btnBack = (Button) findViewById(R.id.btn_device_plug_control_back);
		btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DevicePlugControlLocalActivity.this.finish();
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
					iotDevice.executeAction(IOTActionEnum.IOT_ACTION_SWITCH_OFF);
					btnOp.setBackgroundResource(R.drawable.key_off);
					imgStatus.setBackgroundResource(R.drawable.img_off);
				}
				else{
					isBtnOpPressed = true;
					Log.d(TAG, "Switch on");
					iotDevice.executeAction(IOTActionEnum.IOT_ACTION_SWITCH_ON);
					btnOp.setBackgroundResource(R.drawable.key_on);
					imgStatus.setBackgroundResource(R.drawable.img_on);
				}
			}
			
		});
	}
	
	
}

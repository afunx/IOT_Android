package com.espressif.iot.ui.android.device;

import java.util.ArrayList;

import com.espressif.iot.R;
import com.espressif.iot.ui.android.MessageStatic;
import com.espressif.iot.ui.android.UtilActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class DeviceSettingActivity extends Activity {

	private static final String TAG = "DeviceSettingActivity";
	private Button btnControlBack;
	private Spinner spinnerWifi;
	private ArrayAdapter<String> adapter;
	private Button btnShowPassword;
	private boolean bShowPasswordPressed;
	private EditText edtPassword;
	private TextView tvDeviceName;
	private Button btnStart;
	private static Activity leakActivity;
	
	public static void finishS(){
		leakActivity.finish();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_setting);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, getData());
		init();
		leakActivity = this;
	}

	private void init() {

		btnControlBack = (Button) this.findViewById(R.id.btn_control_back);
		btnControlBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DeviceSettingActivity.this.finish();
			}

		});

		spinnerWifi = (Spinner) this.findViewById(R.id.spinner_device_setting_wifi);
		spinnerWifi.setAdapter(adapter);

		bShowPasswordPressed = false;
		btnShowPassword = (Button) findViewById(R.id.btn_device_setting_show_password);
		btnShowPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (bShowPasswordPressed) {
					edtPassword.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					btnShowPassword
							.setBackgroundResource(R.drawable.show_password_but);
					bShowPasswordPressed = false;
				} else {
					edtPassword.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					btnShowPassword
							.setBackgroundResource(R.drawable.show_password_but_pressed);
					bShowPasswordPressed = true;
				}
			}

		});

		edtPassword = (EditText) findViewById(R.id.edt_device_setting_show_password);
		tvDeviceName = (TextView) findViewById(R.id.tv_device_setting_name);
		tvDeviceName.setText(MessageStatic.settingDeviceName);

		btnStart = (Button) findViewById(R.id.btn_device_setting_start);
		btnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// startSetting();
				MessageStatic.device_ap_connected_password = edtPassword.getText().toString();
				int index = spinnerWifi.getSelectedItemPosition();
				MessageStatic.device_ap_connected_ssid = MessageStatic.wifiScanSSIDList.get(index);
				MessageStatic.device_ap_type = MessageStatic.wifiScanTypeList.get(index);
				UtilActivity.transferActivity(DeviceSettingActivity.this,
						DeviceSettingProgressActivity.class, false);
			}

		});

	}

	private ArrayList<String> getData() {
		return MessageStatic.wifiScanSSIDList;
	}

}

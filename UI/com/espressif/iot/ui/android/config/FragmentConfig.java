package com.espressif.iot.ui.android.config;



import java.util.ArrayList;
import java.util.List;

import com.espressif.iot.R;
import com.espressif.iot.constants.WIFI_ENUM;
import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.net.lan.wifi.WifiScanResult;
import com.espressif.iot.oapi.OApiIntermediator;
import com.espressif.iot.ui.android.AbsFragment;
import com.espressif.iot.ui.android.MessageStatic;
import com.espressif.iot.ui.android.MyFragmentsActivityUI;
import com.espressif.iot.ui.android.MyFragmentsActivityUI.FRAG_TYPE;
import com.espressif.iot.ui.android.UtilActivity;
import com.espressif.iot.ui.android.device.DeviceSettingActivity;
import com.espressif.iot.ui.android.device.DeviceSettingProgressActivity;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class FragmentConfig extends AbsFragment {

	private static final String TAG = "FragmentConfig";
	
	private Button btnControlBack;
	private Spinner spinnerWifi;
	private ArrayAdapter<String> adapter;
	private Button btnShowPassword;
	private boolean bShowPasswordPressed;
	private EditText edtPassword;
//	private TextView tvDeviceName;
	private Button btnStart;

	private static WifiAdmin mWifiAdmin;
	private static OApiIntermediator oApiIntermediator = OApiIntermediator.getInstance();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.activity_device_setting, container, false);
		adapter = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_expandable_list_item_1, getData());
		mWifiAdmin = new WifiAdmin(this.getActivity());
		
		scanWifi();
		init(view);
		return view;
	}
	
	private void scanWifi(){
		List<WifiScanResult> scanResultList = oApiIntermediator
				.scanAPsLANSyn(mWifiAdmin, false);
		MessageStatic.wifiScanSSIDList.clear();
		MessageStatic.wifiScanSSIDList.add("");
		MessageStatic.wifiScanTypeList.clear();
		MessageStatic.wifiScanTypeList
				.add(WIFI_ENUM.WifiCipherType.WIFICIPHER_INVALID);
		for(WifiScanResult scanResult : scanResultList){
			String SSID = scanResult.getScanResult().SSID;
			WIFI_ENUM.WifiCipherType type = scanResult.getWifiCipherType();
			MessageStatic.wifiScanSSIDList.add(SSID);
			MessageStatic.wifiScanTypeList.add(type);
		}
	}

	@Override
	protected void init(View view) {
		// TODO Auto-generated method stub
		btnControlBack = (Button) view.findViewById(R.id.btn_control_back);
		btnControlBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// back to the device fragment
				Log.d(TAG, "back button is pressed");
				MyFragmentsActivityUI.switchFragment(FRAG_TYPE.DEVICE);
			}

		});

		spinnerWifi = (Spinner) view.findViewById(R.id.spinner_device_setting_wifi);
		spinnerWifi.setAdapter(adapter);

		bShowPasswordPressed = false;
		btnShowPassword = (Button) view.findViewById(R.id.btn_device_setting_show_password);
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

		edtPassword = (EditText) view.findViewById(R.id.edt_device_setting_show_password);
//		tvDeviceName = (TextView) view.findViewById(R.id.tv_device_setting_name);
//		tvDeviceName.setText(MessageStatic.settingDeviceName);

		btnStart = (Button) view.findViewById(R.id.btn_device_setting_start);
		btnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// startSetting();
				MessageStatic.device_ap_connected_password = edtPassword.getText().toString();
				int index = spinnerWifi.getSelectedItemPosition();
				MessageStatic.device_ap_connected_ssid = MessageStatic.wifiScanSSIDList.get(index);
				MessageStatic.device_ap_type = MessageStatic.wifiScanTypeList.get(index);
				UtilActivity.transferActivity(FragmentConfig.this.getActivity(),
						DeviceSettingProgressActivity.class, false);
			}

		});

	}
	
	private ArrayList<String> getData() {
		return MessageStatic.wifiScanSSIDList;
	}
}

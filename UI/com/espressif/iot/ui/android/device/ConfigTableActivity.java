package com.espressif.iot.ui.android.device;


import com.espressif.iot.R;
import com.espressif.iot.model.device.IOTActionEnum;
import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.oapi.OApiIntermediator;
import com.espressif.iot.thread.FixedThreadPool;
//import com.espressif.iot.ui.android.MyFragmentsActivity;
import com.espressif.iot.ui.android.UtilActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class ConfigTableActivity extends Activity {

	/**
	 * !!!NOTE: this is not a good method, it need improving in the future
	 */
	public static IOTDevice iotDevice;
	private static final String TAG = "ConfigTableActivity";

	private Button btnConfirm;
	private Button btnCancel;
	private EditText edtIOTName;
	private EditText edtStaSSID;
	private EditText edtStaPassword;
	private EditText edtSoftApSSID;
	private EditText editSoftApPassword;
	private RadioGroup rgSoftAPPasswordExist;

	private OApiIntermediator oApiIntermediator;
	private WifiAdmin mWifiAdmin;
	
	private static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		oApiIntermediator = OApiIntermediator.getInstance();
		mWifiAdmin = new WifiAdmin(this);
		setContentView(R.layout.config_tablelayout);
		initButtons();
		initEditTexts();
		UtilActivity.handlerUIPost(handler, "refresh", "refreshUI",
				ConfigTableActivity.this, ConfigTableActivity.class, null);
	}

	private void refreshUI() {
		edtIOTName.setText(iotDevice.getName());
		edtStaSSID.setText(iotDevice.getIOTSta().getSSID());
		edtStaPassword.setText(iotDevice.getIOTSta().getPassword());
		edtSoftApSSID.setText(iotDevice.getIOTSoftAP().getSSID());
		editSoftApPassword.setText(iotDevice.getIOTSoftAP().getPassword());
		if (iotDevice.getIOTSoftAP().getAuthmode().equals("OPEN")) {
			rgSoftAPPasswordExist.check(R.id.rb_config_Softap_password_no);
		} else {
			rgSoftAPPasswordExist.check(R.id.rb_config_Softap_password_yes);
		}
	}

	private void refresh() {
		iotDevice
				.executeAction(IOTActionEnum.IOT_ACTION_GET_STA_SOFTAP_CONFIGURE);
	}

	private void initEditTexts() {
		edtIOTName = (EditText) findViewById(R.id.edt_config_iot_name);
		edtStaSSID = (EditText) findViewById(R.id.edt_config_sta_ssid);
		edtStaPassword = (EditText) findViewById(R.id.edt_config_sta_password);
		edtSoftApSSID = (EditText) findViewById(R.id.edt_config_softap_ssid);
		editSoftApPassword = (EditText) findViewById(R.id.edt_config_softap_password);
	}

	/**
	 * process the src password to the wep password,
	 * 	for "\" should be "\\" and """ should be """
	 * 		"" should be embraced the src password
	 * e.g. !@\^"&* should be "!@\\^\"&*" 
	 * @param password
	 * @return
	 */
	private String procWepPassword(String password){
		// replace "\" with "\\"
		// replace """ with "\""
		String result0 = password.replaceAll("\\\\", "\\\\");
		String result1 = result0.replaceAll("\"", "\"\"");
//		return "\"" + result1 + "\"";
		return result1;
	}
	
	private void confirmAction() {
		// iotDevice.setName(edtIOTName.getText().toString());
		String staGateWay = iotDevice.getIOTSta().getGateWay();
		String staIp = iotDevice.getIOTSta().getIP();
		String staMask = iotDevice.getIOTSta().getMask();
		// String staPassword =iotDevice.getIOTSta().getPassword();
		String staPassword = edtStaPassword.getText().toString();
		// String staSSID =iotDevice.getIOTSta().getSSID();
		String staSSID = edtStaSSID.getText().toString();

		iotDevice.getIOTSta().setGateWay(staGateWay);
		iotDevice.getIOTSta().setIP(staIp);
		iotDevice.getIOTSta().setMask(staMask);
		
		/*
		// !!!NOTE WEP should be transferred
		{
			List<WifiScanResult> scanList = oApiIntermediator.scanAPsLANSyn(mWifiAdmin, "");
			for(WifiScanResult scan: scanList){
				// SSID is the same
				if(scan.getScanResult().SSID.equals(staSSID)){
					if(scan.getWifiCipherType()==WIFI_ENUM.WifiCipherType.WIFICIPHER_WEP){
						// WEP
						Logger.d(TAG, "WEP");
//						staPassword = procWepPassword(staPassword);
						Logger.e(TAG, "staPassword="+staPassword);
//						iotDevice.getIOTSta().setPassword(procWepPassword(staPassword));
						break;
					}
					else if(scan.getWifiCipherType()==WIFI_ENUM.WifiCipherType.WIFICIPHER_WPA){
						// WPA
						Logger.d(TAG, "WPA");
//						iotDevice.getIOTSta().setPassword(staPassword);
						break;
					}
				}
			}
		}
		*/
		
		iotDevice.getIOTSta().setPassword(staPassword);
		iotDevice.getIOTSta().setSSID(staSSID);
		iotDevice.executeAction(IOTActionEnum.IOT_ACTION_SET_STA_CONFIGURE);

		// String softAPAuthmode = iotDevice.getIOTSoftAP().getAuthmode();
		String softAPAuthmode = "UNSUPPORTED";
		if (rgSoftAPPasswordExist.getCheckedRadioButtonId() == R.id.rb_config_Softap_password_no) {
			softAPAuthmode = "OPEN";
		} else {
			softAPAuthmode = "WPAPSK/WPA2PSK";
		}
//		if (rgSoftAPPasswordExist.getCheckedRadioButtonId() == R.id.rb_config_Softap_password_no) {
//			Logger.d(TAG, "OPEN");
//			softAPAuthmode = "OPEN";
//		} else {
//			List<WifiScanResult> scanList = oApiIntermediator.scanAPsLANSyn(mWifiAdmin, "");
//			for(WifiScanResult scan: scanList){
//				// SSID is the same
//				if(scan.getScanResult().SSID.equals(staSSID)){
//					if(scan.getWifiCipherType()==WIFI_ENUM.WifiCipherType.WIFICIPHER_WEP){
//						// WEP
//						Logger.d(TAG, "WEP");
//						softAPAuthmode = "WEP";
//						iotDevice.getIOTSta().setPassword(procWepPassword(staPassword));
//						break;
//					}
//					else if(scan.getWifiCipherType()==WIFI_ENUM.WifiCipherType.WIFICIPHER_WPA){
//						// WPA
//						Logger.d(TAG, "WPA");
//						softAPAuthmode = "WPAPSK/WPA2PSK";
//						break;
//					}
//				}
//			}
//		}
		int softAPChannel = iotDevice.getIOTSoftAP().getChannel();
		String softAPGateWay = iotDevice.getIOTSoftAP().getGateWay();
		String softAPIP = iotDevice.getIOTSoftAP().getIP();
		String softAPMask = iotDevice.getIOTSoftAP().getMask();
		// String softAPSSID = iotDevice.getIOTSoftAP().getSSID();
		String softAPSSID = edtSoftApSSID.getText().toString();
		// String softAPPassword = iotDevice.getIOTSoftAP().getPassword();
		String softAPPassword = editSoftApPassword.getText().toString();

		iotDevice.getIOTSoftAP().setAuthmode(softAPAuthmode);
		iotDevice.getIOTSoftAP().setChannel(softAPChannel);
		iotDevice.getIOTSoftAP().setGateWay(softAPGateWay);
		iotDevice.getIOTSoftAP().setIP(softAPIP);
		iotDevice.getIOTSoftAP().setMask(softAPMask);
		iotDevice.getIOTSoftAP().setSSID(softAPSSID);
		iotDevice.getIOTSoftAP().setPassword(softAPPassword);
		iotDevice.executeAction(IOTActionEnum.IOT_ACTION_SET_SOFTAP_CONFIGURE);
	}

	private void confirmActionUI() {
//		UtilActivity.transferActivity(ConfigTableActivity.this,
//				MyFragmentsActivity.class, true);
	}

	private void initButtons() {

		rgSoftAPPasswordExist = (RadioGroup) findViewById(R.id.rg_config_Softap_is_password);

		btnConfirm = (Button) findViewById(R.id.btn_config_confirm);

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				UtilActivity.handlerUIPost(handler, "confirmAction",
//						"confirmActionUI", ConfigTableActivity.this,
//						ConfigTableActivity.class, null);
				FixedThreadPool.getInstance().execute(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						confirmAction();
					}
					
				});
//				UtilActivity.transferActivity(ConfigTableActivity.this,
//						FragmentsActivity.class, true);
				finish();
				
			}
		});

		btnCancel = (Button) findViewById(R.id.btn_config_cancel);

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				UtilActivity.transferActivity(ConfigTableActivity.this,
//						FragmentsActivity.class, true);
				finish();
			}

		});
	}
}

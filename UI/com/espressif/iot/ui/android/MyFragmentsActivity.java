package com.espressif.iot.ui.android;

import java.lang.ref.WeakReference;

import com.espressif.iot.constants.WIFI_ENUM;
import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.thread.single.SingleTaskWifiConnectAsyn;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

public class MyFragmentsActivity extends MyFragmentsActivityUI {

	private static final String TAG = "MyFragmentsActivity";
	
	private final Handler mHandler = new MyHandler(this);
	
	// leak the mHandler's instance
	public static Handler leakHandler;
	
	private WifiAdmin mWifiAdmin;
	
//	private SingleTaskWifiConnectAsyn wifiConnect;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		leakHandler = mHandler;
		mWifiAdmin = new WifiAdmin(this);
//		wifiConnect = SingleTaskWifiConnectAsyn.getInstance(mWifiAdmin);
	}

	static class MyHandler extends Handler {
		WeakReference<MyFragmentsActivity> mActivity;

		MyHandler(MyFragmentsActivity activity) {
			mActivity = new WeakReference<MyFragmentsActivity>(activity);
		}

		// get the WifiCipherType according to the String
		private WIFI_ENUM.WifiCipherType getType(String type) {
			if (type.equals("WIFICIPHER_WEP")) {
				return WIFI_ENUM.WifiCipherType.WIFICIPHER_WEP;
			} else if (type.equals("WIFICIPHER_WPA")) {
				return WIFI_ENUM.WifiCipherType.WIFICIPHER_WPA;
			} else if (type.equals("WIFICIPHER_NOPASS")) {
				return WIFI_ENUM.WifiCipherType.WIFICIPHER_NOPASS;
			} else {
				return WIFI_ENUM.WifiCipherType.WIFICIPHER_INVALID;
			}
		}

		@Override
		public void handleMessage(Message msg) {
			MyFragmentsActivity theActivity = mActivity.get();
//			String BSSID = msg.getData().getString("BSSID");
			String SSID;
			switch (msg.what) {
			case MessageCenter.CONSTANTS.MSG_POP_PASSWORD_SETTING:
				Log.d(TAG, "MSG_POP_PASSWORD_SETTING");
				SSID = msg.getData().getString("SSID");
				WIFI_ENUM.WifiCipherType type = getType(msg.getData()
						.getString("type"));
				theActivity.popPasswordSetting(SSID , type);
				break;
			case MessageCenter.CONSTANTS.MSG_WIFI_CONNECTED_SUCCEED:
				Log.d(TAG, "MSG_WIFI_CONNECTED_SUCCEED");
				SSID = msg.getData().getString("SSID");
				break;
			}
		}

	};

	// pop the wifi's password setting
	private void popPasswordSetting(final String SSID,
			final WIFI_ENUM.WifiCipherType type) {
		
		final EditText edtx_password = new EditText(this);
		edtx_password.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);

		// edtx_password.
		new AlertDialog.Builder(this)
				.setTitle(SSID + "[" + type + "]")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(edtx_password)
				.setPositiveButton("Confirm",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								String password = edtx_password.getText()
										.toString();
//								wifiConnect.connect(SSID, password, type);
								// Connect
								// connectAPAsyn(SSID, password, type);
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
							}

						})
						
				.setOnDismissListener(new DialogInterface.OnDismissListener(){

					@Override
					public void onDismiss(DialogInterface dialog) {
						// TODO Auto-generated method stub
					}
					
				}).show();
	}
}

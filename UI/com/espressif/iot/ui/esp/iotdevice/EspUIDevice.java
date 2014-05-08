package com.espressif.iot.ui.esp.iotdevice;

import java.util.List;

import com.espressif.iot.R;
import com.espressif.iot.constants.WIFI_ENUM;
import com.espressif.iot.db.device.IOTDeviceDBManager;
import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.model.device.IOTDevice.STATUS;
import com.espressif.iot.model.device.IOTDevice.TYPE;
import com.espressif.iot.model.internet.User;
import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.net.lan.wifi.WifiScanResult;
import com.espressif.iot.oapi.OApiIntermediator;
import com.espressif.iot.ui.android.MessageStatic;
import com.espressif.iot.ui.android.UtilActivity;
import com.espressif.iot.ui.android.device.DevicePlugControlInternetActivity;
import com.espressif.iot.ui.android.device.DevicePlugControlLocalActivity;
import com.espressif.iot.ui.android.device.DeviceSettingActivity;
import com.espressif.iot.ui.android.device.DeviceTemHumControlInternetActivity;
import com.espressif.iot.ui.android.device.FragmentDevice;
import com.espressif.iot.util.BSSIDUtil;
import com.espressif.iot.util.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EspUIDevice extends LinearLayout {

	private static final String TAG = "EspUIDevice";
	private IOTDevice mIOTDevice;
	private GestureDetector mGestureDetector;
	private Activity mActivity;
	private TextView mTextViewName;
	private TextView mTextViewStatus;
	private ImageView mImageViewStatus;
	private WifiAdmin mWifiAdmin;
	private String mDeviceName;
	// EspUIDevice's layout Parent
	private static LinearLayout layoutParent;
	private OApiIntermediator oApiIntermediator;
	private static IOTDeviceDBManager sIOTDeviceDBManager;
	
	public EspUIDevice(Context context, IOTDevice iotDevice, Activity activity) {
		super(context);
		this.mIOTDevice = iotDevice;
		this.mActivity = activity;
		mWifiAdmin = new WifiAdmin(getActivity());
		oApiIntermediator = OApiIntermediator.getInstance();
		mGestureDetector = new GestureDetector(context, new EspGestureDetector(
				this));
		Logger.d(TAG, "EspUIDevice()");
		View view = LayoutInflater.from(context).inflate(
				R.layout.esp_iot_device, this);
		layoutParent = FragmentDevice.mLayoutLeak;
		sIOTDeviceDBManager = IOTDeviceDBManager.getInstance(context);
		init(view);
	}

	public void setEspStatusNew() {
		mTextViewStatus.setText("未配置 ");
		mImageViewStatus.setBackgroundResource(R.drawable.device_status_new);
		MessageStatic.addIOTDevice(mIOTDevice);
	}

	public void setEspStatusLocal() {
		mTextViewStatus.setText("本地 "+mIOTDevice.getTypeStr());
		mImageViewStatus.setBackgroundResource(R.drawable.local);
		mImageViewStatus.clearAnimation();
	}

	public void setEspStatusInternet() {
		mTextViewStatus.setText("在线 "+mIOTDevice.getTypeStr());
		mImageViewStatus.setBackgroundResource(R.drawable.cdma);
	}

	public void setEspStatusOffline() {
		mTextViewStatus.setText("离线 "+mIOTDevice.getTypeStr());
		mImageViewStatus.setBackgroundResource(R.drawable.offline);
	}
	
	public void setEspStatusConnecting(){
		mTextViewStatus.setText("连接中...");
		/**
		 * we need a connecting picture here
		 */
		mImageViewStatus.setBackgroundResource(R.drawable.offline);
	}
	

	/**
	 * set the device's name, which displays in the UI
	 * 
	 * @param deviceName
	 *            the deviceName to be displayed
	 */
	public void setEspDeviceName(String deviceName) {
		this.mDeviceName = deviceName;
		mTextViewName.setText(deviceName);
	}

	private Activity getActivity() {
		return this.mActivity;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Logger.d(TAG, "onTouchEvent");
		return mGestureDetector.onTouchEvent(event);
	}

	private void init(View view) {
		mTextViewName = (TextView) view.findViewById(R.id.tv_esp_device_name);
		mTextViewStatus = (TextView) view
				.findViewById(R.id.tv_esp_device_status);
		mImageViewStatus = (ImageView) view
				.findViewById(R.id.img_esp_device_status);
	}

	private class EspGestureDetector extends SimpleOnGestureListener {

		private EspUIDevice mEspUIDevice;
		private static final String TAG = "ESPGestureDetector";
		private final int VerticalMinDistance = 100;

		private void createAlertDialog() {
			new AlertDialog.Builder(mEspUIDevice.getContext())
					.setTitle("删除设备")
					.setMessage("是否要删除该设备")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// delete from the UI
									layoutParent.removeView(mEspUIDevice);
									// delete from the db
									String BSSID = mEspUIDevice.mIOTDevice.getIOTAddress().getBSSID();
									sIOTDeviceDBManager.deleteDeviceByBSSID(BSSID,User.id);
								}
							}).setNegativeButton("取消", null).show();
		}

		public EspGestureDetector(EspUIDevice espUIDevice) {
			this.mEspUIDevice = espUIDevice;
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
		public boolean onSingleTapUp(MotionEvent e) {
			Logger.d(TAG, "onSingleTapUp() is called");
			STATUS status = mEspUIDevice.mIOTDevice.getStatus();
			switch(status){
			case INTERNET:
				switch(mEspUIDevice.mIOTDevice.getType()){
				case LIGHT:
					break;
				case PLUG:
					MessageStatic.currentIOTDevice = mEspUIDevice.mIOTDevice;
					UtilActivity.transferActivity(mEspUIDevice.getActivity(),
							DevicePlugControlInternetActivity.class, false);
					break;
				case TEMPERATURE:
					MessageStatic.currentIOTDevice = mEspUIDevice.mIOTDevice;
					DeviceTemHumControlInternetActivity.token = mEspUIDevice.mIOTDevice.getDeviceKey();
					Logger.e(TAG, "temperature:token:"+DeviceTemHumControlInternetActivity.token );
					UtilActivity.transferActivity(mEspUIDevice.getActivity(),
							DeviceTemHumControlInternetActivity.class, false);
					break;
				default:
					break;
				}
				break;
			case LOCAL:
				if(mEspUIDevice.mIOTDevice.getType()==TYPE.PLUG){
					MessageStatic.currentIOTDevice = mEspUIDevice.mIOTDevice;
					UtilActivity.transferActivity(mEspUIDevice.getActivity(),
						DevicePlugControlLocalActivity.class, false);
				}
				break;
			case NEW:
				scanWifi();
				MessageStatic.settingDeviceName = mEspUIDevice.mDeviceName;
				MessageStatic.clearIOTDeviceList();
				MessageStatic.addIOTDevice(mIOTDevice);
				MessageStatic.currentIOTDevice = mEspUIDevice.mIOTDevice;
				UtilActivity.transferActivity(mEspUIDevice.getActivity(),
						DeviceSettingActivity.class, false);
				break;
			case OFFLINE:
				break;
			case CONNECTING:
				break;
			default:
				break;
			}
			
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			Logger.d(TAG, "onLongPress() is called");
			STATUS status = mEspUIDevice.mIOTDevice.getStatus();
			switch(status){
			case CONNECTING:
				break;
			case INTERNET:
				createAlertDialog();
				break;
			case LOCAL:
				createAlertDialog();
				break;
			case NEW:
				break;
			case OFFLINE:
				createAlertDialog();
				break;
			default:
				break;
			}
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (e1.getX() - e2.getX() > VerticalMinDistance) {
				Logger.d(TAG, "Left onScroll");
				return true;
			} else if (e2.getX() - e1.getX() > VerticalMinDistance) {
				Logger.d(TAG, "Right onScroll");
				return true;
			}
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			Logger.d(TAG, "onShowPress() is called");
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return false;
		}
	}
}

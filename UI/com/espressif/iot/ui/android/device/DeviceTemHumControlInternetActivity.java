package com.espressif.iot.ui.android.device;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.espressif.iot.R;
import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.model.internet.IOTDeviceHelper;
import com.espressif.iot.model.internet.TemHumData;
import com.espressif.iot.ui.android.MessageStatic;
import com.espressif.iot.ui.android.UtilActivity;
import com.espressif.iot.ui.android.share.CreateQRImageActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class DeviceTemHumControlInternetActivity extends Activity{
	
	private PullToRefreshScrollView mEspUIRefreshableView;
	private LinearLayout mLayout;
	private List<TemHumData> mTemHumDataList;
	private volatile boolean mIsRunning;
	public static String token;
	private Button btnShare;
	private IOTDevice iotDevice;
	private static final String TAG = "DeviceTemHumControlInternetActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		iotDevice = MessageStatic.currentIOTDevice;
		setContentView(R.layout.activity_device_hum_tem_control);
		init();
	}
	
	private void share(){
		CreateQRImageActivity.shareKey = IOTDeviceHelper.genShareKey(iotDevice.getDeviceKey());
		if(CreateQRImageActivity.shareKey!=null){
			Log.d(TAG, "shareKey is: " + CreateQRImageActivity.shareKey);
		UtilActivity.transferActivity(
				DeviceTemHumControlInternetActivity.this,
				CreateQRImageActivity.class, false);
		}
		
	}
	
	private void initBtnShare(){
		btnShare = (Button) findViewById(R.id.btn_device_hum_tem_control_share);
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
		
		mEspUIRefreshableView = (PullToRefreshScrollView) findViewById(R.id.refreshable_device_hum_tem_control);

		mEspUIRefreshableView.setOnRefreshListener(new OnRefreshListener<ScrollView>(){
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				// TODO Auto-generated method stub
				if(!mIsRunning){
					mIsRunning = true;
					new GetDataTask1().execute();
				}
//				mEspUIRefreshableView.onRefreshComplete();
			}
			
		});
		
		mLayout = (LinearLayout) findViewById(R.id.linearlayout_device_hum_tem_control);
		mTemHumDataList = new CopyOnWriteArrayList<TemHumData>();
	}
	private class GetDataTask1 extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
//			scanAction();
			temHumAction();
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// Do some stuff here
			// Call onRefreshComplete when the list has been refreshed.
//			scanUI();
			temHumActionUI();
			mEspUIRefreshableView.onRefreshComplete();
			mIsRunning = false;
			super.onPostExecute(result);
		}
	}
	
	private void temHumAction(){
		mTemHumDataList.clear();
		mTemHumDataList = IOTDeviceHelper.getTemHumDataList(token);
	}
	private void temHumActionUI(){
		mLayout.removeAllViews();
		for(TemHumData temHumData : mTemHumDataList){
			TextView tv = new TextView(this);
			tv.setText("时间:  "+temHumData.getAt()+"," +
					"       温度:  "+temHumData.getX()+"°C," +
					"       湿度:  "+temHumData.getY()+"%\n");
			mLayout.addView(tv);
		}
	}
}

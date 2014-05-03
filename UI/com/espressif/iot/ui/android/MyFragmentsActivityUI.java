package com.espressif.iot.ui.android;


import com.espressif.iot.R;
import com.espressif.iot.ui.android.config.FragmentConfig;
import com.espressif.iot.ui.android.device.FragmentDevice;
import com.espressif.iot.ui.android.more.FragmentMore;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MyFragmentsActivityUI extends FragmentActivity {

	protected FragmentConfig mFragmentConfig;
	protected FragmentDevice mFragmentDevice;
	protected FragmentMore mFragmentMore;
	
	private RadioGroup bottomRg;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	@SuppressWarnings("unused")
	private RadioButton rbOne, rbTwo, rbThree;
	private static final String TAG = "FragmentsActivityUI";
	private int index = 0;
	public static final String PREFS_NAME = "MyPrefsFile";
	public static final int FRAG_DEVICE = 0;
	public static final int FRAG_CONFIG = 1;
	public static final int FRAG_MORE = 2;
	public static final int FRAG_USER = 3;
	public static MyFragmentsActivityUI leakThis;
	
	// whether it need refresh or not
	private boolean refresh;
	private void clearRefresh(){
		refresh = false;
	}
	private void setRefresh(){
		refresh = true;
	}
	
	public enum FRAG_TYPE{
		DEVICE,
		CONFIG,
		MORE,
		USER
	}
	
	/**
	 * record the status of the FragmentsActivity at present,which fragment is
	 * used current, before the apk is closed by user, e.g. FragmentConfig,
	 * FragmentDevice, FragmentMore and etc.
	 * 
	 * @see restoreFragmentsStatus()
	 */
	private void recordFragmentsStatus() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("index", index);
		editor.commit();
	}

	/**
	 * restore the fragment which is used before the apk is closed by user.
	 * 
	 * @see recordFragmentsStatus()
	 */
	private void restoreFragmentsStatus() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		index = settings.getInt("index", 0);
		mFragmentDevice = (FragmentDevice)fragmentManager.findFragmentById(R.id.fragement_device);
		mFragmentConfig = (FragmentConfig)fragmentManager.findFragmentById(R.id.fragement_config);
		mFragmentMore = (FragmentMore)fragmentManager.findFragmentById(R.id.fragement_more);
		fragmentTransaction = fragmentManager.beginTransaction()
				.hide(mFragmentDevice).hide(mFragmentConfig).hide(mFragmentMore);
		switch(index){
		case FRAG_DEVICE:
			fragmentTransaction.show(mFragmentDevice).commit();
			break;
		case FRAG_CONFIG:
			fragmentTransaction.show(mFragmentConfig).commit();
			break;
		case FRAG_MORE:
			fragmentTransaction.show(mFragmentMore).commit();
			break;
		}
		
		Log.d(TAG, "index=" + index);

		// check the index according to the saved value
		switch (index) {
		case FRAG_DEVICE:
			bottomRg.check(R.id.rbDevice);
			break;
		case FRAG_CONFIG:
			bottomRg.check(R.id.rbConfig);
			break;
		case FRAG_MORE:
			bottomRg.check(R.id.rbMore);
			break;
		}

	}

	@Override
	public void onStop() {
		super.onStop();
		recordFragmentsStatus();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		leakThis = this;
		this.setRefresh();
		// NOTE: it transmit the handler to AbsFragment,
		// 		 let subclass of AbsFragment could use it	

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_fragments);

		fragmentManager = getSupportFragmentManager();

		initFragmentIndicator();

		restoreFragmentsStatus();
	}

	public static void switchFragment(FRAG_TYPE fragType){
		switch(fragType){
		case DEVICE:
			leakThis.bottomRg.check(R.id.rbDevice);
			leakThis.clearRefresh();
			break;
		case CONFIG:
			leakThis.bottomRg.check(R.id.rbConfig);
			break;
		case MORE:
			leakThis.bottomRg.check(R.id.rbMore);
			break;
		case USER:
			break;
		default:
			break;
		}
	}
	
	private void initFragmentIndicator() {

		bottomRg = (RadioGroup) findViewById(R.id.rgBottom);
		rbOne = (RadioButton) findViewById(R.id.rbDevice);
		rbTwo = (RadioButton) findViewById(R.id.rbConfig);
		rbThree = (RadioButton) findViewById(R.id.rbMore);
		bottomRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				fragmentTransaction = fragmentManager.beginTransaction()
						.hide(mFragmentDevice).hide(mFragmentConfig)
						.hide(mFragmentMore);
				switch (checkedId) {
				case R.id.rbDevice:
					Log.d(TAG, "rbOne is clicked");
					fragmentTransaction.show(mFragmentDevice).commit();
					index = FRAG_DEVICE;
					// whether it need refreshing
					if(refresh)
						FragmentDevice.onResumeAlike();
					Log.d(TAG, "index = 0");
					break;
				case R.id.rbConfig:
					Log.d(TAG, "rbTwo is clicked");
					fragmentTransaction.show(mFragmentConfig).commit();
					index = FRAG_CONFIG;
					Log.d(TAG, "index = 1");
					break;
				case R.id.rbMore:
					Log.d(TAG, "rbThree is clicked");
					fragmentTransaction.show(mFragmentMore).commit();
					index = FRAG_MORE;
					Log.d(TAG, "index = 2");
					break;
				default:
					break;
				}
				
				MyFragmentsActivityUI.this.setRefresh();
			}
		});

	}
}

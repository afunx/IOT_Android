package com.espressif.iot.ui.android.login;

import org.apache.http.HttpStatus;

import com.espressif.iot.R;
import com.espressif.iot.model.internet.IOTDeviceHelper;
import com.espressif.iot.model.internet.LoginResponse;
import com.espressif.iot.model.internet.User;
import com.espressif.iot.ui.android.MyFragmentsActivity;
import com.espressif.iot.ui.android.UtilActivity;
import com.espressif.iot.util.Timer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private EditText mEdtEmail, mEdtPassword;
	private CheckBox mCbRememberPassword, cbAutoLogin;
	private Button mBtnLogin;
	private Button mBtnRegister;
	private ImageButton mImgBtnBack;
	private String mEmailStr, mPasswordStr;
	private SharedPreferences mSp;
	private ProgressDialog mProgressDialog;
//	private boolean mIsCanceled = false;
	private volatile boolean mIsFinished = false;
	
	private static final String EMAIL = "EMAIL";
	private static final String PASSWORD = "PASSWORD";

	private static final String REMEMBER_PASSWORD = "REMEMBER_PASSWORD";
	private static final String LOGIN_AUTO = "LOGIN_AUTO";
	private static final int MSG_LOGIN_SUC = 0;
	private static final int MSG_LOGIN_FAIL = 1;
	private static final int MSG_LOGIN_CANCEL = 2;
	// the login message from server
	private static String loginMessage;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case MSG_LOGIN_SUC:
				loginSucAction();
				break;
			case MSG_LOGIN_FAIL:
				loginFailAction();
				break;
			case MSG_LOGIN_CANCEL:
				loginCancelAction();
				break;
			}
		}
	};
	
	private void loginSucAction(){
		
		if(mIsFinished)
			return;
		
		mIsFinished = true;
		
		mProgressDialog.dismiss();
		// login succeed and remember mEdtPassword is true
		if (mCbRememberPassword.isChecked()) {
			// remember the account and mEdtPassword
			Editor editor = mSp.edit();
			editor.putString(EMAIL, mEmailStr);
			editor.putString(PASSWORD, mPasswordStr);
			editor.commit();
		}
		
		/**
		 * synchronized devices from server
		 */
		SynchronizeServerLocalHelper.init(this);
		SynchronizeServerLocalHelper.synchronize(User.token);
		
		/**
		 * change to the activity which is the main activity
		 */
		UtilActivity.transferActivity(this, MyFragmentsActivity.class, true);
		Toast.makeText(LoginActivity.this, loginMessage,
				Toast.LENGTH_SHORT).show();
	}

	private void loginFailAction() {
		if(mIsFinished)
			return;

		mProgressDialog.dismiss();
		Toast.makeText(LoginActivity.this, loginMessage, Toast.LENGTH_LONG)
				.show();
		mSp.edit().putBoolean(REMEMBER_PASSWORD, false).commit();
		mSp.edit().putBoolean(LOGIN_AUTO, false).commit();
	}
	private void loginCancelAction(){
		if(mIsFinished)
			return;
		mProgressDialog.dismiss();
	}
	
	private void sendMessage(int what){
		Message msg = Message.obtain();
		msg.what = what;
		mHandler.sendMessage(msg);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// remove the title
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		init();

	}

//	private volatile boolean login = true;
	
	private Thread loginTask = new Thread(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			login();
		}
		
	};
	
	
	
	// restore the preference according to the previous one
	private void restorePreference() {
		// check whether the mEdtPassword remembering is enabled
		if (mSp.getBoolean(REMEMBER_PASSWORD, false)) {
			// set the default state is remembered
			mCbRememberPassword.setChecked(true);
			mEdtEmail.setText(mSp.getString(EMAIL, ""));
			mEdtPassword.setText(mSp.getString(PASSWORD, ""));
			// check whether the auto login is enabled
			if (mSp.getBoolean(LOGIN_AUTO, false)) {
				// set the default state is auto login
				cbAutoLogin.setChecked(true);
				// change to the specified Activity
				/**
				 * change!!!
				 */
//				Util.Sleep(3000);
//				if(login)
				login();
				
//				Intent intent = new Intent(LoginActivity.this,
//						LogoActivity.class);
//				LoginActivity.this.startActivity(intent);
			}
		}
		
		/**
		 * add by afunx 2014-05-09
		 */
		mEdtEmail.setText(mSp.getString(EMAIL, ""));
	}
	
	private void saveEmail(){
		Editor editor = mSp.edit();
		editor.putString(EMAIL, mEmailStr);
		editor.commit();
	}

	private class LoginTask extends Thread{
		public boolean isCancel = false;
		public void setCancel(boolean isCancel){
			this.isCancel = isCancel;
		}
		public void run(){
			Timer timer = new Timer();
			timer.setMinTime(3000);
			timer.start();
			
			LoginResponse response = IOTDeviceHelper.getUserKey(mEmailStr, mPasswordStr);
			boolean suc = response.getStatus() == HttpStatus.SC_OK;
			String message = response.getMessage();
			loginMessage = message;
			
			timer.stop();
			
//			if (mIsCanceled&&suc) {
			if (isCancel) {
				sendMessage(MSG_LOGIN_CANCEL);
			} else {
//				if (!mIsFinished) {
					/**
					 * it should check from the server
					 */
					if (suc) {
//						mIsFinished = true;
						sendMessage(MSG_LOGIN_SUC);
					} else {
						sendMessage(MSG_LOGIN_FAIL);
					}
//				}
			}
		}
	}
	
	private void login(){
		mEmailStr = mEdtEmail.getText().toString();
		mPasswordStr = mEdtPassword.getText().toString();

		/**
		 * save the EMAIL
		 */
		saveEmail();
		
		final LoginTask loginTask = new LoginTask();
//		mIsCanceled = false;
//		mIsFinished = false;
		/**
		 * waiting some time, simulating the real login situation
		 */
//		mProgressDialog = ProgressDialog.show(LoginActivity.this,
//				"登陆中...", "请等待...");
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
		mProgressDialog.setTitle("登陆中...");
		mProgressDialog.setMessage("请等待...");
		mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
//				mIsCanceled = true;
				loginTask.setCancel(true);
				loginTask.interrupt();
			}
		
		});
		mProgressDialog.show();
		loginTask.start();
		/*
		new Thread() {
			public void run() {
				Timer timer = new Timer();
				timer.setMinTime(3000);
				timer.start();
				
				LoginResponse response = IOTDeviceHelper.getUserKey(mEmailStr, mPasswordStr);
				boolean suc = response.getStatus() == HttpStatus.SC_OK;
				String message = response.getMessage();
				loginMessage = message;
				
				timer.stop();
				
//				if (mIsCanceled&&suc) {
				if (suc) {
					sendMessage(MSG_LOGIN_CANCEL);
				} else {
//					if (!mIsFinished) {
						// it should check from the server
						if (suc) {
//							mIsFinished = true;
							sendMessage(MSG_LOGIN_SUC);
						} else {
							sendMessage(MSG_LOGIN_FAIL);
						}
//					}
				}
			}
		}.start();
		*/
	}
	
	private void init() {
		// set the instance
		mSp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		mEdtEmail = (EditText) findViewById(R.id.login_edt_account);
		mEdtPassword = (EditText) findViewById(R.id.login_edt_password);
		mCbRememberPassword = (CheckBox) findViewById(R.id.login_cb_password_remember);
		cbAutoLogin = (CheckBox) findViewById(R.id.login_cb_auto_login);
		mBtnLogin = (Button) findViewById(R.id.login_btn_login);
		mBtnRegister = (Button) findViewById(R.id.login_btn_register);
		mImgBtnBack = (ImageButton) findViewById(R.id.login_img_btn_back);
		
		restorePreference();

		// listen the login, the default account: liu, mEdtPassword: 123
		mBtnLogin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				login();
			}
		});
		
		mBtnRegister.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				LoginActivity.this.startActivity(intent);
			}
			
		});

		// listen the mEdtPassword remember event
		mCbRememberPassword
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (mCbRememberPassword.isChecked()) {
							mSp.edit().putBoolean(REMEMBER_PASSWORD, true)
									.commit();
						} else {
							mSp.edit().putBoolean(REMEMBER_PASSWORD, false)
									.commit();
							// forget mEdtPassword can't login auto
							cbAutoLogin.setChecked(false);
						}

					}
				});

		// listen the auto login event
		cbAutoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (cbAutoLogin.isChecked()) {
					mSp.edit().putBoolean(LOGIN_AUTO, true).commit();
					// auto login must remember the mEdtPassword
					mCbRememberPassword.setChecked(true);
				} else {
					mSp.edit().putBoolean(LOGIN_AUTO, false).commit();
				}
			}
		});

		mImgBtnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
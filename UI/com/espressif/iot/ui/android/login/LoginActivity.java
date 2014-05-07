package com.espressif.iot.ui.android.login;

import org.apache.http.HttpStatus;

import com.espressif.iot.R;
import com.espressif.iot.model.internet.IOTDeviceHelper;
import com.espressif.iot.model.internet.LoginResponse;
import com.espressif.iot.model.internet.User;
import com.espressif.iot.ui.android.MyFragmentsActivity;
import com.espressif.iot.ui.android.UtilActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

	private static final String EMAIL = "EMAIL";
	private static final String PASSWORD = "PASSWORD";

	private static final String REMEMBER_PASSWORD = "REMEMBER_PASSWORD";
	private static final String LOGIN_AUTO = "LOGIN_AUTO";
	private static final int MSG_LOGIN_SUC = 0;
	private static final int MSG_LOGIN_FAIL = 1;
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
			}
		}
	};
	
	private void loginSucAction(){
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
	private void loginFailAction(){
		mProgressDialog.dismiss();
		Toast.makeText(LoginActivity.this, loginMessage,
				Toast.LENGTH_LONG).show();
		mSp.edit().putBoolean(REMEMBER_PASSWORD, false).commit();
		mSp.edit().putBoolean(LOGIN_AUTO, false).commit();
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
				login();
//				Intent intent = new Intent(LoginActivity.this,
//						LogoActivity.class);
//				LoginActivity.this.startActivity(intent);
			}
		}
	}
	
	private void login(){
		mEmailStr = mEdtEmail.getText().toString();
		mPasswordStr = mEdtPassword.getText().toString();

		/**
		 * waiting some time, simulating the real login situation
		 */
		mProgressDialog = ProgressDialog.show(LoginActivity.this,
				"登陆中...", "请等待...");
		new Thread() {
			public void run() {
				LoginResponse response = IOTDeviceHelper.getUserKey(mEmailStr, mPasswordStr);
				boolean suc = response.getStatus() == HttpStatus.SC_OK;
				String message = response.getMessage();
				loginMessage = message;
				/**
				 * it should check from the server
				 */
				if (suc) {
					sendMessage(MSG_LOGIN_SUC);
				} else {
					sendMessage(MSG_LOGIN_FAIL);
				}
			}
		}.start();
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
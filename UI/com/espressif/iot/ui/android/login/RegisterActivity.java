package com.espressif.iot.ui.android.login;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;

import com.espressif.iot.R;
import com.espressif.iot.model.internet.IOTDeviceHelper;
import com.espressif.iot.model.internet.LoginResponse;
import com.espressif.iot.util.Logger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private static final String TAG = "RegisterActivity";
	
	private EditText mEdtAccount;
	private EditText mEdtPassword;
	private EditText mEdtPasswordAgain;
	private EditText mEdtMailBox;
	private ImageButton mImgBtnBack;
	private Button mBtnRegister;
	private static final int MSG_REGISTER_SUC = 0;
	private static final int MSG_REGISTER_FAIL = 1;
	private static final String ERR_PASSWORD_INVALID = "请输入相同的密码";
	private ProgressDialog mProgressDialog;
	// the register message from server
	private static String registerMessage;
	
	private Handler mHandler = new Handler(){
		
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case MSG_REGISTER_SUC:
				Logger.d(TAG, "MSG_REGISTER_SUC");
				registerSucAction();
				break;
			case MSG_REGISTER_FAIL:
				Logger.d(TAG, "MSG_REGISTER_FAIL");
				registerFailAction();
				break;
			}
		}
	};
	
	private void registerSucAction(){
		Logger.d(TAG, "registerSucAction");
		mProgressDialog.dismiss();
		Toast.makeText(RegisterActivity.this, "注册成功 "+registerMessage,
				Toast.LENGTH_SHORT).show();
		// go back to the login activity
		finish();
	}
	private void registerFailAction(){
		Logger.d(TAG, "registerFailAction");
		mProgressDialog.dismiss();
		Toast.makeText(RegisterActivity.this, "注册失败 "+registerMessage,
				Toast.LENGTH_LONG).show();
	}
	
	private void sendMessage(int what){
		Message msg = Message.obtain();
		msg.what = what;
		mHandler.sendMessage(msg);
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// remove the title
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		init();
	}
	
	private void passwordInvalidAction(){
		Toast.makeText(RegisterActivity.this, ERR_PASSWORD_INVALID,
				Toast.LENGTH_LONG).show();
	}
	
	private void init(){
		
		mEdtAccount = (EditText) findViewById(R.id.register_edt_account);
		mEdtPassword = (EditText) findViewById(R.id.register_edt_password);
		mEdtPasswordAgain = (EditText) findViewById(R.id.register_edt_password_again);
		mEdtMailBox = (EditText) findViewById(R.id.register_edt_mailbox);
		mImgBtnBack = (ImageButton) findViewById(R.id.register_img_btn_back);
		mBtnRegister = (Button) findViewById(R.id.register_btn_register);
		
		mBtnRegister.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String account = mEdtAccount.getText().toString();
				final String mailbox = mEdtMailBox.getText().toString();
				final String password = mEdtPassword.getText().toString();
				final String passwordAgain = mEdtPasswordAgain.getText().toString();
				// check whether the password and passwordAgain is the same
				if(!password.equals(passwordAgain)){
					passwordInvalidAction();
					return;
				}
				
				mProgressDialog = ProgressDialog.show(RegisterActivity.this,
						"注册中...", "请等待...");
				
				new Thread() {
					public void run() {
						LoginResponse response = IOTDeviceHelper.join(account, mailbox, password);
						registerMessage = response.getMessage();
						int status = response.getStatus();
						if(status == HttpStatus.SC_OK){
							sendMessage(MSG_REGISTER_SUC);
						}
						else{
							sendMessage(MSG_REGISTER_FAIL);
						}
						/**
						 * it should check from the server
						 */
//						registerMessage = "注册失败";
//						sendMessage(MSG_REGISTER_FAIL);
					}
				}.start();
			}
		});
		
		mImgBtnBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
	}

}

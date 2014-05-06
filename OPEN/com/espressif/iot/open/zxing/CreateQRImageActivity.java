package com.espressif.iot.open.zxing;

import com.espressif.iot.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class CreateQRImageActivity extends Activity{
	
	private ImageView imgCreateQr;
	private CreateQRImageTest mCreateQRImageTest = CreateQRImageTest.getInstance();
	private static final String TAG = "CreateQRImageActivity";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_qr_image);
		imgCreateQr = (ImageView) findViewById(R.id.img_create_qr);
		Bitmap bitmap = mCreateQRImageTest.createQRImage("www.baidu.com");
		imgCreateQr.setImageBitmap(bitmap);
		Log.d(TAG, "onCreate() finished");
	}
	
	
}

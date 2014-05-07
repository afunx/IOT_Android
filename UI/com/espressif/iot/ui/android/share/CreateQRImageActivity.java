package com.espressif.iot.ui.android.share;

import com.espressif.iot.R;
import com.espressif.iot.open.zxing.CreateQRImageHelper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class CreateQRImageActivity extends Activity{
	
	private ImageView imgCreateQr;
	private CreateQRImageHelper mCreateQRImageTest = CreateQRImageHelper.getInstance();
	private static final String TAG = "CreateQRImageActivity";
	public static String shareKey = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_create_qr_image);
		imgCreateQr = (ImageView) findViewById(R.id.img_create_qr);
		Bitmap bitmap = mCreateQRImageTest.createQRImage(shareKey);
		imgCreateQr.setImageBitmap(bitmap);
		Log.d(TAG, "onCreate() finished");
	}
	
	
}

package com.espressif.iot.ui.esp.iotgroup;

import com.espressif.iot.R;
import com.espressif.iot.model.container.IOTGroup;
import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.util.Logger;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class EspUIGroup extends LinearLayout{

	private static final String TAG = "EspUIGroup";
	
	private IOTGroup mIOTGroup;
	
	public EspUIGroup(Context context, IOTGroup iotGroup) {
		super(context);
		this.mIOTGroup = iotGroup;
		Logger.d(TAG, "EspUIGroup()");
		View view = LayoutInflater.from(context).inflate(R.layout.esp_iot_group, this);
	}
	
}

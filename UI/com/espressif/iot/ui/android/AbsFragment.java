package com.espressif.iot.ui.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;

public abstract class AbsFragment extends Fragment {

	public static Handler handler;

	protected abstract void init(View view);
	

	protected void showAlertDialog(String title, String message) {
		new AlertDialog.Builder(getActivity()).setTitle(title)
				.setMessage(message)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}

				}).show();
	}
}

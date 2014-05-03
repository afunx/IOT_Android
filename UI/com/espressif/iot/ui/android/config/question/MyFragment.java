package com.espressif.iot.ui.android.config.question;

import com.espressif.iot.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * It is used to build the Fragment as ViewPager's page
 * 
 * @author afunx
 * 
 */
public class MyFragment extends Fragment {

	int mNum; // number of the fragment

	public static MyFragment newInstance(int num) {
		MyFragment fragment = new MyFragment();
		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// here is a simple usage
		mNum = getArguments() != null ? getArguments().getInt("num") : 1;
//		if (mNum == 3) {
//			UtilActivity.transferActivity(getActivity(),
//					FragmentsActivity.class);
//			getActivity().finish();
//		}
	}

	/**
	 * show the picture in the full screen
	 * 
	 */
	private void showPictureFullScreen() {
		getActivity().getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**
	 * when loading the layout, it will be invoked
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.fragment_config_question_pager_list, null);
		ImageView iv = (ImageView) view.findViewById(R.id.iv_config_question_pager_list);
		switch (mNum) {
		case 0:
			iv.setBackgroundResource(R.drawable.answer0);
			break;
		case 1:
			iv.setBackgroundResource(R.drawable.answer1);
			break;
		case 2:
			iv.setBackgroundResource(R.drawable.answer2);
			break;
		}
		this.showPictureFullScreen();
		return view;
	}
}
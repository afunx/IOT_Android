package com.espressif.iot.ui.android.config.question;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * 
 * define the FragmentPageAdapter
 * 
 * @author afunx
 * 
 */
public class MyFragmentPageAdapter extends FragmentPagerAdapter {
	public MyFragmentPageAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public int getCount() {
		// how many pages in all
		return 3;
	}

	@Override
	public Fragment getItem(int position) {
		Log.e("getItem ", ""+position);
		switch (position) {
		case 0:
			return MyFragment.newInstance(position);
		case 1:
			return MyFragment.newInstance(position);
		case 2:
			return MyFragment.newInstance(position);
		case 3:
			return MyFragment.newInstance(position);
		default:
			return null;
		}
	}
}
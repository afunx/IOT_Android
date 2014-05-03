package com.espressif.iot.ui.android.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.espressif.iot.R;
import com.espressif.iot.ui.android.AbsFragment;

/**
 * it is used to store the older FragmentMore,
 * and the FragmentMore will be used to implement a demo via WAN 
 * 
 * @author afunx
 *
 */
public class FragmentMore extends AbsFragment {
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                    Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_more, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
    }
    @Override
	protected void init(View view) {
		// TODO Auto-generated method stub
	}
}

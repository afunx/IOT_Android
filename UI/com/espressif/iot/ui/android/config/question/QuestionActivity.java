package com.espressif.iot.ui.android.config.question;

import com.espressif.iot.R;

import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
public class QuestionActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private MyFragmentPageAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_question);
        mViewPager = (ViewPager) findViewById(R.id.vp_config_question);
                                                                                                                                                                                                                                 
        // before android 3.0, have to extends FragmentActivity and getSupportFragmentManager()
        // after android 3.0, extends Activity and getSupportFragmentManger is O.K.
        FragmentManager fm = getSupportFragmentManager();
        // initiate the definition adapter
        mAdapter =  new MyFragmentPageAdapter(fm);
        // bind the adapter
        mViewPager.setAdapter(mAdapter);
    }
}

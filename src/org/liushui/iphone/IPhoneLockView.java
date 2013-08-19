package org.liushui.iphone;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


//import com.aven.qqdemo.MyFragmentPagerAdapter;
//import com.aven.qqdemo.TestFragment;
//import com.demo.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class IPhoneLockView extends FrameLayout{
	private Context context;
	private TextView tvDate;
	
	private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private TextView tvHome,tvLock,tvCamera;
    //private ImageView lockImage,cameraImage;
    private int currIndex = 0;
    private Resources resources;
    
	public IPhoneLockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	protected void onFinishInflate() {
		super.onFinishInflate();
		findViews();
		//InitViewPager();
	}
    
    private void findViews() {
		tvDate = (TextView) findViewById(R.id.date);
	}
	//	private void InitViewPager() {
	//        mPager = (ViewPager) findViewById(R.id.viewpager);
	//        fragmentsList = new ArrayList<Fragment>();
	//        LayoutInflater mInflater = getLayoutInflater();
	//        View activityView = mInflater.inflate(R.layout.lay1, null);
	//
	//        Fragment lockFragment = TestFragment.newInstance("unlock");
	//        Fragment homeFragment = TestFragment.newInstance("home");
	//        Fragment cameraFragment=TestFragment.newInstance("camera");
	//
	//        fragmentsList.add(lockFragment);
	//        fragmentsList.add(homeFragment);
	//        fragmentsList.add(cameraFragment);
	//        
	//        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
	//        mPager.setCurrentItem(1);
	//        //mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	//    }	

}
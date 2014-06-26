package com.meiyanlock.android;

import java.util.ArrayList;

import com.meiyanlock.android.R;

import com.meiyanlock.android.MainActivity.MyOnPageChangeListener;

import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.CharacterStyle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.style.ForegroundColorSpan;

public class MainActivity extends FragmentActivity {
	private static final String TAG = "MeiYan";
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentsList;
	private String[] sHome;
	private String sCustom;
	private String sRandom;
	private int len;
	private int currIndex = 1;
	private int homeIndex = 0;

	private ImageView viewRightArrow;
	private ImageView viewLeftArrow;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Remove notification bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.lock_view);		
		//获取默认随机诗词
		sHome = (String[]) this.getResources().getStringArray(R.array.Summer);
		len = sHome.length;
		homeIndex = (int)(Math.random()*len);
		sRandom=sHome[homeIndex];
		//获取自定义诗词		
		if(SettingActivity.customText.equals(""))
			sCustom=sRandom;
		else 
			sCustom=SettingActivity.customText;
		Log.d(TAG, sCustom);
		//初始化Viewpager
		InitViewPager();
	}

	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.viewpager);

		fragmentsList = new ArrayList<Fragment>();
		UnlockFragment unlockFragment = new UnlockFragment();
		Fragment homeFragment = MainFragment.newString(sCustom);
		CameraFragment cameraFragment = new CameraFragment();

		fragmentsList.add(unlockFragment);
		fragmentsList.add(homeFragment);
		fragmentsList.add(cameraFragment);

		mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
		mPager.setCurrentItem(currIndex);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());

	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				unLock();
				break;
			default:
//				Intent cameraIntent = new Intent(
//						MediaStore.ACTION_IMAGE_CAPTURE);
//				startActivityForResult(cameraIntent, 1);
				unLock();
				break;
			}
			currIndex = arg0;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			viewRightArrow = (ImageView) findViewById(R.id.iv_arrow_right);
			viewLeftArrow = (ImageView) findViewById(R.id.iv_arrow_left);
			if (arg1>0.0 | arg2>0){
				viewRightArrow.setVisibility(0);//0:VISIBILITY 4:INVISIBILITY
				viewLeftArrow.setVisibility(4);
			}
			else{
				viewRightArrow.setVisibility(0);//
				viewLeftArrow.setVisibility(4);
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			viewRightArrow = (ImageView) findViewById(R.id.iv_arrow_right);
			viewLeftArrow = (ImageView) findViewById(R.id.iv_arrow_left);	
			if (arg0==1){
				viewRightArrow.setVisibility(0);//0:VISIBILITY 4:INVISIBILITY
				viewLeftArrow.setVisibility(4);
			}
			else{
				viewRightArrow.setVisibility(0);//
				viewLeftArrow.setVisibility(4);
			}
		}
	}

	public void unLock() {
		finish();
	}

}
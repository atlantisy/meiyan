package com.meiyanlock.android;

import java.util.ArrayList;

import com.meiyanlock.android.R;

import com.meiyanlock.android.LockActivity.MyOnPageChangeListener;

import com.meiyanlock.widget.PatternPassWordView;
import com.meiyanlock.widget.PatternPassWordView.OnCompleteListener;

import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.style.ForegroundColorSpan;

public class LockActivity extends FragmentActivity {
	private static final String TAG = "MeiYan";
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentsList;
	private String sCustom;
	private int currIndex = 1;
	
	public static final String PREFS = "lock_pref";//pref文件名
	public static final String VERSE = "verse";//锁屏方式pref值名称
	public static final String WALLPAPER = "wallpaper";//壁纸pref值名称	
	public static final String LOCKFLAG = "lockFlag";//锁屏方式pref值名称
	public static final String PWSETUP = "passWordSetUp";//九宫格是否设置pref值名称	
	
	private PatternPassWordView ppwv;
	private Toast toast;
	private ImageView viewRightArrow;
	private ImageView viewLeftArrow;

	
	private void showToast(CharSequence message) {
		if (null == toast) {
			toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}

		toast.show();
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);			
		//Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Remove notification bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_lock);
			
		//获取随机诗词美言
/*		String[] sArray = (String[]) this.getResources().getStringArray(R.array.Summer);
		int index = (int)(Math.random()*sArray.length);
		String str = sArray[index]; */
		//获取pref值			
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
		int wallpaperId = settings.getInt(WALLPAPER, R.drawable.wallpaper00);
		//设置壁纸
		FrameLayout lockLayout = (FrameLayout)findViewById(R.id.LockLayout);
		lockLayout.setBackgroundResource(wallpaperId);
		//设置美言
		sCustom = settings.getString(VERSE, "每时每刻 美妙美言");		
		sCustom = sCustom.trim();//去掉前后空格
		Log.d(TAG, sCustom);
		
		//初始化滑动解锁Viewpager，即锁屏方式1
		InitViewPager();
		//九宫手势解锁，即锁屏方式2
		ppwv = (PatternPassWordView) this.findViewById(R.id.mPatternPassWordView);
		ppwv.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(String mPassword) {
				// 如果密码正确,则进入主页面。
				if (ppwv.verifyPassword(mPassword)) {
					showToast("解锁成功！");
					unLock();
				} else {
					showToast("密码输入错误,请重新输入");
					ppwv.clearPassword();
				}
			}
		});
		
		//获取存储的pref数据
		SharedPreferences home_setting = getSharedPreferences(PREFS, 0);  
		int flag = home_setting.getInt(LOCKFLAG, 1);
		boolean setPassword = home_setting.getBoolean(PWSETUP, false);
		//控制锁屏方式的显示
		if(flag==2 & setPassword==true){			
			mPager.setVisibility(View.GONE);
			ppwv.setVisibility(View.VISIBLE);			
		}
		else{
			mPager.setVisibility(View.VISIBLE);
			ppwv.setVisibility(View.GONE);			
		}
	}

	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.viewpager);

		fragmentsList = new ArrayList<Fragment>();
		UnlockFragment unlockFragment = new UnlockFragment();
		Fragment homeFragment = VerseFragment.newString(sCustom);
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
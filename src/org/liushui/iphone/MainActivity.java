package org.liushui.iphone;

import java.util.ArrayList;

import org.liushui.iphone.MainActivity.MyOnPageChangeListener;


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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.text.style.ForegroundColorSpan;

public class MainActivity extends FragmentActivity{	
	private Handler handler = new Handler();
	private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private String sUnlock;//sCamera;
    private String[] sHome;    
    private int length;
    private int currIndex = 1;
    private int homeIndex = 0;
    private TextView tvSlideUnlock;
    
    boolean isRun = false;
    boolean isCalcuteTextSize = false;
    private int index = -1;
    private String text;
	private int len;
	
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lock_iphone_view2);
		sHome=(String[]) this.getResources().getStringArray(R.array.Spring);		
        sUnlock=(String) this.getResources().getString(R.string.right_slide_unlock);
        
        InitViewPager();
        //startIndicateAnimation();
	}
	
   	
//	public void onResume() {
//		startIndicateAnimation();
//	}
//	
//	public void onPause() {
//		stopIndicateAnimation();
//	}
//	
//	public void onUpdate() {
//		startIndicateAnimation();
//	}
	
	
	private void InitViewPager() {
			tvSlideUnlock = (TextView) findViewById(R.id.tv_hello);
	        mPager = (ViewPager) findViewById(R.id.viewpager);	        
	        fragmentsList = new ArrayList<Fragment>();
	
	        length=sHome.length;
	        homeIndex=(int)(Math.random()*length);
	        UnlockFragment unlockFragment = new UnlockFragment();
	        Fragment homeFragment = TestFragment.newInstance(sHome[homeIndex]);
	
	        fragmentsList.add(unlockFragment);
	        fragmentsList.add(homeFragment);
	        
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
				break;
			}
			currIndex = arg0;
		}

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }	
	
	private void startIndicateAnimation() {
		if (!isRun) {
			handler.postDelayed(task, 200);
		}
		isRun = true;
	}
	
	private void stopIndicateAnimation() {
		if (isRun) {
			handler.removeCallbacks(task);
		}
		isRun = false;
	}
	
	Runnable task = new Runnable() {

		public void run() {
			if (index == len) {
				index = -1;
			}
			text = sHome[homeIndex];
			len = text.length();
			SpannableString spannable = new SpannableString(text);
			CharacterStyle ss = null;
			if (index >= 0 && index < len - 0) {
				ss = new ForegroundColorSpan(Color.argb(0xff, 0xff, 0xff, 0xff));
				spannable.setSpan(ss, index + 0, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			if (!isCalcuteTextSize) {
				int size = 1;
				int width = tvSlideUnlock.getWidth() - tvSlideUnlock.getPaddingLeft() - tvSlideUnlock.getPaddingRight();
				Paint paint = tvSlideUnlock.getPaint();
				paint.setTextSize(size);
				while (paint.measureText(text) <= width) {
					size++;
					paint.setTextSize(size);
				}

//				WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//				DisplayMetrics dm = new DisplayMetrics();
//				windowManager.getDefaultDisplay().getMetrics(dm);
//				tvSlideUnlock.setTextSize((size - 1) / dm.density);
				tvSlideUnlock.setText(text);
			}
			tvSlideUnlock.setText(spannable);
			index++;
			handler.postDelayed(this, 100);
		}
	};
	
	public void unLock(){
		finish();
	}

}
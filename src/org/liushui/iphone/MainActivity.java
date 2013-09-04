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

	private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private String sUnlock;//sCamera;
    private String[] sHome;    
    private int length;
    private int currIndex = 1;
    private int homeIndex = 0;

	
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lock_iphone_view2);
		sHome=(String[]) this.getResources().getStringArray(R.array.Spring);		
        sUnlock=(String) this.getResources().getString(R.string.right_slide_unlock);
        
        InitViewPager();
	}	  		
	
	private void InitViewPager() {
			//tvSlideUnlock = (TextView) findViewById(R.id.tv_hello);
	        mPager = (ViewPager) findViewById(R.id.viewpager);	        
	        fragmentsList = new ArrayList<Fragment>();
	
	        length=sHome.length;
	        homeIndex=(int)(Math.random()*length);
	        UnlockFragment unlockFragment = new UnlockFragment();
	        Fragment homeFragment = MainFragment.newString(sHome[homeIndex]);
	
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
	
	public void unLock(){
		finish();
	}

}
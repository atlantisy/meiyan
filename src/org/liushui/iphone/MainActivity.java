package org.liushui.iphone;

import java.util.ArrayList;

import org.liushui.iphone.MainActivity.MyOnPageChangeListener;


import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

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
        
        //sCamera=(String) this.getResources().getString(R.string.left_slide_camera);
        
        InitViewPager();
	}
	
	public void onStop() {
		
		super.onStop();		
    } 
	
	public void onDestroy() { 		
		super.onDestroy(); 
    } 
	
	private void InitViewPager() {
	        mPager = (ViewPager) findViewById(R.id.viewpager);	        
	        fragmentsList = new ArrayList<Fragment>();
	
	        length=sHome.length;
	        homeIndex=(int)(Math.random()*length);
	        Fragment lockFragment = TestFragment.newInstance(sUnlock);
	        Fragment homeFragment = TestFragment.newInstance(sHome[homeIndex]);
	        //Fragment cameraFragment=TestFragment.newInstance(sCamera);
	
	        fragmentsList.add(lockFragment);
	        fragmentsList.add(homeFragment);
	        //fragmentsList.add(cameraFragment);
	        
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
//			case 2:
//				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
//				startActivityForResult(cameraIntent, 1);
//				unLock();
//				break;
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
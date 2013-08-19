package org.liushui.iphone;

import java.util.ArrayList;



import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

//public class MainActivity extends Activity{
//	IPhoneLockView root;
//
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.lock_iphone_view2);
//		root = (IPhoneLockView) findViewById(R.id.root);
//	}
//}

public class MainActivity extends FragmentActivity{
	private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private TextView tvHome,tvLock,tvCamera;
    //private ImageView lockImage,cameraImage;
    private int currIndex = 0;
    private Resources resources;
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lock_iphone_view2);
        resources = getResources();
        
        InitViewPager();
	}
	
	private void InitViewPager() {
	        mPager = (ViewPager) findViewById(R.id.viewpager);
	        fragmentsList = new ArrayList<Fragment>();
	        //LayoutInflater mInflater = getLayoutInflater();
	
	        Fragment lockFragment = TestFragment.newInstance("unlock");
	        Fragment homeFragment = TestFragment.newInstance("home");
	        Fragment cameraFragment=TestFragment.newInstance("camera");
	
	        fragmentsList.add(lockFragment);
	        fragmentsList.add(homeFragment);
	        fragmentsList.add(cameraFragment);
	        
	        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
	        mPager.setCurrentItem(1);
	    }	
}
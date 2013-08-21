package org.liushui.iphone;

import java.util.ArrayList;

import android.R.string;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.widget.TextView;

public class MainActivity extends FragmentActivity{
	private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private String sUnlock,sCamera;
    private String[] sHome;
    //private ImageView lockImage,cameraImage;
    private int currIndex = 1;
    private int homeIndex = 0;
    private Resources resources;
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lock_iphone_view2);
		sHome=(String[]) this.getResources().getStringArray(R.array.Spring); 
        sUnlock=(String) this.getResources().getString(R.string.right_slide_unlock);	        
        sCamera=(String) this.getResources().getString(R.string.left_slide_camera);
        
		resources = getResources();
        
        InitViewPager();
	}
	
	private void InitViewPager() {
	        mPager = (ViewPager) findViewById(R.id.viewpager);
	        
	        fragmentsList = new ArrayList<Fragment>();
	
	        Fragment lockFragment = TestFragment.newInstance(sUnlock);
	        Fragment homeFragment = TestFragment.newInstance(sHome[homeIndex]);
	        Fragment cameraFragment=TestFragment.newInstance(sCamera);
	
	        fragmentsList.add(lockFragment);
	        fragmentsList.add(homeFragment);
	        fragmentsList.add(cameraFragment);
	        
	        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
	        mPager.setCurrentItem(currIndex);
	    }	
}
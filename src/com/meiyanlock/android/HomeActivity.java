package com.meiyanlock.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		//设置按钮
		ImageButton setting_button = (ImageButton)findViewById(R.id.home_setting);
		setting_button.setOnClickListener(settingOnClickListener);
		
		//编辑美言按钮
		ImageButton text_button = (ImageButton)findViewById(R.id.home_text);
		text_button.setOnClickListener(textOnClickListener);
		
		//切换锁屏方式按钮
		final ImageButton lock_line_button = (ImageButton)findViewById(R.id.home_lock_line);
		final ImageButton lock_grid_button = (ImageButton)findViewById(R.id.home_lock_grid);
				
		lock_line_button.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				lock_line_button.setVisibility(View.GONE);
				lock_grid_button.setVisibility(View.VISIBLE);
			}
		});
		
		lock_grid_button.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				lock_grid_button.setVisibility(View.GONE);
				lock_line_button.setVisibility(View.VISIBLE);
			}
		});
	}

	//设置按钮点击事件
	private OnClickListener settingOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(HomeActivity.this, SettingActivity.class));  
		}
	};
	
	//编辑美言按钮点击事件
	private OnClickListener textOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(HomeActivity.this, TextEditActivity.class));  
		}
	};
	
}

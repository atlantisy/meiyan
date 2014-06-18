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
		
		//���ð�ť
		ImageButton setting_button = (ImageButton)findViewById(R.id.home_setting);
		setting_button.getBackground().setAlpha(0);
		setting_button.setOnClickListener(settingOnClickListener);
		
		//�༭���԰�ť
		ImageButton text_button = (ImageButton)findViewById(R.id.home_text);
//		text_button.getBackground().setAlpha(0);
		text_button.setOnClickListener(textOnClickListener);
		
		//�༭��ֽ��ť
		ImageButton wallpaper_button = (ImageButton)findViewById(R.id.home_image);
//		wallpaper_button.getBackground().setAlpha(0);
		wallpaper_button.setOnClickListener(wallpaperOnClickListener);
	}

	//���ð�ť����¼�
	private OnClickListener settingOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(HomeActivity.this, SettingActivity.class));  
		}
	};
	
	//�༭���԰�ť����¼�
	private OnClickListener textOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(HomeActivity.this, TextEditActivity.class));  
		}
	};
	
	//�༭��ֽ��ť����¼�
	private OnClickListener wallpaperOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(HomeActivity.this, WallpaperEditActivity.class));  
		}
	};
}

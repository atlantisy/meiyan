package cn.minelock.android;

import cn.minelock.android.SettingActivity.OnCheckedListener;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingMoreActivity extends Activity {

	private SharedPreferences prefs = null;
	public static final String PREFS = "lock_pref";// pref文件名
	public static final String STATUSBAR = "statusbar";
	public static final String VIBRATE = "vibrate";
	public static final String SHOWRIGHT = "showRight";	
	public static final String LEFTCAMERA = "leftCamera";
	public static final String SHOWPASSWORD = "showPassword";	
	public static final String PWCOLOR = "passwordColor";
	
	private CheckBox statusbar_checkbox = null;
	private CheckBox vibrate_checkbox = null;
	private CheckBox showright_checkbox = null;
	private CheckBox leftcamera_checkbox = null;
	private CheckBox showpassword_checkbox = null;
	private CheckBox colorpassword_checkbox = null;
	
	private boolean mStatusbar = false;
	private boolean mVibrate = true;
	private boolean mShowRight = true;
	private boolean mLeftCamera = false;
	private boolean mShowPassword = true;
	private boolean mColorPassword = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settingmore);
		// 获取保存的prefs数据
		prefs = getSharedPreferences(PREFS, 0);
		//prefs = PreferenceManager.getDefaultSharedPreferences(this);
		// 显示状态栏
		mStatusbar = prefs.getBoolean(STATUSBAR, false);		 
		statusbar_checkbox = (CheckBox) findViewById(R.id.statusbar_checkbox);
		statusbar_checkbox.setChecked(mStatusbar);
		statusbar_checkbox.setOnClickListener(new OnStatusbarListener());
		// 开启振动
		mVibrate = prefs.getBoolean(VIBRATE, true);		 
		vibrate_checkbox = (CheckBox) findViewById(R.id.vibrate_checkbox);
		vibrate_checkbox.setChecked(mVibrate);
		vibrate_checkbox.setOnClickListener(new OnVibrateListener());		
		// 显示右滑箭头
		mShowRight = prefs.getBoolean(SHOWRIGHT, true);		 
		showright_checkbox = (CheckBox) findViewById(R.id.showright_checkbox);
		showright_checkbox.setChecked(mShowRight);
		showright_checkbox.setOnClickListener(new OnShowRightListener());
		// 左滑相机
		mLeftCamera = prefs.getBoolean(LEFTCAMERA, false);		 
		leftcamera_checkbox = (CheckBox) findViewById(R.id.lefttocamera_checkbox);
		leftcamera_checkbox.setChecked(mLeftCamera);
		leftcamera_checkbox.setOnClickListener(new OnLeftCameraListener());
		// 显示手势密码轨迹
		mShowPassword = prefs.getBoolean(SHOWPASSWORD, true);		 
		showpassword_checkbox = (CheckBox) findViewById(R.id.showpassword_checkbox);
		showpassword_checkbox.setChecked(mShowPassword);
		showpassword_checkbox.setOnClickListener(new OnShowPasswordListener());
		// 显示彩色手势密码
		mColorPassword = prefs.getBoolean(PWCOLOR, false);	 
		colorpassword_checkbox = (CheckBox) findViewById(R.id.colorpassword_checkbox);
		colorpassword_checkbox.setChecked(mColorPassword);
		colorpassword_checkbox.setOnClickListener(new OnColorPasswordListener());		
		// 选择手势密码颜色
/*		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.PatternColorGroup);  	      
	    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
	    		    	
	        @Override  
	        public void onCheckedChanged(RadioGroup group, int checkedId){	
	            // TODO Auto-generated method stub  	            
	    		RadioButton alpha = (RadioButton) findViewById(R.id.PatternAlpha);  
	    		RadioButton red = (RadioButton) findViewById(R.id.PatternRed);  
	    		RadioButton yellow = (RadioButton) findViewById(R.id.PatternYellow);  
	    		RadioButton green = (RadioButton) findViewById(R.id.PatternGreen);  
	    		RadioButton blue = (RadioButton) findViewById(R.id.PatternBlue);
	        	int color = 1;
	    		if(checkedId == alpha.getId()){  
	    			color = 1;
	        	}  
	        	else if(checkedId == red.getId()){
	        		color = 2; 
	        	}
	        	else if(checkedId == yellow.getId()){
	        		color = 3; 
	        	}
	        	else if(checkedId == green.getId()){
	        		color = 4;
	        	}
	        	else if(checkedId == blue.getId()){
	        		color = 5; 
	        	}
				// 将颜色值存入pref中
				SharedPreferences.Editor editor = prefs.edit();						
				editor.putInt(PWCOLOR, color);
				editor.commit();
	        }  
	    });*/
		// 返回按钮
		ImageButton return_btn = (ImageButton) findViewById(R.id.moresetting_return);
		return_btn.setOnClickListener(returnOnClickListener);

	}
	
	// 显示状态栏
	class OnStatusbarListener implements OnClickListener {
		public void onClick(View v) {
			mStatusbar = statusbar_checkbox.isChecked();
			//将开关check值存入pref中
			SharedPreferences.Editor editor = prefs.edit();						
			editor.putBoolean(STATUSBAR, mStatusbar);
			editor.commit();
		}
	}	
	// 开启振动
	class OnVibrateListener implements OnClickListener {
		public void onClick(View v) {
			mVibrate = vibrate_checkbox.isChecked();
			//将开关check值存入pref中
			SharedPreferences.Editor editor = prefs.edit();						
			editor.putBoolean(VIBRATE, mVibrate);
			editor.commit();
		}
	}	
	// 显示右滑箭头
	class OnShowRightListener implements OnClickListener {
		public void onClick(View v) {
			mShowRight = showright_checkbox.isChecked();
			//将开关check值存入pref中
			SharedPreferences.Editor editor = prefs.edit();						
			editor.putBoolean(SHOWRIGHT, mShowRight);
			editor.commit();
		}
	}
	// 左滑相机
	class OnLeftCameraListener implements OnClickListener {
		public void onClick(View v) {
			mLeftCamera = leftcamera_checkbox.isChecked();
			//将开关check值存入pref中
			SharedPreferences.Editor editor = prefs.edit();						
			editor.putBoolean(LEFTCAMERA, mLeftCamera);
			editor.commit();
		}
	}
	// 显示手势密码轨迹
	class OnShowPasswordListener implements OnClickListener {
		public void onClick(View v) {
			mShowPassword = showpassword_checkbox.isChecked();
			//将开关check值存入pref中
			SharedPreferences.Editor editor = prefs.edit();						
			editor.putBoolean(SHOWPASSWORD, mShowPassword);
			editor.commit();
		}
	}
	// 显示多彩手势密码
	class OnColorPasswordListener implements OnClickListener {
		public void onClick(View v) {
			mColorPassword = colorpassword_checkbox.isChecked();
			//将开关check值存入pref中
			SharedPreferences.Editor editor = prefs.edit();						
			editor.putBoolean(PWCOLOR, mColorPassword);
			editor.commit();
		}
	}	
	// 返回按钮
	private OnClickListener returnOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
/*			startActivity(new Intent(SettingMoreActivity.this, SettingActivity.class));
			overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);*/
			finish();
		}
	};
}

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

public class SettingMoreActivity extends Activity {

	private SharedPreferences prefs = null;
	public static final String PREFS = "lock_pref";// pref文件名
	public static final String SHOWRIGHT = "showRight";	
	public static final String LEFTCAMERA = "leftCamera";
	public static final String SHOWPASSWORD = "showPassword";	
	
	private CheckBox showright_checkbox = null;
	private CheckBox leftcamera_checkbox = null;
	private CheckBox showpassword_checkbox = null;
	
	private boolean mShowRight = true;
	private boolean mLeftCamera = true;
	private boolean mShowPassword = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settingmore);
		// 获取保存的prefs数据
		prefs = getSharedPreferences(PREFS, 0);
		//prefs = PreferenceManager.getDefaultSharedPreferences(this);
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
		// 返回按钮
		ImageButton return_btn = (ImageButton) findViewById(R.id.moresetting_return);
		return_btn.setOnClickListener(returnOnClickListener);
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
	// 返回按钮
	private OnClickListener returnOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(SettingMoreActivity.this, SettingActivity.class));
			overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
			//finish();
		}
	};
}

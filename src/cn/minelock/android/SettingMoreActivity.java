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
	public static final String PREFS = "lock_pref";// pref�ļ���
	public static final String STATUSBAR = "statusbar";
	public static final String SHOWRIGHT = "showRight";	
	public static final String LEFTCAMERA = "leftCamera";
	public static final String SHOWPASSWORD = "showPassword";	
	
	private CheckBox statusbar_checkbox = null;
	private CheckBox showright_checkbox = null;
	private CheckBox leftcamera_checkbox = null;
	private CheckBox showpassword_checkbox = null;
	
	private boolean mStatusbar = false;
	private boolean mShowRight = true;
	private boolean mLeftCamera = false;
	private boolean mShowPassword = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settingmore);
		// ��ȡ�����prefs����
		prefs = getSharedPreferences(PREFS, 0);
		//prefs = PreferenceManager.getDefaultSharedPreferences(this);
		// ��ʾ״̬��
		mStatusbar = prefs.getBoolean(STATUSBAR, false);		 
		statusbar_checkbox = (CheckBox) findViewById(R.id.statusbar_checkbox);
		statusbar_checkbox.setChecked(mStatusbar);
		statusbar_checkbox.setOnClickListener(new OnStatusbarListener());
		// ��ʾ�һ���ͷ
		mShowRight = prefs.getBoolean(SHOWRIGHT, true);		 
		showright_checkbox = (CheckBox) findViewById(R.id.showright_checkbox);
		showright_checkbox.setChecked(mShowRight);
		showright_checkbox.setOnClickListener(new OnShowRightListener());
		// �����
		mLeftCamera = prefs.getBoolean(LEFTCAMERA, false);		 
		leftcamera_checkbox = (CheckBox) findViewById(R.id.lefttocamera_checkbox);
		leftcamera_checkbox.setChecked(mLeftCamera);
		leftcamera_checkbox.setOnClickListener(new OnLeftCameraListener());
		// ��ʾ��������켣
		mShowPassword = prefs.getBoolean(SHOWPASSWORD, true);		 
		showpassword_checkbox = (CheckBox) findViewById(R.id.showpassword_checkbox);
		showpassword_checkbox.setChecked(mShowPassword);
		showpassword_checkbox.setOnClickListener(new OnShowPasswordListener());
		// ���ذ�ť
		ImageButton return_btn = (ImageButton) findViewById(R.id.moresetting_return);
		return_btn.setOnClickListener(returnOnClickListener);
	}
	
	// ��ʾ״̬��
	class OnStatusbarListener implements OnClickListener {
		public void onClick(View v) {
			mStatusbar = statusbar_checkbox.isChecked();
			//������checkֵ����pref��
			SharedPreferences.Editor editor = prefs.edit();						
			editor.putBoolean(STATUSBAR, mStatusbar);
			editor.commit();
		}
	}	
	// ��ʾ�һ���ͷ
	class OnShowRightListener implements OnClickListener {
		public void onClick(View v) {
			mShowRight = showright_checkbox.isChecked();
			//������checkֵ����pref��
			SharedPreferences.Editor editor = prefs.edit();						
			editor.putBoolean(SHOWRIGHT, mShowRight);
			editor.commit();
		}
	}
	// �����
	class OnLeftCameraListener implements OnClickListener {
		public void onClick(View v) {
			mLeftCamera = leftcamera_checkbox.isChecked();
			//������checkֵ����pref��
			SharedPreferences.Editor editor = prefs.edit();						
			editor.putBoolean(LEFTCAMERA, mLeftCamera);
			editor.commit();
		}
	}
	// ��ʾ��������켣
	class OnShowPasswordListener implements OnClickListener {
		public void onClick(View v) {
			mShowPassword = showpassword_checkbox.isChecked();
			//������checkֵ����pref��
			SharedPreferences.Editor editor = prefs.edit();						
			editor.putBoolean(SHOWPASSWORD, mShowPassword);
			editor.commit();
		}
	}	
	// ���ذ�ť
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
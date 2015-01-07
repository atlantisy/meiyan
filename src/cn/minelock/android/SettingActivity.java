package cn.minelock.android;

import java.util.ArrayList;

import cn.minelock.android.MyLockScreenService;

import cn.minelock.android.R;
import cn.minelock.util.StringUtil;
import cn.minelock.widget.SwitchButton;
import cn.minelock.widget.SwitchButton.OnChangeListener;


import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.style.ForegroundColorSpan;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingActivity extends Activity  implements OnClickListener{

	private CheckBox lock_checkbox = null;
	private SwitchButton lock_switchbtn = null;
	
	static public String customText = "";

	//private final String LOCK_VERSE = "verse";
	
	private SharedPreferences prefs = null;
	private final String LOCK_SWITCH = "lock_screen_switch";
	private final String LOCK_STATUS = "lock_status";

	private boolean mLockStatus = false;	
	private boolean mIsLockScreenOn = true;
	//private MyServiceConnection myServiceConnection;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		// ��ȡ�����prefs����
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mIsLockScreenOn = prefs.getBoolean(LOCK_SWITCH, true);
		// ��������checkbox
		lock_checkbox = (CheckBox) findViewById(R.id.lock_checkbox);
		lock_checkbox.setChecked(mIsLockScreenOn);
		lock_checkbox.setOnClickListener(new OnCheckedListener());	
		// ��ʼ��������
		Button initialguide_btn = (Button) findViewById(R.id.setting_initialguide);
		initialguide_btn.setOnClickListener(this);
		// ���þŹ����밴ť
		Button setpassword_btn = (Button) findViewById(R.id.setting_setpassword);
		setpassword_btn.setOnClickListener(setPasswordOnClickListener);	
		// ���ڰ�ť
		Button about_btn = (Button) findViewById(R.id.setting_about);
		about_btn.setOnClickListener(aboutOnClickListener);	
		// ���ذ�ť
		ImageButton return_btn = (ImageButton) findViewById(R.id.setting_return);
		return_btn.setOnClickListener(returnOnClickListener);
		// �˳���ť
		Button exit_btn = (Button) findViewById(R.id.setting_exit);
		exit_btn.setOnClickListener(exitOnClickListener);
		// ���С��ʾ
/*		String[] tips = getResources().getStringArray(R.array.tips);
		int random = (int)(Math.random()*tips.length);
		TextView tipView = (TextView)findViewById(R.id.setting_tips);
		tipView.setText("ע: "+tips[random]);*/
		
		//myServiceConnection = new MyServiceConnection();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.setting_initialguide:
			//EnableSystemKeyguard(false);
			//StringUtil.showToast(this, "���óɹ�",  Toast.LENGTH_SHORT);
			startActivity(new Intent(SettingActivity.this, InitialGuideActivity.class));
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		}
	}
	// ���ڰ�ť
	private OnClickListener aboutOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(SettingActivity.this, AboutActivity.class));
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			//finish();
		}
	};
	// ���þŹ����밴ť
	private OnClickListener setPasswordOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(SettingActivity.this, SetPasswordActivity.class));
			//finish();
		}
	};
	// ���ذ�ť
	private OnClickListener returnOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(SettingActivity.this, HomeActivity.class));
			overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
			//finish();
		}
	};
	// �˳���ť
	private OnClickListener exitOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			finish();
			
			//android.os.Process.killProcess(android.os.Process.myPid());
			//System.exit(0);
			
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i);
		}
	};
	
	// ��������
	class OnCheckedListener implements OnClickListener {
		public void onClick(View v) {
			// TODO
			mIsLockScreenOn = lock_checkbox.isChecked();
			//��������
			if (mIsLockScreenOn){
				// keep on disabling the system Keyguard
				EnableSystemKeyguard(false);
				mLockStatus = false;
			}
			else {
				// recover original Keyguard
				EnableSystemKeyguard(true);
				mLockStatus = true;
			}
			//����������checkֵ����pref��
			SharedPreferences.Editor editor = prefs.edit();						
			editor.putBoolean(LOCK_SWITCH, mIsLockScreenOn);
			editor.putBoolean(LOCK_STATUS, mLockStatus);
			editor.commit();
		}
	}

	/**/
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//startService(new Intent(this, MyLockScreenService.class));
		
		//��������
/*		if (mIsLockScreenOn){
			//��������
			startService(new Intent(this, MyLockScreenService.class));
			
			//EnableSystemKeyguard(false);
		}
		else {
			stopService(new Intent(this, MyLockScreenService.class));

			//EnableSystemKeyguard(true);
		}*/
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//��������		
		if (mIsLockScreenOn){
			//��������			
			startService(new Intent(this, MyLockScreenService.class));
			//bindService(new Intent(this, MyLockScreenService.class),myServiceConnection ,Context.BIND_AUTO_CREATE);
			
			EnableSystemKeyguard(false);
		}
		else {
			stopService(new Intent(this, MyLockScreenService.class));
			//unbindService(myServiceConnection);
			
			//EnableSystemKeyguard(true);
		}
	}

	void EnableSystemKeyguard(boolean bEnable) {
		KeyguardManager mKeyguardManager = null;
		KeyguardLock mKeyguardLock = null;

		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		mKeyguardLock = mKeyguardManager.newKeyguardLock("MineLock");
		if (bEnable)
			mKeyguardLock.reenableKeyguard();
		else
			mKeyguardLock.disableKeyguard();
	}

}
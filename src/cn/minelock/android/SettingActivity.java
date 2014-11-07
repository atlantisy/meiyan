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
	
	private boolean mIsLockScreenOn = true;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		// 获取保存的prefs数据
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mIsLockScreenOn = prefs.getBoolean(LOCK_SWITCH, true);

		// 锁屏开关switch
/*		lock_switchbtn = (SwitchButton) findViewById(R.id.lock_switchbtn);
		lock_switchbtn.setSwitch(mIsLockScreenOn);
		lock_switchbtn.setOnChangeListener(new OnChangeListener() {  
              
	            @Override  
	            public void onChange(SwitchButton sb, boolean state) {  
	                // TODO Auto-generated method stub
	    			mIsLockScreenOn = state;
	    			//启动锁屏
	    			if (mIsLockScreenOn){
	    				// keep on disabling the system Keyguard
	    				EnableSystemKeyguard(false);
	    			}
	    			else {
	    				// recover original Keyguard
	    				EnableSystemKeyguard(true);
	    			}
	    			//将锁屏开关check值存入pref中
	    			SharedPreferences.Editor editor = prefs.edit();
	    			editor.putBoolean(LOCK_SWITCH, mIsLockScreenOn);
	    			editor.commit();
	            }  
	        });*/
		// 锁屏开关checkbox
		lock_checkbox = (CheckBox) findViewById(R.id.lock_checkbox);
		lock_checkbox.setChecked(mIsLockScreenOn);
		lock_checkbox.setOnClickListener(new OnCheckedListener());	
		// 关闭系统默认锁屏
		Button closedefaultlock_btn = (Button) findViewById(R.id.setting_closedefaultlock);
		closedefaultlock_btn.setOnClickListener(this);
		// 设置九宫密码按钮
		Button setpassword_btn = (Button) findViewById(R.id.setting_setpassword);
		setpassword_btn.setOnClickListener(setPasswordOnClickListener);	
		// 返回按钮
		ImageButton return_btn = (ImageButton) findViewById(R.id.setting_return);
		return_btn.setOnClickListener(returnOnClickListener);
		// 退出按钮
		Button exit_btn = (Button) findViewById(R.id.setting_exit);
		exit_btn.setOnClickListener(exitOnClickListener);
		
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.setting_closedefaultlock:
			EnableSystemKeyguard(false);
			StringUtil.showToast(this, "已关闭",  Toast.LENGTH_SHORT);
			break;
		}
	}
	// 设置九宫密码按钮
	private OnClickListener setPasswordOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(SettingActivity.this, SetPasswordActivity.class));
			//finish();
		}
	};
	// 返回按钮
	private OnClickListener returnOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(SettingActivity.this, HomeActivity.class));
			//overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
			//overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
			//finish();
		}
	};
	// 退出按钮
	private OnClickListener exitOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			//finish();
			
			//android.os.Process.killProcess(android.os.Process.myPid());
			//System.exit(0);
			
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i);
		}
	};
	
	// 锁屏开关
	class OnCheckedListener implements OnClickListener {
		public void onClick(View v) {
			// TODO
			mIsLockScreenOn = lock_checkbox.isChecked();
			//启动锁屏
			if (mIsLockScreenOn){
				// keep on disabling the system Keyguard
				EnableSystemKeyguard(false);
			}
			else {
				// recover original Keyguard
				EnableSystemKeyguard(true);
			}
			//将锁屏开关check值存入pref中
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean(LOCK_SWITCH, mIsLockScreenOn);
			editor.commit();
		}
	}

	/**/
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//startService(new Intent(this, MyLockScreenService.class));
		
		//启动锁屏
/*		if (mIsLockScreenOn){
			// keep on disabling the system Keyguard
			//启动锁屏
			startService(new Intent(this, MyLockScreenService.class));
			//EnableSystemKeyguard(false);
		}
		else {
			stopService(new Intent(this, MyLockScreenService.class));
			// recover original Keyguard
			//EnableSystemKeyguard(true);
		}*/
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//启动锁屏
		if (mIsLockScreenOn){
			// keep on disabling the system Keyguard
			//启动锁屏
			startService(new Intent(this, MyLockScreenService.class));
			EnableSystemKeyguard(false);
		}
		else {
			stopService(new Intent(this, MyLockScreenService.class));
			// recover original Keyguard
			EnableSystemKeyguard(true);
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
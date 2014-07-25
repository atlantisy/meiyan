package com.meiyanlock.android;

import java.util.ArrayList;

import com.meiyanlock.android.R;

import com.meiyanlock.android.MyLockScreenService;

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

public class SettingActivity extends Activity {

	private CheckBox lock_checkbox = null;
	private SharedPreferences prefs = null;
	static public String customText = "";

	private final String LOCK_VERSE = "verse";
	private final String LOCK_SWITCH = "lock_screen_switch";
	
	private boolean mIsLockScreenOn = true;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		// 获取保存的prefs数据
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mIsLockScreenOn = prefs.getBoolean(LOCK_SWITCH, true);

		// 锁屏开关
		lock_checkbox = (CheckBox) findViewById(R.id.lock_switch);
		lock_checkbox.setChecked(mIsLockScreenOn);
		lock_checkbox.setOnClickListener(new OnCheckedListener());

		// 返回按钮
		ImageButton return_button = (ImageButton) findViewById(R.id.setting_return);
		return_button.setOnClickListener(returnOnClickListener);

/*		edittext = (EditText) findViewById(R.id.editText_custom);
		sp = getPreferences(MODE_PRIVATE);
		String rs = sp.getString(SPF_KEY, null);
		if (rs != null) {
			edittext.setText(rs);
		}*/

		EnableSystemKeyguard(false);
	}

	private OnClickListener returnOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(SettingActivity.this, HomeActivity.class));
		}
	};

	class OnCheckedListener implements OnClickListener {
		public void onClick(View v) {
			// TODO
			mIsLockScreenOn = lock_checkbox.isChecked();
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
		//启动锁屏
		startService(new Intent(this, MyLockScreenService.class));
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		if (mIsLockScreenOn)
			// keep on disabling the system Keyguard
			EnableSystemKeyguard(false);
		else {
			stopService(new Intent(this, MyLockScreenService.class));
			// recover original Keyguard
			EnableSystemKeyguard(true);
		}

		// save the setting before leaving.
/*		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(this).edit();
		editor.putBoolean(LOCK_SCREEN_ON_OFF, mIsLockScreenOn);
		editor.commit();*/

	}

	void EnableSystemKeyguard(boolean bEnable) {
		KeyguardManager mKeyguardManager = null;
		KeyguardLock mKeyguardLock = null;

		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		mKeyguardLock = mKeyguardManager.newKeyguardLock("");
		if (bEnable)
			mKeyguardLock.reenableKeyguard();
		else
			mKeyguardLock.disableKeyguard();
	}

}
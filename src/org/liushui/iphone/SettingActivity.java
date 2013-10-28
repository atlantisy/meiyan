package org.liushui.iphone;

import java.util.ArrayList;

import org.liushui.iphone.MyLockScreenService;

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

	private CheckBox checkbox = null;
	private EditText edittext = null;
	private SharedPreferences sp = null;
	static public String customText = "";

	private final String SPF_KEY = "spf";
	
	private final String LOCK_SCREEN_ON_OFF = "lock_screen_on_off";
	private boolean mIsLockScreenOn;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
		// read saved setting.
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);		
		mIsLockScreenOn = prefs.getBoolean(LOCK_SCREEN_ON_OFF, false);
		
		checkbox = (CheckBox) findViewById(R.id.checkBox_meiyan);
		checkbox.setChecked(mIsLockScreenOn);
		checkbox.setOnClickListener(new OnCheckedListener());

		edittext = (EditText) findViewById(R.id.editText_custom);
		sp = getPreferences(MODE_PRIVATE);
		String rs = sp.getString(SPF_KEY, null);
		if (rs != null) {
			edittext.setText(rs);
		}
		
		EnableSystemKeyguard(false);
	}

	class OnCheckedListener implements OnClickListener {
		public void onClick(View v) {
			// TODO
			//Intent intent = new Intent();
			//intent.setClass(SettingActivity.this, MainActivity.class);
			SharedPreferences.Editor spEdit = sp.edit();

			if (checkbox.isChecked()) {
				customText = edittext.getText().toString();
				spEdit.putString(SPF_KEY, customText);
				spEdit.commit();
				//startActivity(intent);
			}
		}
	}
	
	/**/
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	
    	startService(new Intent(this, MyLockScreenService.class));
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	
    	mIsLockScreenOn = checkbox.isChecked();
    	if(mIsLockScreenOn)
    		// keep on disabling the system Keyguard
    		EnableSystemKeyguard(false);
    	else {
    		stopService(new Intent(this, MyLockScreenService.class));
    		// recover original Keyguard
    		EnableSystemKeyguard(true);
    	}
    	
    	// save the setting before leaving.
    	SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putBoolean(LOCK_SCREEN_ON_OFF, mIsLockScreenOn);
		editor.commit();
    	    	
    }
        
    void EnableSystemKeyguard(boolean bEnable){
    	KeyguardManager mKeyguardManager=null;
    	KeyguardLock mKeyguardLock=null; 
    	
    	mKeyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);  
    	mKeyguardLock = mKeyguardManager.newKeyguardLock(""); 
    	if(bEnable)
    		mKeyguardLock.reenableKeyguard();
    	else
    		mKeyguardLock.disableKeyguard();
    }
    
}
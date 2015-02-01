package cn.minelock.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;

import cn.minelock.android.MyLockScreenService;

import cn.minelock.android.R;
import cn.minelock.util.StringUtil;
import cn.minelock.util.XMLParser;
import cn.minelock.widget.SwitchButton;
import cn.minelock.widget.SwitchButton.OnChangeListener;


import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
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
import android.util.Xml;
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
	//private SwitchButton lock_switchbtn = null;
	
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
		// 获取保存的prefs数据
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mIsLockScreenOn = prefs.getBoolean(LOCK_SWITCH, true);
		// 锁屏开关checkbox
		lock_checkbox = (CheckBox) findViewById(R.id.lock_checkbox);
		lock_checkbox.setChecked(mIsLockScreenOn);
		lock_checkbox.setOnClickListener(new OnCheckedListener());
		// 常见问题
		Button question_btn = (Button) findViewById(R.id.setting_question);
		question_btn.setOnClickListener(this);		
		// 初始引导设置
		Button initialguide_btn = (Button) findViewById(R.id.setting_initialguide);
		initialguide_btn.setOnClickListener(this);
		// 设置九宫密码按钮
		Button setpassword_btn = (Button) findViewById(R.id.setting_setpassword);
		setpassword_btn.setOnClickListener(setPasswordOnClickListener);	
		// 个性设置按钮
		Button more_btn = (Button) findViewById(R.id.setting_more);
		more_btn.setOnClickListener(moreOnClickListener);	
		// 关于按钮
		Button about_btn = (Button) findViewById(R.id.setting_about);
		about_btn.setOnClickListener(aboutOnClickListener);	
		// 返回按钮
		ImageButton return_btn = (ImageButton) findViewById(R.id.setting_return);
		return_btn.setOnClickListener(returnOnClickListener);
		// 检查更新按钮
		Button checkversion_btn = (Button) findViewById(R.id.setting_checkversion);
		checkversion_btn.setOnClickListener(checkversionOnClickListener);
		// 随机小提示
/*		String[] tips = getResources().getStringArray(R.array.tips);
		int random = (int)(Math.random()*tips.length);
		TextView tipView = (TextView)findViewById(R.id.setting_tips);
		tipView.setText("注: "+tips[random]);*/
		
		//myServiceConnection = new MyServiceConnection();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.setting_initialguide:
			//EnableSystemKeyguard(false);
			//StringUtil.showToast(this, "设置成功",  Toast.LENGTH_SHORT);
			startActivity(new Intent(SettingActivity.this, InitialGuideActivity.class));
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.setting_question:
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://dwz.cn/minelockwx20150123"));   
			//i.setClassName("com.android.browser", "com.android.browser.BrowserActivity");   
			startActivity(i);  
			break;	
		}
	}
	// 个性设置按钮
	private OnClickListener moreOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(SettingActivity.this, SettingMoreActivity.class));
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			//finish();
		}
	};	
	// 关于按钮
	private OnClickListener aboutOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(SettingActivity.this, AboutActivity.class));
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			//finish();
		}
	};
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
			overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
			//finish();
		}
	};
	
	// 检查更新按钮
	private OnClickListener checkversionOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
		    // 获取本地版本号
		    PackageInfo packageInfo;
		    String localVersion = "1.0";
		    try {
				packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				localVersion = packageInfo.versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		    // 检查网络连接
		    if(XMLParser.isNetworkConnected(getApplicationContext())){
			    // 获取服务器版本号
		        XMLParser parser = new XMLParser();  
		        String xml = parser.getXmlFromUrl("http://minelock.sinaapp.com/version.xml"); // 从网络获取xml  
		        Document doc = parser.getDomElement(xml); // 获取 DOM 节点
		        NodeList nl = doc.getElementsByTagName("version");
		        Element e = (Element) nl.item(0); // 获取第一个节点
		        String serverVersion = parser.getValue(e, "name"); // 获取服务器versionName
			    // 比较本地与服务器版本
				if(localVersion.equals(serverVersion)){			
					StringUtil.showToast(getApplicationContext(), "当前已是最新版",  Toast.LENGTH_SHORT);
				}
				else{
					StringUtil.showToast(getApplicationContext(), "当前为旧版，请下载最新版",  Toast.LENGTH_SHORT);
					Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.minelock.com"));      
					startActivity(i); 			
				}
		    }
		    else{
		    	StringUtil.showToast(getApplicationContext(), "网络无法连接",  Toast.LENGTH_SHORT);
		    }
				
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
				mLockStatus = false;
			}
			else {
				// recover original Keyguard
				EnableSystemKeyguard(true);
				mLockStatus = true;
			}
			//将锁屏开关check值存入pref中
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
		
		//启动锁屏
/*		if (mIsLockScreenOn){
			//启动锁屏
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
		//启动锁屏		
		if (mIsLockScreenOn){
			//启动锁屏			
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
	
	//
	private void EnableSystemKeyguard(boolean bEnable) {
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
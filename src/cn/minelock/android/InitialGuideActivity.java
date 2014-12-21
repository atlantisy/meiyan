package cn.minelock.android;

import java.io.IOException;
import java.security.Policy;

import cn.minelock.util.FlymeUtil;
import cn.minelock.util.MIUIUtil;
import cn.minelock.util.StringUtil;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class InitialGuideActivity extends Activity {

	public static final String PREFS = "lock_pref";// pref文件名
	public static final String INITIALGUIDE = "initial_guide";// 初始设置pref值名称
	private static boolean bIntialGuide = false;// 初始设置是否完成
	
	private SharedPreferences settings;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initialguide);

		// 引导1
		Button ig1_btn = (Button) findViewById(R.id.igBtn1);
		ig1_btn.setOnClickListener(ig1OnClickListener);	
		// 引导2
		Button ig2_btn = (Button) findViewById(R.id.igBtn2);
		ig2_btn.setOnClickListener(ig2OnClickListener);
		// 引导3
		Button ig3_btn = (Button) findViewById(R.id.igBtn3);
		ig3_btn.setOnClickListener(ig3OnClickListener);
		// 判断是否MIUI
		LinearLayout ig2 = (LinearLayout) findViewById(R.id.ig2);
		LinearLayout ig3 = (LinearLayout) findViewById(R.id.ig3);
		ImageView igDown2 = (ImageView)findViewById(R.id.igDown2);		
		ImageView igDown3 = (ImageView)findViewById(R.id.igDown3);
		TextView igCloseDefaultLock = (TextView)findViewById(R.id.igCloseDefaultLock);
		if(MIUIUtil.isMIUI()){
			igCloseDefaultLock.setVisibility(View.GONE);
			ig2.setVisibility(View.VISIBLE);
			ig3.setVisibility(View.VISIBLE);
			igDown2.setVisibility(View.INVISIBLE);
			igDown3.setVisibility(View.INVISIBLE);
		}
		// 返回
		ImageButton return_btn = (ImageButton) findViewById(R.id.initialguide_return);
		return_btn.setOnClickListener(returnOnClickListener);			
		// 设置完毕
		Button finish_btn = (Button) findViewById(R.id.initialguide_ok);
		finish_btn.setOnClickListener(finishOnClickListener);
		// 初始引导设置
		settings = getSharedPreferences(PREFS, 0);
		bIntialGuide = settings.getBoolean(INITIALGUIDE, false);		
		if(!bIntialGuide){
			View igDivide = (View)findViewById(R.id.igDivide);
			igDivide.setVisibility(View.GONE);
			return_btn.setVisibility(View.GONE);
			finish_btn.setVisibility(View.VISIBLE);
			
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(INITIALGUIDE, true);
			editor.commit();
		}											
	}

	// 引导1
	private OnClickListener ig1OnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub			
			// 4.0前版本关闭系统锁屏
			EnableSystemKeyguard(false);					
			
			String closeDefaultLock = "";
			Intent intent = null;
			if(MIUIUtil.isMIUI()){
				closeDefaultLock = "开启「开启开发者选项」\n开启「直接进入系统」";
				intent =  new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);	
				startActivity(intent);
			}
			else if(FlymeUtil.isFlyme()){
				closeDefaultLock = "请选择「无」"; 
				intent = new Intent();
				ComponentName cm = new ComponentName("com.android.settings","com.android.settings.ChooseLockGeneric");  
				intent.setComponent(cm);  
				intent.setAction("android.intent.action.VIEW"); 
				startActivity(intent);
			}
			else{					
				closeDefaultLock = "已关闭"; 
/*				intent = new Intent();
				ComponentName cm = new ComponentName("com.android.settings","com.android.settings.ChooseLockGeneric");  
				intent.setComponent(cm);  
				intent.setAction("android.intent.action.VIEW"); 
				startActivity(intent);*/
			}					     
			//startActivity(intent);
						
			Toast toast = Toast.makeText(getApplicationContext(),closeDefaultLock, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            
			ImageView igCheck1 = (ImageView)findViewById(R.id.igCheck1);
			igCheck1.setBackgroundResource(R.drawable.ic_check);
		}
	};
	// 引导2
	private OnClickListener ig2OnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub  			
            Uri packageURI = Uri.parse("package:" + "cn.minelock.android");
            Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);  
            startActivity(intent);
            
            Toast toast = Toast.makeText(getApplicationContext(),"开启「显示悬浮窗」", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            
			ImageView igCheck2 = (ImageView)findViewById(R.id.igCheck2);
			igCheck2.setBackgroundResource(R.drawable.ic_check);
		}
	};
	// 引导3
	private OnClickListener ig3OnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub						
			PackageManager pm = getPackageManager();
            PackageInfo info = null;
            try {
            	info = pm.getPackageInfo(getPackageName(), 0);
            } catch (NameNotFoundException e) {
            	e.printStackTrace();
            }
            Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
            i.setClassName("com.android.settings", "com.miui.securitycenter.permission.AppPermissionsEditor");
            i.putExtra("extra_package_uid", info.applicationInfo.uid);			
            try {
            	startActivity(i);
            } catch (Exception e) {
            	e.printStackTrace();
            }
            
            Toast toast = Toast.makeText(getApplicationContext(),"开启「我信任该程序」\n开启「自动启动」", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            
			ImageView igCheck3 = (ImageView)findViewById(R.id.igCheck3);
			igCheck3.setBackgroundResource(R.drawable.ic_check);
		}
	};
	// 返回按钮
	private OnClickListener returnOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	
	// 设置完毕按钮
	private OnClickListener finishOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(InitialGuideActivity.this, HomeActivity.class));
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			
			finish();
		}
	};
		
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

package cn.minelock.android;

import java.io.IOException;
import java.security.Policy;

import cn.minelock.android.R;
import cn.minelock.util.PhoneUtil;
import cn.minelock.util.StringUtil;
import cn.minelock.widget.dbHelper;
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
import android.os.Build;
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

	dbHelper dbRecent;
	
	public static final String PREFS = "lock_pref";// pref文件名
	public static final String INITIALGUIDE = "initial_guide";// 初始设置pref值名称
	private static boolean bIntialGuide = false;// 初始设置是否完成
	
	private Button ig1_btn;
	private Button ig2_btn;
	private Button ig3_btn;
	private Button ig4_btn;
	
	private SharedPreferences settings;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initialguide);
		// 引导1
		ig1_btn = (Button) findViewById(R.id.igBtn1);
		ig1_btn.setOnClickListener(ig1OnClickListener);	
		// 引导2
		ig2_btn = (Button) findViewById(R.id.igBtn2);		
		ig2_btn.setOnClickListener(ig2OnClickListener);
		LinearLayout ig2 = (LinearLayout) findViewById(R.id.ig2);
		ImageView igDown2 = (ImageView)findViewById(R.id.igDown2);
		// 引导3
		ig3_btn = (Button) findViewById(R.id.igBtn3);
		ig3_btn.setOnClickListener(ig3OnClickListener);
		LinearLayout ig3 = (LinearLayout) findViewById(R.id.ig3);				
		ImageView igDown3 = (ImageView)findViewById(R.id.igDown3);		
		// 引导4
		ig4_btn = (Button) findViewById(R.id.igBtn4);
		ig4_btn.setOnClickListener(ig4OnClickListener);
		LinearLayout ig4 = (LinearLayout) findViewById(R.id.ig4);
		ImageView igDown4 = (ImageView)findViewById(R.id.igDown4);
		// 其他设置提示
		TextView igHint = (TextView)findViewById(R.id.igHint);
		String hint1=getResources().getText(R.string.ig_hint1).toString();
		igHint.setText(hint1);
		// 判断是否MIUI
		if(PhoneUtil.isMIUI()){
			// 2
			ig2_btn.setText("开启「显示悬浮窗」\n（确保锁屏运行）");				
			ig2.setVisibility(View.VISIBLE);
			igDown2.setVisibility(View.INVISIBLE);
			// 3
			if(PhoneUtil.getMIUIVersion().equals("V6"))
				ig3_btn.setText("设置「自启动管理」\n（防止锁屏失败）");
			else
				ig3_btn.setText("开启「我信任该程序」和「自动启动」\n（防止锁屏失败）");			
			ig3.setVisibility(View.VISIBLE);			
			igDown3.setVisibility(View.INVISIBLE);
			// 4
			ig4_btn.setText("设置内存加速白名单\n（防止锁屏被释放）");
			ig4.setVisibility(View.VISIBLE);
			igDown4.setVisibility(View.INVISIBLE);
		}
		else if(PhoneUtil.isHuawei()){
			// 2
			ig2_btn.setText("设置「信任此应用程序」\n（确保锁屏运行）");
			ig2.setVisibility(View.VISIBLE);
			igDown2.setVisibility(View.INVISIBLE);
			// 3
			ig3_btn.setText("设置「受保护的后台应用」\n（防止锁屏失败）");
			ig3.setVisibility(View.VISIBLE);			
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
						
			// 初始化锁屏记录
/*			dbRecent = new dbHelper(this);
			String initial_verse = getResources().getString(R.string.initial_verse);
			dbRecent.insert(R.drawable._wallpaper01,initial_verse.substring(0),1,R.drawable.wallpaper01,"1_.png");	*/
			
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(INITIALGUIDE, true);
			//editor.putInt("verse_quantity", 1);
			editor.commit();
		}											
	}

	// 引导1
	private OnClickListener ig1OnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {							
			String closeLockToast = "屏幕锁定选择「无」";
			Intent intent = null;
			if(PhoneUtil.isMIUI()){
				closeLockToast = "开启「开启开发者选项」\n开启「直接进入系统」";
				intent =  new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);	
				startActivity(intent);
			}
			else if(PhoneUtil.isFlyme()){
				closeLockToast = "请选择「无」"; 
				intent = new Intent();
				ComponentName cm = new ComponentName("com.android.settings","com.android.settings.ChooseLockGeneric");  
				intent.setComponent(cm);  
				intent.setAction("android.intent.action.VIEW"); 
				startActivity(intent);
			}
			else if((PhoneUtil.isHuawei())){				
				closeLockToast = "解锁样式中选择「不锁屏」"; 
				intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);	
				startActivity(intent);
			}
			else{		
				intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);	
				startActivity(intent);
			}	
			//startActivity(intent);
						
			Toast toast = Toast.makeText(getApplicationContext(),closeLockToast, Toast.LENGTH_LONG);
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
			  			
			String ig2Toast = "";
			if(PhoneUtil.isMIUI()){
/*				PackageManager pm = getPackageManager();
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
	            ig2Toast = "开启「我信任该程序」\n开启「自动启动」";	*/
				
				Uri packageURI = Uri.parse("package:" + "cn.minelock.android");
	            Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);  
	            startActivity(intent);	            
	            ig2Toast = "在「权限管理」中允许「显示悬浮窗」";	
			}
			else if(PhoneUtil.isHuawei()){
/*				Uri packageURI = Uri.parse("package:" + "cn.minelock.android");
	            Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);  
	            startActivity(intent);*/
	            
				Intent intent =  new Intent(Settings.ACTION_SETTINGS);	
				startActivity(intent);
				
	            ig2Toast = "在「权限管理」中设置信任美言锁屏";	            			
			}				           
            Toast toast = Toast.makeText(getApplicationContext(),ig2Toast, Toast.LENGTH_LONG);
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
			String ig3Toast = "";	           
			if(PhoneUtil.isMIUI()){
	            if(PhoneUtil.getMIUIVersion().equals("V6")){
	    			// 返回桌面
	    			Intent i = new Intent(Intent.ACTION_MAIN);
	    			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    			i.addCategory(Intent.CATEGORY_HOME);
	    			startActivity(i);
		            ig3Toast = "安全中心→授权管理→自启动管理→开启美言锁屏";
	            }	            	
	            else{
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
		            ig3Toast = "开启「我信任该程序」\n开启「自动启动」";
	            }
	            	
			}
			else if(PhoneUtil.isHuawei()){
				Intent intent =  new Intent(Settings.ACTION_SETTINGS);	
				startActivity(intent);
				
	            ig3Toast = "在「受保护的后台应用」中开启美言锁屏";
			}
			Toast toast = Toast.makeText(getApplicationContext(),ig3Toast, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            
			ImageView igCheck3 = (ImageView)findViewById(R.id.igCheck3);
			igCheck3.setBackgroundResource(R.drawable.ic_check);
			
		}
	};
	// 引导4
	private OnClickListener ig4OnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {	
/*            Uri packageURI = Uri.parse("package:" + "cn.minelock.android");
            Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);  
            startActivity(intent);*/
            
			// 返回桌面
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i);

			Toast toast = Toast.makeText(getApplicationContext(),"启动近期任务，下拉美言锁屏至锁定", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            
			ImageView igCheck4 = (ImageView)findViewById(R.id.igCheck4);
			igCheck4.setBackgroundResource(R.drawable.ic_check);
			
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

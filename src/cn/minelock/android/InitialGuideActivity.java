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
	
	public static final String PREFS = "lock_pref";// pref�ļ���
	public static final String INITIALGUIDE = "initial_guide";// ��ʼ����prefֵ����
	private static boolean bIntialGuide = false;// ��ʼ�����Ƿ����
	
	private Button ig1_btn;
	private Button ig2_btn;
	private Button ig3_btn;
	
	private SharedPreferences settings;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initialguide);

		// ����1
		ig1_btn = (Button) findViewById(R.id.igBtn1);
		ig1_btn.setOnClickListener(ig1OnClickListener);	
		// ����2
		ig2_btn = (Button) findViewById(R.id.igBtn2);		
		ig2_btn.setOnClickListener(ig2OnClickListener);
		// ����3
		ig3_btn = (Button) findViewById(R.id.igBtn3);
		ig3_btn.setOnClickListener(ig3OnClickListener);
		// �ж��Ƿ�MIUI
		LinearLayout ig2 = (LinearLayout) findViewById(R.id.ig2);
		LinearLayout ig3 = (LinearLayout) findViewById(R.id.ig3);
		ImageView igDown2 = (ImageView)findViewById(R.id.igDown2);		
		ImageView igDown3 = (ImageView)findViewById(R.id.igDown3);
		TextView igHint = (TextView)findViewById(R.id.igHint);
		String hint1=getResources().getText(R.string.ig_hint1).toString();
		String hint2=getResources().getText(R.string.ig_hint2).toString();
		if(PhoneUtil.isMIUI()){
			igHint.setText(hint1);
			ig2_btn.setText("��������ʾ��������\n��ȷ���������У�");
			ig2.setVisibility(View.VISIBLE);
			ig3_btn.setText("���á��ڴ���١�������\n����ֹ����ʧ�ܣ�");
			ig3.setVisibility(View.VISIBLE);
			igDown2.setVisibility(View.INVISIBLE);
			igDown3.setVisibility(View.INVISIBLE);
		}
		else if(PhoneUtil.isHuawei()){
			igHint.setText(hint1);
			ig2_btn.setText("���á����δ�Ӧ�ó���\n��ȷ���������У�");
			ig2.setVisibility(View.VISIBLE);
			ig3_btn.setText("���á��ܱ����ĺ�̨Ӧ�á�\n����ֹ����ʧ�ܣ�");
			ig3.setVisibility(View.VISIBLE);
			igDown2.setVisibility(View.INVISIBLE);
			igDown3.setVisibility(View.INVISIBLE);
		}
		else{
			igHint.setText(hint1+hint2);
		}
		// ����
		ImageButton return_btn = (ImageButton) findViewById(R.id.initialguide_return);
		return_btn.setOnClickListener(returnOnClickListener);			
		// �������
		Button finish_btn = (Button) findViewById(R.id.initialguide_ok);
		finish_btn.setOnClickListener(finishOnClickListener);
		// ��ʼ��������
		settings = getSharedPreferences(PREFS, 0);
		bIntialGuide = settings.getBoolean(INITIALGUIDE, false);		
		if(!bIntialGuide){
			View igDivide = (View)findViewById(R.id.igDivide);
			igDivide.setVisibility(View.GONE);
			return_btn.setVisibility(View.GONE);
			finish_btn.setVisibility(View.VISIBLE);
						
			// ��ʼ��������¼
/*			dbRecent = new dbHelper(this);
			String initial_verse = getResources().getString(R.string.initial_verse);
			dbRecent.insert(R.drawable._wallpaper01,initial_verse.substring(0),1,R.drawable.wallpaper01,"1_.png");	*/
			
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(INITIALGUIDE, true);
			//editor.putInt("verse_quantity", 1);
			editor.commit();
		}											
	}

	// ����1
	private OnClickListener ig1OnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub			
			// 4.0ǰ�汾�ر�ϵͳ����
			//EnableSystemKeyguard(false);					
			
			String closeLockToast = "��Ļ����ѡ���ޡ�";
			Intent intent = null;
			if(PhoneUtil.isMIUI()){
				closeLockToast = "����������������ѡ�\n������ֱ�ӽ���ϵͳ��";
				intent =  new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);	
				startActivity(intent);
			}
			else if(PhoneUtil.isFlyme()){
				closeLockToast = "��ѡ���ޡ�"; 
				intent = new Intent();
				ComponentName cm = new ComponentName("com.android.settings","com.android.settings.ChooseLockGeneric");  
				intent.setComponent(cm);  
				intent.setAction("android.intent.action.VIEW"); 
				startActivity(intent);
			}
			else if((PhoneUtil.isHuawei())){				
				closeLockToast = "������ʽѡ�񡸲�������"; 
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
	// ����2
	private OnClickListener ig2OnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub  			
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
	            ig2Toast = "�����������θó���\n�������Զ�������";	*/
				
				Uri packageURI = Uri.parse("package:" + "cn.minelock.android");
	            Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);  
	            startActivity(intent);	            
	            ig2Toast = "�ڡ�Ȩ�޹�����������ʾ��������";	
			}
			else if(PhoneUtil.isHuawei()){
/*				Uri packageURI = Uri.parse("package:" + "cn.minelock.android");
	            Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);  
	            startActivity(intent);*/
	            
				Intent intent =  new Intent(Settings.ACTION_SETTINGS);	
				startActivity(intent);
				
	            ig2Toast = "�ڡ�Ȩ�޹���������������������";	            			
			}				           
            Toast toast = Toast.makeText(getApplicationContext(),ig2Toast, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            
			ImageView igCheck2 = (ImageView)findViewById(R.id.igCheck2);
			igCheck2.setBackgroundResource(R.drawable.ic_check);
		}
	};
	// ����3
	private OnClickListener ig3OnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			String ig3Toast = "";	
/*            Uri packageURI = Uri.parse("package:" + "cn.minelock.android");
            Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);  
            startActivity(intent);*/
            
			if(PhoneUtil.isMIUI()){
				// ����home����������
				Intent i = new Intent(Intent.ACTION_MAIN);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addCategory(Intent.CATEGORY_HOME);
				startActivity(i);
				
				ig3Toast = "�򿪡���������������������������";
			}
			else if(PhoneUtil.isHuawei()){
				Intent intent =  new Intent(Settings.ACTION_SETTINGS);	
				startActivity(intent);
				
	            ig3Toast = "�ڡ��ܱ����ĺ�̨Ӧ�á��п�����������";
			}
			Toast toast = Toast.makeText(getApplicationContext(),ig3Toast, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            
			ImageView igCheck3 = (ImageView)findViewById(R.id.igCheck3);
			igCheck3.setBackgroundResource(R.drawable.ic_check);
			
		}
	};
	// ���ذ�ť
	private OnClickListener returnOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	
	// ������ϰ�ť
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

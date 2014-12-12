package cn.minelock.android;

import java.io.IOException;
import java.security.Policy;

import cn.minelock.util.FlymeUtil;
import cn.minelock.util.MIUIUtil;
import cn.minelock.util.StringUtil;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
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
import android.widget.Toast;

public class InitialGuideActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initialguide);

		// ����1
		Button ig1_btn = (Button) findViewById(R.id.igBtn1);
		ig1_btn.setOnClickListener(ig1OnClickListener);	
		// ����2
		Button ig2_btn = (Button) findViewById(R.id.igBtn2);
		ig2_btn.setOnClickListener(ig2OnClickListener);
		// ����3
		Button ig3_btn = (Button) findViewById(R.id.igBtn3);
		ig3_btn.setOnClickListener(ig3OnClickListener);
		// �ж��Ƿ�MIUI
		LinearLayout ig2 = (LinearLayout) findViewById(R.id.ig2);
		LinearLayout ig3 = (LinearLayout) findViewById(R.id.ig3);
		ImageView igDown2 = (ImageView)findViewById(R.id.igDown2);		
		ImageView igDown3 = (ImageView)findViewById(R.id.igDown3);
		if(MIUIUtil.isMIUI()){
			ig2.setVisibility(View.VISIBLE);
			ig3.setVisibility(View.VISIBLE);
			igDown2.setVisibility(View.VISIBLE);
			igDown3.setVisibility(View.VISIBLE);
		}
		// ����
		ImageButton return_btn = (ImageButton) findViewById(R.id.initialguide_return);
		return_btn.setOnClickListener(returnOnClickListener);
		// ���
		Button finish_btn = (Button) findViewById(R.id.initialguide_ok);
		finish_btn.setOnClickListener(returnOnClickListener);
				
	}

	// ����1
	private OnClickListener ig1OnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub			
			String closeDefaultLock = "";
			Intent intent = null;
			if(MIUIUtil.isMIUI()){
				closeDefaultLock = "����������������ѡ�\n������ֱ�ӽ���ϵͳ��";
				intent =  new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);	
			}
			else if(FlymeUtil.isFlyme()){
				closeDefaultLock = "��ѡ���ޡ�"; 
				intent = new Intent();
				ComponentName cm = new ComponentName("com.android.settings","com.android.settings.ChooseLockGeneric");  
				intent.setComponent(cm);  
				intent.setAction("android.intent.action.VIEW");  				 				
			}
			else{
				closeDefaultLock = "��ѡ���ޡ�"; 
				intent = new Intent();
				ComponentName cm = new ComponentName("com.android.settings","com.android.settings.ChooseLockGeneric");  
				intent.setComponent(cm);  
				intent.setAction("android.intent.action.VIEW"); 
				
/*				closeDefaultLock = "�ѹر�"; 
	            intent =  new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);*/  	            	            
			}					     
			startActivity(intent);
			
            Toast toast = Toast.makeText(getApplicationContext(),closeDefaultLock, Toast.LENGTH_LONG);
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
            Uri packageURI = Uri.parse("package:" + "cn.minelock.android");
            Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);  
            startActivity(intent);
            
            Toast toast = Toast.makeText(getApplicationContext(),"��������ʾ��������", Toast.LENGTH_LONG);
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
            
            Toast toast = Toast.makeText(getApplicationContext(),"�����������θó���\n�������Զ�������", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            
			ImageView igCheck3 = (ImageView)findViewById(R.id.igCheck3);
			igCheck3.setBackgroundResource(R.drawable.ic_check);
		}
	};
	// ���ؼ���ɰ�ť
	private OnClickListener returnOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	

}

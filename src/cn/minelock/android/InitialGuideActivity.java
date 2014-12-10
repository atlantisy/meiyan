package cn.minelock.android;

import java.io.IOException;

import cn.minelock.util.FlymeUtil;
import cn.minelock.util.MIUIUtil;
import cn.minelock.util.StringUtil;
import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;

public class InitialGuideActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initialguide);

		// 引导1
		Button ig1_btn = (Button) findViewById(R.id.initialguide1);
		ig1_btn.setOnClickListener(ig1OnClickListener);	
		// 引导2
		Button ig2_btn = (Button) findViewById(R.id.initialguide2);
		ig2_btn.setOnClickListener(ig2OnClickListener);
		// 引导3
		Button ig3_btn = (Button) findViewById(R.id.initialguide3);
		ig3_btn.setOnClickListener(ig3OnClickListener);
		// 判断系统
		if(MIUIUtil.isMIUI()){
			ig2_btn.setVisibility(View.VISIBLE);
			ig3_btn.setVisibility(View.VISIBLE);
		}
		// 返回
		ImageButton return_btn = (ImageButton) findViewById(R.id.initialguide_return);
		return_btn.setOnClickListener(returnOnClickListener);
		// 完成
		Button finish_btn = (Button) findViewById(R.id.initialguide_ok);
		finish_btn.setOnClickListener(returnOnClickListener);
				
	}

	// 引导1
	private OnClickListener ig1OnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String closeDefaultLock = "";
			Intent intent = null;
			if(MIUIUtil.isMIUI()){
				closeDefaultLock = "开启「开启开发者选项」\n开启「直接进入系统」";
				intent =  new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);				
			}
			else if(FlymeUtil.isFlyme()){
				closeDefaultLock = "请选择「无」";
				intent =  new Intent(Settings.ACTION_SECURITY_SETTINGS);	
			}
			else{
				closeDefaultLock = "请选择「无」";
				intent =  new Intent(Settings.ACTION_SECURITY_SETTINGS);	
			}					     
	        startActivity(intent);
	        
            Toast toast = Toast.makeText(getApplicationContext(),closeDefaultLock, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
	        //StringUtil.showToast(getApplication(), closeDefaultLock, Toast.LENGTH_LONG);
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
            //StringUtil.showToast(getApplication(), "开启「显示悬浮窗」", Toast.LENGTH_LONG);
		}
	};
	// 引导3
	private OnClickListener ig3OnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
            Uri packageURI = Uri.parse("package:" + "cn.minelock.android");
            Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);  
            startActivity(intent);
            
            Toast toast = Toast.makeText(getApplicationContext(),"开启「我信任该程序」\n开启「自动启动」", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
	        //StringUtil.showToast(getApplication(), "开启「我信任该程序」\n开启「自动启动」", Toast.LENGTH_LONG);
		}
	};
	// 返回及完成按钮
	private OnClickListener returnOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	

}

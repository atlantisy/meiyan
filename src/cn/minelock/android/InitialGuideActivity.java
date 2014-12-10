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

		// ����1
		Button ig1_btn = (Button) findViewById(R.id.initialguide1);
		ig1_btn.setOnClickListener(ig1OnClickListener);	
		// ����2
		Button ig2_btn = (Button) findViewById(R.id.initialguide2);
		ig2_btn.setOnClickListener(ig2OnClickListener);
		// ����3
		Button ig3_btn = (Button) findViewById(R.id.initialguide3);
		ig3_btn.setOnClickListener(ig3OnClickListener);
		// �ж�ϵͳ
		if(MIUIUtil.isMIUI()){
			ig2_btn.setVisibility(View.VISIBLE);
			ig3_btn.setVisibility(View.VISIBLE);
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
				intent =  new Intent(Settings.ACTION_SECURITY_SETTINGS);	
			}
			else{
				closeDefaultLock = "��ѡ���ޡ�";
				intent =  new Intent(Settings.ACTION_SECURITY_SETTINGS);	
			}					     
	        startActivity(intent);
	        
            Toast toast = Toast.makeText(getApplicationContext(),closeDefaultLock, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
	        //StringUtil.showToast(getApplication(), closeDefaultLock, Toast.LENGTH_LONG);
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
            //StringUtil.showToast(getApplication(), "��������ʾ��������", Toast.LENGTH_LONG);
		}
	};
	// ����3
	private OnClickListener ig3OnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
            Uri packageURI = Uri.parse("package:" + "cn.minelock.android");
            Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);  
            startActivity(intent);
            
            Toast toast = Toast.makeText(getApplicationContext(),"�����������θó���\n�������Զ�������", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
	        //StringUtil.showToast(getApplication(), "�����������θó���\n�������Զ�������", Toast.LENGTH_LONG);
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

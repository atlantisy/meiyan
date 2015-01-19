package cn.minelock.android;

import cn.minelock.android.HomeActivity;

import cn.minelock.android.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class AppStartActivity extends Activity {

	public static final String PREFS = "lock_pref";// pref�ļ���
	public static final String INITIALGUIDE = "initial_guide";// ��ʼ����prefֵ����
	private static boolean bIntialGuide = false;// ��ʼ�����Ƿ����
	
	private SharedPreferences settings;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final View view = View.inflate(this, R.layout.start, null);
        setContentView(view);
		
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            //�������activity
            finish();
        	return;        	
        }
        
        //this.excludeFromRecents
		// ����չʾ������,����ͨ�������������˿���Ӧ�ó���Ľ���
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(1000);
		view.startAnimation(aa);
		// ��ʼ��������
		settings = getSharedPreferences(PREFS, 0);
		bIntialGuide = settings.getBoolean(INITIALGUIDE, false);
		//��������Ӽ�������
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				if(bIntialGuide){
					redirectToHome();
				}					
				else{					
					redirectToInitialGuide();
				}					
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

		});
	}

	/**
	 * ��ת��������
	 */
	private void redirectToHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		//finish();
	}
	
	/**
	 * ��ת����ʼ����
	 */
	private void redirectToInitialGuide() {
		Intent intent = new Intent(this, InitialGuideActivity.class);
		//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);		
		//finish();
	}
	
	
}

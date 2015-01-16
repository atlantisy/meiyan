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

	public static final String PREFS = "lock_pref";// pref文件名
	public static final String INITIALGUIDE = "initial_guide";// 初始设置pref值名称
	private static boolean bIntialGuide = false;// 初始设置是否完成
	
	private SharedPreferences settings;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		final View view = View.inflate(this, R.layout.start, null);
		setContentView(view);
		
		//this.excludeFromRecents
		// 渐变展示启动屏,这里通过动画来设置了开启应用程序的界面
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(1000);
		view.startAnimation(aa);
		// 初始引导设置
		settings = getSharedPreferences(PREFS, 0);
		bIntialGuide = settings.getBoolean(INITIALGUIDE, false);
		//给动画添加监听方法
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
	 * 跳转到主桌面
	 */
	private void redirectToHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		//finish();
	}
	
	/**
	 * 跳转到初始设置
	 */
	private void redirectToInitialGuide() {
		Intent intent = new Intent(this, InitialGuideActivity.class);
		startActivity(intent);		
		//finish();
	}
	
	
}

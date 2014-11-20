package cn.minelock.android;

import java.util.ArrayList;

import cn.minelock.widget.LockLayer;

import cn.minelock.widget.PatternPassWordView;
import cn.minelock.widget.dbHelper;
import cn.minelock.widget.PatternPassWordView.OnCompleteListener;

import cn.minelock.android.R;



import android.R.string;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.style.ForegroundColorSpan;

public class LockActivity extends FragmentActivity {	
	View banHomeKeyView;
	WindowManager banHomeKeyWM;	
	LockLayer lockLayer;
		
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);			
	    // 设置布局文件
	    //setContentView(R.layout.activity_lock);
		
		// 悬浮错误框屏蔽home键		
		BanHomeKey();
	}
		
	public void BanHomeKey(){		
		banHomeKeyView = View.inflate(getApplicationContext(), R.layout.minelock_layout, null);  
		banHomeKeyWM = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);  
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();   
		params.width = WindowManager.LayoutParams.MATCH_PARENT;  
		params.height = WindowManager.LayoutParams.MATCH_PARENT; 
		
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//实现屏蔽Home
		//params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;//实现屏蔽Home
		//params.flags = 1280;//不显示状态栏
		
		banHomeKeyWM.addView(banHomeKeyView, params); 
	}
	
	public void BanHomeKey1(){		
		banHomeKeyView = View.inflate(this, R.layout.minelock_layout, null);
	    lockLayer = LockLayer.getInstance(this);
	    lockLayer.setLockView(banHomeKeyView);
	    lockLayer.lock();
	}
}
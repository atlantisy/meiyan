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

//此文件无用
public class LockActivity extends FragmentActivity {	
	LockLayer lockLayer;
		
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);			
	    // 设置布局文件
	    setContentView(R.layout.activity_lock);
		
	}
		
}
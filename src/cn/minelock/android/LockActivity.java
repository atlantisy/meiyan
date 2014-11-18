package cn.minelock.android;

import java.util.ArrayList;

import cn.minelock.android.LockActivity.MyOnPageChangeListener;
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
	private static final String TAG = "MeiYan";
	private static final int IDTAG = -1;
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentsList;
	private String sCustom = "";
	private int currIndex = 1;
	
	public static final String PREFS = "lock_pref";//pref文件名
	public static final String VERSE = "verse";//锁屏方式pref值名称
	public static final String BOOLIDPATH = "wallpaper_idorpath";//应用内or外壁纸bool的pref值名称,true为ID，false为path
	public static final String WALLPAPERID = "wallpaper_id";//应用内壁纸资源ID的pref值名称
	public static final String WALLPAPERPATH = "wallpaper_path";//应用外壁纸Path的pref值名称
	public static final String VERSEQTY = "verse_quantity";// 美言数量pref值名称
	public static final String VERSEID = "verse_id";// 美言id pref值名称
	public static final String LOCKFLAG = "lockFlag";//锁屏方式pref值名称
	public static final String SHOWVERSEFLAG = "showVerseFlag";//美言显示方式pref值名称
	public static final String PWSETUP = "passWordSetUp";//九宫格是否设置pref值名称	
	
	private PatternPassWordView ppwv=null;
	private Toast toast;
	private ImageView viewRightArrow;
	private ImageView viewLeftArrow;
	
	dbHelper dbRecent;
	private Cursor lockCursor;
	
	private SharedPreferences defaultPrefs = null;
	private SharedPreferences.Editor defaultEditor = null;
	private final String LOCK_STATUS = "lock_status";
	private boolean mLockStatus;
	
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	
	private void showToast(CharSequence message) {
		if (null == toast) {
			toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}

		toast.show();
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);			
		// Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Remove notification bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_lock);
		FrameLayout lockLayout = (FrameLayout)findViewById(R.id.LockLayout);		
		// 获取pref值			
		settings = getSharedPreferences(PREFS, 0);
		editor = settings.edit();
		// 获取默认的prefs数据
		defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		defaultEditor = defaultPrefs.edit();
		// SQL数据库
		dbRecent = new dbHelper(this);
		lockCursor = dbRecent.select();
		// 获取保存的美言数量及美言显示方式
		int showVerseFlag = settings.getInt(SHOWVERSEFLAG, 1);
		sCustom = settings.getString(VERSE, "感觉自己萌萌哒  ");	
		// 设置美言显示
		SetVerseShow(showVerseFlag);
		sCustom = sCustom.trim();//去掉前后空格
		Log.d(TAG, sCustom);
		// 设置壁纸		
		boolean bIdOrPath = settings.getBoolean(BOOLIDPATH, true);
		int wallpaperId = settings.getInt(WALLPAPERID, R.drawable.wallpaper00);
		String wallpaperPath = settings.getString(WALLPAPERPATH, "");	
		if(bIdOrPath==true)//设置壁纸			
			lockLayout.setBackgroundResource(wallpaperId);
		else{
			Bitmap bitmap = BitmapFactory.decodeFile(wallpaperPath);
			lockLayout.setBackgroundDrawable(new BitmapDrawable(bitmap));
		}				
		// 简单滑动解锁，即锁屏方式1
		mPager = (ViewPager) findViewById(R.id.viewpager);
		InitViewPager();
		// 九宫手势解锁，即锁屏方式2
		ppwv = (PatternPassWordView) this.findViewById(R.id.mPatternPassWordView);
		ppwv.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(String mPassword) {
				// 如果密码正确,则进入主页面。
				if (ppwv.verifyPassword(mPassword)) {
					//showToast("解锁成功！");
					mLockStatus=false;
					unLock();
					//returnHome();					
				} else {
					mLockStatus=true;
					showToast("手势错误,请重新输入");
					ppwv.clearPassword();					
				}
				saveLockStatus();//保存锁屏状态
			}
		});		
		// 获取存储的pref数据  
		int flag = settings.getInt(LOCKFLAG, 1);
		boolean setPassword = settings.getBoolean(PWSETUP, false);
		// 控制锁屏方式的显示
		if(flag==2 & setPassword==true){			
			mPager.setVisibility(View.GONE);
			ppwv.setVisibility(View.VISIBLE);			
		}
		else{
			mPager.setVisibility(View.VISIBLE);
			ppwv.setVisibility(View.GONE);			
		}
		mPager.setSelected(true);
	}

	private void SetVerseShow(int showVerseFlag){
		int verseQty = settings.getInt(VERSEQTY, 0);
		switch (showVerseFlag) {
		case 1:
			// 单句循环
			break;
		case 2:
			// 顺序循环
			if(verseQty>0 && sCustom.trim()!=""){
				// 获取当前美言id
				int verseId = (int)settings.getLong(VERSEID,0);							
				// 移动到下一位置
				if(verseId+1==verseQty)
					verseId=0;
				else
					verseId=verseId+1;
				lockCursor.moveToPosition(verseId);	
				sCustom = lockCursor.getString(1) + lockCursor.getString(2);	
				// 将美言及id存入SharedPreferences				
				editor.putString(VERSE, sCustom);// 美言
				editor.putLong(VERSEID,(long)verseId);// 美言id
				editor.commit();
			}
			break;	
		case 3:
			// 随机显示
			if(verseQty>0 && sCustom.trim()!=""){
				int random = (int)(Math.random()*verseQty);
				lockCursor.moveToPosition(random);
				sCustom = lockCursor.getString(1) + lockCursor.getString(2);
				// 将美言存入SharedPreferences				
				editor.putString(VERSE, sCustom);// 美言
				editor.putLong(VERSEID, random);// 美言id
				editor.commit();						
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mLockStatus = false;
		saveLockStatus();//保存锁屏状态
	}
	
	private void InitViewPager() {		
		fragmentsList = new ArrayList<Fragment>();
		UnlockFragment unlockFragment = new UnlockFragment();
		Fragment homeFragment = VerseFragment.newString(sCustom);
		CameraFragment cameraFragment = new CameraFragment();

		fragmentsList.add(unlockFragment);
		fragmentsList.add(homeFragment);
		fragmentsList.add(cameraFragment);

		mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
		mPager.setCurrentItem(currIndex);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());

	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				mLockStatus = false;
				unLock();
				//returnHome();				
				break;
			case 2:				
				mLockStatus = false;
				launchCamera();
				finish();								
				break;
			default:
				mLockStatus = true;	
				//unLock();
				break;
			}
			saveLockStatus();//保存锁屏状态
			currIndex = arg0;
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			viewRightArrow = (ImageView) findViewById(R.id.iv_arrow_right);
			viewLeftArrow = (ImageView) findViewById(R.id.iv_arrow_left);
			//arg0 :当前页面，即你点击滑动的页面，arg1:当前页面偏移的百分比，arg2:当前页面偏移的像素位置   
			if (arg1>0.0 | arg2>0){
/*				viewRightArrow.setVisibility(0);//0:VISIBILITY
				viewLeftArrow.setVisibility(8);//4:INVISIBILITY，8:GONE
*/			}
			else{
/*				viewRightArrow.setVisibility(0);//0:VISIBILITY
				viewLeftArrow.setVisibility(8);//4:INVISIBILITY，8:GONE
*/			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			viewRightArrow = (ImageView) findViewById(R.id.iv_arrow_right);
			viewLeftArrow = (ImageView) findViewById(R.id.iv_arrow_left);	
			//arg0 ==1的时辰默示正在滑动，arg0==2的时辰默示滑动完毕了，arg0==0的时辰默示什么都没做。
			if (arg0==1){
/*				viewRightArrow.setVisibility(0);//0:VISIBILITY
				viewLeftArrow.setVisibility(8);//4:INVISIBILITY，8:GONE
*/			}
			else{
/*				viewRightArrow.setVisibility(0);//0:VISIBILITY
				viewLeftArrow.setVisibility(8);//4:INVISIBILITY，8:GONE
*/			}
		}
	}
	
	// 屏蔽返回键、MENU键
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
/*		if (keyCode==KeyEvent.KEYCODE_MENU) {
			return true;
		}
		else if (keyCode==KeyEvent.KEYCODE_BACK) {
			return true;
		}*/
		
		switch(keyCode){
		case KeyEvent.KEYCODE_MENU:return true;
		case KeyEvent.KEYCODE_BACK:return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN: return true;
		case KeyEvent.KEYCODE_VOLUME_UP: return true;
/*		case KeyEvent.KEYCODE_CALL:return true;
		case KeyEvent.KEYCODE_SYM: return true;
		case KeyEvent.KEYCODE_STAR: return true;*/
		}
		
		return super.onKeyDown(keyCode, event);
    } 
	
	// 获取android版本号
	public static int getSDKVersion() { 
		int version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
		return version; 
	}
	
	// 屏蔽home键，android 2.2和2.3适用，4.0及以上不适用
    public void onAttachedToWindow() { 
    	if (getSDKVersion()<=10)
    		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD); //TYPE_KEYGUARD_DIALOG 
        super.onAttachedToWindow(); 
    }
    
	//启动短信应用  
    private void launchSms() {    
        //mFocusView.setVisibility(View.GONE);  
        Intent intent = new Intent();  
        ComponentName comp = new ComponentName("com.android.mms",  
                "com.android.mms.ui.ConversationList");  
        intent.setComponent(comp);  
        intent.setAction("android.intent.action.VIEW");  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);  
        startActivity(intent);  
    }       
    //启动拨号应用  
    private void launchDial() {  
        Intent intent = new Intent(Intent.ACTION_DIAL);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);  
        startActivity(intent);  
    }
    //启动相机应用  
    private void launchCamera() {      	
    	Intent intent = new Intent(); 
        
    	// 4.0以下的系统开启相机
/*        ComponentName comp = new ComponentName("com.android.camera",  
                "com.android.camera.Camera");  
        intent.setComponent(comp);  
        intent.setAction("android.intent.action.VIEW");  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK   | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);*/ 
        
        // 4.0以上的系统开启相机1    	
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        
    	// 4.0以上的系统开启相机2，可切换摄像
/*		intent.setAction(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);  
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); */ 
		
        startActivity(intent); 
    }	
	
	// 解锁
	public void unLock() {
		finish();
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
	}
	
	// 进入桌面
	public void returnHome(){
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
	}
	
	// 保存锁屏状态
	public void saveLockStatus(){
		defaultEditor.putBoolean(LOCK_STATUS, mLockStatus);
		defaultEditor.commit();
	}
	

}
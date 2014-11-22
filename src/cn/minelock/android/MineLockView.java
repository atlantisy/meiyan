package cn.minelock.android;

import java.util.ArrayList;


import cn.minelock.widget.MyScrollLayout;

import cn.minelock.widget.LockLayer;
import cn.minelock.widget.MyScrollLayout.OnViewChangeListener;
import cn.minelock.widget.PatternPassWordView;
import cn.minelock.widget.dbHelper;
import cn.minelock.widget.PatternPassWordView.OnCompleteListener;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MineLockView extends FrameLayout{

	private static final String TAG = "MeiYan";
	private static final int IDTAG = -1;
	private String sCustom = "";
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentsList;
	private int currIndex = 1;
	
	private MyScrollLayout mScrollLayout;	
	
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
	
	private PatternPassWordView ppwv = null;
	private Toast toast;
	
	dbHelper dbRecent;
	private Cursor lockCursor;
	
	private SharedPreferences defaultPrefs = null;
	private SharedPreferences.Editor defaultEditor = null;
	private final String LOCK_STATUS = "lock_status";
	private boolean mLockStatus;
	
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	
	private TextView viewVerse;
	private Context context;
	
	LockLayer lockLayer;
	View banHomeKeyView;
	WindowManager banHomeKeyWM;	
	WindowManager.LayoutParams params;
	
		
	public MineLockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
	}	

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		// 获取pref值			
		settings = context.getSharedPreferences(PREFS, 0);
		editor = settings.edit();
		// 获取默认的prefs数据
		defaultPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		defaultEditor = defaultPrefs.edit();
		// SQL数据库
		dbRecent = new dbHelper(context);
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
		FrameLayout lockLayout = (FrameLayout)findViewById(R.id.LockLayout);
		if(bIdOrPath==true)//设置壁纸			
			lockLayout.setBackgroundResource(wallpaperId);
		else{
			Bitmap bitmap = BitmapFactory.decodeFile(wallpaperPath);
			lockLayout.setBackgroundDrawable(new BitmapDrawable(bitmap));
		}				
		// 简单滑动解锁，即锁屏方式1
		//mPager = (ViewPager) findViewById(R.id.viewpager);
		//InitViewPager();
		viewVerse = (TextView) findViewById(R.id.tv_verse);
		viewVerse.setText(sCustom);
    	mScrollLayout = (MyScrollLayout) this.findViewById(R.id.mMyScrollLayout); 	  	 	
    	mScrollLayout.SetOnViewChangeListener(new OnViewChangeListener() {
			
			@Override
			public void OnViewChange(int view) {
				// TODO Auto-generated method stub
				switch (view) {
				case 0:
					mLockStatus = false;
					saveLockStatus();//保存锁屏状态
					unLock();			
					break;
				case 2:				
					mLockStatus = false;
					saveLockStatus();//保存锁屏状态
					launchCamera();							
					break;
				default:
					mLockStatus = true;
					saveLockStatus();//保存锁屏状态
					unLock();
					break;
				}
				//saveLockStatus();//保存锁屏状态
				currIndex = view;				
			}
		});
		// 九宫手势解锁，即锁屏方式2
		ppwv = (PatternPassWordView) this.findViewById(R.id.mPatternPassWordView);
		ppwv.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(String mPassword) {
				// 如果密码正确,则进入主页面。
				if (ppwv.verifyPassword(mPassword)) {
					//showToast("解锁成功！");
					mLockStatus=false;
					saveLockStatus();//保存锁屏状态
					unLock();					
				} else {
					mLockStatus=true;
					saveLockStatus();//保存锁屏状态
					showToast("手势错误,请重新输入");
					ppwv.clearPassword();					
				}
			}
		});		
		// 获取存储的pref数据  
		int flag = settings.getInt(LOCKFLAG, 1);
		boolean setPassword = settings.getBoolean(PWSETUP, false);
		// 控制锁屏方式的显示
		if(flag==2 & setPassword==true){			
			//mPager.setVisibility(View.GONE);
			mScrollLayout.setVisibility(View.GONE);			
			ppwv.setVisibility(View.VISIBLE);			
		}
		else{
			//mPager.setVisibility(View.VISIBLE);
			mScrollLayout.setVisibility(View.VISIBLE);			
			ppwv.setVisibility(View.GONE);			
		}
				
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
	
	// 屏蔽返回键、MENU键
	public boolean onKeyDown(int keyCode, KeyEvent event) {  		
		switch(keyCode){
		case KeyEvent.KEYCODE_MENU:return true;
		case KeyEvent.KEYCODE_BACK:return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN: return true;
		case KeyEvent.KEYCODE_VOLUME_UP: return true;		
		case KeyEvent.KEYCODE_HOME: return true;// 屏蔽home键		
		}			
			
		return super.onKeyDown(keyCode, event);
    } 
	
	// 获取android版本号
	public static int getSDKVersion() { 
		int version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
		return version; 
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
        context.startActivity(intent);  
    }       
    //启动拨号应用  
    private void launchDial() {  
        Intent intent = new Intent(Intent.ACTION_DIAL);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);  
        context.startActivity(intent);  
    }
    //启动相机应用  
    private void launchCamera() {      	
    	Intent intent = new Intent();                 	
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);        		
        context.startActivity(intent); 
        
        //banHomeKeyWM.removeView(banHomeKeyView);
    }	
	
	// 解锁进入桌面
	public void unLock() {	
		//banHomeKeyWM.removeView(banHomeKeyView);
		
		Intent intent = new Intent(Intent.ACTION_MAIN);		
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);		

/*		if (context instanceof Activity) {
			Activity act = (Activity) context;
			act.finish();
		}	*/	
						
/*		Intent i = new Intent(context, MyLockScreenService.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(i);*/
				
		//banHomeKeyWM.addView(banHomeKeyView, params);
		if(banHomeKeyView.getParent()!=null)
			banHomeKeyWM.removeView(banHomeKeyView);				
	}	
	
	// 保存锁屏状态
	public void saveLockStatus(){
		defaultEditor.putBoolean(LOCK_STATUS, mLockStatus);
		defaultEditor.commit();
	}
	
	private void showToast(CharSequence message) {
		if (null == toast) {
			toast = Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}

		toast.show();
	}

}

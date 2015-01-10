package cn.minelock.android;

import java.util.ArrayList;

import cn.minelock.android.R;
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
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
	public static final String SHOWRIGHT = "showRight";	
	public static final String LEFTCAMERA = "leftCamera";
	
	private PatternPassWordView ppwv = null;
	
	dbHelper dbRecent;
	private Cursor lockCursor;	
	
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	
	private TextView viewVerse;
	private TextView batteryValue;
	private Context context;	
	
		
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
		// SQL数据库
		dbRecent = new dbHelper(context);
		lockCursor = dbRecent.select();
		// 获取保存的美言数量及美言显示方式
		int showVerseWallpaperFlag = settings.getInt(SHOWVERSEFLAG, 1);
		sCustom = settings.getString(VERSE, getResources().getString(R.string.initial_verse));	
		Log.d(TAG, sCustom);
		// 设置美言、壁纸显示
		//SetVerseWallpaperShow(showVerseWallpaperFlag);		
		// 设置壁纸		
		boolean bIdOrPath = settings.getBoolean(BOOLIDPATH, true);
		int wallpaperId = settings.getInt(WALLPAPERID, R.drawable.wallpaper01);
		String wallpaperPath = settings.getString(WALLPAPERPATH, "");	
		FrameLayout lockLayout = (FrameLayout)findViewById(R.id.LockLayout);
		if(bIdOrPath==true)//设置壁纸			
			lockLayout.setBackgroundResource(wallpaperId);
		else{
			Bitmap bitmap = BitmapFactory.decodeFile(wallpaperPath);
			try {
				lockLayout.setBackgroundDrawable(new BitmapDrawable(bitmap));
			} catch (Exception e) {
				// TODO: handle exception
				lockLayout.setBackgroundResource(wallpaperId);
			}			
		}
		// 简单滑动解锁，即锁屏方式1
		viewVerse = (TextView) findViewById(R.id.tv_verse);
		viewVerse.setText(sCustom.trim());
    	mScrollLayout = (MyScrollLayout) this.findViewById(R.id.mMyScrollLayout); 	  	 	
		// 九宫手势解锁，即锁屏方式2
		ppwv = (PatternPassWordView) this.findViewById(R.id.mPatternPassWordView);
		// 获取存储的pref数据  
		int flag = settings.getInt(LOCKFLAG, 1);
		boolean setPassword = settings.getBoolean(PWSETUP, false);
		// 控制锁屏方式的显示
		if(flag==2 & setPassword==true){			
			mScrollLayout.setVisibility(View.GONE);			
			ppwv.setVisibility(View.VISIBLE);			
		}
		else{
			mScrollLayout.setVisibility(View.VISIBLE);
			ppwv.setVisibility(View.GONE);
			// 右滑箭头
			TextView tv_right=(TextView)findViewById(R.id.tv_right);
			if(settings.getBoolean(SHOWRIGHT, true))				
				tv_right.setVisibility(View.VISIBLE);
			else
				tv_right.setVisibility(View.GONE);
			// 左滑相机
			ImageButton ib_camera=(ImageButton)findViewById(R.id.ib_camera);
			if(settings.getBoolean(LEFTCAMERA, false))				
				ib_camera.setVisibility(View.VISIBLE);
			else
				ib_camera.setVisibility(View.GONE);						
		}		
		// 设置下一个美言、壁纸显示
		SetVerseWallpaperShow(showVerseWallpaperFlag);
		// 电池充电量显示
		batteryValue = (TextView) this.findViewById(R.id.battery_value);	
		onBattery(status, level, scale);
	}
	
	public void onResume() {
		onBattery(status, level, scale);
	}

	public void onPause() {
		batteryHandler.sendEmptyMessage(MSG_STOP_ANIM);
	}

	public void onUpdate() {
		onBattery(status, level, scale);
	}	
	// 电池
	public void onBattery(int status, int level, int scale) {
		this.status = status;
		this.level = level;
		this.scale = scale;
		if (level == -1 || scale == -1 || status == -1) {
			return;
		}
		//animStart = getAnimStart(getBatteryPrecent(level, scale));
		if (isBatteryCharging(status)) {
			batteryHandler.sendEmptyMessage(MSG_START_ANIM);
		} else {
			batteryHandler.sendEmptyMessage(MSG_STOP_ANIM);
		}
	}
	public static boolean isBatteryCharging(int state) {
		boolean isCharing = false;
		switch (state) {
			case BatteryManager.BATTERY_STATUS_CHARGING:
				isCharing = true;
				break;
			case BatteryManager.BATTERY_STATUS_DISCHARGING:
				isCharing = false;
				break;
			case BatteryManager.BATTERY_STATUS_FULL:
				isCharing = true;
				break;
			case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
				isCharing = false;
				break;
			case BatteryManager.BATTERY_STATUS_UNKNOWN:
				isCharing = false;
				break;
		}
		return isCharing;
	}
	static final int ANIM_IMAGE_LEN = 17;
	static final int MSG_START_ANIM = 100;
	static final int MSG_STOP_ANIM = 200;
	static final int MSG_RUN_AIM = 300;	
	private boolean isAnim = false;
	private int status = -1;
	private int level = -1;
	private int scale = -1;
	private Handler batteryHandler = new BatteryHandler(this);
	
	static class BatteryHandler extends Handler {
		MineLockView lockView;

		public BatteryHandler(MineLockView lockView) {
			this.lockView = lockView;
		}

		public void handleMessage(Message msg) {
			int what = msg.what;
			if (what == MSG_START_ANIM) {
				lockView.batteryHandler.sendEmptyMessage(MSG_RUN_AIM);
			} else if (what == MSG_STOP_ANIM) {
				lockView.isAnim = false;
				lockView.batteryValue.setVisibility(View.INVISIBLE);
				//lockView.animStart = 0;
				//lockView.animIndex = 0;
			} else if (what == MSG_RUN_AIM) {
				if (!lockView.isAnim) {
					lockView.isAnim = true;
					lockView.batteryValue.setVisibility(View.VISIBLE);
					lockView.batteryHandler.post(lockView.batteryAnim);
				}
			}
		}
	}
	private Runnable batteryAnim = new Runnable() {
		public void run() {
			if (isAnim) {
/*				if (animIndex < animStart) {
					animIndex = animStart;
				}
				if (animIndex > animEnd) {
					animIndex = animStart;
				}
				batteryImage.setBackgroundResource(animImages[animIndex]);*/
				int value = (int) (100.0F * level / scale);
				String text = "";
				if (value == 100) {
					text = getResources().getString(R.string.charging_full);
				} else {
					text = getResources().getString(R.string.charging) + "(" + value + "%)";
				}
				batteryValue.setText(text);
/*				animIndex++;
				if (animIndex == ANIM_IMAGE_LEN) {
					// 最后一个，延迟一点
					batteryHandler.postDelayed(this, 1000);
				} else {
					batteryHandler.postDelayed(this, 1000);
				}*/
			}
		}
	};
	//
	private void SetVerseWallpaperShow(int showVerseFlag){
		String verse=sCustom;
		int verseQty = settings.getInt(VERSEQTY, 0);
		boolean bIdPath=false;
		int idPath;
		int id;
		String path;
		
		switch (showVerseFlag) {
		case 1:
			// 单句循环
			break;
		case 2:
			// 顺序循环
			if(verseQty>0 && verse.trim()!=""){
				// 获取当前美言id
				int verseId = (int)settings.getLong(VERSEID,0);			
				// 移动到下一位置										
				if(verseId+1>=verseQty)
					verseId = 0;
				else
					verseId = verseId+1;
				lockCursor.moveToPosition(verseId);	
				// 美言
				verse = lockCursor.getString(2);// + lockCursor.getString(1);	
				// 壁纸
				idPath = lockCursor.getInt(3);
				id = lockCursor.getInt(4);
				path = lockCursor.getString(5);
				// 将壁纸存入SharedPreferences
				if(idPath==1)
					bIdPath = true;
				editor.putBoolean(BOOLIDPATH, bIdPath);// 壁纸
				editor.putInt(WALLPAPERID, id);// 壁纸id
				editor.putString(WALLPAPERPATH, path);// 壁纸path
				// 将美言存入SharedPreferences				
				editor.putString(VERSE, verse);// 美言
				editor.putLong(VERSEID,(long)verseId);// 美言id	
				editor.commit();
			}
			break;	
		case 3:
			// 随机显示
			if(verseQty>0 && verse.trim()!=""){
				int random = (int)(Math.random()*verseQty);
				lockCursor.moveToPosition(random);
				// 美言
				verse = lockCursor.getString(2);// + lockCursor.getString(1);
				// 壁纸
				idPath = lockCursor.getInt(3);
				id = lockCursor.getInt(4);
				path = lockCursor.getString(5);
				// 将壁纸存入SharedPreferences
				if(idPath==1)
					bIdPath = true;
				editor.putBoolean(BOOLIDPATH, bIdPath);// 壁纸
				editor.putInt(WALLPAPERID, id);// 壁纸id
				editor.putString(WALLPAPERPATH, path);// 壁纸path				
				// 将美言存入SharedPreferences				
				editor.putString(VERSE, verse);// 美言
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
		//case KeyEvent.KEYCODE_HOME: return true;// 屏蔽home键		
		}			
			
		return super.onKeyDown(keyCode, event);
    }	
	// 解锁进入桌面
	public void unLock() {			
		Intent intent = new Intent(Intent.ACTION_MAIN);		
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);		

/*		if(banHomeKeyView.getParent()!=null)
			banHomeKeyWM.removeView(banHomeKeyView);	*/			
	}
	
	
}

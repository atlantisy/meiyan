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
	
	public static final String PREFS = "lock_pref";//pref�ļ���
	public static final String VERSE = "verse";//������ʽprefֵ����
	public static final String BOOLIDPATH = "wallpaper_idorpath";//Ӧ����or���ֽbool��prefֵ����,trueΪID��falseΪpath
	public static final String WALLPAPERID = "wallpaper_id";//Ӧ���ڱ�ֽ��ԴID��prefֵ����
	public static final String WALLPAPERPATH = "wallpaper_path";//Ӧ�����ֽPath��prefֵ����
	public static final String VERSEQTY = "verse_quantity";// ��������prefֵ����
	public static final String VERSEID = "verse_id";// ����id prefֵ����
	public static final String LOCKFLAG = "lockFlag";//������ʽprefֵ����
	public static final String SHOWVERSEFLAG = "showVerseFlag";//������ʾ��ʽprefֵ����
	public static final String PWSETUP = "passWordSetUp";//�Ź����Ƿ�����prefֵ����	
	
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
		// ��ȡprefֵ			
		settings = context.getSharedPreferences(PREFS, 0);
		editor = settings.edit();
		// ��ȡĬ�ϵ�prefs����
		defaultPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		defaultEditor = defaultPrefs.edit();
		// SQL���ݿ�
		dbRecent = new dbHelper(context);
		lockCursor = dbRecent.select();
		// ��ȡ���������������������ʾ��ʽ
		int showVerseFlag = settings.getInt(SHOWVERSEFLAG, 1);
		sCustom = settings.getString(VERSE, "�о��Լ�������  ");	
		// ����������ʾ
		SetVerseShow(showVerseFlag);
		sCustom = sCustom.trim();//ȥ��ǰ��ո�
		Log.d(TAG, sCustom);
		// ���ñ�ֽ		
		boolean bIdOrPath = settings.getBoolean(BOOLIDPATH, true);
		int wallpaperId = settings.getInt(WALLPAPERID, R.drawable.wallpaper00);
		String wallpaperPath = settings.getString(WALLPAPERPATH, "");	
		FrameLayout lockLayout = (FrameLayout)findViewById(R.id.LockLayout);
		if(bIdOrPath==true)//���ñ�ֽ			
			lockLayout.setBackgroundResource(wallpaperId);
		else{
			Bitmap bitmap = BitmapFactory.decodeFile(wallpaperPath);
			lockLayout.setBackgroundDrawable(new BitmapDrawable(bitmap));
		}				
		// �򵥻�����������������ʽ1
		viewVerse = (TextView) findViewById(R.id.tv_verse);
		viewVerse.setText(sCustom);
    	mScrollLayout = (MyScrollLayout) this.findViewById(R.id.mMyScrollLayout); 	  	 	
		// �Ź����ƽ�������������ʽ2
		ppwv = (PatternPassWordView) this.findViewById(R.id.mPatternPassWordView);
		// ��ȡ�洢��pref����  
		int flag = settings.getInt(LOCKFLAG, 1);
		boolean setPassword = settings.getBoolean(PWSETUP, false);
		// ����������ʽ����ʾ
		if(flag==2 & setPassword==true){			
			mScrollLayout.setVisibility(View.GONE);			
			ppwv.setVisibility(View.VISIBLE);			
		}
		else{
			mScrollLayout.setVisibility(View.VISIBLE);			
			ppwv.setVisibility(View.GONE);			
		}
		// ��س������ʾ
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
	// ���
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
				//lockView.batteryImage.setVisibility(View.GONE);
				lockView.batteryValue.setVisibility(View.GONE);
				//lockView.animStart = 0;
				//lockView.animIndex = 0;
			} else if (what == MSG_RUN_AIM) {
				if (!lockView.isAnim) {
					lockView.isAnim = true;
					//lockView.batteryImage.setVisibility(View.VISIBLE);
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
					// ���һ�����ӳ�һ��
					batteryHandler.postDelayed(this, 1000);
				} else {
					batteryHandler.postDelayed(this, 1000);
				}*/
			}
		}
	};
	//
	private void SetVerseShow(int showVerseFlag){
		int verseQty = settings.getInt(VERSEQTY, 0);
		switch (showVerseFlag) {
		case 1:
			// ����ѭ��
			break;
		case 2:
			// ˳��ѭ��
			if(verseQty>0 && sCustom.trim()!=""){
				// ��ȡ��ǰ����id
				int verseId = (int)settings.getLong(VERSEID,0);							
				// �ƶ�����һλ��
				if(verseId+1==verseQty)
					verseId=0;
				else
					verseId=verseId+1;
				lockCursor.moveToPosition(verseId);	
				sCustom = lockCursor.getString(1) + lockCursor.getString(2);	
				// �����Լ�id����SharedPreferences				
				editor.putString(VERSE, sCustom);// ����
				editor.putLong(VERSEID,(long)verseId);// ����id
				editor.commit();
			}
			break;	
		case 3:
			// �����ʾ
			if(verseQty>0 && sCustom.trim()!=""){
				int random = (int)(Math.random()*verseQty);
				lockCursor.moveToPosition(random);
				sCustom = lockCursor.getString(1) + lockCursor.getString(2);
				// �����Դ���SharedPreferences				
				editor.putString(VERSE, sCustom);// ����
				editor.putLong(VERSEID, random);// ����id
				editor.commit();						
			}
			break;
		default:
			break;
		}
	}	
	// ���η��ؼ���MENU��
	public boolean onKeyDown(int keyCode, KeyEvent event) {  		
		switch(keyCode){
		case KeyEvent.KEYCODE_MENU:return true;
		case KeyEvent.KEYCODE_BACK:return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN: return true;
		case KeyEvent.KEYCODE_VOLUME_UP: return true;		
		//case KeyEvent.KEYCODE_HOME: return true;// ����home��		
		}			
			
		return super.onKeyDown(keyCode, event);
    }	
	// ������������
	public void unLock() {			
		Intent intent = new Intent(Intent.ACTION_MAIN);		
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);		

/*		if(banHomeKeyView.getParent()!=null)
			banHomeKeyWM.removeView(banHomeKeyView);	*/			
	}	
	
}

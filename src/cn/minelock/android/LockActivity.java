package cn.minelock.android;

import java.util.ArrayList;

import cn.minelock.android.LockActivity.MyOnPageChangeListener;
import cn.minelock.widget.PatternPassWordView;
import cn.minelock.widget.dbHelper;
import cn.minelock.widget.PatternPassWordView.OnCompleteListener;

import cn.minelock.android.R;



import android.R.string;
import android.app.Activity;
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
	
	private PatternPassWordView ppwv=null;
	private Toast toast;
	private ImageView viewRightArrow;
	private ImageView viewLeftArrow;
	
	dbHelper dbRecent;
	private Cursor lockCursor;
	
	private SharedPreferences defaultPrefs = null;
	SharedPreferences.Editor defaultEditor = null;
	private final String LOCK_STATUS = "lock_status";
	private boolean mLockStatus;
	
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
		
		// ��ȡprefֵ			
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
		SharedPreferences.Editor editor = settings.edit();
		// ��ȡĬ�ϵ�prefs����
		defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		defaultEditor = defaultPrefs.edit();
		// SQL���ݿ�
		dbRecent = new dbHelper(this);
		lockCursor = dbRecent.select();
		// ��ȡ���������������������ʾ��ʽ
		int verseQty = settings.getInt(VERSEQTY, 0);
		int showVerseFlag = settings.getInt(SHOWVERSEFLAG, 1);
		sCustom = settings.getString(VERSE, "�о��Լ�������  ");	
		// ��������
		switch (showVerseFlag) {
		case 1:
			// ����ѭ��
			break;
		case 2:
			// ˳��ѭ��
			if(verseQty>0 & sCustom.trim()!=""){
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
			if(verseQty>0 & sCustom.trim()!=""){
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
		sCustom = sCustom.trim();//ȥ��ǰ��ո�
		Log.d(TAG, sCustom);
		// ���ñ�ֽ		
		boolean bIdOrPath = settings.getBoolean(BOOLIDPATH, true);
		int wallpaperId = settings.getInt(WALLPAPERID, R.drawable.wallpaper00);
		String wallpaperPath = settings.getString(WALLPAPERPATH, "");	
		if(bIdOrPath==true)//���ñ�ֽ			
			lockLayout.setBackgroundResource(wallpaperId);
		else{
			Bitmap bitmap = BitmapFactory.decodeFile(wallpaperPath);
			lockLayout.setBackgroundDrawable(new BitmapDrawable(bitmap));
		}		
		
		// �򵥻�����������������ʽ1
		InitViewPager();
		// �Ź����ƽ�������������ʽ2
		ppwv = (PatternPassWordView) this.findViewById(R.id.mPatternPassWordView);
		ppwv.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(String mPassword) {
				// ���������ȷ,�������ҳ�档
				if (ppwv.verifyPassword(mPassword)) {
					//showToast("�����ɹ���");
					unLock();
					mLockStatus=false;
				} else {
					showToast("���ƴ���,����������");
					ppwv.clearPassword();
					mLockStatus=true;
				}
				saveLockStatus();//��������״̬
			}
		});
		
		// ��ȡ�洢��pref����  
		int flag = settings.getInt(LOCKFLAG, 1);
		boolean setPassword = settings.getBoolean(PWSETUP, false);
		// ����������ʽ����ʾ
		if(flag==2 & setPassword==true){			
			mPager.setVisibility(View.GONE);
			ppwv.setVisibility(View.VISIBLE);			
		}
		else{
			mPager.setVisibility(View.VISIBLE);
			ppwv.setVisibility(View.GONE);			
		}
	}

	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.viewpager);

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
				unLock();
				mLockStatus = false;
				break;
			case 2:
				Intent cameraIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, 1);
				unLock();
				mLockStatus = false;
				break;
			default:
				mLockStatus = true;				
				break;
			}
			saveLockStatus();//��������״̬
			currIndex = arg0;
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			viewRightArrow = (ImageView) findViewById(R.id.iv_arrow_right);
			viewLeftArrow = (ImageView) findViewById(R.id.iv_arrow_left);
			if (arg1>0.0 | arg2>0){
				viewRightArrow.setVisibility(0);//0:VISIBILITY 4:INVISIBILITY
				viewLeftArrow.setVisibility(4);
			}
			else{
				viewRightArrow.setVisibility(0);//
				viewLeftArrow.setVisibility(4);
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			viewRightArrow = (ImageView) findViewById(R.id.iv_arrow_right);
			viewLeftArrow = (ImageView) findViewById(R.id.iv_arrow_left);	
			if (arg0==1){
				viewRightArrow.setVisibility(0);//0:VISIBILITY 4:INVISIBILITY
				viewLeftArrow.setVisibility(4);
			}
			else{
				viewRightArrow.setVisibility(0);//
				viewLeftArrow.setVisibility(4);
			}
		}
	}
	
	// ���η��ؼ���MENU��
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
/*		case KeyEvent.KEYCODE_CALL:return true;
		case KeyEvent.KEYCODE_SYM: return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN: return true;
		case KeyEvent.KEYCODE_VOLUME_UP: return true;
		case KeyEvent.KEYCODE_STAR: return true;*/
		}
		
		return super.onKeyDown(keyCode, event);
    } 
	
	// ����home����android4.0���ϲ�����
/*    public void onAttachedToWindow() { 
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);  
        super.onAttachedToWindow(); 
    }*/
    
    // ��������������
	public void unLock() {
		finish();
	}
	
	// ��������״̬
	public void saveLockStatus(){
		defaultEditor.putBoolean(LOCK_STATUS, mLockStatus);
		defaultEditor.commit();
	}
	

}
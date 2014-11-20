package cn.minelock.android;

import java.util.ArrayList;

import cn.minelock.widget.LockLayer;
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
import android.widget.Toast;

public class MineLockView extends FrameLayout{

	private static final String TAG = "MeiYan";
	private static final int IDTAG = -1;
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentsList;
	private int currIndex = 1;
	private String sCustom = "";	
	
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
	
	View banHomeKeyView;
	WindowManager banHomeKeyWM;	
	LockLayer lockLayer;
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
		mPager = (ViewPager) findViewById(R.id.viewpager);
		//InitViewPager();
		// �Ź����ƽ�������������ʽ2
		ppwv = (PatternPassWordView) this.findViewById(R.id.mPatternPassWordView);
		ppwv.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(String mPassword) {
				// ���������ȷ,�������ҳ�档
				if (ppwv.verifyPassword(mPassword)) {
					//showToast("�����ɹ���");
					mLockStatus=false;
					saveLockStatus();//��������״̬
					unLock();					
				} else {
					mLockStatus=true;
					saveLockStatus();//��������״̬
					showToast("���ƴ���,����������");
					ppwv.clearPassword();					
				}
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
			mPager.setSelected(true);
			ppwv.setVisibility(View.GONE);			
		}
				
	}
	
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
	
/*	private void InitViewPager() {		
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

	}*/

/*	public class MyOnPageChangeListener implements OnPageChangeListener {

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
				//finish();								
				break;
			default:
				mLockStatus = true;	
				//unLock();
				break;
			}
			saveLockStatus();//��������״̬
			currIndex = arg0;
			
		}	
	}*/
	
	// ���η��ؼ���MENU��
	public boolean onKeyDown(int keyCode, KeyEvent event) {  		
		switch(keyCode){
		case KeyEvent.KEYCODE_MENU:return true;
		case KeyEvent.KEYCODE_BACK:return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN: return true;
		case KeyEvent.KEYCODE_VOLUME_UP: return true;		
		case KeyEvent.KEYCODE_HOME: return true;// ����home��		
		}			
			
		return super.onKeyDown(keyCode, event);
    } 
	
	// ��ȡandroid�汾��
	public static int getSDKVersion() { 
		int version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
		return version; 
	}
	
	//��������Ӧ��  
    private void launchSms() {    
        //mFocusView.setVisibility(View.GONE);  
        Intent intent = new Intent();  
        ComponentName comp = new ComponentName("com.android.mms",  
                "com.android.mms.ui.ConversationList");  
        intent.setComponent(comp);  
        intent.setAction("android.intent.action.VIEW");  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);  
        this.getContext().startActivity(intent);  
    }       
    //��������Ӧ��  
    private void launchDial() {  
        Intent intent = new Intent(Intent.ACTION_DIAL);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);  
        this.getContext().startActivity(intent);  
    }
    //�������Ӧ��  
    private void launchCamera() {      	
    	Intent intent = new Intent();                 	
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);        
		
        this.getContext().startActivity(intent); 
    }	
	
	// ������������
	public void unLock() {	
		Intent intent = new Intent(Intent.ACTION_MAIN);		
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);		

		if (context instanceof Activity) {
			Activity act = (Activity) context;
			act.finish();
		}		
		
		banHomeKeyWM.removeView(banHomeKeyView);
		
		Intent i = new Intent(context, MyLockScreenService.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(i);
					    				
	}	
	
	// ��������״̬
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

package cn.minelock.android;



import java.io.File;

import cn.minelock.android.BatteryObserver;
import cn.minelock.android.BatteryObserver.OnBatteryChange;
import cn.minelock.android.R;
import cn.minelock.util.StringUtil;
import cn.minelock.widget.MyScrollLayout;
import cn.minelock.widget.MyScrollLayout.OnViewChangeListener;
import cn.minelock.widget.PatternPassWordView;
import cn.minelock.widget.PatternPassWordView.OnCompleteListener;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class MyLockScreenService extends Service {
	private final String ACT_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
	//private final String ACT_SCREEN_ON = "android.intent.action.SCREEN_ON";
	
	private SharedPreferences prefs = null;
	private SharedPreferences.Editor editor = null;
	private final String LOCK_SWITCH = "lock_screen_switch";
	private final String LOCK_STATUS = "lock_status";
	private boolean mIsLockScreenOn = true;
	private boolean mLockStatus = false;
		
	//���帡�����ڲ���
	private WindowManager.LayoutParams wmParams;
    private View mFloatLayout;
    //���������������ò��ֲ����Ķ���
    private WindowManager mWindowManager;
	
    private PatternPassWordView mGridView;
    private TextView errorView;
    private MyScrollLayout mLineView;
	
       
    @Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.e("", "***********onBind MyLockScreenService");
		return null;
		//return myBinder;
	}
	
	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		Log.e("", "***********onReBind MyLockScreenService");
		super.onRebind(intent);
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Log.e("", "***********onUnBind MyLockScreenService");
		return super.onUnbind(intent);
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		startForegroundCompat();
					
		// ��ȡ�����prefs����
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		editor = prefs.edit();
		mIsLockScreenOn = prefs.getBoolean(LOCK_SWITCH, true);
				
		// register Broadcast
		Log.e("", "***********onCreate registerReceiver");
        if(mIsLockScreenOn){
        	IntentFilter intentFilter= new IntentFilter(ACT_SCREEN_OFF);
        	registerReceiver(mScreenBCR, intentFilter);
        }
	}
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// �Ƴ�������
		if(mFloatLayout != null)
		{
			mWindowManager.removeView(mFloatLayout);
			mFloatLayout = null;
		}
		// ���Ϩ���㲥service
		unregisterReceiver(mScreenBCR);		
		// ��ԭ����״̬������
		editor.putBoolean(LOCK_STATUS, false);
		editor.commit();
		//������ʱ�����������������ں�̨���
        if(mIsLockScreenOn){
        	IntentFilter intentFilter= new IntentFilter(ACT_SCREEN_OFF);
        	registerReceiver(mScreenBCR, intentFilter);        	
        }
        
        batteryObserver.unRegister();
                
        //stopForeground(true);
        //startForegroundCompat();
        Log.e("", "***********onDestroy registerReceiver");
        
        startService(new Intent(this, MyLockScreenService.class));
	}
	
	private BroadcastReceiver mScreenBCR = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.e("", "***********onReceive Intent="+intent);
			{
				try{
					mLockStatus = prefs.getBoolean(LOCK_STATUS, false);
					if(!mLockStatus){
						mLockStatus = true;						
						saveLockStatus();
						
						//�����ֻ����õ����� 
						KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);    
						km.newKeyguardLock("MineLock").disableKeyguard();						 			            
			            
			            CreateFloatView();
					}												            
				}catch (Exception e) {
					// TODO: handle exception
					Log.e("", "***********onReceive Error="+e);
				}
			}
		}
	};
	
	BatteryObserver batteryObserver;
	MineLockView root;
	// ����ȫ��������������
	public void CreateFloatView(){
		mFloatLayout = View.inflate(getApplicationContext(), R.layout.minelock_layout, null);  
		mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);  
		wmParams = new WindowManager.LayoutParams();   
		wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;  
		wmParams.height = WindowManager.LayoutParams.MATCH_PARENT; 
		
		wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;//
		wmParams.flags = 1280;//����״̬��    FLAG_FULLSCREEN|FLAG_LAYOUT_IN_SCREEN						
		//wmParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		// ��ʾ��������
		mWindowManager.addView(mFloatLayout, wmParams); 
		// �������ʾ
		root = (MineLockView)mFloatLayout.findViewById(R.id.LockLayout);
		batteryObserver = BatteryObserver.getInstance(this);
		batteryObserver.register();
		batteryObserver.setOnBatteryChange(new OnBatteryChange() {
			
			@Override
			public void onChange(int status, int level, int scale) {
				// TODO Auto-generated method stub
				root.onBattery(status, level, scale);
				root.onUpdate();
			}
		});
		// �򵥻�������
		mLineView = (MyScrollLayout)mFloatLayout.findViewById(R.id.mMyScrollLayout);
		mLineView.SetOnViewChangeListener(new OnViewChangeListener() {
			
			@Override
			public void OnViewChange(int view) {
				// TODO Auto-generated method stub
				switch (view) {
				case 0:
					mLockStatus = false;
					unLock();
					// ��������
/*					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.addCategory(Intent.CATEGORY_HOME);
					startActivity(intent);*/
					break;
				case 2:				
					mLockStatus = false;
					unLock();
					//
					SharedPreferences settings = getSharedPreferences("lock_pref", 0);
					if(settings.getBoolean("leftCamera", false)){
						// �����
						launchCamera();
					}
/*					else{
						// ��������
						Intent i = new Intent(Intent.ACTION_MAIN);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						i.addCategory(Intent.CATEGORY_HOME);
						startActivity(i);
					}*/
					break;
				default:
					mLockStatus = true;
					break;
				}
				saveLockStatus();//��������״̬			
			}
		});
		// �Ź������
		errorView = (TextView)mFloatLayout.findViewById(R.id.error_view);
		mGridView = (PatternPassWordView)mFloatLayout.findViewById(R.id.mPatternPassWordView);
		mGridView.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(String mPassword) {
				// ���������ȷ,�������ҳ�档
				if (mGridView.verifyPassword(mPassword)) {
					mLockStatus=false;
					unLock();					
				} else {
					mLockStatus=true;
					mGridView.clearPassword();	
					errorView.setVisibility(View.VISIBLE);
				}
				saveLockStatus();//��������״̬
			}
		});
		// �����Զ�����
		TelephonyManager manager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
		manager.listen(new MyPhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);
		
	}
	
	// ����
	public void unLock(){
		Log.d("", "unLock");
		// �Ƴ�������
/*		if(mFloatLayout != null)
		{*/
			mWindowManager.removeView(mFloatLayout);
			mFloatLayout = null;
/*		}*/
	}
	
	// ��������״̬
	public void saveLockStatus(){
		editor.putBoolean(LOCK_STATUS, mLockStatus);
		editor.commit();
	}

	// ��ȡandroid SDK�汾��
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
        this.startActivity(intent);  
    }       
    //��������Ӧ��  
    private void launchDial() {  
        Intent intent = new Intent(Intent.ACTION_DIAL);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);  
        this.startActivity(intent);  
    }
    //�������Ӧ��  
    private void launchCamera() {      	
    	Intent intent = new Intent();                 	
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);  
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera";					
        String wallpaperPath = dir + "/" + "IMG" + StringUtil.makeFileName() + ".jpg"; 
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(wallpaperPath)));

        this.startActivity(intent);        
    }	
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	// TODO Auto-generated method stub    	
    	//startForeground(1120, new Notification());
    	//startForegroundCompat();//����serviceΪǰ��
    	
    	flags = START_STICKY;//START_STICKY��service��kill�����Զ���д����
    	return super.onStartCommand(intent, flags, startId); 
    }
    
    private void startForegroundCompat() {
        try {
            if (Build.VERSION.SDK_INT < 18) {
                //Log.v(TAG, "startForgroundCompat");
                startForeground(1235, new Notification());// 1120
            }
        } catch (Exception e) {
            //if (DEBUG) Log.e(TAG, "", e);
        }
    }

    private class MyPhoneListener extends PhoneStateListener{
    	@Override
    	public void onCallStateChanged(int state, String incomingNumber) {
    		// TODO Auto-generated method stub
    		try {
				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:
					mLockStatus=false;
					editor.putBoolean(LOCK_STATUS, mLockStatus);
					editor.commit();
					
					unLock();
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					//mLockStatus=false;
				default:
					//mLockStatus=true;
					break;
					
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
    		
    		super.onCallStateChanged(state, incomingNumber);
    	}
    }
}

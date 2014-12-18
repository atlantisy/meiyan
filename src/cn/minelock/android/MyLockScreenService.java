package cn.minelock.android;



import cn.minelock.android.BatteryObserver;
import cn.minelock.android.BatteryObserver.OnBatteryChange;
import cn.minelock.widget.MyScrollLayout;
import cn.minelock.widget.MyScrollLayout.OnViewChangeListener;
import cn.minelock.widget.PatternPassWordView;
import cn.minelock.widget.PatternPassWordView.OnCompleteListener;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MyLockScreenService extends Service {
	private final String ACT_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
	private final String ACT_SCREEN_ON = "android.intent.action.SCREEN_ON";
	
	private SharedPreferences prefs = null;
	private SharedPreferences.Editor editor = null;
	private final String LOCK_SWITCH = "lock_screen_switch";
	private final String LOCK_STATUS = "lock_status";
	private boolean mIsLockScreenOn = true;
	private boolean mLockStatus = false;
		
	//定义浮动窗口布局
    WindowManager.LayoutParams wmParams;
    View mFloatLayout;
    //创建浮动窗口设置布局参数的对象
	WindowManager mWindowManager;
	
	PatternPassWordView mGridView;
	TextView errorView;
	MyScrollLayout mLineView;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.e("", "***********onBind MyLockScreenService");
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		// 获取保存的prefs数据
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
		// 移除悬浮窗
		if(mFloatLayout != null)
		{
			mWindowManager.removeView(mFloatLayout);
			mFloatLayout = null;
		}
		// 解除熄屏广播service
		unregisterReceiver(mScreenBCR);
		// 还原锁屏状态并保存
		editor.putBoolean(LOCK_STATUS, false);
		editor.commit();
		//被销毁时启动自身，保持自身在后台存活
        if(mIsLockScreenOn){
        	IntentFilter intentFilter= new IntentFilter(ACT_SCREEN_OFF);
        	registerReceiver(mScreenBCR, intentFilter);        	
        }
        
        batteryObserver.unRegister();
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
						
						//屏蔽手机内置的锁屏 
						KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);    
						KeyguardLock kl = km.newKeyguardLock("MineLock");						 
			            kl.disableKeyguard();			            
			            
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
	// 创建全屏悬浮锁屏窗口
	public void CreateFloatView(){
		mFloatLayout = View.inflate(getApplicationContext(), R.layout.minelock_layout, null);  
		mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);  
		wmParams = new WindowManager.LayoutParams();   
		wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;  
		wmParams.height = WindowManager.LayoutParams.MATCH_PARENT; 
		
		wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;//
		wmParams.flags = 1280;//隐藏状态栏    FLAG_FULLSCREEN|FLAG_LAYOUT_IN_SCREEN						
		//wmParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		// 显示锁屏界面
		mWindowManager.addView(mFloatLayout, wmParams); 
		// 充电量显示
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
		// 简单滑动解锁
		mLineView = (MyScrollLayout)mFloatLayout.findViewById(R.id.mMyScrollLayout);
		mLineView.SetOnViewChangeListener(new OnViewChangeListener() {
			
			@Override
			public void OnViewChange(int view) {
				// TODO Auto-generated method stub
				switch (view) {
				case 0:
					mLockStatus = false;
					unLock();			
					break;
				case 2:				
					mLockStatus = false;
					unLock();
					//launchCamera();							
					break;
				default:
					mLockStatus = true;
					break;
				}
				saveLockStatus();//保存锁屏状态			
			}
		});
		// 九宫格解锁
		errorView = (TextView)mFloatLayout.findViewById(R.id.error_view);
		mGridView = (PatternPassWordView)mFloatLayout.findViewById(R.id.mPatternPassWordView);
		mGridView.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(String mPassword) {
				// 如果密码正确,则进入主页面。
				if (mGridView.verifyPassword(mPassword)) {
					mLockStatus=false;
					unLock();					
				} else {
					mLockStatus=true;
					mGridView.clearPassword();	
					errorView.setVisibility(View.VISIBLE);
				}
				saveLockStatus();//保存锁屏状态
			}
		});		
		
	}
	
	// 解锁
	public void unLock(){
		// 移除悬浮窗
		if(mFloatLayout != null)
		{
			mWindowManager.removeView(mFloatLayout);
			mFloatLayout = null;
		}
	}	
	// 保存锁屏状态
	public void saveLockStatus(){
		editor.putBoolean(LOCK_STATUS, mLockStatus);
		editor.commit();
	}

	// 获取android SDK版本号
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
        this.startActivity(intent);  
    }       
    //启动拨号应用  
    private void launchDial() {  
        Intent intent = new Intent(Intent.ACTION_DIAL);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);  
        this.startActivity(intent);  
    }
    //启动相机应用  
    private void launchCamera() {      	
    	Intent intent = new Intent();                 	
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);        		
        this.startActivity(intent); 
        
    }	

}

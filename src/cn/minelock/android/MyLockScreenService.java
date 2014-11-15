package cn.minelock.android;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.CheckBox;

public class MyLockScreenService extends Service {
	private final String ACT_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
	private final String ACT_SCREEN_ON = "android.intent.action.SCREEN_ON";
	
	private SharedPreferences prefs = null;
	private final String LOCK_SWITCH = "lock_screen_switch";
	private final String LOCK_STATUS = "lock_status";
	private boolean mIsLockScreenOn = true;
	private boolean mLockStatus = false;
	
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
		unregisterReceiver(mScreenBCR);
		//被销毁时启动自身，保持自身在后台存活
        if(mIsLockScreenOn){
        	IntentFilter intentFilter= new IntentFilter(ACT_SCREEN_OFF);
        	registerReceiver(mScreenBCR, intentFilter);
        }
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
						SharedPreferences.Editor editor = prefs.edit();
						editor.putBoolean(LOCK_STATUS, mLockStatus);
						editor.commit();
						
						//屏蔽手机内置的锁屏 
						KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);    
						KeyguardLock kl = km.newKeyguardLock("MineLock");						 
			            kl.disableKeyguard();
			            
						Intent i = new Intent();  
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
						i.setClass(context, LockActivity.class); 
						context.startActivity(i);						
					}												            
				}catch (Exception e) {
					// TODO: handle exception
					Log.e("", "***********onReceive Error="+e);
				}
			}
		}
	};

}

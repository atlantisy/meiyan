package cn.minelock.android;

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
	private boolean mIsLockScreenOn = true;
	
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
		
		// ��ȡ�����prefs����
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
	}
	
	private BroadcastReceiver mScreenBCR = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.e("", "***********onReceive Intent="+intent);
			{
				try{
					Intent i = new Intent();  
		            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		            i.setClass(context, LockActivity.class); 
		            context.startActivity(i);
				}catch (Exception e) {
					// TODO: handle exception
					Log.e("", "***********onReceive Error="+e);
				}
			}
		}
	};

}

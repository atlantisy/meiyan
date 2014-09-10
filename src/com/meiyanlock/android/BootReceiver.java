package com.meiyanlock.android;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		final Intent mIntent = new Intent(context, MyLockScreenService.class);
		final Context mContext = context;
		
		if (intent.getAction().equals(ACTION) & SettingActivity.mIsLockScreenOn==true) {
//			Toast.makeText(context, "OlympicsReminder service has started!",
//					Toast.LENGTH_LONG).show();
//			Timer timer = new Timer();
//				timer.schedule(new TimerTask() {
//				public void run() {
//					mContext.startService(mIntent);
//					this.cancel();
//				}
//			}, 30000);
			mContext.startService(mIntent);
		}
	}
}

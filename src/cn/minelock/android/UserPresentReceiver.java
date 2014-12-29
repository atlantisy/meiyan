package cn.minelock.android;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class UserPresentReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		final Intent mIntent = new Intent(context, MyLockScreenService.class);
		final Context mContext = context;
		
		if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)||intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)
				||intent.getAction().equals(Intent.ACTION_TIME_CHANGED)) {	
			
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					mContext.startService(mIntent);
					this.cancel();
				}
			}, 30000);
			
			//mContext.startService(mIntent);
		}
	}
}

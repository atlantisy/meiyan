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

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub				
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("lock_status", false);// ÷ÿ∆Ù∫Û…Ë÷√À¯∆¡◊¥Ã¨
		editor.commit();

		final Intent mIntent = new Intent(context, MyLockScreenService.class);
		final Context mContext = context;
		
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {	
			
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

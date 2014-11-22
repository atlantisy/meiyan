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

	private final String LOCK_STATUS = "lock_status";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		final Intent mIntent = new Intent(context, MyLockScreenService.class);
		final Context mContext = context;
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(LOCK_STATUS, false);// ��������������״̬
		editor.commit();
		
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {	
			
/*			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					mContext.startService(mIntent);
					this.cancel();
				}
			}, 30000);*/
			
			mContext.startService(mIntent);
		}
	}
}

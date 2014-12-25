package cn.minelock.android;

import android.content.ComponentName;
import android.os.IBinder;
import android.util.Log;
import android.content.ServiceConnection;

public class MyServiceConnection implements ServiceConnection {

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		// TODO Auto-generated method stub
		Log.d("info", "Service Connection Failed");
		//成功连接服务，该方法被执行。在该方法中可以通过IBinder对象取得onBind方法的返回值，一般通过向下转型
	}
	
	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		// TODO Auto-generated method stub
		Log.d("info", "Service Connection Success");
		//连接失败执行
	}
}

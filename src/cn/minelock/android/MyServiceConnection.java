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
		//�ɹ����ӷ��񣬸÷�����ִ�С��ڸ÷����п���ͨ��IBinder����ȡ��onBind�����ķ���ֵ��һ��ͨ������ת��
	}
	
	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		// TODO Auto-generated method stub
		Log.d("info", "Service Connection Success");
		//����ʧ��ִ��
	}
}

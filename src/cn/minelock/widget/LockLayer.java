package cn.minelock.widget;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class LockLayer {
	private Activity mActivty;
	private WindowManager mWindowManager;
	private View mLockView;
	private LayoutParams mLockViewLayoutParams;
	private static LockLayer mLockLayer;
	private boolean isLocked;
	private final static int FLAG_APKTOOL_VALUE = 1280;
	
	public static synchronized LockLayer getInstance(Activity act){
		if(mLockLayer == null){
			mLockLayer = new LockLayer(act);
		}
		return mLockLayer;
	}
	
	private LockLayer(Activity act) {
		mActivty = act;
		init();
	}

	private void init(){
		isLocked = false;
		mWindowManager = mActivty.getWindowManager();
		mLockViewLayoutParams = new LayoutParams();
		mLockViewLayoutParams.width = LayoutParams.MATCH_PARENT;
		mLockViewLayoutParams.height = LayoutParams.MATCH_PARENT;
		//��һ��ʵ������Home
		mLockViewLayoutParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
		mLockViewLayoutParams.flags = FLAG_APKTOOL_VALUE;//����ʾ״̬��
	}
	public synchronized void lock() {
		if(mLockView!=null&&!isLocked){
			mWindowManager.addView(mLockView, mLockViewLayoutParams);
		}
		isLocked = true;
	}
	public synchronized void unlock() {
		if(mWindowManager!=null&&isLocked){
			mWindowManager.removeView(mLockView);
		}
		isLocked = false;
	}
	public synchronized void setLockView(View v){
		mLockView = v;
	}
}

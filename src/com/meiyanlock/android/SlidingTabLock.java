package com.meiyanlock.android;

import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SlidingTabLock extends RelativeLayout implements OnTouchListener {

	static final int UNLOCK_DIST = 30;
	IPhoneLockView lockView;
	ImageView unLockBlock;

	Context context;
	float startX, startY;
	float nowX, nowY;
	Vibrator vibrator;
	boolean isInit = false;
	int startScrollX, startScrollY;

	boolean mAnimate = false;

	public SlidingTabLock(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	protected void onFinishInflate() {
		super.onFinishInflate();
		//unLockBlock = (ImageView) findViewById(R.id.un_lock_block);
		unLockBlock.setLongClickable(true);
		unLockBlock.setOnTouchListener(this);
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	}

	public void setLockView(IPhoneLockView lockview) {
		this.lockView = lockview;
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (!isInit) {
			startScrollX = getScrollX();
			startScrollY = getScrollY();
			isInit = true;
		}
		if (mAnimate) {
			return false;
		}
		int action = event.getAction();
		nowX = event.getX();
		nowY = event.getY();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				startX = event.getX();
				startY = event.getY();
				vibrator.vibrate(30);
				break;
			case MotionEvent.ACTION_MOVE:
				int dx = (int) (nowX - startX);
				int x = getScrollX();
				x = x - dx;
				int max = getWidth() - getPaddingLeft() - getPaddingRight() - unLockBlock.getWidth();
				if (x > 0) {
					scrollTo(0, 0);
				} else if (x < -max) {
					scrollTo(-max, 0);
				} else {
					scrollBy(-dx, 0);
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				// getScrollX()为负数，所以用加号
				if (getWidth() - unLockBlock.getWidth() + getScrollX() < UNLOCK_DIST) {
					int i=0;
					//lockView.unLock();
				}
				scrollTo(0, 0);
				startX = nowX = 0;
				nowX = nowY = 0;
				break;
		}
		return false;
	}
}
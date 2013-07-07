package org.liushui.iphone;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class IPhoneLockView extends FrameLayout implements OnClickListener, ILife {
	private Context context;
	private Button button;
	private TextView tvDate;
	private SlidingTabLock slidingTabLock;
	private ImageView batteryImage;
	private TextView batteryValue;
	static final int ANIM_IMAGE_LEN = 17;
	static final int MSG_START_ANIM = 100;
	static final int MSG_STOP_ANIM = 200;
	static final int MSG_RUN_AIM = 300;

	private int[] animImages;
	private int animStart = 0;
	private int animEnd = 0;
	private int animIndex = 0;
	private boolean isAnim = false;
	private DigitalClock clock;

	private Handler handler = new Handler();
	private int index = -4;
	private String text;
	private int len;
	private TextView tvSlideUnlock;

	private int status = -1;
	private int level = -1;
	private int scale = -1;

	public IPhoneLockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	protected void onFinishInflate() {
		super.onFinishInflate();
		findViews();
		registerListener();
		setValues();
	}

	private void findViews() {
		button = (Button) findViewById(R.id.button1);
		tvDate = (TextView) findViewById(R.id.date);
		batteryImage = (ImageView) findViewById(R.id.energy_display);
		batteryValue = (TextView) findViewById(R.id.battery_value);
		clock = (DigitalClock) findViewById(R.id.time);
		slidingTabLock = (SlidingTabLock) findViewById(R.id.slidingTabLock);
		tvSlideUnlock = (TextView) findViewById(R.id.tv_slide_unlock);
		slidingTabLock.setLockView(this);
	}

	private void registerListener() {
		button.setOnClickListener(this);
	}

	private void setValues() {
		animEnd = ANIM_IMAGE_LEN - 1;
		animImages = new int[ANIM_IMAGE_LEN];
		for (int i = 0; i < 17; i++) {
			animImages[i] = context.getResources().getIdentifier("battery_" + (i + 1), "drawable", context.getPackageName());
		}
		reSetValues();
	}

	private void reSetValues() {
		refreshTimeAndDateDisplay();
		clock.updateTime();
		startIndicateAnimation();
		onBattery(status, level, scale);
	}

	public void onResume() {
		reSetValues();
	}

	public void onPause() {
		stopIndicateAnimation();
		batteryHandler.sendEmptyMessage(MSG_STOP_ANIM);
	}

	public void onUpdate() {
		reSetValues();
	}

	public void onBattery(int status, int level, int scale) {
		this.status = status;
		this.level = level;
		this.scale = scale;
		if (level == -1 || scale == -1 || status == -1) {
			return;
		}
		animStart = getAnimStart(getBatteryPrecent(level, scale));
		if (isBatteryCharging(status)) {
			batteryHandler.sendEmptyMessage(MSG_START_ANIM);
		} else {
			batteryHandler.sendEmptyMessage(MSG_STOP_ANIM);
		}
	}

	public static boolean isBatteryCharging(int state) {
		boolean isCharing = false;
		switch (state) {
			case BatteryManager.BATTERY_STATUS_CHARGING:
				isCharing = true;
				break;
			case BatteryManager.BATTERY_STATUS_DISCHARGING:
				isCharing = false;
				break;
			case BatteryManager.BATTERY_STATUS_FULL:
				isCharing = true;
				break;
			case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
				isCharing = false;
				break;
			case BatteryManager.BATTERY_STATUS_UNKNOWN:
				isCharing = false;
				break;
		}
		return isCharing;
	}

	private Handler batteryHandler = new BatteryHandler(this);

	static class BatteryHandler extends Handler {
		IPhoneLockView lockView;

		public BatteryHandler(IPhoneLockView lockView) {
			this.lockView = lockView;
		}

		public void handleMessage(Message msg) {
			int what = msg.what;
			if (what == MSG_START_ANIM) {
				lockView.batteryHandler.sendEmptyMessage(MSG_RUN_AIM);
			} else if (what == MSG_STOP_ANIM) {
				lockView.isAnim = false;
				lockView.batteryImage.setVisibility(View.GONE);
				lockView.batteryValue.setVisibility(View.GONE);
				lockView.animStart = 0;
				lockView.animIndex = 0;
			} else if (what == MSG_RUN_AIM) {
				if (!lockView.isAnim) {
					lockView.isAnim = true;
					lockView.batteryImage.setVisibility(View.VISIBLE);
					lockView.batteryValue.setVisibility(View.VISIBLE);
					lockView.batteryHandler.post(lockView.batteryAnim);
				}
			}
		}
	}

	private Runnable batteryAnim = new Runnable() {
		public void run() {
			if (isAnim) {
				if (animIndex < animStart) {
					animIndex = animStart;
				}
				if (animIndex > animEnd) {
					animIndex = animStart;
				}
				batteryImage.setBackgroundResource(animImages[animIndex]);
				int value = (int) (100.0F * level / scale);
				String text = "";
				if (value == 100) {
					text = getResources().getString(R.string.charging_full);
				} else {
					text = getResources().getString(R.string.charging) + "(" + value + "%)";
				}
				batteryValue.setText(text);
				animIndex++;
				if (animIndex == ANIM_IMAGE_LEN) {
					// 最后一个，延迟一点
					batteryHandler.postDelayed(this, 1000);
				} else {
					batteryHandler.postDelayed(this, 1000);
				}
			}
		}
	};

	private int getBatteryPrecent(int level, int scale) {
		int b = (int) (level * 100.0 / scale);
		return b;
	}

	private int getAnimStart(int batteryPrecent) {
		int idx = -1;
		if (batteryPrecent >= 100) {
			idx = 17;
		} else if (batteryPrecent >= 96) {
			idx = 16;
		} else if (batteryPrecent >= 90) {
			idx = 15;
		} else if (batteryPrecent >= 85) {
			idx = 14;
		} else if (batteryPrecent >= 80) {
			idx = 13;
		} else if (batteryPrecent >= 75) {
			idx = 12;
		} else if (batteryPrecent >= 70) {
			idx = 11;
		} else if (batteryPrecent >= 60) {
			idx = 10;
		} else if (batteryPrecent >= 50) {
			idx = 9;
		} else if (batteryPrecent >= 45) {
			idx = 8;
		} else if (batteryPrecent >= 40) {
			idx = 7;
		} else if (batteryPrecent >= 30) {
			idx = 6;
		} else if (batteryPrecent >= 25) {
			idx = 5;
		} else if (batteryPrecent >= 20) {
			idx = 4;
		} else if (batteryPrecent >= 15) {
			idx = 3;
		} else if (batteryPrecent >= 10) {
			idx = 2;
		} else if (batteryPrecent >= 5) {
			idx = 1;
		}
		return idx - 1;
	}

	private void refreshTimeAndDateDisplay() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		tvDate.setText(sdf.format(new Date()));
	}

	private void startIndicateAnimation() {
		if (!isRun) {
			handler.postDelayed(task, 200);
		}
		isRun = true;
	}

	private void stopIndicateAnimation() {
		if (isRun) {
			handler.removeCallbacks(task);
		}
		isRun = false;
	}

	boolean isRun = false;
	boolean isCalcuteTextSize = false;
	Runnable task = new Runnable() {

		public void run() {
			if (index == len) {
				index = -4;
			}
			text = getResources().getString(R.string.slide_to_unlock);
			len = text.length();
			SpannableString spannable = new SpannableString(text);
			CharacterStyle ss = null;
			if (index >= 0 && index < len - 0) {
				ss = new ForegroundColorSpan(getColor(1));
				spannable.setSpan(ss, index + 0, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (index >= -1 && index < len - 1) {
				ss = new ForegroundColorSpan(getColor(2));
				spannable.setSpan(ss, index + 1, index + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (index >= -2 && index < len - 2) {
				ss = new ForegroundColorSpan(getColor(3));
				spannable.setSpan(ss, index + 2, index + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (index >= -3 && index < len - 3) {
				ss = new ForegroundColorSpan(getColor(4));
				spannable.setSpan(ss, index + 3, index + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (index >= -4 && index < len - 4) {
				ss = new ForegroundColorSpan(getColor(5));
				spannable.setSpan(ss, index + 4, index + 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (!isCalcuteTextSize) {
				int size = 1;
				int width = tvSlideUnlock.getWidth() - tvSlideUnlock.getPaddingLeft() - tvSlideUnlock.getPaddingRight();
				Paint paint = tvSlideUnlock.getPaint();
				paint.setTextSize(size);
				while (paint.measureText(text) <= width) {
					size++;
					paint.setTextSize(size);
				}

				WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
				DisplayMetrics dm = new DisplayMetrics();
				windowManager.getDefaultDisplay().getMetrics(dm);
				tvSlideUnlock.setTextSize((size - 1) / dm.density);
				tvSlideUnlock.setText(text);
			}
			tvSlideUnlock.setText(spannable);
			index++;
			handler.postDelayed(this, 100);
		}
	};

	/**
	 * 5阶颜色（一二三二一）
	 */
	int getColor(int i) {
		int a = 0;
		if (i == 1 || i == 5) {
			a = 0x58;
		} else if (i == 2 || i == 4) {
			a = 0xa8;
		} else if (i == 3) {
			a = 0xff;
		}
		return Color.argb(a, 0xff, 0xff, 0xff);
	}

	public void onClick(View v) {
		if (v == button) {
			unLock();
		}
	}

	public void unLock() {
		/*Intent intent = new Intent(context, UnLockActivity.class);
		context.startActivity(intent);*/
		if (context instanceof Activity) {
			Activity act = (Activity) context;
			act.finish();
		}		
	}

	protected int getDefaultLockWallpaperResId() {
		return R.drawable.default_wallpaper;
	}
}
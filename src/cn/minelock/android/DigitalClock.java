package cn.minelock.android;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.minelock.android.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.os.Handler;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
public class DigitalClock extends RelativeLayout {

	private Calendar mCalendar;
	private String mFormat;
	private TextView mTimeDisplay;
	private TextView mDateDisplay;
	private TextView mWeekDisplay;
	private AmPm mAmPm;
	private ContentObserver mFormatChangeObserver;
	private Context mContext;
	/* called by system on minute ticks */
	private final Handler mHandler = new Handler();
	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
				mCalendar = Calendar.getInstance();
			} 
			else if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
				mAmPm.reloadStringResource();
			}
			// Post a runnable to avoid blocking the broadcast.
			mHandler.post(new Runnable() {
				public void run() {
					updateTime();
				}
			});
		}
	};
	
	public void updateTime() {
		mCalendar = Calendar.getInstance();
		setDateFormat();
		mAmPm.reloadStringResource();
		// 年月日
		String year = String.valueOf(mCalendar.get(Calendar.YEAR));
		String month = String.valueOf(mCalendar.get(Calendar.MONTH)+1);
		String day = String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH));				
		mDateDisplay.setText(month + "月" + day + "日");
		// 周
		String week = String.valueOf(mCalendar.get(Calendar.DAY_OF_WEEK));
	    if("1".equals(week)){ 	    		    
	    	week = "日";  
	    }else if("2".equals(week)){  
	    	week = "一";  
	    }else if("3".equals(week)){  
	    	week = "二";  
	    }else if("4".equals(week)){  
	    	week = "三";  
	    }else if("5".equals(week)){  
	    	week = "四";  
	    }else if("6".equals(week)){  
	    	week = "五";  
	    }else if("7".equals(week)){  
	    	week = "六";  
	    } 
		mWeekDisplay.setText("星期"+week);
		// 时间
		CharSequence newTime = new SimpleDateFormat(mFormat).format(mCalendar.getTime());
		mTimeDisplay.setText(newTime);
		
		mAmPm.setIsMorning(mCalendar.get(Calendar.AM_PM) == 0);
	}

	static class AmPm {
		private TextView mAmPm;
		private String mAmString, mPmString;

		AmPm(View parent, Typeface tf) {
			mAmPm = (TextView) parent.findViewById(R.id.am_pm);
			if (tf != null) {
				mAmPm.setTypeface(tf);
			}

			String[] ampm = new DateFormatSymbols().getAmPmStrings();
			mAmString = ampm[0];
			mPmString = ampm[1];
		}

		void setShowAmPm(boolean show) {
			mAmPm.setVisibility(show ? View.VISIBLE : View.GONE);
		}

		void setIsMorning(boolean isMorning) {
			mAmPm.setText(isMorning ? mAmString : mPmString);
		}

		void reloadStringResource() {
			String[] ampm = new DateFormatSymbols().getAmPmStrings();
			mAmString = ampm[0];
			mPmString = ampm[1];
		}
	}

	private class FormatChangeObserver extends ContentObserver {
		public FormatChangeObserver() {
			super(new Handler());
		}

		public void onChange(boolean selfChange) {
			setDateFormat();
			updateTime();
		}
	}

	public DigitalClock(Context context) {
		this(context, null);
		mContext = context;
	}

	public DigitalClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	protected void onFinishInflate() {
		super.onFinishInflate();
		
		mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
		mDateDisplay.setTypeface(Typeface.createFromFile("/system/fonts/DroidSans.ttf"));
		mWeekDisplay = (TextView) findViewById(R.id.weekDisplay);
		mWeekDisplay.setTypeface(Typeface.createFromFile("/system/fonts/DroidSans.ttf"));
		
		mTimeDisplay = (TextView) findViewById(R.id.timeDisplay);
		mTimeDisplay.setTypeface(Typeface.createFromFile("/system/fonts/DroidSans.ttf"));//DroidSansMono
		//mTimeDisplay.setTypeface(Typeface.createFromFile("/system/fonts/SegoeWP.ttf"));
		//mAmPm = new AmPm(this, Typeface.createFromFile("/system/fonts/DroidSansFallback.ttf"));
		mAmPm = new AmPm(this, null);
		mCalendar = Calendar.getInstance();

		setDateFormat();
	}

	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		/* monitor time ticks, time changed, timezone */
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_TICK);
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		filter.addAction(Intent.ACTION_LOCALE_CHANGED);
		mContext.registerReceiver(mIntentReceiver, filter);

		/* monitor 12/24-hour display preference */
		mFormatChangeObserver = new FormatChangeObserver();
		mContext.getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, mFormatChangeObserver);

		updateTime();
	}

	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		mContext.unregisterReceiver(mIntentReceiver);
		mContext.getContentResolver().unregisterContentObserver(mFormatChangeObserver);
	}

	private void setDateFormat() {
		boolean is24Format = android.text.format.DateFormat.is24HourFormat(getContext());
		// int fommatStringId = is24Format ? R.string.twenty_four_hour_time_format : R.string.twelve_hour_time_format;
		// String format = getContext().getString(fommatStringId);
		String format = "HH:mm";
		mAmPm.setShowAmPm(!is24Format);
		int a = -1;
		boolean quoted = false;
		for (int i = 0; i < format.length(); i++) {
			char c = format.charAt(i);
			if (c == '\'') {
				quoted = !quoted;
			}
			if (!quoted && c == 'a') {
				a = i;
				break;
			}
		}
		if (a == 0) {
			format = format.substring(1);
		} 
		else if (a > 0) {
			format = format.substring(0, a - 1) + format.substring(a + 1);
		}
		format = format.trim();
		mFormat = format;
	}
}

package org.liushui.iphone;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainFragment extends Fragment {
	private static final String TAG = "MainFragment";
	private String hello = "Hello,everyone!";

	private int index = -2;
	// private String text;
	static private int len;
	private Handler handler = new Handler();
	private TextView viewhello;

	boolean isRun = false;
	boolean isCalcuteTextSize = false;

	static MainFragment newString(String s) {
		len = s.length();
		MainFragment newFragment = new MainFragment();
		Bundle bundle = new Bundle();
		bundle.putString("hello", s);
		newFragment.setArguments(bundle);

		return newFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "MainFragment-----onCreate");
		Bundle args = getArguments();
		hello = args != null ? args.getString("hello") : "";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "MainFragment-----onCreateView");
		View view = inflater.inflate(R.layout.lay1, container, false);
		viewhello = (TextView) view.findViewById(R.id.tv_hello);		
		viewhello.setText(hello);
		
		//LinearLayout lay1 = (LinearLayout) view.findViewById(R.layout.lay1);
		viewhello.setOnTouchListener(textViewTouchListener);
		
		startIndicateAnimation();
		return view;
	}

	@Override
	public void onDestroy() {

		stopIndicateAnimation();

		super.onDestroy();
		Log.d(TAG, "MainFragment-----onDestroy");
	}

	private void startIndicateAnimation() {
		Log.d(TAG, "startIndicateAnimation");

		if (!isRun) {
			handler.postDelayed(task, 200);
		}
		isRun = true;
	}

	private void stopIndicateAnimation() {
		Log.d(TAG, "stopIndicateAnimation");

		if (isRun) {
			handler.removeCallbacks(task);
		}
		isRun = false;
	}

	Runnable task = new Runnable() {

		public void run() {
			Log.d(TAG, "Runnable");
			if (index == len) {
				index = -2;
			}

			SpannableString spannable = new SpannableString(hello);
			CharacterStyle ss = null;
			if (index >= 0 && index < len) {
				ss = new ForegroundColorSpan(Color.argb(0xff, 0xff, 0xff, 0xff));
				spannable.setSpan(ss, index + 0, index + 1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (index >= 1 && index < len - 1) {
				ss = new ForegroundColorSpan(Color.argb(0xff, 0xff, 0xff, 0xff));
				spannable.setSpan(ss, index + 1, index + 2,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			viewhello.setText(spannable);
			index++;

			if (index == len) {
				ss = new ForegroundColorSpan(Color.argb(0xff, 0xaa, 0xaa, 0xaa));
				spannable.setSpan(ss, len - 1, len,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				viewhello.setText(spannable);
				handler.postDelayed(this, 800);
			} else
				handler.postDelayed(this, 250);
			// handler.postDelayed(this, 250);
		}
	};

	private OnTouchListener textViewTouchListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				// 按住事件发生后执行代码的区域
				//v.setBackgroundColor(Color.argb(0xc6, 0x39, 0xb5, 0xf5));				
				//v.setTextColor(Color.argb(0xff, 0xff, 0xff, 0xff));
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				// 移动事件发生后执行代码的区域
				//v.setBackgroundColor(Color.argb(0xc6, 0x39, 0xb5, 0xf5));
				break;
			}
			case MotionEvent.ACTION_UP: {
				// 松开事件发生后执行代码的区域
				//v.setBackgroundColor(Color.argb(0x00, 0x00, 0x00, 0x00));				
				break;
			}
			default:
				v.setBackgroundColor(Color.argb(0x00, 0x00, 0x00, 0x00));	
				break;
			}
			
			return false;
		}
	};
}

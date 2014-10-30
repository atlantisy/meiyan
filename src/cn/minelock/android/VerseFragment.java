package cn.minelock.android;

import cn.minelock.android.R;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VerseFragment extends Fragment {
	private static final String TAG = "MainFragment";
	private String hello = "Hello,Everyone!";

	private int index = -2;
	// private String text;
	static private int len;
	private Handler handler = new Handler();
	private TextView viewVerse;
	private ImageView viewRightArrow;
	private ImageView viewLeftArrow;

	boolean isRun = false;
	boolean isCalcuteTextSize = false;

	static VerseFragment newString(String s) {
		len = s.length();
		VerseFragment newFragment = new VerseFragment();
		Bundle bundle = new Bundle();
		bundle.putString("hello", s);
		newFragment.setArguments(bundle);

		return newFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "VerseFragment-----onCreate");
		Bundle args = getArguments();
		hello = args != null ? args.getString("hello") : "";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "VerseFragment-----onCreateView");
		View view = inflater.inflate(R.layout.frag_verse, container, false);
		viewVerse = (TextView) view.findViewById(R.id.tv_verse);
		viewVerse.setText(hello);

		viewRightArrow = (ImageView) view.findViewById(R.id.iv_arrow_right);
		viewLeftArrow = (ImageView) view.findViewById(R.id.iv_arrow_left);
		viewRightArrow.setVisibility(0);// 0:VISIBILITY 4:INVISIBILITY
		viewLeftArrow.setVisibility(4);

		viewVerse.setOnTouchListener(textViewTouchListener);
		
		//启动美言跑马灯功能  
		viewVerse.setSelected(true);
		//循环逐字黑白显示美言
		//startIndicateAnimation();
		return view;
	}

	@Override
	public void onDestroy() {

		stopIndicateAnimation();

		super.onDestroy();
		Log.d(TAG, "VerseFragment-----onDestroy");
	}

	private void startIndicateAnimation() {
		Log.d(TAG, "startIndicateAnimation");

		if (!isRun) {
			handler.postDelayed(task, 300);
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
				ss = new ForegroundColorSpan(Color.argb(0xff, 0x00, 0x00, 0x00));
				spannable.setSpan(ss, index + 0, index + 1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (index >= 1 && index < len - 1) {
				ss = new ForegroundColorSpan(Color.argb(0xff, 0x00, 0x00, 0x00));
				spannable.setSpan(ss, index + 1, index + 2,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			viewVerse.setText(spannable);
			index++;

			if (index == len) {
				ss = new ForegroundColorSpan(Color.argb(0xff, 0xff, 0xff, 0xff));
				spannable.setSpan(ss, len - 1, len,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				viewVerse.setText(spannable);
				handler.postDelayed(this, 800);
			} else
				handler.postDelayed(this, 300);
		}
	};

	private OnTouchListener textViewTouchListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				viewRightArrow.setVisibility(0);//0:VISIBILITY 4:INVISIBILITY
				viewLeftArrow.setVisibility(4);
				break;
			}
			case MotionEvent.ACTION_UP: {
				viewRightArrow.setVisibility(0);//
				viewLeftArrow.setVisibility(4);
				break;
			}
			default:
				break;
			}

			return true;
		}
	};
}

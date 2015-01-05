package cn.minelock.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import cn.minelock.android.R;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);		
		// ����
		ImageButton return_btn = (ImageButton) findViewById(R.id.about_return);
		return_btn.setOnClickListener(returnOnClickListener);		
		// ����/΢��
		Button help_btn = (Button) findViewById(R.id.about_help);
		help_btn.setOnClickListener(helpOnClickListener);
		// ����/΢��
		Button feedback_btn = (Button) findViewById(R.id.about_feedback);
		feedback_btn.setOnClickListener(feedBackOnClickListener);
	}
	// ������ť/΢��
	private OnClickListener helpOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://dwz.cn/minelock0"));   
			//i.setClassName("com.android.browser", "com.android.browser.BrowserActivity");   
			startActivity(i);  
		}
	};	
	// ������ť/΢��
	private OnClickListener feedBackOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://dwz.cn/minelock3"));   
			//i.setClassName("com.android.browser", "com.android.browser.BrowserActivity");   
			startActivity(i);  
		}
	};
	// ���ذ�ť
	private OnClickListener returnOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
/*			startActivity(new Intent(AboutActivity.this, SettingActivity.class));
			overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);*/
			finish();
		}
	};
}

package cn.minelock.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		// ����
		ImageButton return_btn = (ImageButton) findViewById(R.id.about_return);
		return_btn.setOnClickListener(returnOnClickListener);	
	}
	
	// ���ذ�ť
	private OnClickListener returnOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(AboutActivity.this, SettingActivity.class));
			overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
		}
	};
}

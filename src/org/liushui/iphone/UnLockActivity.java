package org.liushui.iphone;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class UnLockActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView tv = new TextView(this);
		tv.setText("unlock");
		setContentView(tv);
	}
}
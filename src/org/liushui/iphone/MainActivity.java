package org.liushui.iphone;

import org.liushui.iphone.BatteryObserver.OnBatteryChange;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity implements OnBatteryChange {
	BatteryObserver batteryObserver;
	IPhoneLockView root;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lock_iphone_view2);
		root = (IPhoneLockView) findViewById(R.id.root);
		batteryObserver = BatteryObserver.getInstance(this);
		batteryObserver.register();
	}

	protected void onResume() {
		super.onResume();
		batteryObserver.setOnBatteryChange(this);
		root.onResume();
	}

	protected void onPause() {
		super.onPause();
		root.onPause();
	}

	protected void onDestroy() {
		super.onDestroy();
		batteryObserver.unRegister();
	}

	public void onChange(int status, int level, int scale) {
		root.onBattery(status, level, scale);
		root.onUpdate();
	}
}
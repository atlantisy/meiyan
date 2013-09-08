package org.liushui.iphone;

import java.util.ArrayList;

import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.CharacterStyle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.style.ForegroundColorSpan;

public class SettingActivity extends Activity {

	private CheckBox checkbox = null;
	private EditText edittext = null;
	static public String customText="";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		checkbox = (CheckBox)findViewById(R.id.checkBox_meiyan);
		checkbox.setOnClickListener(new OnCheckedListener());
		
		edittext = (EditText)findViewById(R.id.editText_custom);		
	}


	class OnCheckedListener implements OnClickListener {
		public void onClick(View v) {
			// TODO	
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, MainActivity.class);
			//intent.putExtra("customText", customText);			
			if(checkbox.isChecked()){				
				customText=edittext.getText().toString();
				startActivity(intent);
			}				
		}
	}
}
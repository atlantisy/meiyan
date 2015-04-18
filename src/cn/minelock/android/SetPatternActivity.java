package cn.minelock.android;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SetPatternActivity extends Activity {

	private Button setpattern_option_btn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setpattern);
		// 返回
		ImageButton setpattern_return = (ImageButton) findViewById(R.id.setpattern_return);
		setpattern_return.setOnClickListener(new View.OnClickListener() {					
			public void onClick(View v) {
				finish();
			}
		 });
		// 手势样式选项
		setpattern_option_btn = (Button) findViewById(R.id.setpattern_optionbtn);
		setpattern_option_btn.setOnClickListener(optionOnClickListener);
	}
	
	// 手势样式选项按钮
	private OnClickListener optionOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// 手势样式选项文字及九宫拼图按钮
			final TextView optiontext = (TextView) findViewById(R.id.setpattern_optiontext);
			final Button puzzle = (Button) findViewById(R.id.setpattern_puzzle);
			// 选项弹出框
			final AlertDialog dlg = new AlertDialog.Builder(SetPatternActivity.this).create();
			dlg.show();
			Window window = dlg.getWindow();						 
			window.setContentView(R.layout.pattern_option_dialog);
			// 透明黑			
			Button patternoption1 = (Button) window.findViewById(R.id.patternoption1);				
			patternoption1.setOnClickListener(new View.OnClickListener() {					
				public void onClick(View v) {
					dlg.cancel();
					
					optiontext.setText("透明黑");
					puzzle.setVisibility(View.GONE);
				}
			 });
			// 百变多彩			
			Button patternoption2 = (Button) window.findViewById(R.id.patternoption2);				
			patternoption2.setOnClickListener(new View.OnClickListener() {					
				public void onClick(View v) {
					dlg.cancel();
					
					optiontext.setText("百变多彩");
					puzzle.setVisibility(View.GONE);
				}
			 });
			// 九宫拼图			
			Button patternoption3 = (Button) window.findViewById(R.id.patternoption3);				
			patternoption3.setOnClickListener(new View.OnClickListener() {					
				public void onClick(View v) {
					dlg.cancel();
					
					optiontext.setText("九宫拼图");						
					puzzle.setVisibility(View.VISIBLE);
				}
			 });			
		}
	};
}

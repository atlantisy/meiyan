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
		// ����
		ImageButton setpattern_return = (ImageButton) findViewById(R.id.setpattern_return);
		setpattern_return.setOnClickListener(new View.OnClickListener() {					
			public void onClick(View v) {
				finish();
			}
		 });
		// ������ʽѡ��
		setpattern_option_btn = (Button) findViewById(R.id.setpattern_optionbtn);
		setpattern_option_btn.setOnClickListener(optionOnClickListener);
	}
	
	// ������ʽѡ�ť
	private OnClickListener optionOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// ������ʽѡ�����ּ��Ź�ƴͼ��ť
			final TextView optiontext = (TextView) findViewById(R.id.setpattern_optiontext);
			final Button puzzle = (Button) findViewById(R.id.setpattern_puzzle);
			// ѡ�����
			final AlertDialog dlg = new AlertDialog.Builder(SetPatternActivity.this).create();
			dlg.show();
			Window window = dlg.getWindow();						 
			window.setContentView(R.layout.pattern_option_dialog);
			// ͸����			
			Button patternoption1 = (Button) window.findViewById(R.id.patternoption1);				
			patternoption1.setOnClickListener(new View.OnClickListener() {					
				public void onClick(View v) {
					dlg.cancel();
					
					optiontext.setText("͸����");
					puzzle.setVisibility(View.GONE);
				}
			 });
			// �ٱ���			
			Button patternoption2 = (Button) window.findViewById(R.id.patternoption2);				
			patternoption2.setOnClickListener(new View.OnClickListener() {					
				public void onClick(View v) {
					dlg.cancel();
					
					optiontext.setText("�ٱ���");
					puzzle.setVisibility(View.GONE);
				}
			 });
			// �Ź�ƴͼ			
			Button patternoption3 = (Button) window.findViewById(R.id.patternoption3);				
			patternoption3.setOnClickListener(new View.OnClickListener() {					
				public void onClick(View v) {
					dlg.cancel();
					
					optiontext.setText("�Ź�ƴͼ");						
					puzzle.setVisibility(View.VISIBLE);
				}
			 });			
		}
	};
}

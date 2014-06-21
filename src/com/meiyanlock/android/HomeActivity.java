package com.meiyanlock.android;

import java.util.ArrayList;
import java.util.HashMap;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	private TextView verse_line = null;
	private GridView verse_grid = null;
	private ImageButton lockbtn = null;// 锁屏按钮
	
	private static final int LINE = 1;// 简约状态
	private static final int GRID = 2;// 九宫状态
	private static final int STATE_LINE = 1;// 锁屏状态设为1
	private static final int STATE_GRID = 2;// 锁屏状态设为2
	private int flag = 1;// 标记

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		// 设置按钮
		ImageButton setting_button = (ImageButton) findViewById(R.id.home_setting);
		setting_button.setOnClickListener(settingOnClickListener);
		// 编辑美言按钮
		ImageButton text_button = (ImageButton) findViewById(R.id.home_text);
		text_button.setOnClickListener(textOnClickListener);
		
		// Verse
		String strVerse = "你是我的小呀小苹果";
		// 简约视图
		verse_line = (TextView) findViewById(R.id.line_verse);
		// 九宫格视图
		verse_grid = (GridView) findViewById(R.id.grid_verse);		
		char[] chVerse = strVerse.toCharArray();
		// 生成动态数组，并且转入数据
		ArrayList<HashMap<String, Object>> listVerse = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 9; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("Verse", "" + chVerse[i]);
			listVerse.add(map);
		}
		// 生成适配器的Verses <====> 动态数组的元素，一一对应
		SimpleAdapter saVerses = new SimpleAdapter(this, listVerse,
				R.layout.grid_verse, new String[] { "Verse" },
				new int[] { R.id.verse_content });
		// 添加并且显示
		verse_grid.setAdapter(saVerses);
		// 添加消息处理
		verse_grid.setOnItemClickListener(new ItemClickListener());

		// 切换锁屏方式
		switch (flag) {
		case STATE_LINE:
			verse_line.setVisibility(View.VISIBLE);
			verse_grid.setVisibility(View.GONE);			
			break;			
		case STATE_GRID:
			verse_line.setVisibility(View.GONE);
			verse_grid.setVisibility(View.VISIBLE);
			break;
		}
		ShowLockBtn();

	}

	/**
	 * 显示锁屏按钮
	 */
	private void ShowLockBtn() {
		lockbtn = (ImageButton) findViewById(R.id.home_lock);
		lockbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				switch (flag) {
				case STATE_LINE:
					grid();
					break;

				case STATE_GRID:
					line();
					break;
				}
			}
		});

	}

	// 简约锁屏
	protected void line() {
		flag = LINE;
		lockbtn.setImageResource(R.drawable.lock_grid);
		verse_line.setVisibility(View.VISIBLE);
		verse_grid.setVisibility(View.GONE);
        Toast.makeText(this, R.string.line_verse_style, Toast.LENGTH_LONG)
        .show();
	}

	// 九宫锁屏
	protected void grid() {
		flag = GRID;
		lockbtn.setImageResource(R.drawable.lock_line);
		verse_line.setVisibility(View.GONE);
		verse_grid.setVisibility(View.VISIBLE);
        Toast.makeText(this, R.string.grid_verse_style, Toast.LENGTH_LONG)
        .show();
	}

	// 当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件
	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
													// click happened
				View arg1,// The view within the AdapterView that was clicked
				int arg2,// The position of the view in the adapter
				long arg3// The row id of the item that was clicked
		) {
			// 在本例中arg2=arg3
			HashMap<String, Object> item = (HashMap<String, Object>) arg0
					.getItemAtPosition(arg2);
			// 显示所选Item的ItemText
			setTitle((String) item.get("Verse"));
		}

	}

	// 设置按钮点击事件
	private OnClickListener settingOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(HomeActivity.this, SettingActivity.class));
		}
	};

	// 编辑美言按钮点击事件
	private OnClickListener textOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(HomeActivity.this, TextEditActivity.class));
		}
	};

}

package com.meiyanlock.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		// �Ź�����ͼ
		GridView verse_gridview = (GridView) findViewById(R.id.grid_verse);
		// ���ɶ�̬���飬����ת������
		ArrayList<HashMap<String, Object>> listVerse = new ArrayList<HashMap<String, Object>>();
		for (int i = 1; i < 10; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("Verse", "" + String.valueOf(i));
			listVerse.add(map);
		}
		// ������������Verses <====> ��̬�����Ԫ�أ�һһ��Ӧ
		SimpleAdapter saVerses = new SimpleAdapter(this, listVerse,
				R.layout.grid_verse, new String[] { "Verse" },
				new int[] { R.id.verse_content });
		// ��Ӳ�����ʾ
		verse_gridview.setAdapter(saVerses);
		// �����Ϣ����
		verse_gridview.setOnItemClickListener(new ItemClickListener());

		// ���ð�ť
		ImageButton setting_button = (ImageButton) findViewById(R.id.home_setting);
		setting_button.setOnClickListener(settingOnClickListener);

		// �༭���԰�ť
		ImageButton text_button = (ImageButton) findViewById(R.id.home_text);
		text_button.setOnClickListener(textOnClickListener);

		// �л�������ʽ��ť
		final ImageButton lock_line_button = (ImageButton) findViewById(R.id.home_lock_line);
		final ImageButton lock_grid_button = (ImageButton) findViewById(R.id.home_lock_grid);
		// ��Լ�����¼�
		lock_line_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				lock_line_button.setVisibility(View.GONE);
				lock_grid_button.setVisibility(View.VISIBLE);
			}
		});
		// �Ź��������¼�
		lock_grid_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				lock_grid_button.setVisibility(View.GONE);
				lock_line_button.setVisibility(View.VISIBLE);
			}
		});
	}

	// ��AdapterView������(���������߼���)���򷵻ص�Item�����¼�
	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
													// click happened
				View arg1,// The view within the AdapterView that was clicked
				int arg2,// The position of the view in the adapter
				long arg3// The row id of the item that was clicked
		) {
			// �ڱ�����arg2=arg3
			HashMap<String, Object> item = (HashMap<String, Object>) arg0
					.getItemAtPosition(arg2);
			// ��ʾ��ѡItem��ItemText
			setTitle((String) item.get("Verse"));
		}

	}

	// ���ð�ť����¼�
	private OnClickListener settingOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(HomeActivity.this, SettingActivity.class));
		}
	};

	// �༭���԰�ť����¼�
	private OnClickListener textOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(HomeActivity.this, TextEditActivity.class));
		}
	};

}

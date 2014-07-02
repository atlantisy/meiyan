package com.meiyanlock.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.meiyanlock.android.R;
import com.meiyanlock.widget.CustemSpinerAdapter;
import com.meiyanlock.widget.SpinerPopWindow;
import com.meiyanlock.widget.CustemObject;
import com.meiyanlock.widget.AbstractSpinerAdapter;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class HomeActivity extends Activity implements OnClickListener, AbstractSpinerAdapter.IOnItemSelectListener{
	private TextView verse_line = null;
	private GridView verse_grid = null;
	private ImageButton lockbtn = null;// ������ť
	
	private Button mBtnDropDown;
	private List<CustemObject> nameList = new ArrayList<CustemObject>();
	private AbstractSpinerAdapter mAdapter;
	
	private static final int LINE = 1;// ��Լ״̬
	private static final int GRID = 2;// �Ź�״̬
	private static final int STATE_LINE = 1;// ����״̬��Ϊ1
	private static final int STATE_GRID = 2;// ����״̬��Ϊ2
	private int flag = 1;// ���

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		// ��������ѡ��
		verseOptionSetup();
		
		// ���ð�ť
		ImageButton setting_button = (ImageButton) findViewById(R.id.home_setting);
		setting_button.setOnClickListener(settingOnClickListener);
		// �༭���԰�ť
		ImageButton text_button = (ImageButton) findViewById(R.id.home_text);
		text_button.setOnClickListener(textOnClickListener);
		
		// Verse
		String strVerse = "�����ҵ�СѽСƻ��";
		// ��Լ��ͼ
		verse_line = (TextView) findViewById(R.id.line_verse);
		// �Ź�����ͼ
		verse_grid = (GridView) findViewById(R.id.grid_verse);		
		char[] chVerse = strVerse.toCharArray();
		// ���ɶ�̬���飬����ת������
		ArrayList<HashMap<String, Object>> listVerse = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 9; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("Verse", "" + chVerse[i]);
			listVerse.add(map);
		}
		// ������������Verses <====> ��̬�����Ԫ�أ�һһ��Ӧ
		SimpleAdapter saVerses = new SimpleAdapter(this, listVerse,
				R.layout.grid_verse, new String[] { "Verse" },
				new int[] { R.id.verse_content });
		// ��Ӳ�����ʾ
		verse_grid.setAdapter(saVerses);
		// �����Ϣ����
		verse_grid.setOnItemClickListener(new ItemClickListener());

		// �л�������ʽ
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

    private void verseOptionSetup(){

		mBtnDropDown = (Button) findViewById(R.id.verse_option);
		mBtnDropDown.setOnClickListener(this);
				
		String[] names = getResources().getStringArray(R.array.verse_option_name);
		for(int i = 0; i < names.length; i++){
			CustemObject object = new CustemObject();
			object.data = names[i];
			nameList.add(object);
		}
				
		mAdapter = new CustemSpinerAdapter(this);
		mAdapter.refreshData(nameList, 0);

		mSpinerPopWindow = new SpinerPopWindow(this);
		mSpinerPopWindow.setAdatper(mAdapter);
		mSpinerPopWindow.setItemListener(this);
    }

	
	/**
	 * ��ʾ������ť
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

	// ��Լ����
	protected void line() {
		flag = LINE;
		lockbtn.setImageResource(R.drawable.ic_lock_grid);
		verse_line.setVisibility(View.VISIBLE);
		verse_grid.setVisibility(View.GONE);
        Toast.makeText(this, R.string.line_verse_style, Toast.LENGTH_LONG)
        .show();
	}

	// �Ź�����
	protected void grid() {
		flag = GRID;
		lockbtn.setImageResource(R.drawable.ic_lock_line);
		verse_line.setVisibility(View.GONE);
		verse_grid.setVisibility(View.VISIBLE);
        Toast.makeText(this, R.string.grid_verse_style, Toast.LENGTH_LONG)
        .show();
        //���þŹ�������
        startActivity(new Intent(HomeActivity.this, SetPasswordActivity.class));
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
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.verse_option:
			showSpinWindow();
			break;
		}
	}
	
	private void setVerse(int pos){
		if (pos >= 0 && pos <= nameList.size()){
			CustemObject value = nameList.get(pos);
		
			mBtnDropDown.setText(value.toString());
		}
	}
	
	private SpinerPopWindow mSpinerPopWindow;
	private void showSpinWindow(){
		Log.e("", "showSpinWindow");
		mSpinerPopWindow.setWidth(mBtnDropDown.getWidth());
		mSpinerPopWindow.showAsDropDown(mBtnDropDown);
	}
	
	@Override
	public void onItemClick(int pos) {
		setVerse(pos);
	}

}

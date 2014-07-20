package com.meiyanlock.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.meiyanlock.android.R;
import com.meiyanlock.widget.CustemSpinerAdapter;
import com.meiyanlock.widget.LocusPassWordView;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity implements OnClickListener, AbstractSpinerAdapter.IOnItemSelectListener{
	private TextView verse_line = null;
	private GridView verse_grid = null;
	private LinearLayout verse_grid1 = null;
	
	//private LocusPassWordView verse_grid = null;
	private ImageButton lockbtn = null;// ������ť
	private Button setup_grid_button = null;//���þŹ����ư�ť
	
	private Button mBtnDropDown;
	private List<CustemObject> nameList = new ArrayList<CustemObject>();
	private AbstractSpinerAdapter mAdapter;
	
	private static final int LINE = 1;// ��Լ״̬
	private static final int GRID = 2;// �Ź�״̬
	private static final int STATE_LINE = 1;// ����״̬��Ϊ1
	private static final int STATE_GRID = 2;// ����״̬��Ϊ2
	private int flag = 1;// ���
	private boolean bPassWord = false;//�Ź����Ƿ���������
		
	static final int MENU_SET_MODE = 0;

	private PullToRefreshGridView mPullRefreshGridView;
	private GridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		//����ˢ��
		mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.pull_refresh_grid);
		mGridView = mPullRefreshGridView.getRefreshableView();
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
//				mPullRefreshGridView.getLoadingLayoutProxy().setRefreshingLabel("���ڼ���"); 
//				mPullRefreshGridView.getLoadingLayoutProxy().setPullLabel("�����Ƽ����ԡ�"); 
//				mPullRefreshGridView.getLoadingLayoutProxy().setReleaseLabel("�ſ����Ƽ���");
				//����
				PullDown();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
/*				mPullRefreshGridView.getLoadingLayoutProxy().setRefreshingLabel("���ڼ���"); 
				mPullRefreshGridView.getLoadingLayoutProxy().setPullLabel("�����༭����"); 
				mPullRefreshGridView.getLoadingLayoutProxy().setReleaseLabel("�ſ��Ա༭");*/
				//����
				PullUp();
			}

		});
		
		//������������ѡ��
		verseOptionSetup();		
		// ���ð�ť
		ImageButton setting_button = (ImageButton) findViewById(R.id.home_setting);
		setting_button.setOnClickListener(settingOnClickListener);
		// �༭���԰�ť
		ImageButton text_button = (ImageButton) findViewById(R.id.home_text);
		text_button.setOnClickListener(textOnClickListener);
		
		// ���þŹ�������
		setup_grid_button = (Button) findViewById(R.id.home_setup_grid);
		setup_grid_button.setOnClickListener(setGridOnClickListener);
		
		// Verse
		String strVerse = "�о� �Լ� ������";
		// ��Լ��ͼ
		verse_line = (TextView) findViewById(R.id.line_verse);
		// �Ź�����ͼ
		//verse_grid = (LocusPassWordView) findViewById(R.id.mLocusPassWordView);
		verse_grid1 = (LinearLayout) findViewById(R.id.verse_layout);
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
			verse_grid1.setVisibility(View.GONE);
			setup_grid_button.setVisibility(View.GONE);
			break;			
		case STATE_GRID:
			verse_line.setVisibility(View.GONE);
			verse_grid1.setVisibility(View.VISIBLE);
			if(bPassWord==true)
				setup_grid_button.setVisibility(View.GONE);
			else
				setup_grid_button.setVisibility(View.VISIBLE);
			break;
		}
		ShowLockBtn();

	}

	//����
	private void PullDown(){
		// Call onRefreshComplete when the list has been refreshed.
		mPullRefreshGridView.onRefreshComplete();
		
	}
	
	//�����༭
	private void PullUp(){
		//����༭ģʽ		
		startActivity(new Intent(HomeActivity.this, TextEditActivity.class));
		// Call onRefreshComplete when the list has been refreshed.
		mPullRefreshGridView.onRefreshComplete();
		
	}
	
    //������������ѡ��
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
		lockbtn.setImageResource(R.drawable.ic_lock_line);
		verse_line.setVisibility(View.VISIBLE);
		verse_grid1.setVisibility(View.GONE);
		setup_grid_button.setVisibility(View.GONE);
        Toast.makeText(this, R.string.line_verse_style, Toast.LENGTH_LONG)
        .show();
	}

	// �Ź�����
	protected void grid() {
		flag = GRID;
		lockbtn.setImageResource(R.drawable.ic_lock_grid);
		verse_line.setVisibility(View.GONE);
		verse_grid1.setVisibility(View.VISIBLE);
		if (bPassWord==true)
			setup_grid_button.setVisibility(View.GONE);
		else
			setup_grid_button.setVisibility(View.VISIBLE);
        Toast.makeText(this, R.string.grid_verse_style, Toast.LENGTH_LONG)
        .show();
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
	
	// ���þŹ���ť����¼�
	private OnClickListener setGridOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stubsetGridOnClickListener
	        //���þŹ�������
	        startActivityForResult(new Intent(HomeActivity.this, SetPasswordActivity.class), 100);
		}
	};
	
	
	//��������activity���ݵĽ��
	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		//��ȡ�Ź������Ƿ����ý��
		if (20==resultCode){
			if(data.getExtras().getBoolean("SetPassWord")==true){
				bPassWord = true;
				setup_grid_button.setVisibility(View.GONE);
			}	
/*			else
				bPassWord = false;*/			
		}
		super.onActivityResult(requestCode, resultCode, data); 
	} 
	
	
	
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

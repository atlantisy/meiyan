package cn.minelock.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cn.minelock.util.StringUtil;
import cn.minelock.widget.AbstractSpinerAdapter;
import cn.minelock.widget.CustemObject;
import cn.minelock.widget.CustemSpinerAdapter;
import cn.minelock.widget.PatternPassWordView;
import cn.minelock.widget.SpinerPopWindow;
import cn.minelock.widget.dbHelper;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

import cn.minelock.android.R;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity implements OnClickListener,
		AbstractSpinerAdapter.IOnItemSelectListener {
	private long mExitTime;
	private TextView verse_line = null;
	private GridView verse_grid = null;
	private LinearLayout verse_grid1 = null;

	private ImageButton lockbtn = null;// ������ʽ��ť
	private ImageButton show_verse_btn = null;// ������ʾ��ʽ��ť
	private Button setup_grid_button = null;// ���þŹ����ư�ť

	private Button mBtnDropDown;
	private List<CustemObject> nameList = new ArrayList<CustemObject>();
	private AbstractSpinerAdapter mAdapter;

	private static final int LINE = 1;// ��Լ״̬
	private static final int GRID = 2;// �Ź�״̬
	private static final int STATE_LINE = 1;// ����״̬��Ϊ1
	private static final int STATE_GRID = 2;// ����״̬��Ϊ2
	private static int flag = 1;// ������ʽprefֵ
	private static final int SINGLE_REPEAT = 1;// ����ѭ��
	private static final int ORDER_REPEAT = 2;// ˳��ѭ��
	private static final int SHUFFLE = 3;// �����ʾ
	private static int showVerseFlag = 1;// ������ʾ��ʽprefֵ
	private static boolean bPassWord = false;// �Ź����Ƿ�����
	private static String verse = "";// ����

	public static final String PREFS = "lock_pref";// pref�ļ���
	public static final String VERSE = "verse";// ����prefֵ����
	public static final String BOOLIDPATH = "wallpaper_idorpath";// Ӧ����or���ֽbool��prefֵ����,trueΪID��falseΪpath
	public static final String WALLPAPERID = "wallpaper_id";// Ӧ���ڱ�ֽ��ԴID��prefֵ����
	public static final String WALLPAPERPATH = "wallpaper_path";// Ӧ�����ֽPath��prefֵ����	
	public static final String LOCKFLAG = "lockFlag";// ������ʽprefֵ����
	public static final String SHOWVERSEFLAG = "showVerseFlag";//������ʾ��ʽprefֵ����
	public static final String PWSETUP = "passWordSetUp";// �Ź����Ƿ�����prefֵ����

	private SharedPreferences home_setting;
	
	static final int MENU_SET_MODE = 0;

	private PullToRefreshGridView mPullRefreshGridView;
	private GridView mGridView;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		        
		// ����ˢ��
		mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.pull_refresh_grid);
		mGridView = mPullRefreshGridView.getRefreshableView();
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<GridView> refreshView) {					
						// ����
						PullDown();
					}
					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<GridView> refreshView) {
						// ����
						PullUp();
					}
				});

		// ������������ѡ��
		verseOptionSetup();
		
		// ����ť
		ImageButton share_button = (ImageButton) findViewById(R.id.home_share1);
		share_button.setOnClickListener(this);
		// ���ð�ť
		ImageButton setting_button = (ImageButton) findViewById(R.id.home_setting1);
		setting_button.setOnClickListener(this);
		// ��ʷ��¼��ť
		ImageButton recent_button = (ImageButton) findViewById(R.id.home_recent);
		recent_button.setOnClickListener(this);
		// �༭���԰�ť
		ImageButton text_button = (ImageButton) findViewById(R.id.home_text);
		text_button.setOnClickListener(this);
		// ���þŹ�������
		setup_grid_button = (Button) findViewById(R.id.home_setup_grid);
		setup_grid_button.setOnClickListener(this);
		// Verse
		String strVerse = "123456789";
		// ��Լ��ͼ
		verse_line = (TextView) findViewById(R.id.line_verse);
		// �Ź�����ͼ
		// verse_grid = (PatternPassWordView)findViewById(R.id.mPatternPassWordView);
		verse_grid1 = (LinearLayout) findViewById(R.id.verse_layout);
		//
		verse_grid = (GridView) findViewById(R.id.grid_verse);
		char[] chVerse = strVerse.toCharArray();
		// ���ɶ�̬���飬����ת������
		ArrayList<HashMap<String, Object>> listVerse = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 9; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("Verse", "" + chVerse[i]);
			listVerse.add(map);
		}
		// ����������
		SimpleAdapter saVerses = new SimpleAdapter(this, listVerse,
				R.layout.gridview_verse, new String[] { "Verse" },
				new int[] { R.id.verse_content });
		// ��Ӳ�����ʾ,�����Ϣ����
		verse_grid.setAdapter(saVerses);
		verse_grid.setOnItemClickListener(new ItemClickListener());
		
		// ��ȡ�洢��pref����
		home_setting = getSharedPreferences(PREFS, 0);				
		// ���ñ�ֽ
		LinearLayout homeLayout = (LinearLayout)findViewById(R.id.HomeLayout);		
		boolean bIdOrPath = home_setting.getBoolean(BOOLIDPATH, true);
		int wallpaperId = home_setting.getInt(WALLPAPERID, R.drawable.wallpaper00);
		String wallpaperPath = home_setting.getString(WALLPAPERPATH, "");	
		if(bIdOrPath==true)//���ñ�ֽ			
			homeLayout.setBackgroundResource(wallpaperId);
		else{
			Bitmap bitmap = BitmapFactory.decodeFile(wallpaperPath);
			homeLayout.setBackgroundDrawable(new BitmapDrawable(bitmap));
		}
		// �������ԣ����Ժ;Ź���
		SetVerse();		
		// �л�������ʽ��ʼ������ťͼ���л��¼�
		InitShowLock();
		ShowLock();			
		// �л�������ʾ��ʽ��ʼ������ťͼ���л��¼�
		InitShowVerse();	
		ShowVerse();
	}
	
	private final String LOCK_SWITCH = "lock_screen_switch";
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// ��ȡ��default prefs����		
		SharedPreferences defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isLockScreenOn = defaultPrefs.getBoolean(LOCK_SWITCH, true);
		//��������
		if (isLockScreenOn){
			// keep on disabling the system Keyguard
			//��������
			startService(new Intent(getApplicationContext(), MyLockScreenService.class));
			EnableSystemKeyguard(false);
		}
		else {
			stopService(new Intent(getApplicationContext(), MyLockScreenService.class));
			// recover original Keyguard
			EnableSystemKeyguard(true);
		}
	}
	// ������ǰ��ʱˢ�����Ե�����
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SetVerse();
		InitShowVerse();		
	}
	
	// ����
	private void PullDown() {
		// Call onRefreshComplete when the list has been refreshed.
		mPullRefreshGridView.onRefreshComplete();
	}

	// �����༭
	private void PullUp() {
		// ����༭ģʽ
		startActivity(new Intent(HomeActivity.this, EditVerseActivity.class));
		// Call onRefreshComplete when the list has been refreshed.
		mPullRefreshGridView.onRefreshComplete();
	}

	// ��������
	private void SetVerse() {
		TextView line_verse = (TextView) findViewById(R.id.line_verse);		
		verse = home_setting.getString(VERSE, "�о��Լ�������  ");// ��ȡ����
		//���ü���
		line_verse.setText(verse.trim());
		//���þŹ���
		String s = verse.replace("\n", " ");
		TextView verse0 = (TextView) findViewById(R.id.verse0);
		verse0.setText(s.substring(0, 1));
		TextView verse1 = (TextView) findViewById(R.id.verse1);
		verse1.setText(s.substring(1, 2));
		TextView verse2 = (TextView) findViewById(R.id.verse2);
		verse2.setText(s.substring(2, 3));
		TextView verse3 = (TextView) findViewById(R.id.verse3);
		verse3.setText(s.substring(3, 4));
		TextView verse4 = (TextView) findViewById(R.id.verse4);
		verse4.setText(s.substring(4, 5));
		TextView verse5 = (TextView) findViewById(R.id.verse5);
		verse5.setText(s.substring(5, 6));
		TextView verse6 = (TextView) findViewById(R.id.verse6);
		verse6.setText(s.substring(6, 7));
		TextView verse7 = (TextView) findViewById(R.id.verse7);
		verse7.setText(s.substring(7, 8));
		TextView verse8 = (TextView) findViewById(R.id.verse8);
		verse8.setText(s.substring(8, 9));
	}

	//�����η��ؼ��˳�
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
/*			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Object mHelperUtils;
				Toast.makeText(this, "�ٰ�һ���˳���������", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} 
			else{					
				Intent i = new Intent(Intent.ACTION_MAIN);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addCategory(Intent.CATEGORY_HOME);
				startActivity(i);
			}*/
			
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i);
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// ��������ѡ�������
	private void verseOptionSetup() {
		mBtnDropDown = (Button) findViewById(R.id.verse_option);
		mBtnDropDown.setOnClickListener(this);

		String[] names = getResources().getStringArray(
				R.array.verse_option_name);
		for (int i = 0; i < names.length; i++) {
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
	
	// ������ʾ��ʽ��ť��ʼ��
	private void InitShowVerse() {
		show_verse_btn = (ImageButton) findViewById(R.id.home_repeat_shuffle);
		showVerseFlag=home_setting.getInt(SHOWVERSEFLAG, 1);
		switch (showVerseFlag) {
		case SINGLE_REPEAT:
			show_verse_btn.setImageResource(R.drawable.ic_single_repeat);
			break;
		case ORDER_REPEAT:
			show_verse_btn.setImageResource(R.drawable.ic_order_repeat);
			break;
		case SHUFFLE:
			show_verse_btn.setImageResource(R.drawable.ic_shuffle);
			break;
		}
	}
	/**
	 * �л�������ʾ��ʽ��ť������Ϊ������ѭ��=��˳��ѭ��=�������ʾ
	 */
	private void ShowVerse() {		
		show_verse_btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				switch (showVerseFlag) {
				case SINGLE_REPEAT:
					orderRepeat();
					break;
				case ORDER_REPEAT:
					shuffle();					
					break;
				case SHUFFLE:					
					singleRepeat();
					break;
				}
				// ��������ʾ��ʽ���ô���SharedPreferences
				SharedPreferences setting = getSharedPreferences(PREFS, 0);
				SharedPreferences.Editor editor = setting.edit();
				editor.putInt(SHOWVERSEFLAG, showVerseFlag);
				editor.commit();
			}
		});

	}
	// ����ѭ��
	protected void singleRepeat() {
		showVerseFlag = SINGLE_REPEAT;
		show_verse_btn.setImageResource(R.drawable.ic_single_repeat);

		StringUtil.showToast(this, "����ѭ��",  Toast.LENGTH_SHORT);
	}
	// ˳��ѭ��
	protected void orderRepeat() {
		showVerseFlag = ORDER_REPEAT;
		show_verse_btn.setImageResource(R.drawable.ic_order_repeat);

		StringUtil.showToast(this, "˳��ѭ��",  Toast.LENGTH_SHORT);
	}	
	// �����ʾ
	protected void shuffle() {
		showVerseFlag = SHUFFLE;
		show_verse_btn.setImageResource(R.drawable.ic_shuffle);
		
		StringUtil.showToast(this, "�������",  Toast.LENGTH_SHORT);
	}
	
	// �л�������ʽ��ť��ʼ��
	private void InitShowLock() {
		lockbtn = (ImageButton) findViewById(R.id.home_lock);
		flag = home_setting.getInt(LOCKFLAG, 1);
		bPassWord = home_setting.getBoolean(PWSETUP, false);
		switch (flag) {
		case STATE_LINE:
			lockbtn.setImageResource(R.drawable.ic_lock_grid);
			verse_line.setVisibility(View.VISIBLE);
			verse_grid1.setVisibility(View.GONE);
			setup_grid_button.setVisibility(View.GONE);
			break;
		case STATE_GRID:
			lockbtn.setImageResource(R.drawable.ic_lock_line);
			verse_line.setVisibility(View.GONE);
			verse_grid1.setVisibility(View.VISIBLE);
			if (bPassWord == true)
				setup_grid_button.setVisibility(View.GONE);
			else
				setup_grid_button.setVisibility(View.VISIBLE);
			break;
		}
	}
	/**
	 * �л�������ʽ��ť
	 */
	private void ShowLock() {
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
				// ��������ʽ���ô���SharedPreferences
				SharedPreferences setting = getSharedPreferences(PREFS, 0);
				SharedPreferences.Editor editor = setting.edit();
				editor.putInt(LOCKFLAG, flag);
				editor.commit();
			}
		});

	}
	// �򵥻�������
	protected void line() {
		flag = LINE;
		lockbtn.setImageResource(R.drawable.ic_lock_grid);
		verse_line.setVisibility(View.VISIBLE);
		verse_grid1.setVisibility(View.GONE);
		setup_grid_button.setVisibility(View.GONE);
		//Toast.makeText(this, R.string.line_verse_style, Toast.LENGTH_SHORT).show();
		StringUtil.showToast(this, "��������",  Toast.LENGTH_SHORT);
	}
	// �Ź���������
	protected void grid() {
		flag = GRID;
		lockbtn.setImageResource(R.drawable.ic_lock_line);
		verse_line.setVisibility(View.GONE);
		verse_grid1.setVisibility(View.VISIBLE);
		if (bPassWord == true)
			setup_grid_button.setVisibility(View.GONE);
		else
			setup_grid_button.setVisibility(View.VISIBLE);
		//Toast.makeText(this, R.string.grid_verse_style,  Toast.LENGTH_SHORT).show();
		StringUtil.showToast(this, "���ƽ���",  Toast.LENGTH_SHORT);
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
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.home_share1:
			Intent intent=new Intent(Intent.ACTION_SEND);  
			intent.setType("text/plain");  
			intent.putExtra(Intent.EXTRA_SUBJECT, "����");  
			intent.putExtra(Intent.EXTRA_TEXT, verse+"#minelock#");  
			startActivity(Intent.createChooser(intent, getTitle()));
			break;
		case R.id.home_recent:
			startActivity(new Intent(HomeActivity.this, RecentActivity.class));
			//overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.home_setting1:
			startActivity(new Intent(HomeActivity.this, SettingActivity.class));
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.home_text:
			startActivity(new Intent(HomeActivity.this, EditVerseActivity.class));
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.home_setup_grid:
			startActivityForResult(new Intent(HomeActivity.this,SetPasswordActivity.class), 100);
			break;
		case R.id.verse_option:
			showSpinWindow();
			break;

		}
	}
	
	// ��������activity���ݵĽ��
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// ��ȡ�Ź������Ƿ����ý��
		if (20 == resultCode) {
			if (data.getExtras().getBoolean("SetPassWord") == true) {
				// ��setPasswordActivity���ص��Ƿ����þŹ�����ȷ��ֵ
				bPassWord = data.getExtras().getBoolean("SetPassWord");
				// ���Ź�������ʾ���ô���SharedPreferences
				SharedPreferences setting = getSharedPreferences(PREFS, 0);
				SharedPreferences.Editor editor = setting.edit();
				editor.putBoolean(PWSETUP, bPassWord);
				editor.commit();
				// ���ؾŹ�����������ʾ��ť
				setup_grid_button.setVisibility(View.GONE);
			}
			/*
			 * else bPassWord = false;
			 */
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onItemClick(int pos) {
		setVerse(pos);
	}
	// ����ϵͳ����
	void EnableSystemKeyguard(boolean bEnable) {
		KeyguardManager mKeyguardManager = null;
		KeyguardLock mKeyguardLock = null;

		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		mKeyguardLock = mKeyguardManager.newKeyguardLock("MineLock");
		if (bEnable)
			mKeyguardLock.reenableKeyguard();
		else
			mKeyguardLock.disableKeyguard();
	}
	
	private void setVerse(int pos) {
		if (pos >= 0 && pos <= nameList.size()) {
			CustemObject value = nameList.get(pos);

			mBtnDropDown.setText(value.toString());
		}
	}

	private SpinerPopWindow mSpinerPopWindow;

	private void showSpinWindow() {
		Log.e("", "showSpinWindow");
		mSpinerPopWindow.setWidth(mBtnDropDown.getWidth());
		mSpinerPopWindow.showAsDropDown(mBtnDropDown);
	}

}

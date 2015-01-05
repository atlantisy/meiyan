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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
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

	private ImageButton lockbtn = null;// 锁屏方式按钮
	private ImageButton show_verse_btn = null;// 美言显示方式按钮
	private Button setup_grid_button = null;// 设置九宫手势按钮

	private Button mBtnDropDown;
	private List<CustemObject> nameList = new ArrayList<CustemObject>();
	private AbstractSpinerAdapter mAdapter;

	private static final int LINE = 1;// 简约状态
	private static final int GRID = 2;// 九宫状态
	private static final int STATE_LINE = 1;// 锁屏状态设为1
	private static final int STATE_GRID = 2;// 锁屏状态设为2
	private static int flag = 1;// 锁屏方式pref值
	private static final int SINGLE_REPEAT = 1;// 单句循环
	private static final int ORDER_REPEAT = 2;// 顺序循环
	private static final int SHUFFLE = 3;// 随机显示
	private static int showVerseFlag = 3;// 美言显示方式pref值
	private static boolean bPassWord = false;// 九宫格是否设置
	private static String verse = "";// 美言

	public static final String PREFS = "lock_pref";// pref文件名
	public static final String VERSE = "verse";// 美言pref值名称
	public static final String BOOLIDPATH = "wallpaper_idorpath";// 应用内or外壁纸bool的pref值名称,true为ID，false为path
	public static final String WALLPAPERID = "wallpaper_id";// 应用内壁纸资源ID的pref值名称
	public static final String WALLPAPERPATH = "wallpaper_path";// 应用外壁纸Path的pref值名称	
	public static final String LOCKFLAG = "lockFlag";// 锁屏方式pref值名称
	public static final String SHOWVERSEFLAG = "showVerseFlag";//美言显示方式pref值名称
	public static final String PWSETUP = "passWordSetUp";// 九宫格是否设置pref值名称

	private SharedPreferences home_setting;
	private SharedPreferences defaultPrefs;
	
	static final int MENU_SET_MODE = 0;

	private MyServiceConnection myServiceConnection;
	private boolean isLockScreenOn;
	
	private PullToRefreshGridView mPullRefreshGridView;
	private GridView mGridView;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		        
		// 下拉刷新
		mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.pull_refresh_grid);
		mGridView = mPullRefreshGridView.getRefreshableView();
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<GridView> refreshView) {					
						// 下拉
						PullDown();
					}
					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<GridView> refreshView) {
						// 上拉
						PullUp();
					}
				});

		// 美言类型设置选项
		verseOptionSetup();
		
		// 分享按钮
		ImageButton share_button = (ImageButton) findViewById(R.id.home_share1);
		share_button.setOnClickListener(this);
		// 设置按钮
		ImageButton setting_button = (ImageButton) findViewById(R.id.home_setting1);
		setting_button.setOnClickListener(this);
		// 历史记录按钮
		ImageButton recent_button = (ImageButton) findViewById(R.id.home_recent);
		recent_button.setOnClickListener(this);
		// 编辑美言按钮
		ImageButton text_button = (ImageButton) findViewById(R.id.home_text);
		text_button.setOnClickListener(this);
		// 设置九宫格手势
		setup_grid_button = (Button) findViewById(R.id.home_setup_grid);
		setup_grid_button.setOnClickListener(this);
		// Verse
		String strVerse = "123456789";
		// 简约视图
		verse_line = (TextView) findViewById(R.id.line_verse);
		// 九宫格视图
		// verse_grid = (PatternPassWordView)findViewById(R.id.mPatternPassWordView);
		verse_grid1 = (LinearLayout) findViewById(R.id.verse_layout);
		//
		verse_grid = (GridView) findViewById(R.id.grid_verse);
		char[] chVerse = strVerse.toCharArray();
		// 生成动态数组，并且转入数据
		ArrayList<HashMap<String, Object>> listVerse = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 9; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("Verse", "" + chVerse[i]);
			listVerse.add(map);
		}
		// 生成适配器
		SimpleAdapter saVerses = new SimpleAdapter(this, listVerse,
				R.layout.gridview_verse, new String[] { "Verse" },
				new int[] { R.id.verse_content });
		// 添加并且显示,添加消息处理
		verse_grid.setAdapter(saVerses);
		verse_grid.setOnItemClickListener(new ItemClickListener());
		
		// 获取存储的pref数据
		home_setting = getSharedPreferences(PREFS, 0);				
		// 设置壁纸
		SetWallpaper();		
		// 设置美言，简言和九宫言
		SetVerse();		
		// 切换锁屏方式初始化及按钮图标切换事件
		InitShowLock();
		ShowLock();			
		// 切换美言显示方式初始化及按钮图标切换事件
		InitShowVerse();	
		ShowVerse();
		
		//myServiceConnection = new MyServiceConnection();
		// 获取的default prefs数据	
		defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		//isLockScreenOn = defaultPrefs.getBoolean(LOCK_SWITCH, true);
/*		if(isLockScreenOn){
			startService(new Intent(this, MyLockScreenService.class));
			//bindService(new Intent(this, MyLockScreenService.class),myServiceConnection ,Context.BIND_AUTO_CREATE);			
		}		
		else{
			stopService(new Intent(this, MyLockScreenService.class));
			//unbindService(myServiceConnection);
		}*/
		
	}
	
	private final String LOCK_SWITCH = "lock_screen_switch";
	private final String LOCK_STATUS = "lock_status";
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		isLockScreenOn = defaultPrefs.getBoolean(LOCK_SWITCH, true);
		//启动锁屏
		if (isLockScreenOn){
			//启动锁屏服务			
			startService(new Intent(this, MyLockScreenService.class));
			//bindService(new Intent(this, MyLockScreenService.class),myServiceConnection ,Context.BIND_AUTO_CREATE);
			
			EnableSystemKeyguard(false);
		}
		else {
			//关闭锁屏服务
			stopService(new Intent(this, MyLockScreenService.class));
			//unbindService(myServiceConnection);
			
			EnableSystemKeyguard(true);
		}
		
		// 重置锁屏状态
		SharedPreferences.Editor editor = defaultPrefs.edit();	
		editor.putBoolean(LOCK_STATUS, false);
		editor.commit();
	}
	// 界面在前端时刷新美言的设置
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		SetWallpaper();
		SetVerse();
		InitShowVerse();
		bPassWord = home_setting.getBoolean(PWSETUP, false);
		if (flag==GRID && !bPassWord)
			setup_grid_button.setVisibility(View.VISIBLE);
		else
			setup_grid_button.setVisibility(View.GONE);
		
	}
	
	// 下拉
	private void PullDown() {
		// Call onRefreshComplete when the list has been refreshed.
		mPullRefreshGridView.onRefreshComplete();
	}

	// 上拉编辑
	private void PullUp() {
		// 进入编辑模式
		startActivity(new Intent(HomeActivity.this, EditVerseActivity.class));
		// Call onRefreshComplete when the list has been refreshed.
		mPullRefreshGridView.onRefreshComplete();
	}

	// 设置壁纸
	private void SetWallpaper(){
		LinearLayout homeLayout = (LinearLayout)findViewById(R.id.HomeLayout);		
		boolean bIdOrPath = home_setting.getBoolean(BOOLIDPATH, true);
		int wallpaperId = home_setting.getInt(WALLPAPERID, R.drawable.wallpaper01);
		String wallpaperPath = home_setting.getString(WALLPAPERPATH, "");	
		if(bIdOrPath)//设置壁纸			
			homeLayout.setBackgroundResource(wallpaperId);
		else{
			Bitmap bitmap = BitmapFactory.decodeFile(wallpaperPath);
			try {
				homeLayout.setBackgroundDrawable(new BitmapDrawable(bitmap));
			} catch (Exception e) {
				// TODO: handle exception
				homeLayout.setBackgroundResource(wallpaperId);
			}			
		}
	}
	
	// 设置美言
	private void SetVerse() {
		TextView line_verse = (TextView) findViewById(R.id.line_verse);
		String initial_verse = getResources().getString(R.string.initial_verse);
		verse = home_setting.getString(VERSE, StringUtil.getNineStr(initial_verse));// 获取美言
		//设置简言
		line_verse.setText(verse.trim());
		//设置九宫言
		String s = StringUtil.getGridStr(verse);//.replace("\n", " ");
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

	//按两次返回键退出
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
/*			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Object mHelperUtils;
				Toast.makeText(this, "再按一次退出美言锁屏", Toast.LENGTH_SHORT).show();
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

	// 美言类型选项弹出窗口
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
	
	// 美言显示方式按钮初始化
	private void InitShowVerse() {
		show_verse_btn = (ImageButton) findViewById(R.id.home_repeat_shuffle);
		showVerseFlag=home_setting.getInt(SHOWVERSEFLAG, 3);
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
	 * 切换美言显示方式按钮，次序为：单句循环=》顺序循环=》随机显示
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
				// 将美言显示方式设置存入SharedPreferences
				SharedPreferences setting = getSharedPreferences(PREFS, 0);
				SharedPreferences.Editor editor = setting.edit();
				editor.putInt(SHOWVERSEFLAG, showVerseFlag);
				editor.commit();
			}
		});

	}
	// 单句循环
	protected void singleRepeat() {
		showVerseFlag = SINGLE_REPEAT;
		show_verse_btn.setImageResource(R.drawable.ic_single_repeat);

		StringUtil.showToast(this, "单屏循环",  Toast.LENGTH_SHORT);
	}
	// 顺序循环
	protected void orderRepeat() {
		showVerseFlag = ORDER_REPEAT;
		show_verse_btn.setImageResource(R.drawable.ic_order_repeat);

		StringUtil.showToast(this, "顺序循环",  Toast.LENGTH_SHORT);
	}	
	// 随机显示
	protected void shuffle() {
		showVerseFlag = SHUFFLE;
		show_verse_btn.setImageResource(R.drawable.ic_shuffle);
		
		StringUtil.showToast(this, "随机锁屏",  Toast.LENGTH_SHORT);
	}
	
	// 切换锁屏方式按钮初始化
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
			if (bPassWord)
				setup_grid_button.setVisibility(View.GONE);
			else
				setup_grid_button.setVisibility(View.VISIBLE);
			break;
		}
	}
	/**
	 * 切换锁屏方式按钮
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
				// 将锁屏方式设置存入SharedPreferences
				SharedPreferences setting = getSharedPreferences(PREFS, 0);
				SharedPreferences.Editor editor = setting.edit();
				editor.putInt(LOCKFLAG, flag);
				editor.commit();
			}
		});

	}
	// 简单滑动锁屏
	protected void line() {
		flag = LINE;
		lockbtn.setImageResource(R.drawable.ic_lock_grid);
		verse_line.setVisibility(View.VISIBLE);
		verse_grid1.setVisibility(View.GONE);
		setup_grid_button.setVisibility(View.GONE);
		//Toast.makeText(this, R.string.line_verse_style, Toast.LENGTH_SHORT).show();
		StringUtil.showToast(this, "滑动解锁",  Toast.LENGTH_SHORT);
	}
	// 九宫手势锁屏
	protected void grid() {
		flag = GRID;
		lockbtn.setImageResource(R.drawable.ic_lock_line);
		verse_line.setVisibility(View.GONE);
		verse_grid1.setVisibility(View.VISIBLE);
		bPassWord = home_setting.getBoolean(PWSETUP, false);
		if (bPassWord)
			setup_grid_button.setVisibility(View.GONE);
		else
			setup_grid_button.setVisibility(View.VISIBLE);
		//Toast.makeText(this, R.string.grid_verse_style,  Toast.LENGTH_SHORT).show();
		StringUtil.showToast(this, "手势解锁",  Toast.LENGTH_SHORT);
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
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.home_share1:
			Intent intent=new Intent(Intent.ACTION_SEND);  
			intent.setType("text/plain");  
			intent.putExtra(Intent.EXTRA_SUBJECT, "分享");  
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
	
	// 返回其他activity传递的结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 获取九宫密码是否设置结果
		if (20 == resultCode) {
			if (data.getExtras().getBoolean("SetPassWord") == true) {
				// 从setPasswordActivity返回的是否设置九宫密码确认值
				bPassWord = data.getExtras().getBoolean("SetPassWord");
				// 将九宫密码提示设置存入SharedPreferences
				SharedPreferences setting = getSharedPreferences(PREFS, 0);
				SharedPreferences.Editor editor = setting.edit();
				editor.putBoolean(PWSETUP, bPassWord);
				editor.commit();
				// 隐藏九宫密码设置提示按钮
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
	
	// 控制系统锁屏
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

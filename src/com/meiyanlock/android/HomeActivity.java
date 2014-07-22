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
import android.content.SharedPreferences;
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
	private ImageButton lockbtn = null;// 锁屏按钮
	private Button setup_grid_button = null;//设置九宫手势按钮
	
	private Button mBtnDropDown;
	private List<CustemObject> nameList = new ArrayList<CustemObject>();
	private AbstractSpinerAdapter mAdapter;
	
	private static final int LINE = 1;// 简约状态
	private static final int GRID = 2;// 九宫状态
	private static final int STATE_LINE = 1;// 锁屏状态设为1
	private static final int STATE_GRID = 2;// 锁屏状态设为2	
	private static int flag = 1;// 锁屏方式pref值
	private static boolean bPassWord = false;//九宫格是否设置pref值
	
	public static final String PREFS = "lock_pref";//pref文件名	
	public static final String LOCKFLAG = "lockFlag";//锁屏方式pref值名称
	public static final String PWSETUP = "passWordSetUp";//九宫格是否设置pref值名称	
	
	static final int MENU_SET_MODE = 0;

	private PullToRefreshGridView mPullRefreshGridView;
	private GridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		//下拉刷新
		mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.pull_refresh_grid);
		mGridView = mPullRefreshGridView.getRefreshableView();
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
//				mPullRefreshGridView.getLoadingLayoutProxy().setRefreshingLabel("正在加载"); 
//				mPullRefreshGridView.getLoadingLayoutProxy().setPullLabel("下拉推荐美言…"); 
//				mPullRefreshGridView.getLoadingLayoutProxy().setReleaseLabel("放开以推荐…");
				//下拉
				PullDown();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
/*				mPullRefreshGridView.getLoadingLayoutProxy().setRefreshingLabel("正在加载"); 
				mPullRefreshGridView.getLoadingLayoutProxy().setPullLabel("上拉编辑美言"); 
				mPullRefreshGridView.getLoadingLayoutProxy().setReleaseLabel("放开以编辑");*/
				//上拉
				PullUp();
			}

		});
		
		//美言类型设置选项
		verseOptionSetup();		
		// 设置按钮
		ImageButton setting_button = (ImageButton) findViewById(R.id.home_setting);
		setting_button.setOnClickListener(settingOnClickListener);
		// 编辑美言按钮
		ImageButton text_button = (ImageButton) findViewById(R.id.home_text);
		text_button.setOnClickListener(textOnClickListener);
		
		// 设置九宫格手势
		setup_grid_button = (Button) findViewById(R.id.home_setup_grid);
		setup_grid_button.setOnClickListener(setGridOnClickListener);
		
		// Verse
		String strVerse = "感觉 自己 萌萌哒";
		// 简约视图
		verse_line = (TextView) findViewById(R.id.line_verse);
		// 九宫格视图
		//verse_grid = (LocusPassWordView) findViewById(R.id.mLocusPassWordView);
		verse_grid1 = (LinearLayout) findViewById(R.id.verse_layout);
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

		//获取存储的pref数据
		SharedPreferences home_setting = getSharedPreferences(PREFS, 0);  
		flag = home_setting.getInt(LOCKFLAG, 1);
		bPassWord = home_setting.getBoolean(PWSETUP, false);		
		// 切换锁屏方式
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
		//切换显示锁屏方式按钮图标
		ShowLockBtn();
	}

	//下拉
	private void PullDown(){
		// Call onRefreshComplete when the list has been refreshed.
		mPullRefreshGridView.onRefreshComplete();
		
	}
	
	//上拉编辑
	private void PullUp(){
		//进入编辑模式		
		startActivity(new Intent(HomeActivity.this, TextEditActivity.class));
		// Call onRefreshComplete when the list has been refreshed.
		mPullRefreshGridView.onRefreshComplete();
		
	}
	
    //美言类型设置选项
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
				//将锁屏方式设置存入SharedPreferences
				SharedPreferences setting1 = getSharedPreferences(PREFS, 0);  
				SharedPreferences.Editor editor1 = setting1.edit();  
				editor1.putInt(LOCKFLAG, flag);  
				editor1.commit();
			}
		});

	}

	// 简约锁屏
	protected void line() {
		flag = LINE;
		lockbtn.setImageResource(R.drawable.ic_lock_line);
		verse_line.setVisibility(View.VISIBLE);
		verse_grid1.setVisibility(View.GONE);
		setup_grid_button.setVisibility(View.GONE);
        Toast.makeText(this, R.string.line_verse_style, Toast.LENGTH_LONG)
        .show();
	}

	// 九宫锁屏
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
	
	// 设置九宫格按钮点击事件
	private OnClickListener setGridOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stubsetGridOnClickListener
	        //设置九宫格手势
	        startActivityForResult(new Intent(HomeActivity.this, SetPasswordActivity.class), 100);
		}
	};
	
	
	//返回其他activity传递的结果
	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		//获取九宫密码是否设置结果
		if (20==resultCode){
			if(data.getExtras().getBoolean("SetPassWord")==true){
				//从setPasswordActivity返回的是否设置九宫密码确认值
				bPassWord = data.getExtras().getBoolean("SetPassWord");			
				//将九宫密码提示设置存入SharedPreferences
				SharedPreferences setting = getSharedPreferences(PREFS, 0);  
				SharedPreferences.Editor editor = setting.edit();  
				editor.putBoolean(PWSETUP, bPassWord);  
				editor.commit();
				//隐藏九宫密码设置提示按钮
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

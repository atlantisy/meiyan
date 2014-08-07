package com.meiyanlock.android;

import java.util.ArrayList;
import java.util.List;
import com.meiyanlock.widget.AbstractSpinerAdapter;
import com.meiyanlock.widget.CustemObject;
import com.meiyanlock.widget.CustemSpinerAdapter;
import com.meiyanlock.widget.SpinerPopWindow;
import com.meiyanlock.widget.WallpaperAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

public class EditVerseActivity extends Activity implements OnClickListener, AbstractSpinerAdapter.IOnItemSelectListener{

	private Button mBtnDropDown;//下拉按钮
	private List<CustemObject> nameList = new ArrayList<CustemObject>();
	private AbstractSpinerAdapter mAdapter;
	private EditText verse_edit = null;

	public static final String PREFS = "lock_pref";//pref文件名
	public static final String VERSE = "verse";//锁屏方式pref值名称
	public static final String WALLPAPER = "wallpaper";//壁纸pref值名称
	
	private boolean bControlEditColor = true;
	private LinearLayout mEditVerseLayout ;
	private WallpaperAdapter wpAdapter;
	private GridView wpGridview;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editverse);
		mEditVerseLayout = (LinearLayout)findViewById(R.id.editverse_layout);
		// 获取存储的pref数据
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
		int wallpaperId = settings.getInt(WALLPAPER, R.drawable.wallpaper00);
		mEditVerseLayout.setBackgroundResource(wallpaperId);
		//壁纸初始化
		wpAdapter = new WallpaperAdapter(this);
		wpGridview = (GridView) findViewById(R.id.wallpaper_grid);
		wpGridview.setAdapter(wpAdapter);
		wpGridview.setOnItemClickListener(wallpaperListener);		
		//获取保存的美言			  
		String verse = settings.getString(VERSE, "");
		//设置默认美言
		verse_edit = (EditText)findViewById(R.id.edit_verse);	
		//verse_edit.setText(verse.trim().toCharArray(), 0, verse.trim().length());
		//verse_edit.setSelection(verse.trim().length());//设置光标在末尾		
		//发布美言
		ImageButton editok_btn = (ImageButton)findViewById(R.id.edit_ok);
		editok_btn.setOnClickListener(editOkOnClickListener);
		//选择背景颜色
		ImageButton editcolor_btn = (ImageButton)findViewById(R.id.edit_color);
		editcolor_btn.setOnClickListener(editColorOnClickListener);
		
		// 自定义种类选择
		customOptionSetup();
	}
	
	/** 选择壁纸**/
	OnItemClickListener wallpaperListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			int wallpaperId = WallpaperAdapter.wallpaper[arg2];
			mEditVerseLayout.setBackgroundResource(wallpaperId);
			// 将锁屏方式设置存入SharedPreferences
			SharedPreferences setting = getSharedPreferences(PREFS, 0);
			SharedPreferences.Editor editor = setting.edit();
			editor.putInt(WALLPAPER,wallpaperId);//
			editor.commit();		
		}
	};
	
	//发布美言及壁纸
	private OnClickListener editOkOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
/*			verse_edit.clearFocus();
			verse_edit.setCursorVisible(false);*/
			//获取自定义美言
			String verse = verse_edit.getText().toString();						
			//判断是否需要扩展美言
			int len = verse.length();
			if(len<9)
				for(int i=0; i<=9-len; i++)
					verse+=" ";
			//将美言存入SharedPreferences
			SharedPreferences setting = getSharedPreferences(PREFS, 0);  
			SharedPreferences.Editor editor = setting.edit();  
			editor.putString(VERSE, verse);  
			editor.commit();
			
			startActivity(new Intent(EditVerseActivity.this, HomeActivity.class));
			finish();
		}
	};

	//选择背景颜色
	private OnClickListener editColorOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(bControlEditColor==true){
				bControlEditColor=false;
				wpGridview.setVisibility(View.VISIBLE);				
			}
			else{
				bControlEditColor=true;
				wpGridview.setVisibility(View.GONE);				
			}
		}
		
	};
	
	private void customOptionSetup(){
		mBtnDropDown = (Button) findViewById(R.id.edit_label);
		mBtnDropDown.setOnClickListener(this);
				
		String[] names = getResources().getStringArray(R.array.customize);
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
    
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.edit_label:
			showSpinWindow();
			break;
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
		setCustom(pos);
	}
	
	private void setCustom(int pos){
		if (pos >= 0 && pos <= nameList.size()){
			CustemObject value = nameList.get(pos);
		
			mBtnDropDown.setText(value.toString());
		}
	}
	
}

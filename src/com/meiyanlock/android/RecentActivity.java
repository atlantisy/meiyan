package com.meiyanlock.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.meiyanlock.android.R;
import com.meiyanlock.widget.dbHelper;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SimpleCursorAdapter;

public class RecentActivity extends Activity {
	
	dbHelper dbRecent;
	private Cursor recentCursor;
	private ListView recentList;
	private int _id;
	
	public static final String PREFS = "lock_pref";// pref文件名
	public static final String VERSE = "verse";// 美言pref值名称

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent);				
        recentList = (ListView) findViewById(R.id.list_verse);
        
        dbRecent = new dbHelper(this);
        recentCursor = dbRecent.select();
    	SimpleCursorAdapter recentAdapter = new SimpleCursorAdapter(this,
        		R.layout.view_recent,recentCursor,
        		new String[]{dbHelper.FIELD_TITLE},
        		new int[]{R.id.recent_item});
        recentList.setAdapter(recentAdapter);  
        
        //添加点击
        recentList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				recentCursor.moveToPosition(arg2);
				_id = recentCursor.getInt(0);
				String verse = recentCursor.getString(1);
				// 将美言存入SharedPreferences
				SharedPreferences settings = getSharedPreferences(PREFS, 0);
				SharedPreferences.Editor editor = settings.edit();				
				editor.putString(VERSE, verse);// 美言
				editor.commit();
				
				startActivity(new Intent(RecentActivity.this, HomeActivity.class));
			}
		});
        
/*        recentList.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				SQLiteCursor sc = (SQLiteCursor)arg0.getSelectedItem();
				_id = sc.getInt(0);
				//String verse = sc.getString(1);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		}); */       
        
        //添加长按点击
        recentList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				//menu.setHeaderTitle("美言");   
				menu.add(0, 0, 0, "删除");
				//menu.add(0, 1, 0, "收藏");   
			}
		}); 
	}
	
	//长按菜单响应函数
	@Override
	public boolean onContextItemSelected(MenuItem item) {		
		super.onOptionsItemSelected(item);
		
		switch (item.getItemId()) {
		case 0:
			operation("delete");
			break;
		default:
			break;
		}
		return true;
	}
	
    private void operation(String cmd)
    {
/*    	if(cmd=="add")
    		dbRecent.insert( myEditText.getText().toString());
    	if(cmd=="edit")
    		dbRecent.update(_id,  myEditText.getText().toString());*/
    	if(cmd=="delete")
    		dbRecent.delete(_id);
    	recentCursor.requery();
    	recentList.invalidateViews();
    	_id=0;    	
    }
	
}

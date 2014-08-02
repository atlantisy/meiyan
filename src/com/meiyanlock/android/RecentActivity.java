package com.meiyanlock.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.meiyanlock.android.R;
import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class RecentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent);		
		
        ListView list = (ListView) findViewById(R.id.list_verse);
        //生成动态数组，加入数据
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        //历史记录只取30条
        for(int i = 0; i < 30; i++){
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	map.put("ItemTitle", "What will your verse be? "+i);
        	listItem.add(map);
        }
        //生成适配器的Item和动态数组对应的元素
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,
        	//数据源
        	listItem, 
        	//listview_recent的XML实现
        	R.layout.listview_recent,
            //动态数组与ImageItem对应的子项        
            new String[] {"ItemTitle"}, 
            new int[] {R.id.ItemTitle}
        );       
        //添加并且显示
        list.setAdapter(listItemAdapter);        
        //添加点击
        list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				setTitle("点击第"+arg2+"个项目");
			}
		});        
        //添加长按点击
        list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				menu.setHeaderTitle("美言");   
				menu.add(0, 0, 0, "分享");
				menu.add(0, 1, 0, "收藏");   
			}
		}); 
	}
	
	//长按菜单响应函数
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		setTitle("点击第"+item.getItemId()+"个项目"); 
		return super.onContextItemSelected(item);
	}
	
}

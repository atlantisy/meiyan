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
        //���ɶ�̬���飬��������
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        //��ʷ��¼ֻȡ30��
        for(int i = 0; i < 30; i++){
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	map.put("ItemTitle", "What will your verse be? "+i);
        	listItem.add(map);
        }
        //������������Item�Ͷ�̬�����Ӧ��Ԫ��
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,
        	//����Դ
        	listItem, 
        	//listview_recent��XMLʵ��
        	R.layout.listview_recent,
            //��̬������ImageItem��Ӧ������        
            new String[] {"ItemTitle"}, 
            new int[] {R.id.ItemTitle}
        );       
        //��Ӳ�����ʾ
        list.setAdapter(listItemAdapter);        
        //��ӵ��
        list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				setTitle("�����"+arg2+"����Ŀ");
			}
		});        
        //��ӳ������
        list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				menu.setHeaderTitle("����");   
				menu.add(0, 0, 0, "����");
				menu.add(0, 1, 0, "�ղ�");   
			}
		}); 
	}
	
	//�����˵���Ӧ����
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		setTitle("�����"+item.getItemId()+"����Ŀ"); 
		return super.onContextItemSelected(item);
	}
	
}

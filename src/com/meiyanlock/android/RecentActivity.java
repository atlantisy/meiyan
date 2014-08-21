package com.meiyanlock.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.meiyanlock.android.R;
import com.meiyanlock.widget.dbHelper;

import android.app.Activity;
import android.app.ListActivity;
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
	
	public static final String PREFS = "lock_pref";// pref�ļ���
	public static final String VERSE = "verse";// ����prefֵ����

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
        
        
/*        //���ɶ�̬���飬��������
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
        	R.layout.view_recent,
            //��̬������ImageItem��Ӧ������        
            new String[] {"ItemTitle"}, 
            new int[] {R.id.recent_item}
        );       
        //��Ӳ�����ʾ
        recentList.setAdapter(listItemAdapter); */
        
        
        //��ӵ��
        recentList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				//setTitle("�����"+arg2+"����Ŀ");
				recentCursor.moveToPosition(arg2);
				_id = recentCursor.getInt(0);
				String verse = recentCursor.getString(1);
			}
		});
        
        recentList.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				SQLiteCursor sc=(SQLiteCursor)arg0.getSelectedItem();
				_id=sc.getInt(0);
				String verse = sc.getString(1);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
        //��ӳ������
        recentList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				//menu.setHeaderTitle("����");   
				menu.add(0, 0, 0, "ɾ��");
				//menu.add(0, 1, 0, "�ղ�");   
			}
		}); 
	}
	
	//�����˵���Ӧ����
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//setTitle("�����"+item.getItemId()+"����Ŀ"); 
		//return super.onContextItemSelected(item);
		
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

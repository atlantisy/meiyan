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
		
		/*		SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.recent_row,
		new String[]{"verse"},
		new int[]{R.id.recent_verse});
		setListAdapter(adapter);*/
		
		//��Layout�����list_verse
        ListView list = (ListView) findViewById(R.id.list_verse);
        //���ɶ�̬���飬��������
        ArrayList<HashMap<String, Object>> listItem 
        	= new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<10;i++){
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	map.put("ItemImage", R.drawable.checked);//ͼ����Դ��ID
        	map.put("ItemTitle", "Level "+i);
        	map.put("ItemText", "Finished in 1 Min 54 Secs, 70 Moves! ");
        	listItem.add(map);
        }
        //������������Item�Ͷ�̬�����Ӧ��Ԫ��
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,//����Դ 
            R.layout.recent_row,//ListItem��XMLʵ��
            //��̬������ImageItem��Ӧ������        
            new String[] {"ItemImage","ItemTitle", "ItemText"}, 
            //ImageItem��XML�ļ������һ��ImageView,����TextView ID
            new int[] {R.id.ItemImage,R.id.ItemTitle,R.id.ItemText}
        );
       
        //��Ӳ�����ʾ
        list.setAdapter(listItemAdapter);
        
        //��ӵ��
        list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				setTitle("�����"+arg2+"����Ŀ");
			}
		});
        
      //��ӳ������
        list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
											ContextMenuInfo menuInfo) {
				menu.setHeaderTitle("�����˵�-ContextMenu");   
				menu.add(0, 0, 0, "���������˵�0");
				menu.add(0, 1, 0, "���������˵�1");   
			}
		}); 
	}
	
	//�����˵���Ӧ����
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		setTitle("����˳����˵�����ĵ�"+item.getItemId()+"����Ŀ"); 
		return super.onContextItemSelected(item);
	}
	
/*	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("verse", "What");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("verse", "will");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("verse", "your verse");
		list.add(map);
		
		return list;
	}*/
}

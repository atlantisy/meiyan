package cn.minelock.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import cn.minelock.widget.ImageSimpleAdapter;
import cn.minelock.widget.dbHelper;

import cn.minelock.android.R;

public class RecentActivity extends Activity {
	
	dbHelper dbRecent;
	private Cursor recentCursor;
	private ListView recentList;
	private SimpleCursorAdapter recentAdapter;
	private ImageButton random_btn;
	private int _id;
	
	private String deleteItem;
	
	public static final String PREFS = "lock_pref";// pref�ļ���
	public static final String VERSE = "verse";// ����prefֵ����
	public static final String VERSEID = "verse_id";// ����id prefֵ����
	public static final String VERSEQTY = "verse_quantity";// ��������prefֵ����
	public static final String SHOWVERSEFLAG = "showVerseFlag";//������ʾ��ʽprefֵ����
	public static final String BOOLIDPATH = "wallpaper_idorpath";//Ӧ����or���ֽbool��prefֵ����,trueΪID��falseΪpath
	public static final String WALLPAPERID = "wallpaper_id";//Ӧ���ڱ�ֽ��ԴID��prefֵ����
	public static final String WALLPAPERPATH = "wallpaper_path";//Ӧ�����ֽPath��prefֵ����
	
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	
	private int verseQty;
	private TextView recent_label;
	private String title = "������¼";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent);				
        recentList = (ListView) findViewById(R.id.list_verse);
        //����listΪ��ʱ����ʾ
		String[] none_verse = getResources().getStringArray(R.array.none_verse);
		int random = (int)(Math.random()*none_verse.length);		        
        TextView noneView = new TextView(this);
        noneView.setText(none_verse[random]);        
        noneView.setTextSize(20.0f);
        noneView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);     
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
        addContentView(noneView, params);
        recentList.setEmptyView(noneView);
        // �������ݿ�
        dbRecent = new dbHelper(this);
        recentCursor = dbRecent.select();
    	recentAdapter = new SimpleCursorAdapter(this,
        		R.layout.view_recent,
        		recentCursor,
        		new String[]{dbHelper.FIELD_TITLE,dbHelper.FIELD_ITEM},
        		new int[]{R.id.recent_title,R.id.recent_item});
    	
/*        SimpleAdapter recentAdapter1= new ImageSimpleAdapter(this, 
        		getDatas(), 
        		R.layout.view_recent, 
        		new String[]{dbHelper.FIELD_TITLE,dbHelper.FIELD_ITEM},
        		new int[]{R.id.recent_title,R.id.recent_item});*/
    	// ������Ա�ֽ�б�
    	recentList.setAdapter(recentAdapter);
        // ��ȡ�����SharedPreferences
        settings = getSharedPreferences(PREFS, 0);
		editor = settings.edit();
		verseQty = settings.getInt(VERSEQTY, 0);//ʵ������������
               
        // ��ӵ��
        recentList.setOnItemClickListener(new OnItemClickListener() {
			
        	@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				recentCursor.moveToPosition(arg2);
				_id = recentCursor.getInt(0);
				String verse = recentCursor.getString(2);// + recentCursor.getString(1);
				int idPath = recentCursor.getInt(3);
				int wallpaperId = recentCursor.getInt(4);
				String wallpaperPath = recentCursor.getString(5);
				// ����ֽ����SharedPreferences
				boolean bIdPath=false;
				if(idPath==1)
					bIdPath = true;
				editor.putBoolean(BOOLIDPATH, bIdPath);// ��ֽ
				editor.putInt(WALLPAPERID, wallpaperId);// ��ֽid
				editor.putString(WALLPAPERPATH, wallpaperPath);// ��ֽpath
				// �����Դ���SharedPreferences				
				editor.putString(VERSE, verse);// ����
				//editor.putLong(VERSEID, (long)_id);// ����id
				//editor.putInt(SHOWVERSEFLAG, 1);// ����ѭ��				
				editor.commit();
				
				startActivity(new Intent(RecentActivity.this, HomeActivity.class));
				overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
				//overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
			}
		});
        
/*        recentList.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				SQLiteCursor sc = (SQLiteCursor)arg0.getSelectedItem();
				_id = sc.getInt(0);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		}); */       
        
        // ��ӳ������
        recentList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				recentCursor.moveToPosition(arg2);
				_id = recentCursor.getInt(0);
				deleteItem = recentCursor.getString(2);//+recentCursor.getInt(1);
				return false;
			}
		});
        
        // ��ӳ����˵���Ӧ
        recentList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {			
			
        	@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				//menu.setHeaderTitle(deleteItem.trim());   
				menu.add(0, 0, 0, "����");				
				menu.add(0, 1, 0, "����");
				menu.add(0, 2, 0, "ɾ��");
			}
		}); 
        
        // ��ȡ��ɾ�����������
        String Qty = title + "(" + String.valueOf(verseQty) + ")";
        recent_label = (TextView) findViewById(R.id.recent_label);
        recent_label.setText(Qty);
        
        // ���ѡȡ����
        random_btn = (ImageButton)findViewById(R.id.recent_random);
        random_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int random = (int)(Math.random()*verseQty);
				if(verseQty>0){
					recentCursor.moveToPosition(random);
					_id = recentCursor.getInt(0);
					String verse = recentCursor.getString(1) + recentCursor.getString(2);
					// �����Դ���SharedPreferences				
					editor.putString(VERSE, verse);// ����
					editor.commit();
				
					startActivity(new Intent(RecentActivity.this, HomeActivity.class));
				}
/*				else
					random_btn.setClickable(false);*/
			}
		});
        
		// ����
		ImageButton return_btn = (ImageButton) findViewById(R.id.recent_return);
		return_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(RecentActivity.this, HomeActivity.class));
				overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
				//overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
			}
		});
		
/*		String[] initial_verse = getResources().getStringArray(R.array.recent_inital_verse);
		int[] initial_wallpaper = {	
				R.drawable.wallpaper02,R.drawable.wallpaper04,R.drawable.wallpaper03,R.drawable.wallpaper05,R.drawable.wallpaper01};
		for(int i=0;i<5;i++)
			dbRecent.insert(initial_verse[i].substring(0, 1),initial_verse[i].substring(1),1,initial_wallpaper[i],"");*/
		
	}
	
    private List<HashMap<String,Object>> getDatas() {  
    	int columnsSize = recentCursor.getColumnCount();  
    	List<HashMap<String,Object>> listData = new ArrayList<HashMap<String, Object>>();  
        // ��ȡ�������  
        while (recentCursor.moveToNext()) {  
            HashMap<String, Object> map = new HashMap<String, Object>();  
            for (int i = 0; i < columnsSize; i++) {  
                map.put(dbHelper.FIELD_TITLE, recentCursor.getInt(1));  
                map.put(dbHelper.FIELD_ITEM, recentCursor.getString(2));  
                map.put(dbHelper.BOOL_ID_PATH, recentCursor.getInt(3));  
                map.put(dbHelper.WALLPAPER_ID, recentCursor.getInt(4));
                map.put(dbHelper.WALLPAPER_PATH, recentCursor.getString(5)); 
            }  
            listData.add(map);                      
        } 
        
        return listData; 
    }
	//�����˵���Ӧ����
	@Override
	public boolean onContextItemSelected(MenuItem item) {		
		super.onOptionsItemSelected(item);
		
		switch (item.getItemId()) {
		case 0:
			operation("share");
			break;
		case 1:
			operation("copy");
			Toast.makeText(getApplicationContext(), "�Ѹ���", Toast.LENGTH_SHORT).show();
			break;			
		case 2:			
			operation("delete");
			if(verseQty>0)
				verseQty = verseQty-1;
			else
				verseQty = 0;
			editor.putInt(VERSEQTY, verseQty);// ��������
			editor.commit();
			
			recent_label.setText(title + "(" + String.valueOf(verseQty) + ")");
			Toast.makeText(getApplicationContext(), "��ɾ��", Toast.LENGTH_SHORT).show();
			break;
		case 3:
			operation("edit");
			//Toast.makeText(getApplicationContext(), "���³ɹ�", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
		return true;
	}
	
    private void operation(String cmd)
    {
    	setTitle("");
    	if(cmd=="share"){
			Intent intent=new Intent(Intent.ACTION_SEND);  
			intent.setType("text/plain");  
			intent.putExtra(Intent.EXTRA_SUBJECT, "����");  
			intent.putExtra(Intent.EXTRA_TEXT, deleteItem.trim()+"#minelock#");  
			startActivity(Intent.createChooser(intent, getTitle()));
    	}
    	if(cmd=="copy"){
    		//dbRecent.insert( myEditText.getText().toString());    		
    		ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    		cbm.setText(deleteItem);     		
    	}
    	if(cmd=="edit"){
    		final EditText myEditText = new EditText(this);
    		//myEditText.setText(deleteItem.trim().toCharArray(), 0, deleteItem.trim().length());
    		myEditText.setText(deleteItem);
    		myEditText.setSelection(deleteItem.trim().length());//���ù����ĩβ
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
    		builder.setView(myEditText).setNegativeButton("ȡ��", null);
    		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
            		String str = myEditText.getText().toString();
            		dbRecent.update(_id, str.substring(0, 1), str.substring(1));           		
                 }
            });
    		builder.show();
    	}
    	if(cmd=="delete")
    		dbRecent.delete(_id);    		
    	recentCursor.requery();
    	//recentList.invalidateViews();
    	recentAdapter.notifyDataSetChanged();
    	recentList.invalidate();
    	_id=0;    	
    }
	
}

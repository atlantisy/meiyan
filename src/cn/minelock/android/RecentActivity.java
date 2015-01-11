package cn.minelock.android;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
	//private SimpleCursorAdapter recentAdapter;
	private SimpleAdapter recentAdapter1;
	private ImageButton random_btn;
	private int _id;
	private int position;
	
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
	private String title = "�����б�";
	
	private ArrayList<HashMap<String,Object>> listData;
	
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
/*    	recentAdapter = new SimpleCursorAdapter(this,
        		R.layout.view_recent,
        		recentCursor,
        		new String[]{dbHelper.FIELD_TITLE,dbHelper.FIELD_ITEM},
        		new int[]{R.id.recent_title,R.id.recent_item});*/
    	
        recentAdapter1 = new ImageSimpleAdapter(this, 
        		getData(), 
        		R.layout.view_recent, 
        		new String[]{dbHelper.FIELD_TITLE,dbHelper.FIELD_ITEM},
        		new int[]{R.id.recent_title,R.id.recent_item});
    	// ������Ա�ֽ�б�
    	recentList.setAdapter(recentAdapter1);
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
				position=arg2;
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
/*        random_btn = (ImageButton)findViewById(R.id.recent_random);
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
				else
					random_btn.setClickable(false);
			}
		});*/
        
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
	}
	
    private ArrayList<HashMap<String,Object>> getData() {  
    	int columnsSize = recentCursor.getColumnCount();  
    	listData = new ArrayList<HashMap<String, Object>>();  
        // ��ȡ�������  
        while (recentCursor.moveToNext()) {  
        	HashMap<String, Object> map = new HashMap<String, Object>();  
            for (int i = 0; i < columnsSize; i++) {
            	int bool=recentCursor.getInt(3);
            	String path=recentCursor.getString(5);
            	String _path=path.substring(0, path.length()-4)+"_.png";
            	// ��ֵ
            	if(bool==1)
            		map.put(dbHelper.FIELD_TITLE, recentCursor.getInt(1));
            	else
            		map.put(dbHelper.FIELD_TITLE, BitmapFactory.decodeFile(_path));
                map.put(dbHelper.FIELD_ITEM, recentCursor.getString(2));                 
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
			Toast.makeText(getApplicationContext(), "�Ѹ�������", Toast.LENGTH_SHORT).show();
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
    	if(cmd=="delete"){
    		dbRecent.delete(_id);
    		listData.remove(position);
    	}   		    		
    	recentCursor.requery();
    	//recentList.invalidateViews();    	
    	recentAdapter1.notifyDataSetChanged();
    	recentList.invalidate();
    	_id=0;    	
    }
    
    public void shareMsg(String activityTitle, String msgTitle, String msgText,  
            String imgPath) {  
        Intent intent = new Intent(Intent.ACTION_SEND);  
        if (imgPath == null || imgPath.equals("")) {  
            intent.setType("text/plain"); // ���ı�   
        } else {  
            File f = new File(imgPath);  
            if (f != null && f.exists() && f.isFile()) {  
                intent.setType("image/jpg");  
                Uri u = Uri.fromFile(f);  
                intent.putExtra(Intent.EXTRA_STREAM, u);  
            }  
        }  
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);  
        intent.putExtra(Intent.EXTRA_TEXT, msgText);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        startActivity(Intent.createChooser(intent, activityTitle));  
    }
}

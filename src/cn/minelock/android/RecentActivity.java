package cn.minelock.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
	
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	
	private int verseQty;
	private TextView recent_label;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent);				
        recentList = (ListView) findViewById(R.id.list_verse);
        //����listΪ��ʱ����ʾ
        TextView emptyView = new TextView(this);
        emptyView.setText("�˵�����������");
        emptyView.setTextSize(20.0f);
        emptyView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);     
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
        addContentView(emptyView, params);
        recentList.setEmptyView(emptyView);
        // �������ݿ�
        dbRecent = new dbHelper(this);
        recentCursor = dbRecent.select();
    	recentAdapter = new SimpleCursorAdapter(this,
        		R.layout.view_recent,recentCursor,
        		new String[]{dbHelper.FIELD_TITLE,dbHelper.FIELD_ITEM},
        		new int[]{R.id.recent_title,R.id.recent_item});
        recentList.setAdapter(recentAdapter);
        // ��ȡ�����SharedPreferences
        settings = getSharedPreferences(PREFS, 0);
		editor = settings.edit();
		verseQty = settings.getInt(VERSEQTY, 0);//ʵ������������
               
        //��ӵ��
        recentList.setOnItemClickListener(new OnItemClickListener() {
			
        	@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				recentCursor.moveToPosition(arg2);
				_id = recentCursor.getInt(0);
				String verse = recentCursor.getString(1) + recentCursor.getString(2);
				// �����Դ���SharedPreferences				
				editor.putString(VERSE, verse);// ����
				//editor.putLong(VERSEID, (long)_id);// ����id
				editor.commit();
				
				startActivity(new Intent(RecentActivity.this, HomeActivity.class));
				//overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				//overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
				overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
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
        
        //��ӳ������
        recentList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				recentCursor.moveToPosition(arg2);
				_id = recentCursor.getInt(0);
				deleteItem = recentCursor.getString(1)+recentCursor.getString(2);
				return false;
			}
		});
        
        //��ӳ����˵���Ӧ
        recentList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {			
			
        	@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				menu.setHeaderTitle(deleteItem.trim());   
				menu.add(0, 0, 0, "����");				
				menu.add(0, 1, 0, "ɾ��");
				//menu.add(0, 2, 0, "�޸�");
			}
		}); 
        
        //��ȡ��ɾ�����������
        String Qty = "�ҵ�����(" + String.valueOf(verseQty) + ")";
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
					//overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
					//overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
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
				//overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				//overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
				overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
			}
		});
	}
	
	//�����˵���Ӧ����
	@Override
	public boolean onContextItemSelected(MenuItem item) {		
		super.onOptionsItemSelected(item);
		
		switch (item.getItemId()) {
		case 0:
			operation("copy");
			Toast.makeText(getApplicationContext(), "�Ѹ���", Toast.LENGTH_SHORT).show();
			break;
		case 1:			
			operation("delete");
			if(verseQty>0)
				verseQty = verseQty-1;
			else
				verseQty = 0;
			editor.putInt(VERSEQTY, verseQty);// ��������
			editor.commit();
			
			recent_label.setText("�ҵ�����(" + String.valueOf(verseQty) + ")");
			Toast.makeText(getApplicationContext(), "��ɾ��", Toast.LENGTH_SHORT).show();
			break;
		case 2:
			operation("edit");
			Toast.makeText(getApplicationContext(), "���³ɹ�", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
		return true;
	}
	
    private void operation(String cmd)
    {
    	setTitle("");
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

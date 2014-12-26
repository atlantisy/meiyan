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
	
	public static final String PREFS = "lock_pref";// pref文件名
	public static final String VERSE = "verse";// 美言pref值名称
	public static final String VERSEID = "verse_id";// 美言id pref值名称
	public static final String VERSEQTY = "verse_quantity";// 美言数量pref值名称
	public static final String SHOWVERSEFLAG = "showVerseFlag";//美言显示方式pref值名称
	public static final String BOOLIDPATH = "wallpaper_idorpath";//应用内or外壁纸bool的pref值名称,true为ID，false为path
	public static final String WALLPAPERID = "wallpaper_id";//应用内壁纸资源ID的pref值名称
	public static final String WALLPAPERPATH = "wallpaper_path";//应用外壁纸Path的pref值名称
	
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	
	private int verseQty;
	private TextView recent_label;
	private String title = "锁屏记录";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent);				
        recentList = (ListView) findViewById(R.id.list_verse);
        //设置list为空时的提示
		String[] none_verse = getResources().getStringArray(R.array.none_verse);
		int random = (int)(Math.random()*none_verse.length);		        
        TextView noneView = new TextView(this);
        noneView.setText(none_verse[random]);        
        noneView.setTextSize(20.0f);
        noneView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);     
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
        addContentView(noneView, params);
        recentList.setEmptyView(noneView);
        // 创建数据库
        dbRecent = new dbHelper(this);
        recentCursor = dbRecent.select();
    	recentAdapter = new SimpleCursorAdapter(this,
        		R.layout.view_recent,recentCursor,
        		new String[]{dbHelper.FIELD_TITLE,dbHelper.FIELD_ITEM},
        		new int[]{R.id.recent_title,R.id.recent_item});
        // 最近美言壁纸列表
    	recentList.setAdapter(recentAdapter);
        // 获取保存的SharedPreferences
        settings = getSharedPreferences(PREFS, 0);
		editor = settings.edit();
		verseQty = settings.getInt(VERSEQTY, 0);//实际美言总数量
               
        //添加点击
        recentList.setOnItemClickListener(new OnItemClickListener() {
			
        	@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				recentCursor.moveToPosition(arg2);
				_id = recentCursor.getInt(0);
				String verse = recentCursor.getString(1) + recentCursor.getString(2);
				int idPath = recentCursor.getInt(3);
				int wallpaperId = recentCursor.getInt(4);
				String wallpaperPath = recentCursor.getString(5);
				// 将壁纸存入SharedPreferences
				boolean bIdPath=false;
				if(idPath==1)
					bIdPath = true;
				editor.putBoolean(BOOLIDPATH, bIdPath);// 壁纸
				editor.putInt(WALLPAPERID, wallpaperId);// 壁纸id
				editor.putString(WALLPAPERPATH, wallpaperPath);// 壁纸path
				// 将美言存入SharedPreferences				
				editor.putString(VERSE, verse);// 美言
				//editor.putLong(VERSEID, (long)_id);// 美言id
				//editor.putInt(SHOWVERSEFLAG, 1);// 单句循环				
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
        
        //添加长按点击
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
        
        //添加长按菜单响应
        recentList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {			
			
        	@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				//menu.setHeaderTitle(deleteItem.trim());   
				menu.add(0, 0, 0, "分享");				
				menu.add(0, 1, 0, "复制");
				menu.add(0, 2, 0, "删除");
			}
		}); 
        
        //获取增删后的美言数量
        String Qty = title + "(" + String.valueOf(verseQty) + ")";
        recent_label = (TextView) findViewById(R.id.recent_label);
        recent_label.setText(Qty);
        
        // 随机选取美言
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
					// 将美言存入SharedPreferences				
					editor.putString(VERSE, verse);// 美言
					editor.commit();
				
					startActivity(new Intent(RecentActivity.this, HomeActivity.class));
				}
/*				else
					random_btn.setClickable(false);*/
			}
		});
        
		// 返回
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
	
	//长按菜单响应函数
	@Override
	public boolean onContextItemSelected(MenuItem item) {		
		super.onOptionsItemSelected(item);
		
		switch (item.getItemId()) {
		case 0:
			operation("share");
			break;
		case 1:
			operation("copy");
			Toast.makeText(getApplicationContext(), "已复制", Toast.LENGTH_SHORT).show();
			break;			
		case 2:			
			operation("delete");
			if(verseQty>0)
				verseQty = verseQty-1;
			else
				verseQty = 0;
			editor.putInt(VERSEQTY, verseQty);// 美言数量
			editor.commit();
			
			recent_label.setText(title + "(" + String.valueOf(verseQty) + ")");
			Toast.makeText(getApplicationContext(), "已删除", Toast.LENGTH_SHORT).show();
			break;
		case 3:
			operation("edit");
			//Toast.makeText(getApplicationContext(), "更新成功", Toast.LENGTH_SHORT).show();
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
			intent.putExtra(Intent.EXTRA_SUBJECT, "分享");  
			intent.putExtra(Intent.EXTRA_TEXT, deleteItem+"#minelock#");  
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
    		myEditText.setSelection(deleteItem.trim().length());//设置光标在末尾
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
    		builder.setView(myEditText).setNegativeButton("取消", null);
    		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

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

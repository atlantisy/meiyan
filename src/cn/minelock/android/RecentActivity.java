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
import android.os.Environment;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import cn.minelock.widget.ImageSimpleAdapter;
import cn.minelock.widget.ImageTools;
import cn.minelock.widget.dbHelper;

import cn.minelock.android.R;

public class RecentActivity extends Activity {
	
	dbHelper dbRecent;
	private Cursor recentCursor;
	private ListView recentList;
	//private SimpleCursorAdapter recentAdapter;
	private SimpleAdapter recentAdapter1;
	//private ImageButton random_btn;
	private int _id;
	private int position;
	
	private String deleteVerse;
	
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
	private String title = "锁屏列表";
	
	private ArrayList<HashMap<String,Object>> listData;
	
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
    	// 最近美言壁纸列表
    	recentList.setAdapter(recentAdapter1);
        // 获取保存的SharedPreferences
        settings = getSharedPreferences(PREFS, 0);
		editor = settings.edit();
		verseQty = settings.getInt(VERSEQTY, 0);//实际美言总数量
               
        // 添加点击
        recentList.setOnItemClickListener(new OnItemClickListener() {
			
        	@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				recentCursor.moveToPosition(arg2);
				_id = recentCursor.getInt(0);
				String verse = recentCursor.getString(2);// + recentCursor.getString(1);
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
			}
		});             
        
        // 添加长按点击
        recentList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// 列表位置
				position = arg2;
				recentCursor.moveToPosition(arg2);
				_id = recentCursor.getInt(0);
				deleteVerse = recentCursor.getString(2);//+recentCursor.getInt(1);
				// 列表处理对话框
				final AlertDialog dlg = new AlertDialog.Builder(RecentActivity.this).create();
				dlg.show();
				Window window = dlg.getWindow();						 
				window.setContentView(R.layout.recent_action_dialog);
				// 标题
				TextView label = (TextView) window.findViewById(R.id.recent_action_label);
				if(deleteVerse.trim().equals(""))
					label.setText("操作");					
				else
					label.setText(deleteVerse.trim());
				// 复制
				Button copy = (Button) window.findViewById(R.id.recent_action_copy);				
				copy.setOnClickListener(new View.OnClickListener() {					
					public void onClick(View v) {
						operation("copy");
						dlg.cancel();
				  }
				 });
				if(deleteVerse.trim().equals(""))
					copy.setVisibility(View.GONE);				
				// 分享
				Button share = (Button) window.findViewById(R.id.recent_action_share);
				share.setOnClickListener(new View.OnClickListener() {					
					public void onClick(View v) {
						operation("share");	
						dlg.cancel();
				  }
				 });
				if(recentCursor.getInt(3)==1)
					share.setVisibility(View.GONE);
				// 删除
				Button delete = (Button) window.findViewById(R.id.recent_action_delete);
				delete.setOnClickListener(new View.OnClickListener() {					
					public void onClick(View v) {
						operation("delete");
						int verseId = position;				
						// 移动到上一位置										
						if(verseQty==1){
							editor.putBoolean(BOOLIDPATH, true);// 壁纸
							editor.putInt(WALLPAPERID, R.drawable.wallpaper01);// 壁纸id
							editor.putString(WALLPAPERPATH, "1_.png");// 壁纸path
							// 将美言存入SharedPreferences				
							editor.putString(VERSE, getResources().getString(R.string.initial_verse));// 美言
							editor.putLong(VERSEID,0);// 美言id				
						}
						else{
							if(verseId==verseQty-1)
								verseId = verseId-1;
							//verseId = verseId-1;
							recentCursor.moveToPosition(verseId);	
							// 美言
							String verse = recentCursor.getString(2);
							// 壁纸
							int idPath = recentCursor.getInt(3);
							int id = recentCursor.getInt(4);
							String path = recentCursor.getString(5);
							// 将壁纸存入SharedPreferences
							boolean bIdPath = false;
							if(idPath==1)
								bIdPath = true;
							editor.putBoolean(BOOLIDPATH, bIdPath);// 壁纸
							editor.putInt(WALLPAPERID, id);// 壁纸id
							editor.putString(WALLPAPERPATH, path);// 壁纸path
							// 将美言存入SharedPreferences				
							editor.putString(VERSE, verse);// 美言
							//editor.putLong(VERSEID,(long)verseId);// 美言id	
						}
										
						if(verseQty>0){
							verseQty = verseQty-1;
							recent_label.setText(title + "(" + String.valueOf(verseQty) + ")");
						}				
						else{
							verseQty = 0;
							recent_label.setText(title);
						}				
						editor.putInt(VERSEQTY, verseQty);// 美言数量
						editor.commit();
									
						Toast.makeText(getApplicationContext(), "已删除", Toast.LENGTH_SHORT).show();
						
						dlg.cancel();
				  }
				 });
				
				return true;
			}
		});
        
        // 添加长按菜单响应
/*        recentList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {			
			
        	@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				//menu.setHeaderTitle(deleteItem.trim());   
				menu.add(0, 0, 0, "复制文字");				
				menu.add(0, 1, 0, "分享壁纸");
				menu.add(0, 2, 0, "删除");
			}
		}); */
        
        // 获取增删后的美言数量
        String Qty = title;
        if(verseQty>0)
        	Qty = title + "(" + String.valueOf(verseQty) + ")";
        recent_label = (TextView) findViewById(R.id.recent_label);
        recent_label.setText(Qty);   
        
		// 增加
		ImageButton add_btn = (ImageButton) findViewById(R.id.recent_add);
		add_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(RecentActivity.this, EditVerseActivity.class));
				//overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
				//finish();
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
	}
	
    private ArrayList<HashMap<String,Object>> getData() {  
    	int columnsSize = recentCursor.getColumnCount();  
    	listData = new ArrayList<HashMap<String, Object>>();  
        // 获取表的内容  
        while (recentCursor.moveToNext()) {  
        	HashMap<String, Object> map = new HashMap<String, Object>();  
            for (int i = 0; i < columnsSize; i++) {
            	int bool=recentCursor.getInt(3);
            	String path=recentCursor.getString(5);
            	String _path=path.substring(0, path.length()-4)+"_.png";
            	// 赋值
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
/*	//长按菜单响应函数
	@Override
	public boolean onContextItemSelected(MenuItem item) {		
		super.onOptionsItemSelected(item);
		
		switch (item.getItemId()) {
		case 0:
			operation("copy");
			break;
		case 1:
			operation("share");			
			break;			
		case 2:			
			operation("delete");
			int verseId = position;				
			// 移动到上一位置										
			if(verseQty==1){
				editor.putBoolean(BOOLIDPATH, true);// 壁纸
				editor.putInt(WALLPAPERID, R.drawable.wallpaper01);// 壁纸id
				editor.putString(WALLPAPERPATH, "1_.png");// 壁纸path
				// 将美言存入SharedPreferences				
				editor.putString(VERSE, getResources().getString(R.string.initial_verse));// 美言
				editor.putLong(VERSEID,0);// 美言id				
			}
			else{
				if(verseId==verseQty-1)
					verseId = verseId-1;
				//verseId = verseId-1;
				recentCursor.moveToPosition(verseId);	
				// 美言
				String verse = recentCursor.getString(2);
				// 壁纸
				int idPath = recentCursor.getInt(3);
				int id = recentCursor.getInt(4);
				String path = recentCursor.getString(5);
				// 将壁纸存入SharedPreferences
				boolean bIdPath = false;
				if(idPath==1)
					bIdPath = true;
				editor.putBoolean(BOOLIDPATH, bIdPath);// 壁纸
				editor.putInt(WALLPAPERID, id);// 壁纸id
				editor.putString(WALLPAPERPATH, path);// 壁纸path
				// 将美言存入SharedPreferences				
				editor.putString(VERSE, verse);// 美言
				//editor.putLong(VERSEID,(long)verseId);// 美言id	
			}
							
			if(verseQty>0){
				verseQty = verseQty-1;
				recent_label.setText(title + "(" + String.valueOf(verseQty) + ")");
			}				
			else{
				verseQty = 0;
				recent_label.setText(title);
			}				
			editor.putInt(VERSEQTY, verseQty);// 美言数量
			editor.commit();
						
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
	}*/
	
    private void operation(String cmd)
    {
    	setTitle("");
    	if(cmd=="share"){
/*			Intent intent=new Intent(Intent.ACTION_SEND);  
			intent.setType("text/plain");  
			intent.putExtra(Intent.EXTRA_SUBJECT, "分享");  
			intent.putExtra(Intent.EXTRA_TEXT, deleteVerse.trim()+sharePath);  
			startActivity(Intent.createChooser(intent, getTitle()));*/
    		    		
    		String imgPath="";
    		if(recentCursor.getInt(3)==0){
        		imgPath=recentCursor.getString(5); 
        		shareMsg(deleteVerse.trim(),"#美言壁纸#"+deleteVerse.trim()+getResources().getString(R.string.share_word),imgPath);
    		}
    		else{
    			Toast.makeText(getApplicationContext(), "无图无真相", Toast.LENGTH_SHORT).show();
    			//String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Minelock";
    		}
    			
    	}
    	if(cmd=="copy"){
    		//dbRecent.insert( myEditText.getText().toString());    		
    		ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    		cbm.setText(deleteVerse.trim());
    		if(deleteVerse.trim().equals(""))
    			Toast.makeText(getApplicationContext(), "无言以对", Toast.LENGTH_SHORT).show();
    		else
    			Toast.makeText(getApplicationContext(), "已复制", Toast.LENGTH_SHORT).show();
    	}
    	if(cmd=="edit"){
    		final EditText myEditText = new EditText(this);
    		//myEditText.setText(deleteItem.trim().toCharArray(), 0, deleteItem.trim().length());
    		myEditText.setText(deleteVerse);
    		myEditText.setSelection(deleteVerse.trim().length());//设置光标在末尾
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
    	if(cmd=="delete"){
    		dbRecent.delete(_id);
    		listData.remove(position);
			// 删除SD卡上图片
    		//recentCursor.moveToPosition(position);
    		if(recentCursor.getInt(3)==0){
				String deletePath=recentCursor.getString(5);
				String _deletePath=deletePath.substring(0, deletePath.length()-4)+"_.png";
				ImageTools.deletePhoto(deletePath);
				ImageTools.deletePhoto(_deletePath);
			}
    	}   		    		
    	recentCursor.requery();
    	//recentList.invalidateViews();    	
    	recentAdapter1.notifyDataSetChanged();
    	recentList.invalidate();
    	_id=0;    	
    }
    
    public void shareMsg(String msgTitle, String msgText,String imgPath) {  
        Intent intent = new Intent(Intent.ACTION_SEND);  
        if (imgPath == null || imgPath.equals("")) {  
            intent.setType("text/plain"); // 纯文本   
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
        startActivity(Intent.createChooser(intent, getTitle()));
    }
}

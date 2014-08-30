package com.meiyanlock.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.meiyanlock.util.FileUtils;
import com.meiyanlock.widget.AbstractSpinerAdapter;
import com.meiyanlock.widget.CustemObject;
import com.meiyanlock.widget.CustemSpinerAdapter;
import com.meiyanlock.widget.SpinerPopWindow;
import com.meiyanlock.widget.WallpaperAdapter;
import com.meiyanlock.widget.ImageTools;
import com.meiyanlock.widget.dbHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

public class EditVerseActivity extends Activity implements OnClickListener,
		AbstractSpinerAdapter.IOnItemSelectListener {

	private Button mBtnDropDown;// 下拉按钮
	private List<CustemObject> nameList = new ArrayList<CustemObject>();
	private AbstractSpinerAdapter mAdapter;
	private EditText verse_edit = null;

	private SharedPreferences settings = null;
	private SharedPreferences.Editor editor = null;
	public static final String PREFS = "lock_pref";// pref文件名
	public static final String VERSE = "verse";// 美言pref值名称
	public static final String VERSEQTY = "verse_quantity";// 美言数量pref值名称	
	public static final String BOOLIDPATH = "wallpaper_idorpath";// 应用内or外壁纸bool的pref值名称,true为ID，false为path
	public static final String WALLPAPERID = "wallpaper_id";// 应用内壁纸资源ID的pref值名称
	public static final String WALLPAPERPATH = "wallpaper_path";// 应用外壁纸Path的pref值名称
	
	private int wallpaperId;
	private String wallpaperPath;
	private boolean bIdOrPath;//true为Id，false为Path
	private int verseQty;
	
	private boolean bControlEditColor = true;
	private LinearLayout mEditVerseLayout;
	private WallpaperAdapter wpAdapter;
	private GridView wpGridview;
	
	dbHelper dbRecent;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editverse);
		mEditVerseLayout = (LinearLayout) findViewById(R.id.editverse_layout);
		// 获取存储的pref数据
		settings = getSharedPreferences(PREFS, 0);
		editor = settings.edit();
		// 获取保存的壁纸
		bIdOrPath = settings.getBoolean(BOOLIDPATH, true);
		wallpaperId = settings.getInt(WALLPAPERID, R.drawable.wallpaper00);
		wallpaperPath = settings.getString(WALLPAPERPATH, "");	
		if(bIdOrPath==true)//设置壁纸			
			mEditVerseLayout.setBackgroundResource(wallpaperId);
		else{
			Bitmap bitmap = BitmapFactory.decodeFile(wallpaperPath);
			mEditVerseLayout.setBackgroundDrawable(new BitmapDrawable(bitmap));
		}
		// 获取保存的美言
		verseQty = settings.getInt(VERSEQTY, 0);
		String verse = settings.getString(VERSE, "");		
		verse_edit = (EditText) findViewById(R.id.edit_verse);
		//verse_edit.setText(verse.trim().toCharArray(), 0, verse.trim().length());//设置默认美言
		//verse_edit.setSelection(verse.trim().length());//设置光标在末尾
		// 选择应用自带颜色初始化
		wpAdapter = new WallpaperAdapter(this);
		wpGridview = (GridView) findViewById(R.id.wallpaper_grid);
		wpGridview.setAdapter(wpAdapter);
		wpGridview.setOnItemClickListener(wallpaperListener);
		// 选择应用内颜色作为壁纸点击事件
		ImageButton editcolor_btn = (ImageButton) findViewById(R.id.edit_color);
		editcolor_btn.setOnClickListener(editColorOnClickListener);
		// 拍照或选取相册图片为背景
		ImageButton editwallpaper_btn = (ImageButton) findViewById(R.id.edit_camera);
		editwallpaper_btn.setOnClickListener(editWallpaperOnClickListener);
		// 发布锁屏美言及壁纸
		Button editok_btn = (Button) findViewById(R.id.edit_ok);
		editok_btn.setOnClickListener(editOkOnClickListener);

        //存入SQL数据库
		dbRecent = new dbHelper(this);
		
		// 自定义种类选择
		customOptionSetup();
	}
	
	// 发布美言及壁纸
	private OnClickListener editOkOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			// 获取自定义美言
			String verse = verse_edit.getText().toString();
			// 判断是否需要扩展美言
			int len = verse.length();
			if (len < 9)
				for (int i = 0; i <= 9 - len; i++)
					verse += " ";			
			// 将新增美言存入SQL数据库
			dbRecent.insert(verse.substring(0, 1),verse.substring(1));
			// 将美言总数存入SharedPreferences
			verseQty = verseQty+1;
			editor.putInt(VERSEQTY, verseQty);
			// 将美言存入SharedPreferences
			editor.putString(VERSE, verse);// 美言
			// 将壁纸结果存入SharedPreferences
			editor.putBoolean(BOOLIDPATH, bIdOrPath);
			editor.putInt(WALLPAPERID, wallpaperId);// 壁纸ID
			editor.putString(WALLPAPERPATH, wallpaperPath);// 壁纸路径
			editor.commit();

			startActivity(new Intent(EditVerseActivity.this, HomeActivity.class));
			//finish();
		}
	};
	
	/** 选择应用内自带颜色或壁纸 **/
	OnItemClickListener wallpaperListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			wallpaperId = WallpaperAdapter.wallpaper[arg2];
			mEditVerseLayout.setBackgroundResource(wallpaperId);
			bIdOrPath = true;//壁纸来源为应用内ID
		}
	};	
	// 选择颜色作为背景
	private OnClickListener editColorOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (bControlEditColor == true) {
				bControlEditColor = false;
				wpGridview.setVisibility(View.VISIBLE);
			} else {
				bControlEditColor = true;
				wpGridview.setVisibility(View.GONE);
			}
		}
	};

	/** 选择应用外壁纸 **/	
	private OnClickListener editWallpaperOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			showPicturePicker(EditVerseActivity.this);
		}
	};
	// 拍照或选取相册图片为锁屏壁纸
	public void showPicturePicker(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("选壁纸");
		builder.setItems(new String[] { "拍照", "从相册选取" },
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case TAKE_PHOTO:							
							Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							startActivityForResult(openCameraIntent,TAKE_PHOTO);							
							bIdOrPath = false;//壁纸来源为应用外路径
							break;

						case CHOOSE_PICTURE:
							Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
							openAlbumIntent.setType("image/*");
							startActivityForResult(openAlbumIntent,CHOOSE_PICTURE);							
							bIdOrPath = false;//壁纸来源为应用外路径
							break;

						default:
							break;
						}
						
					}
				});
		builder.create().show();
	}

	private static final int TAKE_PHOTO = 0;// 拍照
	private static final int CHOOSE_PICTURE = 1;// 从相册选取

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case TAKE_PHOTO:
				// 获取图片数据
				Bundle bundle = data.getExtras(); 
				Bitmap photo = (Bitmap)bundle.get("data");
				// 设置锁屏壁纸
				mEditVerseLayout.setBackgroundDrawable(new BitmapDrawable(photo));
				// 保存到SD卡
				String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Minelock";
				String photoName = "wallpaper";
				ImageTools.savePhotoToSDCard(photo, dir, photoName);
				wallpaperPath = dir + "/" + photoName + ".png";
				break;
			case CHOOSE_PICTURE:
				ContentResolver resolver = getContentResolver();
				Uri originalUri = data.getData();
				try {
					Bitmap picture = MediaStore.Images.Media.getBitmap(resolver,originalUri);
					if (picture != null) {	
						// 设置锁屏壁纸
						mEditVerseLayout.setBackgroundDrawable(new BitmapDrawable(picture));
						// 获取选择图片的路径
						String[] proj = { MediaStore.Images.Media.DATA };
						Cursor cursor = managedQuery(originalUri, proj, null,null,null);
						int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						wallpaperPath = cursor.getString(column_index);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}			
			
		}
	}

	private void customOptionSetup() {
		mBtnDropDown = (Button) findViewById(R.id.edit_label);
		mBtnDropDown.setOnClickListener(this);

		String[] names = getResources().getStringArray(R.array.customize);
		for (int i = 0; i < names.length; i++) {
			CustemObject object = new CustemObject();
			object.data = names[i];
			nameList.add(object);
		}

		mAdapter = new CustemSpinerAdapter(this);
		mAdapter.refreshData(nameList, 0);

		mSpinerPopWindow = new SpinerPopWindow(this);
		mSpinerPopWindow.setAdatper(mAdapter);
		mSpinerPopWindow.setItemListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.edit_label:
			showSpinWindow();
			break;
		}
	}

	private SpinerPopWindow mSpinerPopWindow;
	private void showSpinWindow() {
		Log.e("", "showSpinWindow");
		mSpinerPopWindow.setWidth(mBtnDropDown.getWidth());
		mSpinerPopWindow.showAsDropDown(mBtnDropDown);
	}

	@Override
	public void onItemClick(int pos) {
		setCustom(pos);
	}

	private void setCustom(int pos) {
		if (pos >= 0 && pos <= nameList.size()) {
			CustemObject value = nameList.get(pos);

			mBtnDropDown.setText(value.toString());
		}
	}

}

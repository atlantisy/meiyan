package cn.minelock.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.minelock.util.FileUtils;
import cn.minelock.widget.AbstractSpinerAdapter;
import cn.minelock.widget.CustemObject;
import cn.minelock.widget.CustemSpinerAdapter;
import cn.minelock.widget.ImageTools;
import cn.minelock.widget.SpinerPopWindow;
import cn.minelock.widget.WallpaperAdapter;
import cn.minelock.widget.dbHelper;

import cn.minelock.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
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
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.inputmethod.InputMethodManager;
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
	public static final String VERSEID = "verse_id";// 美言id pref值名称
	public static final String VERSEQTY = "verse_quantity";// 美言数量pref值名称	
	public static final String SHOWVERSEFLAG = "showVerseFlag";//美言显示方式pref值名称
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
	private WallpaperManager wallpaperManager;
	private GridView wpGridview;
	private ImageButton clear_btn;
	
	dbHelper dbRecent;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editverse);
		mEditVerseLayout = (LinearLayout) findViewById(R.id.editverse_layout);
		wpGridview = (GridView) findViewById(R.id.wallpaper_grid);
		// 获取存储的pref数据
		settings = getSharedPreferences(PREFS, 0);
		editor = settings.edit();
		// 获取系统主屏幕壁纸
		wallpaperManager = WallpaperManager.getInstance(this);
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
		verse_edit = (EditText) findViewById(R.id.edit_verse);
		//String verse = settings.getString(VERSE, "感觉自己萌萌哒");	
		//verse_edit.setText(verse);//设置默认美言
		//verse_edit.addTextChangedListener(textChangedWatcher);//有字时显示清空按钮
		verse_edit.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
			    // 如果输入法在窗口上已经显示，则隐藏，反之则显示
			    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
			    //imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			    boolean bool = imm.isActive();
			    if(bool){
			    	wpGridview.setVisibility(View.GONE);
			    }
			    return false;
			}
		});
		// 选择应用自带颜色初始化
		wpAdapter = new WallpaperAdapter(this);
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
		
        // 存入SQL数据库
		dbRecent = new dbHelper(this);
		
		// 清空当前美言
		clear_btn = (ImageButton) findViewById(R.id.edit_clear);
		clear_btn.setOnClickListener(clearOnClickListener);
		// 监控写美言是否获得焦点
		verse_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {  
		    @Override  
		    public void onFocusChange(View v, boolean hasFocus) {  
		        if(hasFocus & TextUtils.isEmpty(verse_edit.getText().toString())==false) {
		        	// 此处为得到焦点时的处理内容
		        	//clear_btn.setVisibility(View.VISIBLE);
		        } else {
		        	// 此处为失去焦点时的处理内容
		        	//clear_btn.setVisibility(View.GONE);
		        }
		    }
		});
		// 返回
		ImageButton editreturn_btn = (ImageButton) findViewById(R.id.editverse_return);
		editreturn_btn.setOnClickListener(editReturnOnClickListener);
		
		// 自定义种类选择
		customOptionSetup();
	}
	
	// 文本改变监控
	private TextWatcher textChangedWatcher = new TextWatcher() {

        //缓存上一次文本框内是否为空
        private boolean isnull = true;

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(s)) {
                if (!isnull) {
                	clear_btn.setVisibility(View.GONE);
                    isnull = true;
                }
            } else {
                if (isnull) {
                	clear_btn.setVisibility(View.VISIBLE);
                    isnull = false;
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
            
        }
    };
	// 清空当前美言
	private OnClickListener clearOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			verse_edit.setText("");//
			// 获取编辑框焦点
			//verse_edit.requestFocus();
			//打开软键盘
			//InputMethodManager imm = (InputMethodManager) verse_edit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  			
			//imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	};	
	// 返回
	private OnClickListener editReturnOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(EditVerseActivity.this, HomeActivity.class));
			finish();
			overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
		}
	};	
	/** 发布美言及壁纸**/
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
			if (len>0){
				long verseId = dbRecent.insert(verse.substring(0, 1),verse.substring(1));
				// 将美言总数存入SharedPreferences
				verseQty = verseQty+1;
				editor.putInt(VERSEQTY, verseQty);
				//editor.putLong(VERSEID, verseId);
			}
			else{
				int showVerseFlag = 1;
				editor.putInt(SHOWVERSEFLAG, showVerseFlag);
			}
				
			// 将美言存入SharedPreferences
			editor.putString(VERSE, verse);// 美言
			// 将壁纸结果存入SharedPreferences
			editor.putBoolean(BOOLIDPATH, bIdOrPath);
			editor.putInt(WALLPAPERID, wallpaperId);// 壁纸ID
			editor.putString(WALLPAPERPATH, wallpaperPath);// 壁纸路径
			editor.commit();

			startActivity(new Intent(EditVerseActivity.this, HomeActivity.class));			
			//finish();
			//overridePendingTransition(R.anim.push_down_in,R.anim.alpha_action_out);
			overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
		}
	};
	
	// 选择应用内自带颜色或壁纸 
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
	// 点击实现颜色、壁纸选择项的显示/隐藏
	private OnClickListener editColorOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (bControlEditColor == true) {
				bControlEditColor = false;
				wpGridview.setVisibility(View.VISIBLE);
				hideSoftKeyboard();
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
			hideSoftKeyboard();
			wpGridview.setVisibility(View.GONE);
			showPicturePicker(EditVerseActivity.this);
		}
	};
	// 拍照或选取相册图片为锁屏壁纸
	public void showPicturePicker(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("锁屏壁纸");
		builder.setItems(new String[] { "拍照", "从相册选取", "与桌面壁纸同步"},
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
							
						case RANDOM_PAPER:							
							int[] wallpaper = {
									R.drawable.wallpaper00,R.drawable.wallpaper01,R.drawable.wallpaper02,
									R.drawable.wallpaper03,R.drawable.wallpaper04,R.drawable.wallpaper05,									
								};
							int random = (int)(Math.random()*wallpaper.length);
							wallpaperId = wallpaper[random];							
							mEditVerseLayout.setBackgroundResource(wallpaperId);
							bIdOrPath = true;//壁纸来源为应用内ID		
							break;
							
						case DEFAULT_PAPER:						
							// 获取壁纸管理器  
				            //WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);  
				            // 获取当前壁纸  
				            Drawable wallpaperDrawable = wallpaperManager.getDrawable();  
				            // 将Drawable,转成Bitmap  
				            Bitmap bm = ((BitmapDrawable) wallpaperDrawable).getBitmap();  				  
				            // 设置 背景  
				            mEditVerseLayout.setBackgroundDrawable(new BitmapDrawable(bm));
							// 保存到SD卡
							String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Minelock";
							String photoName = "wallpaper";
							ImageTools.savePhotoToSDCard(bm, dir, photoName);
							wallpaperPath = dir + "/" + photoName + ".png";
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
	private static final int RANDOM_PAPER = 3;// 随机壁纸
	private static final int DEFAULT_PAPER = 2;// 默认壁纸

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
	
	// 隐藏软键盘
	private void hideSoftKeyboard(){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
		if(isOpen)
			imm.hideSoftInputFromWindow(verse_edit.getWindowToken(), 0); //强制隐藏键盘		
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

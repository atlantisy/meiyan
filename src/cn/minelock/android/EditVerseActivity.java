package cn.minelock.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import cn.minelock.util.StringUtil;
import cn.minelock.widget.AbstractSpinerAdapter;
import cn.minelock.widget.CustemObject;
import cn.minelock.widget.CustemSpinerAdapter;
import cn.minelock.widget.EmojiAdapter;
import cn.minelock.widget.ImageTools;
import cn.minelock.widget.SpinerPopWindow;
import cn.minelock.widget.WallpaperAdapter;
import cn.minelock.widget.dbHelper;

import cn.minelock.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
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
	//public static final String VERSEID = "verse_id";// 美言id pref值名称
	public static final String VERSEQTY = "verse_quantity";// 美言数量pref值名称	
	public static final String SHOWVERSEFLAG = "showVerseFlag";//美言显示方式pref值名称
	public static final String BOOLIDPATH = "wallpaper_idorpath";// 应用内or外壁纸bool的pref值名称,true为ID，false为path
	public static final String WALLPAPERID = "wallpaper_id";// 应用内壁纸资源ID的pref值名称
	public static final String WALLPAPERPATH = "wallpaper_path";// 应用外壁纸Path的pref值名称
	
	private int wallpaperId;
	private int _wallpaperId;
	private String wallpaperPath;
	//private String _wallpaperPath;
	private boolean bIdOrPath;//true为Id，false为Path
	private int verseQty;
	
	private boolean bControlEditColor = true;
	private boolean bControlEditEmoji = true;
	private LinearLayout mEditVerseLayout;
	private WallpaperAdapter colorAdapter;
	private EmojiAdapter emojiAdapter;
	private WallpaperManager wallpaperManager;
	private GridView colorGridview;
	private GridView emojiGridview;
	private ImageButton clear_btn;
	private ImageButton editemoji_btn;
	private ImageButton recommend_btn;
	
	dbHelper dbRecent;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editverse);
		mEditVerseLayout = (LinearLayout) findViewById(R.id.editverse_layout);		
		// 获取存储的pref数据
		settings = getSharedPreferences(PREFS, 0);
		editor = settings.edit();
		// 获取系统主屏幕壁纸
		wallpaperManager = WallpaperManager.getInstance(this);
		// 获取保存的壁纸
		wallpaperPath = settings.getString(WALLPAPERPATH, "1_.png");
		// 随机壁纸
		bIdOrPath = true;
		wallpaperId = WallpaperAdapter.wallpaper[(int)(Math.random()*WallpaperAdapter.wallpaper.length)];		
		mEditVerseLayout.setBackgroundResource(wallpaperId);
		// 获取保存的美言
		verseQty = settings.getInt(VERSEQTY, 0);			
		verse_edit = (EditText) findViewById(R.id.edit_verse);
		//String verse = getResources().getString(R.string.initial_verse);		
		//verse_edit.setText(settings.getString(VERSE, verse).trim());//设置默认美言
		verse_hint = verse_edit.getText().toString();
		verse_edit.setHighlightColor(getResources().getColor(R.color.alpha_black1));
		verse_edit.selectAll();
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
			    	colorGridview.setVisibility(View.GONE);
			    	bControlEditColor = true;
			    	
			    	emojiGridview.setVisibility(View.GONE);
			    	editemoji_btn.setImageResource(R.drawable.ic_emoji);
			    	bControlEditEmoji = true;
			    }
			    return false;
			}
		});
		// 美日一荐
		recommend_btn = (ImageButton) findViewById(R.id.recommend_photo);
		recommend_btn.setOnClickListener(recommendOnClickListener);
		// 应用自带表情
		emojiAdapter = new EmojiAdapter(this);
		emojiGridview = (GridView) findViewById(R.id.emoji_grid);
		emojiGridview.setAdapter(emojiAdapter);
		emojiGridview.setOnItemClickListener(emojiListener);
		// 选择表情作为美言
		editemoji_btn = (ImageButton) findViewById(R.id.edit_emoji);
		editemoji_btn.setOnClickListener(editEmojiOnClickListener);
		// 应用自带颜色		
		colorAdapter = new WallpaperAdapter(this);
		colorGridview = (GridView) findViewById(R.id.color_grid);
		colorGridview.setAdapter(colorAdapter);
		colorGridview.setOnItemClickListener(colorListener);
		// 选择应用内颜色为背景
		ImageButton editcolor_btn = (ImageButton) findViewById(R.id.edit_color);
		editcolor_btn.setOnClickListener(editColorOnClickListener);
		// 拍照或选取相册图片为背景
		ImageButton editwallpaper_btn = (ImageButton) findViewById(R.id.edit_photo);
		editwallpaper_btn.setOnClickListener(editWallpaperOnClickListener);
        // SQLite数据库
		dbRecent = new dbHelper(this);	
		// 发布锁屏美言及壁纸
		Button editok_btn = (Button) findViewById(R.id.edit_ok);
		editok_btn.setOnClickListener(editOkOnClickListener);			
		// 清空当前美言
		clear_btn = (ImageButton) findViewById(R.id.edit_clear);
		clear_btn.setOnClickListener(clearOnClickListener);
		// 监控写美言是否获得焦点
		verse_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {  
		    @Override  
		    public void onFocusChange(View v, boolean hasFocus) {  
		        if(hasFocus && TextUtils.isEmpty(verse_edit.getText().toString())==false) {
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
			//startActivity(new Intent(EditVerseActivity.this, HomeActivity.class));
			//overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
			finish();			
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
			int idPath = 0;
			if(bIdOrPath){
				idPath = 1;
				int index=0;
				for(int j=0; j<WallpaperAdapter.wallpaper.length; j++)
					if(wallpaperId==WallpaperAdapter.wallpaper[j])
						index=j;		
				_wallpaperId = WallpaperAdapter._wallpaper[index];				
			}
			else
				_wallpaperId = R.drawable.app_icon_grey;										
			dbRecent.insert(_wallpaperId,verse.substring(0),idPath,wallpaperId,wallpaperPath);
			
/*			if (len>=0){
				if(verse_hint.trim().equals(verse.trim())==false){//编辑前后美言不同
					long verseId = dbRecent.insert(_wallpaperId,verse.substring(0),idPath,wallpaperId,wallpaperPath);
					// 将美言总数存入SharedPreferences
					verseQty = verseQty+1;
					editor.putInt(VERSEQTY, verseQty);
					//editor.putLong(VERSEID, verseId);
				}
			}
			else{
				int showVerseFlag = 1;
				editor.putInt(SHOWVERSEFLAG, showVerseFlag);
			}*/
				
			// 将美言存入SharedPreferences
			verseQty = verseQty+1;//美言总数
			editor.putInt(VERSEQTY, verseQty);
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
	// 选择应用内自带表情
	OnItemClickListener emojiListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
				
		}
	};	
	// 选择应用内自带颜色或壁纸 
	OnItemClickListener colorListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			wallpaperId = WallpaperAdapter.wallpaper[arg2];
			_wallpaperId = WallpaperAdapter._wallpaper[arg2];
			mEditVerseLayout.setBackgroundResource(wallpaperId);
			bIdOrPath = true;//壁纸来源为应用内ID					
		}
	};
	// 控制表情列表的显示/隐藏
	private OnClickListener editEmojiOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (bControlEditEmoji == true) {
				bControlEditEmoji = false;
				emojiGridview.setVisibility(View.VISIBLE);
				editemoji_btn.setImageResource(R.drawable.ic_emoji_grey);
				hideSoftKeyboard();
			} else {
				bControlEditEmoji = true;
				emojiGridview.setVisibility(View.GONE);
				editemoji_btn.setImageResource(R.drawable.ic_emoji);
			}
		}
	};
	// 控制颜色、壁纸列表的显示/隐藏
	private OnClickListener editColorOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (bControlEditColor == true) {
				bControlEditColor = false;
				colorGridview.setVisibility(View.VISIBLE);
				hideSoftKeyboard();
			} else {
				bControlEditColor = true;
				colorGridview.setVisibility(View.GONE);
			}
		}
	};
	
	final Handler handlerRecommend = new Handler();
	// 美日一荐
	private OnClickListener recommendOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			//hideSoftKeyboard();
	    	verse_hint = verse_edit.getText().toString();
	    	verse_edit.setCursorVisible(false);
	    	verse_edit.setText("推荐每日壁纸中...");
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					handlerRecommend.post(runnableRecommend);
				}
			}).start();
		}
	};
	// 构建Runnable对象，在runnable中更新界面  
    Runnable runnableRecommend = new  Runnable(){  
        @Override  
        public void run() {
        	String url = "http://dn-mylock.qbox.me/"+StringUtil.makeDayName()+".jpg";
        	Bitmap bm = getUrlBitmap(url);
    		//Bitmap bm = ImageTools.zoomBitmap(bitmap, bitmap.getWidth()-2, bitmap.getHeight()-2);	
    		Bitmap _bm = ImageTools.zoomBitmap(bm, 72, 72);// 压缩
    		mEditVerseLayout.setBackgroundDrawable(new BitmapDrawable(bm));
    		// 保存到SD卡
    		String photoName = "star" + StringUtil.makeDayName();
    		ImageTools.savePhotoToSDCard(bm, dir, photoName);
    		ImageTools.savePhotoToSDCard(_bm, dir, photoName+"_");
    		wallpaperPath = dir + "/" + photoName + ".png";
    		//_wallpaperPath = dir + "/" + photoName +"_"+ ".png";
    		bIdOrPath = false;//壁纸来源为应用外路径
    		
            StringUtil.showToast(getApplication(), "推荐成功", Toast.LENGTH_SHORT);
            verse_edit.setText(verse_hint);
            verse_edit.setSelection(verse_hint.length());
            verse_edit.setCursorVisible(true);
        }           
    };
    // 从网络获取图片
    private Bitmap getUrlBitmap(String url) 
    {         
        try { 
            URL imageUrl = new URL(url); 
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection(); 
            conn.setDoInput(true);
            conn.connect(); 
            InputStream inputStream=conn.getInputStream();            
            return BitmapFactory.decodeStream(inputStream);
            
        } catch (Exception ex){ 
           ex.printStackTrace(); 
           return null; 
        } 
    } 
    
	/** 选择应用外壁纸 **/	
	private OnClickListener editWallpaperOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			hideSoftKeyboard();
			colorGridview.setVisibility(View.GONE);
			emojiGridview.setVisibility(View.GONE);
			showPicturePicker(EditVerseActivity.this);
		}
	};
	
	final Handler handlerDefaultPaper = new Handler();
	private String verse_hint;
	private String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Minelock";
	private String photoName = "photo" + StringUtil.makeFileName();	//
	// 拍照或选取相册图片为锁屏壁纸
	public void showPicturePicker(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		//builder.setTitle("更换锁屏壁纸");
		builder.setItems(new String[] { "拍照", "从相册选取", "使用桌面壁纸"},
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case TAKE_PHOTO:							
							Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							
							//清晰图片
							photoName = "photo" + StringUtil.makeFileName();
							wallpaperPath = dir + "/" + photoName + ".png";
							//_wallpaperPath = dir + "/" + photoName + "_" + ".png";
							openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(wallpaperPath)));							
							
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
							int random = (int)(Math.random()*WallpaperAdapter.wallpaper.length);
							wallpaperId = WallpaperAdapter.wallpaper[random];							
							mEditVerseLayout.setBackgroundResource(wallpaperId);
							bIdOrPath = true;//壁纸来源为应用内ID		
							break;
							
						case DEFAULT_PAPER:
					    	verse_hint = verse_edit.getText().toString();
					    	verse_edit.setCursorVisible(false);
					    	verse_edit.setText("正在获取...");
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									handlerDefaultPaper.post(runnableDefaultPaper);
								}
							}).start();
							break;
							
						default:
							break;
						}
						
					}
				});
		builder.create().show();
	}
	
	// 构建Runnable对象，在runnable中更新界面  
    Runnable runnableDefaultPaper = new  Runnable(){  
        @Override  
        public void run() { 
        	getDefaultWallpaper();
        }           
    };
    
    public void getDefaultWallpaper(){
		// 获取当前壁纸 ,转成Bitmap，并设置 背景 
		Drawable wallpaperDrawable = wallpaperManager.getDrawable();  
		//Bitmap bm = ((BitmapDrawable) wallpaperDrawable).getBitmap();
		Bitmap bm = ImageTools.zoomBitmap(((BitmapDrawable) wallpaperDrawable).getBitmap(), 720, 1280);		
		Bitmap _bm = ImageTools.zoomBitmap(bm, 72, 72);//压缩
		mEditVerseLayout.setBackgroundDrawable(new BitmapDrawable(bm));
		// 保存到SD卡
		String photoName = "desk" + StringUtil.makeFileName();
		ImageTools.savePhotoToSDCard(bm, dir, photoName);
		ImageTools.savePhotoToSDCard(_bm, dir, photoName+"_");
		wallpaperPath = dir + "/" + photoName + ".png";
		//_wallpaperPath = dir + "/" + photoName +"_"+ ".png";
		bIdOrPath = false;//壁纸来源为应用外路径
		
        StringUtil.showToast(getApplication(), "获取成功", Toast.LENGTH_SHORT);
        verse_edit.setText(verse_hint);
        verse_edit.setSelection(verse_hint.length());
        verse_edit.setCursorVisible(true);
    }
    
    private Dialog mDialog;
    public void showRoundProcessDialog(int layout)//(Context mContext, int layout)
    {
        OnKeyListener keyListener = new OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_SEARCH)
                {
                    return true;
                }
                return false;
            }
        };
        
        //mDialog = new AlertDialog.Builder(mContext).create();
        mDialog = new AlertDialog.Builder(this).create();
        //mDialog.setOnKeyListener(keyListener);
        mDialog.show();
        // 注意此处要放在show之后 否则会报异常
        mDialog.setContentView(layout);
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
				// 模糊图片
				//Bitmap photo = (Bitmap)data.getExtras().get("data");
				
				// 清晰图片
				Bitmap photo = null;
				Bitmap smallPhoto = null;
				Bitmap _smallPhoto = null;
				try {
					FileInputStream fis = new FileInputStream(wallpaperPath);
					photo = BitmapFactory.decodeStream(fis);
					//smallPhoto = ImageTools.zoomBitmap(photo, photo.getWidth() / 2, photo.getHeight() / 2);//压缩
					smallPhoto = ImageTools.zoomBitmap(photo, 720, 1280);//压缩
					_smallPhoto = ImageTools.zoomBitmap(smallPhoto, 72, 72);//压缩
					photo.recycle();
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
				// 设置锁屏壁纸
				mEditVerseLayout.setBackgroundDrawable(new BitmapDrawable(smallPhoto));
				// 保存到SD卡
				ImageTools.savePhotoToSDCard(smallPhoto, dir, photoName);//清晰数据
				ImageTools.savePhotoToSDCard(_smallPhoto, dir, photoName+"_");
				
/*				String photoName = "photo" + StringUtil.makeFileName();
				ImageTools.savePhotoToSDCard(photo, dir, photoName);
				wallpaperPath = dir + "/" + photoName + ".png";*/
				break;
			
			case CHOOSE_PICTURE:
				ContentResolver resolver = getContentResolver();
				Uri originalUri = data.getData();
				Bitmap smallImage = null;
				Bitmap _smallImage = null;
				try {
					Bitmap image = MediaStore.Images.Media.getBitmap(resolver,originalUri);
					if (image != null) {	
						// 设置锁屏壁纸
						if(image.getHeight()>=1800)
							smallImage = ImageTools.zoomBitmap(image, image.getWidth() / 2, image.getHeight() / 2);
						else							
							smallImage = ImageTools.zoomBitmap(image, image.getWidth()-2, image.getHeight()-2);
						_smallImage = ImageTools.zoomBitmap(smallImage, 72, 72);
						mEditVerseLayout.setBackgroundDrawable(new BitmapDrawable(smallImage));
						image.recycle();												
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// 获取选择图片的路径
/*						String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(originalUri, proj, null,null,null);
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				wallpaperPath = cursor.getString(column_index);*/						
				// 保存到SD卡				
				if(smallImage!=null){
					String imageName = "image" + StringUtil.makeFileName();
					ImageTools.savePhotoToSDCard(smallImage, dir, imageName);
					ImageTools.savePhotoToSDCard(_smallImage, dir, imageName+"_");
					wallpaperPath = dir + "/" + imageName + ".png";
					//_wallpaperPath = dir + "/" + imageName +"_"+ ".png";
				}
				break;
				
			default:
				break;
			}			
			
		}
	}
	
	// 隐藏软键盘
	private void hideSoftKeyboard(){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		if(imm.isActive())// isOpen若返回true，则表示输入法打开
			imm.hideSoftInputFromWindow(verse_edit.getWindowToken(), 0); //强制隐藏键盘		
	}
	/**
	 * 获取季节诗词
	 * @return
	 */
	private String[] getSeasonVerse() {	
		String[] s= getResources().getStringArray(R.array.Spring);
		int month = Calendar.getInstance().get(Calendar.MONTH)+1;
		switch (month) {
		case 3:
			s= getResources().getStringArray(R.array.Spring);
			break;
		case 4:
			s= getResources().getStringArray(R.array.Spring);
			break;
		case 5:
			s= getResources().getStringArray(R.array.Spring);
			break;
		case 6:
			s= getResources().getStringArray(R.array.Summer);
			break;
		case 7:
			s= getResources().getStringArray(R.array.Summer);
			break;
		case 8:
			s= getResources().getStringArray(R.array.Summer);
			break;
		case 9:
			s= getResources().getStringArray(R.array.Autumn);
			break;
		case 10:
			s= getResources().getStringArray(R.array.Autumn);
			break;
		case 11:
			s= getResources().getStringArray(R.array.Autumn);
			break;
		case 12:
			s= getResources().getStringArray(R.array.Winter);
			break;
		case 1:
			s= getResources().getStringArray(R.array.Winter);
			break;
		case 2:
			s= getResources().getStringArray(R.array.Winter);
			break;
		default:
			break;
		}
		return s;
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

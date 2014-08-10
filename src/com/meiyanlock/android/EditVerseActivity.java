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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

public class EditVerseActivity extends Activity implements OnClickListener,
		AbstractSpinerAdapter.IOnItemSelectListener {

	private Button mBtnDropDown;// 下拉按钮
	private List<CustemObject> nameList = new ArrayList<CustemObject>();
	private AbstractSpinerAdapter mAdapter;
	private EditText verse_edit = null;

	public static final String PREFS = "lock_pref";// pref文件名
	public static final String VERSE = "verse";// 锁屏方式pref值名称
	public static final String WALLPAPER = "wallpaper";// 壁纸pref值名称

	private boolean bControlEditColor = true;
	private LinearLayout mEditVerseLayout;
	private WallpaperAdapter wpAdapter;
	private GridView wpGridview;
	private int wallpaperId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editverse);
		mEditVerseLayout = (LinearLayout) findViewById(R.id.editverse_layout);
		// 获取存储的pref数据
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
		wallpaperId = settings.getInt(WALLPAPER, R.drawable.wallpaper00);
		mEditVerseLayout.setBackgroundResource(wallpaperId);
		// 壁纸初始化
		wpAdapter = new WallpaperAdapter(this);
		wpGridview = (GridView) findViewById(R.id.wallpaper_grid);
		wpGridview.setAdapter(wpAdapter);
		wpGridview.setOnItemClickListener(wallpaperListener);
		// 获取保存的美言
		String verse = settings.getString(VERSE, "");
		// 设置默认美言
		verse_edit = (EditText) findViewById(R.id.edit_verse);
		verse_edit
				.setText(verse.trim().toCharArray(), 0, verse.trim().length());
		verse_edit.setSelection(verse.trim().length());// 设置光标在末尾
		// 发布美言
		ImageButton editok_btn = (ImageButton) findViewById(R.id.edit_ok);
		editok_btn.setOnClickListener(editOkOnClickListener);
		// 选择颜色作为背景
		ImageButton editcolor_btn = (ImageButton) findViewById(R.id.edit_color);
		editcolor_btn.setOnClickListener(editColorOnClickListener);
		// 拍照或选取相册图片为背景
		ImageButton editcamera_btn = (ImageButton) findViewById(R.id.edit_camera);
		editcamera_btn.setOnClickListener(editCameraOnClickListener);

		// 自定义种类选择
		customOptionSetup();
	}

	/** 选择壁纸 **/
	OnItemClickListener wallpaperListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			wallpaperId = WallpaperAdapter.wallpaper[arg2];
			mEditVerseLayout.setBackgroundResource(wallpaperId);
		}
	};

	// 发布美言及壁纸
	private OnClickListener editOkOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			/*
			 * verse_edit.clearFocus(); verse_edit.setCursorVisible(false);
			 */
			// 获取自定义美言
			String verse = verse_edit.getText().toString();
			// 判断是否需要扩展美言
			int len = verse.length();
			if (len < 9)
				for (int i = 0; i <= 9 - len; i++)
					verse += " ";
			// 将美言存入SharedPreferences
			SharedPreferences setting = getSharedPreferences(PREFS, 0);
			SharedPreferences.Editor editor = setting.edit();
			editor.putString(VERSE, verse);// 美言
			editor.putInt(WALLPAPER, wallpaperId);// 壁纸
			editor.commit();

			startActivity(new Intent(EditVerseActivity.this, HomeActivity.class));
			finish();
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
	
	private static final int TAKE_PICTURE = 0;
	private static final int CHOOSE_PICTURE = 1;
	
	// 拍照为背景
	private OnClickListener editCameraOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			//Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			//startActivityForResult(i, RESULT_TAKE_PHOTO);
			
			// photo();
			
			showPicturePicker(EditVerseActivity.this);
		}
	};
	// 选取相册图片为背景
	private OnClickListener editCameraOnClickListener1 = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub 
			//Intent.ACTION_GET_CONTENT    Intent.ACTION_PICK
			Intent i = new Intent(
					Intent.ACTION_GET_CONTENT,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, RESULT_LOAD_IMAGE);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case RESULT_TAKE_PHOTO:
				// 检测SD卡是否可用
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
					Log.v("MinelockFile","SD card is not avaiable/writeable right now.");
					return;
				}
				// 获取图片数据
				Bundle bundle = data.getExtras();
				Bitmap bitmap = (Bitmap) bundle.get("data");
				// 设置背景图片
				mEditVerseLayout.setBackgroundDrawable(new BitmapDrawable(bitmap));
				
				// 创建文件夹 及命名图片文件
/*				File filedirName = new File("/sdcard/minelock/");
				filedirName.mkdirs();
				String fileName = "/sdcard/minelock/wallpaper.jpg";
				// 将图片压缩并输出到SD卡上命名的文件上
				FileOutputStream output = null;								
				try {
					output = new FileOutputStream(fileName);					
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);// 把数据写入文件
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						output.flush();
						output.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}*/
				//bitmap.recycle();
				break;
			case RESULT_LOAD_IMAGE:
				Uri uri = data.getData();
				if (uri != null) {
					Bitmap photo = BitmapFactory.decodeFile(uri.getPath());
					mEditVerseLayout.setBackgroundDrawable(new BitmapDrawable(photo));

/*					savePhotoToSDCard(photo, Environment
							.getExternalStorageDirectory().getAbsolutePath(),
							String.valueOf(System.currentTimeMillis()));
					photo.recycle();*/
				}
				break;
			}
		}
	}

	public void showPicturePicker(Context context){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("锁屏壁纸");
		builder.setNegativeButton("取消", null);
		builder.setItems(new String[]{"拍照","从相册中选取"}, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case TAKE_PICTURE:
					Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(i, RESULT_TAKE_PHOTO);
					break;
					
				case CHOOSE_PICTURE:
					Intent intent = new Intent(
							Intent.ACTION_GET_CONTENT,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, RESULT_LOAD_IMAGE);
					break;
					
				default:
					break;
				}
			}
		});
		builder.create().show();
	}
	
	private static final int RESULT_TAKE_PHOTO = 10;
	private static final int RESULT_LOAD_IMAGE = 11;
	private Uri photoUri;

	public void photo() {
		try {
			Intent openCameraIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);

			String sdcardState = Environment.getExternalStorageState();
			String sdcardPathDir = android.os.Environment
					.getExternalStorageDirectory().getPath() + "/minelock/";
			File file = null;
			if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
				File fileDir = new File(sdcardPathDir);
				if (!fileDir.exists()) {
					fileDir.mkdirs();// 建立图片目录
				}
				// 按系统时间命名图片名称
				file = new File(sdcardPathDir + System.currentTimeMillis()
						+ ".JPEG");
			}
			if (file != null) {
				String path = file.getPath();
				photoUri = Uri.fromFile(file);
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

				startActivityForResult(openCameraIntent, RESULT_TAKE_PHOTO);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Save image to the SD card **/

	public void savePhotoToSDCard(Bitmap photoBitmap, String path,
			String photoName) {

		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {

			File dir = new File(path);

			if (!dir.exists()) {

				dir.mkdirs();

			}

			File photoFile = new File(path, photoName); // 在指定路径下创建文件

			FileOutputStream fileOutputStream = null;

			try {

				fileOutputStream = new FileOutputStream(photoFile);

				if (photoBitmap != null) {

					if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100,

					fileOutputStream)) {

						fileOutputStream.flush();

					}

				}

			} catch (FileNotFoundException e) {

				photoFile.delete();

				e.printStackTrace();

			} catch (IOException e) {

				photoFile.delete();

				e.printStackTrace();

			} finally {

				try {

					fileOutputStream.close();

				} catch (IOException e) {

					e.printStackTrace();

				}

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

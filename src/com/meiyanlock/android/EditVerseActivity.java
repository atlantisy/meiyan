package com.meiyanlock.android;

import java.io.File;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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

	private Button mBtnDropDown;// ������ť
	private List<CustemObject> nameList = new ArrayList<CustemObject>();
	private AbstractSpinerAdapter mAdapter;
	private EditText verse_edit = null;

	public static final String PREFS = "lock_pref";// pref�ļ���
	public static final String VERSE = "verse";// ������ʽprefֵ����
	public static final String WALLPAPER = "wallpaper";// ��ֽprefֵ����

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
		// ��ȡ�洢��pref����
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
		wallpaperId = settings.getInt(WALLPAPER, R.drawable.wallpaper00);
		mEditVerseLayout.setBackgroundResource(wallpaperId);
		// ��ֽ��ʼ��
		wpAdapter = new WallpaperAdapter(this);
		wpGridview = (GridView) findViewById(R.id.wallpaper_grid);
		wpGridview.setAdapter(wpAdapter);
		wpGridview.setOnItemClickListener(wallpaperListener);
		// ��ȡ���������
		String verse = settings.getString(VERSE, "");
		// ����Ĭ������
		verse_edit = (EditText) findViewById(R.id.edit_verse);
		verse_edit
				.setText(verse.trim().toCharArray(), 0, verse.trim().length());
		verse_edit.setSelection(verse.trim().length());// ���ù����ĩβ
		// ��������
		ImageButton editok_btn = (ImageButton) findViewById(R.id.edit_ok);
		editok_btn.setOnClickListener(editOkOnClickListener);
		// ѡ����ɫ��Ϊ����
		ImageButton editcolor_btn = (ImageButton) findViewById(R.id.edit_color);
		editcolor_btn.setOnClickListener(editColorOnClickListener);
		// ���ջ�ѡȡ���ͼƬΪ����
		ImageButton editcamera_btn = (ImageButton) findViewById(R.id.edit_camera);
		editcamera_btn.setOnClickListener(editCameraOnClickListener);

		// �Զ�������ѡ��
		customOptionSetup();
	}

	/** ѡ���ֽ **/
	OnItemClickListener wallpaperListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			wallpaperId = WallpaperAdapter.wallpaper[arg2];
			mEditVerseLayout.setBackgroundResource(wallpaperId);
		}
	};

	// �������Լ���ֽ
	private OnClickListener editOkOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			/*
			 * verse_edit.clearFocus(); verse_edit.setCursorVisible(false);
			 */
			// ��ȡ�Զ�������
			String verse = verse_edit.getText().toString();
			// �ж��Ƿ���Ҫ��չ����
			int len = verse.length();
			if (len < 9)
				for (int i = 0; i <= 9 - len; i++)
					verse += " ";
			// �����Դ���SharedPreferences
			SharedPreferences setting = getSharedPreferences(PREFS, 0);
			SharedPreferences.Editor editor = setting.edit();
			editor.putString(VERSE, verse);// ����
			editor.putInt(WALLPAPER, wallpaperId);// ��ֽ
			editor.commit();

			startActivity(new Intent(EditVerseActivity.this, HomeActivity.class));
			finish();
		}
	};

	// ѡ����ɫ��Ϊ����
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

	// ����Ϊ����
	private OnClickListener editCameraOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent i = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(i, RESULT_TAKE_PHOTO);
			//photo();
		}
	};
	// ѡȡ���ͼƬΪ����
	private OnClickListener editCameraOnClickListener1 = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent i = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, RESULT_LOAD_IMAGE);
		}
	};

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
					fileDir.mkdirs();//����ͼƬĿ¼
				}
				// ��ϵͳʱ������ͼƬ����
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case RESULT_TAKE_PHOTO:
            Bundle bundle = data.getExtras();             
            Bitmap bitmap = (Bitmap) bundle.get("data"); 
            mEditVerseLayout.setBackgroundDrawable(new BitmapDrawable(bitmap));
			break;
		case RESULT_LOAD_IMAGE:
			Uri uri = data.getData();
			if (uri != null) {

			}
			break;
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

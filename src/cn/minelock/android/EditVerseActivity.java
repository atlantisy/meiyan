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

	private Button mBtnDropDown;// ������ť
	private List<CustemObject> nameList = new ArrayList<CustemObject>();
	private AbstractSpinerAdapter mAdapter;
	private EditText verse_edit = null;

	private SharedPreferences settings = null;
	private SharedPreferences.Editor editor = null;
	public static final String PREFS = "lock_pref";// pref�ļ���
	public static final String VERSE = "verse";// ����prefֵ����
	public static final String VERSEQTY = "verse_quantity";// ��������prefֵ����	
	public static final String BOOLIDPATH = "wallpaper_idorpath";// Ӧ����or���ֽbool��prefֵ����,trueΪID��falseΪpath
	public static final String WALLPAPERID = "wallpaper_id";// Ӧ���ڱ�ֽ��ԴID��prefֵ����
	public static final String WALLPAPERPATH = "wallpaper_path";// Ӧ�����ֽPath��prefֵ����
	
	private int wallpaperId;
	private String wallpaperPath;
	private boolean bIdOrPath;//trueΪId��falseΪPath
	private int verseQty;
	
	private boolean bControlEditColor = true;
	private LinearLayout mEditVerseLayout;
	private WallpaperAdapter wpAdapter;
	private GridView wpGridview;
	private ImageButton clear_btn;
	
	dbHelper dbRecent;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editverse);
		mEditVerseLayout = (LinearLayout) findViewById(R.id.editverse_layout);
		// ��ȡ�洢��pref����
		settings = getSharedPreferences(PREFS, 0);
		editor = settings.edit();
		// ��ȡ����ı�ֽ
		bIdOrPath = settings.getBoolean(BOOLIDPATH, true);
		wallpaperId = settings.getInt(WALLPAPERID, R.drawable.wallpaper00);
		wallpaperPath = settings.getString(WALLPAPERPATH, "");	
		if(bIdOrPath==true)//���ñ�ֽ			
			mEditVerseLayout.setBackgroundResource(wallpaperId);
		else{
			Bitmap bitmap = BitmapFactory.decodeFile(wallpaperPath);
			mEditVerseLayout.setBackgroundDrawable(new BitmapDrawable(bitmap));
		}
		// ��ȡ���������
		verseQty = settings.getInt(VERSEQTY, 0);
		String verse = settings.getString(VERSE, "�о��Լ�������");		
		verse_edit = (EditText) findViewById(R.id.edit_verse);
		verse_edit.setText(verse);//����Ĭ������
		verse_edit.addTextChangedListener(textChangedWatcher);
		// ѡ��Ӧ���Դ���ɫ��ʼ��
		wpAdapter = new WallpaperAdapter(this);
		wpGridview = (GridView) findViewById(R.id.wallpaper_grid);
		wpGridview.setAdapter(wpAdapter);
		wpGridview.setOnItemClickListener(wallpaperListener);
		// ѡ��Ӧ������ɫ��Ϊ��ֽ����¼�
		ImageButton editcolor_btn = (ImageButton) findViewById(R.id.edit_color);
		editcolor_btn.setOnClickListener(editColorOnClickListener);
		// ���ջ�ѡȡ���ͼƬΪ����
		ImageButton editwallpaper_btn = (ImageButton) findViewById(R.id.edit_camera);
		editwallpaper_btn.setOnClickListener(editWallpaperOnClickListener);
		// �����������Լ���ֽ
		Button editok_btn = (Button) findViewById(R.id.edit_ok);
		editok_btn.setOnClickListener(editOkOnClickListener);
		
        // ����SQL���ݿ�
		dbRecent = new dbHelper(this);
		
		// ��յ�ǰ����
		clear_btn = (ImageButton) findViewById(R.id.edit_clear);
		clear_btn.setOnClickListener(clearOnClickListener);
		// ���д�����Ƿ��ý���
		verse_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {  
		    @Override  
		    public void onFocusChange(View v, boolean hasFocus) {  
		        if(hasFocus & TextUtils.isEmpty(verse_edit.getText().toString())==false) {
		        	// �˴�Ϊ�õ�����ʱ�Ĵ�������
		        	clear_btn.setVisibility(View.VISIBLE);
		        } else {
		        	// �˴�Ϊʧȥ����ʱ�Ĵ�������
		        	clear_btn.setVisibility(View.GONE);
		        }
		    }
		});
		// ����
		ImageButton editreturn_btn = (ImageButton) findViewById(R.id.editverse_return);
		editreturn_btn.setOnClickListener(editReturnOnClickListener);
		
		// �Զ�������ѡ��
		customOptionSetup();
	}
	
	// �ı��ı���
	private TextWatcher textChangedWatcher = new TextWatcher() {

        //������һ���ı������Ƿ�Ϊ��
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
	// ��յ�ǰ����
	private OnClickListener clearOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			verse_edit.setText("");//
			// ��ȡ�༭�򽹵�
			//verse_edit.requestFocus();
			//�������
			//InputMethodManager imm = (InputMethodManager) verse_edit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  			
			//imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	};	
	// ����
	private OnClickListener editReturnOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(EditVerseActivity.this, HomeActivity.class));
			finish();
			overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
		}
	};	
	// �������Լ���ֽ
	private OnClickListener editOkOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			// ��ȡ�Զ�������
			String verse = verse_edit.getText().toString();
			// �ж��Ƿ���Ҫ��չ����
			int len = verse.length();
			if (len < 9)
				for (int i = 0; i <= 9 - len; i++)
					verse += " ";			
			// ���������Դ���SQL���ݿ�
			dbRecent.insert(verse.substring(0, 1),verse.substring(1));
			// ��������������SharedPreferences
			verseQty = verseQty+1;
			editor.putInt(VERSEQTY, verseQty);
			// �����Դ���SharedPreferences
			editor.putString(VERSE, verse);// ����
			// ����ֽ�������SharedPreferences
			editor.putBoolean(BOOLIDPATH, bIdOrPath);
			editor.putInt(WALLPAPERID, wallpaperId);// ��ֽID
			editor.putString(WALLPAPERPATH, wallpaperPath);// ��ֽ·��
			editor.commit();

			startActivity(new Intent(EditVerseActivity.this, HomeActivity.class));			
			//finish();
			//overridePendingTransition(R.anim.push_down_in,R.anim.alpha_action_out);
			overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
		}
	};
	
	/** ѡ��Ӧ�����Դ���ɫ���ֽ **/
	OnItemClickListener wallpaperListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			wallpaperId = WallpaperAdapter.wallpaper[arg2];
			mEditVerseLayout.setBackgroundResource(wallpaperId);
			bIdOrPath = true;//��ֽ��ԴΪӦ����ID
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

	/** ѡ��Ӧ�����ֽ **/	
	private OnClickListener editWallpaperOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			showPicturePicker(EditVerseActivity.this);
		}
	};
	// ���ջ�ѡȡ���ͼƬΪ������ֽ
	public void showPicturePicker(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("ѡ��ֽ");
		builder.setItems(new String[] { "����", "�����ѡȡ", "�����ֽ"},
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case TAKE_PHOTO:							
							Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							startActivityForResult(openCameraIntent,TAKE_PHOTO);							
							bIdOrPath = false;//��ֽ��ԴΪӦ����·��
							break;

						case CHOOSE_PICTURE:
							Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
							openAlbumIntent.setType("image/*");
							startActivityForResult(openAlbumIntent,CHOOSE_PICTURE);							
							bIdOrPath = false;//��ֽ��ԴΪӦ����·��
							break;
							
						case DEFAULT_PAPER:
							wallpaperId = R.drawable.wallpaper00;
							mEditVerseLayout.setBackgroundResource(wallpaperId);
							bIdOrPath = true;//��ֽ��ԴΪӦ����ID
							break;
							
						default:
							break;
						}
						
					}
				});
		builder.create().show();
	}

	private static final int TAKE_PHOTO = 0;// ����
	private static final int CHOOSE_PICTURE = 1;// �����ѡȡ
	private static final int DEFAULT_PAPER = 2;// Ĭ�ϱ�ֽ

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case TAKE_PHOTO:
				// ��ȡͼƬ����
				Bundle bundle = data.getExtras(); 
				Bitmap photo = (Bitmap)bundle.get("data");
				// ����������ֽ
				mEditVerseLayout.setBackgroundDrawable(new BitmapDrawable(photo));
				// ���浽SD��
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
						// ����������ֽ
						mEditVerseLayout.setBackgroundDrawable(new BitmapDrawable(picture));
						// ��ȡѡ��ͼƬ��·��
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

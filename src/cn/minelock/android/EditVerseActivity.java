package cn.minelock.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.minelock.util.FileUtils;
import cn.minelock.util.StringUtil;
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

	private Button mBtnDropDown;// ������ť
	private List<CustemObject> nameList = new ArrayList<CustemObject>();
	private AbstractSpinerAdapter mAdapter;
	private EditText verse_edit = null;

	private SharedPreferences settings = null;
	private SharedPreferences.Editor editor = null;
	public static final String PREFS = "lock_pref";// pref�ļ���
	public static final String VERSE = "verse";// ����prefֵ����
	//public static final String VERSEID = "verse_id";// ����id prefֵ����
	public static final String VERSEQTY = "verse_quantity";// ��������prefֵ����	
	public static final String SHOWVERSEFLAG = "showVerseFlag";//������ʾ��ʽprefֵ����
	public static final String BOOLIDPATH = "wallpaper_idorpath";// Ӧ����or���ֽbool��prefֵ����,trueΪID��falseΪpath
	public static final String WALLPAPERID = "wallpaper_id";// Ӧ���ڱ�ֽ��ԴID��prefֵ����
	public static final String WALLPAPERPATH = "wallpaper_path";// Ӧ�����ֽPath��prefֵ����
	
	private int wallpaperId;
	private int _wallpaperId;
	private String wallpaperPath;
	private String _wallpaperPath;
	private boolean bIdOrPath;//trueΪId��falseΪPath
	private int verseQty;
	
	private boolean bControlEditColor = true;
	private LinearLayout mEditVerseLayout;
	private WallpaperAdapter wpAdapter;
	private WallpaperManager wallpaperManager;
	private GridView wpGridview;
	private ImageButton clear_btn;
	//private ProgressBar pb;
	
	dbHelper dbRecent;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editverse);
		mEditVerseLayout = (LinearLayout) findViewById(R.id.editverse_layout);
		wpGridview = (GridView) findViewById(R.id.wallpaper_grid);
		//pb = (ProgressBar) findViewById(R.id.editverse_progressbar);
		// ��ȡ�洢��pref����
		settings = getSharedPreferences(PREFS, 0);
		editor = settings.edit();
		// ��ȡϵͳ����Ļ��ֽ
		wallpaperManager = WallpaperManager.getInstance(this);
		// ��ȡ����ı�ֽ
		//bIdOrPath = settings.getBoolean(BOOLIDPATH, true);
		//wallpaperId = settings.getInt(WALLPAPERID, R.drawable.wallpaper01);
		wallpaperPath = settings.getString(WALLPAPERPATH, "1_.png");
		// �����ֽ
		bIdOrPath = true;
		wallpaperId = WallpaperAdapter.wallpaper[(int)(Math.random()*WallpaperAdapter.wallpaper.length)];		
		mEditVerseLayout.setBackgroundResource(wallpaperId);
/*		if(bIdOrPath==true)//���ñ�ֽ			
			mEditVerseLayout.setBackgroundResource(wallpaperId);
		else{
			Bitmap bitmap = BitmapFactory.decodeFile(wallpaperPath);
			try {
				mEditVerseLayout.setBackgroundDrawable(new BitmapDrawable(bitmap));
			} catch (Exception e) {
				// TODO: handle exception
				mEditVerseLayout.setBackgroundResource(wallpaperId);
			}			
		}*/
		// ��ȡ���������
		verseQty = settings.getInt(VERSEQTY, 0);			
		verse_edit = (EditText) findViewById(R.id.edit_verse);
		//String verse = getResources().getString(R.string.initial_verse);		
		//verse_edit.setText(settings.getString(VERSE, verse).trim());//����Ĭ������
		String[] seasonVerse = getSeasonVerse();
		verse_edit.setText(seasonVerse[(int)(Math.random()*seasonVerse.length)]);//�����������
		verse_hint = verse_edit.getText().toString();
		verse_edit.setHighlightColor(getResources().getColor(R.color.alpha_black1));
		verse_edit.selectAll();
		//verse_edit.addTextChangedListener(textChangedWatcher);//����ʱ��ʾ��հ�ť
		verse_edit.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
			    // ������뷨�ڴ������Ѿ���ʾ�������أ���֮����ʾ
			    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
			    //imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			    boolean bool = imm.isActive();
			    if(bool){
			    	wpGridview.setVisibility(View.GONE);
			    }
			    return false;
			}
		});
		// ѡ��Ӧ���Դ���ɫ��ʼ��
		wpAdapter = new WallpaperAdapter(this);
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
		
        // SQLite���ݿ�
		dbRecent = new dbHelper(this);
		
		// ��յ�ǰ����
		clear_btn = (ImageButton) findViewById(R.id.edit_clear);
		clear_btn.setOnClickListener(clearOnClickListener);
		// ���д�����Ƿ��ý���
		verse_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {  
		    @Override  
		    public void onFocusChange(View v, boolean hasFocus) {  
		        if(hasFocus && TextUtils.isEmpty(verse_edit.getText().toString())==false) {
		        	// �˴�Ϊ�õ�����ʱ�Ĵ�������
		        	//clear_btn.setVisibility(View.VISIBLE);
		        } else {
		        	// �˴�Ϊʧȥ����ʱ�Ĵ�������
		        	//clear_btn.setVisibility(View.GONE);
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
	/** �������Լ���ֽ**/
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
				if(verse_hint.trim().equals(verse.trim())==false){//�༭ǰ�����Բ�ͬ
					long verseId = dbRecent.insert(_wallpaperId,verse.substring(0),idPath,wallpaperId,wallpaperPath);
					// ��������������SharedPreferences
					verseQty = verseQty+1;
					editor.putInt(VERSEQTY, verseQty);
					//editor.putLong(VERSEID, verseId);
				}
			}
			else{
				int showVerseFlag = 1;
				editor.putInt(SHOWVERSEFLAG, showVerseFlag);
			}*/
				
			// �����Դ���SharedPreferences
			verseQty = verseQty+1;//��������
			editor.putInt(VERSEQTY, verseQty);
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
	
	// ѡ��Ӧ�����Դ���ɫ���ֽ 
	OnItemClickListener wallpaperListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			wallpaperId = WallpaperAdapter.wallpaper[arg2];
			_wallpaperId = WallpaperAdapter._wallpaper[arg2];
			mEditVerseLayout.setBackgroundResource(wallpaperId);
			bIdOrPath = true;//��ֽ��ԴΪӦ����ID					
		}
	};	
	// ���ʵ����ɫ����ֽѡ�������ʾ/����
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

	/** ѡ��Ӧ�����ֽ **/	
	private OnClickListener editWallpaperOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			hideSoftKeyboard();
			wpGridview.setVisibility(View.GONE);
			showPicturePicker(EditVerseActivity.this);
		}
	};
	
	final Handler handler = new Handler();
	private String verse_hint;
	private String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Minelock";
	private String photoName = "photo" + StringUtil.makeFileName();	//
	// ���ջ�ѡȡ���ͼƬΪ������ֽ
	public void showPicturePicker(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		//builder.setTitle("����������ֽ");
		builder.setItems(new String[] { "����", "�����ѡȡ", "�������ֽͬ��"},
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case TAKE_PHOTO:							
							Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							
							//����ͼƬ
							photoName = "photo" + StringUtil.makeFileName();
							wallpaperPath = dir + "/" + photoName + ".png";
							_wallpaperPath = dir + "/" + photoName + "_" + ".png";
							openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(wallpaperPath)));							
							
							startActivityForResult(openCameraIntent,TAKE_PHOTO);							
							bIdOrPath = false;//��ֽ��ԴΪӦ����·��
							break;

						case CHOOSE_PICTURE:
							Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
							openAlbumIntent.setType("image/*");
							startActivityForResult(openAlbumIntent,CHOOSE_PICTURE);							
							bIdOrPath = false;//��ֽ��ԴΪӦ����·��
							break;
							
						case RANDOM_PAPER:							
							int random = (int)(Math.random()*WallpaperAdapter.wallpaper.length);
							wallpaperId = WallpaperAdapter.wallpaper[random];							
							mEditVerseLayout.setBackgroundResource(wallpaperId);
							bIdOrPath = true;//��ֽ��ԴΪӦ����ID		
							break;
							
						case DEFAULT_PAPER:
							//verse_edit.setHint("����ͬ��...");
							//showRoundProcessDialog(R.layout.process_dialog);
					    	verse_hint = verse_edit.getText().toString();
					    	verse_edit.setCursorVisible(false);
					    	verse_edit.setText("����ͬ��...");
					    	//verse_edit.setSelection(verse_hint.length());
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									handler.post(runnableDefaultPaper);
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
	
	// ����Runnable������runnable�и��½���  
    Runnable runnableDefaultPaper = new  Runnable(){  
        @Override  
        public void run() { 
        	getDefaultWallpaper();
        }           
    };
    
    public void getDefaultWallpaper(){
		// ��ȡ��ǰ��ֽ ,ת��Bitmap�������� ���� 
		Drawable wallpaperDrawable = wallpaperManager.getDrawable();  
		Bitmap bm = ((BitmapDrawable) wallpaperDrawable).getBitmap();
		Bitmap _bm = ImageTools.zoomBitmap(bm, 72, 72);//ѹ��
		mEditVerseLayout.setBackgroundDrawable(new BitmapDrawable(bm));
		// ���浽SD��
		String photoName = "desk" + StringUtil.makeFileName();
		ImageTools.savePhotoToSDCard(bm, dir, photoName);
		ImageTools.savePhotoToSDCard(_bm, dir, photoName+"_");
		wallpaperPath = dir + "/" + photoName + ".png";
		_wallpaperPath = dir + "/" + photoName +"_"+ ".png";
		bIdOrPath = false;//��ֽ��ԴΪӦ����·��
		
        StringUtil.showToast(getApplication(), "ͬ���ɹ�", Toast.LENGTH_SHORT);
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
        // ע��˴�Ҫ����show֮�� ����ᱨ�쳣
        mDialog.setContentView(layout);
    }
    
	private static final int TAKE_PHOTO = 0;// ����
	private static final int CHOOSE_PICTURE = 1;// �����ѡȡ
	private static final int RANDOM_PAPER = 3;// �����ֽ
	private static final int DEFAULT_PAPER = 2;// Ĭ�ϱ�ֽ

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case TAKE_PHOTO:
				// ģ��ͼƬ
				//Bitmap photo = (Bitmap)data.getExtras().get("data");
				
				// ����ͼƬ
				Bitmap photo = null;
				Bitmap smallPhoto = null;
				Bitmap _smallPhoto = null;
				try {
					FileInputStream fis = new FileInputStream(wallpaperPath);
					photo = BitmapFactory.decodeStream(fis);
					smallPhoto = ImageTools.zoomBitmap(photo, photo.getWidth() / 2, photo.getHeight() / 2);//ѹ��
					_smallPhoto = ImageTools.zoomBitmap(smallPhoto, 72, 72);//ѹ��
					photo.recycle();
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
				// ����������ֽ
				mEditVerseLayout.setBackgroundDrawable(new BitmapDrawable(smallPhoto));
				// ���浽SD��
				ImageTools.savePhotoToSDCard(smallPhoto, dir, photoName);//��������
				ImageTools.savePhotoToSDCard(_smallPhoto, dir, photoName+"_");
				
/*				String photoName = "photo" + StringUtil.makeFileName();
				ImageTools.savePhotoToSDCard(photo, dir, photoName);
				wallpaperPath = dir + "/" + photoName + ".png";*/
				break;
			
			case CHOOSE_PICTURE:
				ContentResolver resolver = getContentResolver();
				Uri originalUri = data.getData();
				try {
					Bitmap image = MediaStore.Images.Media.getBitmap(resolver,originalUri);
					if (image != null) {	
						// ����������ֽ
						Bitmap smallImage = ImageTools.zoomBitmap(image, image.getWidth() / 2, image.getHeight() / 2);
						Bitmap _smallImage = ImageTools.zoomBitmap(smallImage, 72, 72);
						mEditVerseLayout.setBackgroundDrawable(new BitmapDrawable(smallImage));
						image.recycle();
						
						// ��ȡѡ��ͼƬ��·��
/*						String[] proj = { MediaStore.Images.Media.DATA };
						Cursor cursor = managedQuery(originalUri, proj, null,null,null);
						int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						wallpaperPath = cursor.getString(column_index);*/						
						// ���浽SD��
						String imageName = "image" + StringUtil.makeFileName();
						ImageTools.savePhotoToSDCard(smallImage, dir, imageName);
						ImageTools.savePhotoToSDCard(_smallImage, dir, imageName+"_");
						wallpaperPath = dir + "/" + imageName + ".png";
						_wallpaperPath = dir + "/" + imageName +"_"+ ".png";
						
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
				
			default:
				break;
			}			
			
		}
	}
	
	// ���������
	private void hideSoftKeyboard(){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		if(imm.isActive())// isOpen������true�����ʾ���뷨��
			imm.hideSoftInputFromWindow(verse_edit.getWindowToken(), 0); //ǿ�����ؼ���		
	}
	/**
	 * ��ȡ����ʫ��
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

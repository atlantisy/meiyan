package cn.minelock.android;

import java.io.File;
import java.io.IOException;

import cn.minelock.android.SettingMoreActivity.OnShowPasswordListener;
import cn.minelock.util.StringUtil;
import cn.minelock.widget.ImagePiece;
import cn.minelock.widget.ImageTools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SetPatternActivity extends Activity {

	private SharedPreferences prefs = null;
	public static final String PREFS = "lock_pref";// pref�ļ���
	public static final String SHOWPASSWORD = "showPassword";
	public static final String PATTERNOPTION = "patternOption";// ����ѡ��prefֵ����
	
	private CheckBox showpassword_checkbox;
	private boolean mShowPassword;
	private TextView optiontext;
	private Button puzzle;
	private Button setpattern_option_btn;
	private Button setpattern_puzzle_btn;
	private int PUZZLE_SIZE = 96;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setpattern);
		// ������ʽѡ�����ּ��Ź�ƴͼ��ť
		optiontext = (TextView) findViewById(R.id.setpattern_optiontext);
		puzzle = (Button) findViewById(R.id.setpattern_puzzle);
		prefs = getSharedPreferences(PREFS, 0);		
		if(prefs.getInt(PATTERNOPTION, 1)==1)
			optiontext.setText("͸����");
		else if(prefs.getInt(PATTERNOPTION, 1)==2)
			optiontext.setText("�ٱ���");
		else if(prefs.getInt(PATTERNOPTION, 1)==3){
			optiontext.setText("�Ź�ƴͼ");
			puzzle.setVisibility(View.VISIBLE);
		}
		// �Ƿ���ʾ��������
		mShowPassword = prefs.getBoolean(SHOWPASSWORD, true);		 
		showpassword_checkbox = (CheckBox) findViewById(R.id.showpassword_checkbox);
		showpassword_checkbox.setChecked(mShowPassword);
		showpassword_checkbox.setOnClickListener(new View.OnClickListener() {					
			public void onClick(View v) {
				mShowPassword = showpassword_checkbox.isChecked();
				//������checkֵ����pref��				
				SharedPreferences.Editor editor = prefs.edit();						
				editor.putBoolean(SHOWPASSWORD, mShowPassword);
				editor.commit();
			}
		 });		
		
		// ������ʽѡ��
		setpattern_option_btn = (Button) findViewById(R.id.setpattern_optionbtn);
		setpattern_option_btn.setOnClickListener(optionOnClickListener);
		// ƴͼ����
		setpattern_puzzle_btn = (Button) findViewById(R.id.setpattern_puzzle);
		setpattern_puzzle_btn.setOnClickListener(puzzleOnClickListener);
		// ����
		ImageButton setpattern_return = (ImageButton) findViewById(R.id.setpattern_return);
		setpattern_return.setOnClickListener(new View.OnClickListener() {					
			public void onClick(View v) {
				finish();
			}
		 });		
	}
	
	
	private String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Minelock/Puzzle";
	// ƴͼ����
	private OnClickListener puzzleOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// ѡ�����
			final AlertDialog dlg = new AlertDialog.Builder(SetPatternActivity.this).create();
			dlg.show();
			Window window = dlg.getWindow();						 
			window.setContentView(R.layout.photo_action_dialog);
			// ����			
			Button camera = (Button) window.findViewById(R.id.photo_action_camera);				
			camera.setOnClickListener(new View.OnClickListener() {					
				public void onClick(View v) {
					dlg.cancel();
					
					Intent intent = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					//�������ָ������������պ����Ƭ�洢��·��
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
							.fromFile(new File(dir, "puzzle.jpg")));
					startActivityForResult(intent, 2);
				}
			 });
			// ���			
			Button album = (Button) window.findViewById(R.id.photo_action_album);				
			album.setOnClickListener(new View.OnClickListener() {					
				public void onClick(View v) {
					dlg.cancel();

					/**
					 * �տ�ʼ�����Լ�Ҳ��֪��ACTION_PICK�Ǹ���ģ�����ֱ�ӿ�IntentԴ�룬
					 * ���Է�������ܶණ����Intent�Ǹ���ǿ��Ķ��������һ����ϸ�Ķ���
					 */
					Intent intent = new Intent(Intent.ACTION_PICK, null);
					
					/**
					 * ������仰����������ʽд��һ����Ч���������
					 * intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					 * intent.setType(""image/*");������������
					 * ���������Ҫ�����ϴ�����������ͼƬ����ʱ����ֱ��д�磺"image/jpeg �� image/png�ȵ�����"
					 * ����ط��и����ʣ�ϣ�����ֽ���£������������URI������ΪʲôҪ��������ʽ��дѽ����ʲô����
					 */
					intent.setDataAndType(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							"image/*");
					startActivityForResult(intent, 1);					

				}
			 });
			//
			View divide2 = (View) window.findViewById(R.id.photo_action_divide2);
			divide2.setVisibility(View.GONE);
			Button desk = (Button) window.findViewById(R.id.photo_action_desk);
			desk.setVisibility(View.GONE);
		}
	};
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		// �����ֱ�Ӵ�����ȡ
		case 1:
			startPhotoZoom(data.getData());
			break;
		// ����ǵ����������ʱ
		case 2:
			File temp = new File(dir + "/"+ "puzzle.jpg");
			startPhotoZoom(Uri.fromFile(temp));
			break;
		// ȡ�òü����ͼƬ
		case 3:
			if(data != null){
				setPicToPuzzle(data);
			}
			break;
		default:
			break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * �ü�ͼƬ����ʵ��
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		// yourself_sdk_path/docs/reference/android/content/Intent.html
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// �������crop=true�������ڿ�����Intent��������ʾ��VIEW�ɲü�
		intent.putExtra("crop", "true");
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY �ǲü�ͼƬ���
		intent.putExtra("outputX", PUZZLE_SIZE*3);
		intent.putExtra("outputY", PUZZLE_SIZE*3);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}
	/**
	 * ����ü�֮���ͼƬ����
	 * @param picdata
	 */
	private void setPicToPuzzle(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			
	        for (int i = 0; i < 3; i++) {  
	            for (int j = 0; j < 3; j++) {   
	            	int index = j + i * 3;
	            	int xValue = j * PUZZLE_SIZE;  
	                int yValue = i * PUZZLE_SIZE;  
	                Bitmap piece = Bitmap.createBitmap(photo, xValue, yValue, PUZZLE_SIZE, PUZZLE_SIZE);
					ImageTools.savePhotoToSDCard(piece, dir, String.valueOf(index));	                
	            }  
	        }
	        StringUtil.showToast(this, "ƴͼ���",  Toast.LENGTH_SHORT);
	        startActivity(new Intent(SetPatternActivity.this, SetPasswordActivity.class));
			/**
			 * ����ע�͵ķ����ǽ��ü�֮���ͼƬ��Base64Coder���ַ���ʽ��
			 * ������������QQͷ���ϴ����õķ������������
			 */
			
			/*ByteArrayOutputStream stream = new ByteArrayOutputStream();
			photo.compress(Bitmap.CompressFormat.JPEG, 60, stream);
			byte[] b = stream.toByteArray();
			// ��ͼƬ�����ַ�����ʽ�洢����
			
			tp = new String(Base64Coder.encodeLines(b));
			����ط���ҿ���д�¸��������ϴ�ͼƬ��ʵ�֣�ֱ�Ӱ�tpֱ���ϴ��Ϳ����ˣ�
			
			������ص��ķ����������ݻ�����Base64Coder����ʽ�Ļ������������·�ʽת��
			Ϊ���ǿ����õ�ͼƬ����
			Bitmap dBitmap = BitmapFactory.decodeFile(tp);
			Drawable drawable = new BitmapDrawable(dBitmap);
			*/
		}
	}	
	// ������ʽѡ��
	private OnClickListener optionOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {			
			// ѡ�����
			final AlertDialog dlg = new AlertDialog.Builder(SetPatternActivity.this).create();
			dlg.show();
			Window window = dlg.getWindow();						 
			window.setContentView(R.layout.pattern_option_dialog);
			// ͸����			
			Button patternoption1 = (Button) window.findViewById(R.id.patternoption1);				
			patternoption1.setOnClickListener(new View.OnClickListener() {					
				public void onClick(View v) {
					dlg.cancel();
					//����pref��
					SharedPreferences.Editor editor = prefs.edit();						
					editor.putInt(PATTERNOPTION, 1);
					editor.commit();
					
					optiontext.setText("͸����");
					puzzle.setVisibility(View.GONE);
				}
			 });
			// �ٱ���			
			Button patternoption2 = (Button) window.findViewById(R.id.patternoption2);				
			patternoption2.setOnClickListener(new View.OnClickListener() {					
				public void onClick(View v) {
					dlg.cancel();
					//����pref��
					SharedPreferences.Editor editor = prefs.edit();						
					editor.putInt(PATTERNOPTION, 2);
					editor.commit();
					
					optiontext.setText("�ٱ���");
					puzzle.setVisibility(View.GONE);
				}
			 });
			// �Ź�ƴͼ			
			Button patternoption3 = (Button) window.findViewById(R.id.patternoption3);				
			patternoption3.setOnClickListener(new View.OnClickListener() {					
				public void onClick(View v) {
					dlg.cancel();
					//����pref��
					SharedPreferences.Editor editor = prefs.edit();						
					editor.putInt(PATTERNOPTION, 3);
					editor.commit();
					
					optiontext.setText("�Ź�ƴͼ");						
					puzzle.setVisibility(View.VISIBLE);
				}
			 });			
		}
	};
}

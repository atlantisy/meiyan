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
	public static final String PREFS = "lock_pref";// pref文件名
	public static final String SHOWPASSWORD = "showPassword";
	public static final String PATTERNOPTION = "patternOption";// 手势选项pref值名称
	
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
		// 手势样式选项文字及九宫拼图按钮
		optiontext = (TextView) findViewById(R.id.setpattern_optiontext);
		puzzle = (Button) findViewById(R.id.setpattern_puzzle);
		prefs = getSharedPreferences(PREFS, 0);		
		if(prefs.getInt(PATTERNOPTION, 1)==1)
			optiontext.setText("透明黑");
		else if(prefs.getInt(PATTERNOPTION, 1)==2)
			optiontext.setText("百变多彩");
		else if(prefs.getInt(PATTERNOPTION, 1)==3){
			optiontext.setText("九宫拼图");
			puzzle.setVisibility(View.VISIBLE);
		}
		// 是否显示手势密码
		mShowPassword = prefs.getBoolean(SHOWPASSWORD, true);		 
		showpassword_checkbox = (CheckBox) findViewById(R.id.showpassword_checkbox);
		showpassword_checkbox.setChecked(mShowPassword);
		showpassword_checkbox.setOnClickListener(new View.OnClickListener() {					
			public void onClick(View v) {
				mShowPassword = showpassword_checkbox.isChecked();
				//将开关check值存入pref中				
				SharedPreferences.Editor editor = prefs.edit();						
				editor.putBoolean(SHOWPASSWORD, mShowPassword);
				editor.commit();
			}
		 });		
		
		// 手势样式选项
		setpattern_option_btn = (Button) findViewById(R.id.setpattern_optionbtn);
		setpattern_option_btn.setOnClickListener(optionOnClickListener);
		// 拼图手势
		setpattern_puzzle_btn = (Button) findViewById(R.id.setpattern_puzzle);
		setpattern_puzzle_btn.setOnClickListener(puzzleOnClickListener);
		// 返回
		ImageButton setpattern_return = (ImageButton) findViewById(R.id.setpattern_return);
		setpattern_return.setOnClickListener(new View.OnClickListener() {					
			public void onClick(View v) {
				finish();
			}
		 });		
	}
	
	
	private String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Minelock/Puzzle";
	// 拼图手势
	private OnClickListener puzzleOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// 选项弹出框
			final AlertDialog dlg = new AlertDialog.Builder(SetPatternActivity.this).create();
			dlg.show();
			Window window = dlg.getWindow();						 
			window.setContentView(R.layout.photo_action_dialog);
			// 拍照			
			Button camera = (Button) window.findViewById(R.id.photo_action_camera);				
			camera.setOnClickListener(new View.OnClickListener() {					
				public void onClick(View v) {
					dlg.cancel();
					
					Intent intent = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					//下面这句指定调用相机拍照后的照片存储的路径
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
							.fromFile(new File(dir, "puzzle.jpg")));
					startActivityForResult(intent, 2);
				}
			 });
			// 相册			
			Button album = (Button) window.findViewById(R.id.photo_action_album);				
			album.setOnClickListener(new View.OnClickListener() {					
				public void onClick(View v) {
					dlg.cancel();

					/**
					 * 刚开始，我自己也不知道ACTION_PICK是干嘛的，后来直接看Intent源码，
					 * 可以发现里面很多东西，Intent是个很强大的东西，大家一定仔细阅读下
					 */
					Intent intent = new Intent(Intent.ACTION_PICK, null);
					
					/**
					 * 下面这句话，与其它方式写是一样的效果，如果：
					 * intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					 * intent.setType(""image/*");设置数据类型
					 * 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
					 * 这个地方有个疑问，希望高手解答下：就是这个数据URI与类型为什么要分两种形式来写呀？有什么区别？
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
		// 如果是直接从相册获取
		case 1:
			try {
				startPhotoZoom(data.getData());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}			
			break;
		// 如果是调用相机拍照时
		case 2:
			File temp = new File(dir + "/"+ "puzzle.jpg");
			try {
				startPhotoZoom(Uri.fromFile(temp));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}			
			break;
		// 取得裁剪后的图片
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
	 * 裁剪图片方法实现
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		// yourself_sdk_path/docs/reference/android/content/Intent.html
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", PUZZLE_SIZE*3);
		intent.putExtra("outputY", PUZZLE_SIZE*3);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}
	/**
	 * 保存裁剪之后的图片数据
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
	        StringUtil.showToast(this, "拼图完成",  Toast.LENGTH_SHORT);
	        
	        finish();
			/**
			 * 下面注释的方法是将裁剪之后的图片以Base64Coder的字符方式上
			 * 传到服务器，QQ头像上传采用的方法跟这个类似
			 */
			
			/*ByteArrayOutputStream stream = new ByteArrayOutputStream();
			photo.compress(Bitmap.CompressFormat.JPEG, 60, stream);
			byte[] b = stream.toByteArray();
			// 将图片流以字符串形式存储下来
			
			tp = new String(Base64Coder.encodeLines(b));
			这个地方大家可以写下给服务器上传图片的实现，直接把tp直接上传就可以了，
			
			如果下载到的服务器的数据还是以Base64Coder的形式的话，可以用以下方式转换
			为我们可以用的图片类型
			Bitmap dBitmap = BitmapFactory.decodeFile(tp);
			Drawable drawable = new BitmapDrawable(dBitmap);
			*/
		}
	}	
	// 手势样式选项
	private OnClickListener optionOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {			
			// 选项弹出框
			final AlertDialog dlg = new AlertDialog.Builder(SetPatternActivity.this).create();
			dlg.show();
			Window window = dlg.getWindow();						 
			window.setContentView(R.layout.pattern_option_dialog);
			// 透明黑			
			Button patternoption1 = (Button) window.findViewById(R.id.patternoption1);				
			patternoption1.setOnClickListener(new View.OnClickListener() {					
				public void onClick(View v) {
					dlg.cancel();
					//存入pref中
					SharedPreferences.Editor editor = prefs.edit();						
					editor.putInt(PATTERNOPTION, 1);
					editor.commit();
					
					optiontext.setText("透明黑");
					puzzle.setVisibility(View.GONE);
				}
			 });
			// 百变多彩			
			Button patternoption2 = (Button) window.findViewById(R.id.patternoption2);				
			patternoption2.setOnClickListener(new View.OnClickListener() {					
				public void onClick(View v) {
					dlg.cancel();
					//存入pref中
					SharedPreferences.Editor editor = prefs.edit();						
					editor.putInt(PATTERNOPTION, 2);
					editor.commit();
					
					optiontext.setText("百变多彩");
					puzzle.setVisibility(View.GONE);
				}
			 });
			// 九宫拼图			
			Button patternoption3 = (Button) window.findViewById(R.id.patternoption3);				
			patternoption3.setOnClickListener(new View.OnClickListener() {					
				public void onClick(View v) {
					dlg.cancel();
					//存入pref中
					SharedPreferences.Editor editor = prefs.edit();						
					editor.putInt(PATTERNOPTION, 3);
					editor.commit();
					
					optiontext.setText("九宫拼图");						
					puzzle.setVisibility(View.VISIBLE);
				}
			 });			
		}
	};
}

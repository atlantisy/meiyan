package cn.minelock.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cn.minelock.util.StringUtil;
import cn.minelock.widget.PatternPassWordView;
import cn.minelock.widget.PatternPassWordView.OnCompleteListener;

import com.meiyanlock.android.R;

public class SetPasswordActivity extends Activity {
	private PatternPassWordView ppwv;
	private String password;
	private boolean needverify = true;
	private Toast toast;
	
	public static final String PREFS = "lock_pref";//pref文件名
	public static final String BOOLIDPATH = "wallpaper_idorpath";//应用内or外壁纸bool的pref值名称,true为ID，false为path
	public static final String WALLPAPERID = "wallpaper_id";//应用内壁纸资源ID的pref值名称
	public static final String WALLPAPERPATH = "wallpaper_path";//应用外壁纸Path的pref值名称
	
	private void showToast(CharSequence message) {
		if (null == toast) {
			toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
//			toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}

		toast.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setpassword);
		RelativeLayout setPasswordLayout = (RelativeLayout)findViewById(R.id.SetPasswordLayout);
		//获取pref值
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
		boolean bIdOrPath = settings.getBoolean(BOOLIDPATH, true);
		int wallpaperId = settings.getInt(WALLPAPERID, R.drawable.wallpaper00);
		String wallpaperPath = settings.getString(WALLPAPERPATH, "");	
		if(bIdOrPath==true)//设置壁纸			
			setPasswordLayout.setBackgroundResource(wallpaperId);
		else{
			Bitmap bitmap = BitmapFactory.decodeFile(wallpaperPath);
			setPasswordLayout.setBackgroundDrawable(new BitmapDrawable(bitmap));
		}
		//
		ppwv = (PatternPassWordView) this.findViewById(R.id.mPatternPassWordView);
		ppwv.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(String mPassword) {
				password = mPassword;
				if (needverify) {
					if (ppwv.verifyPassword(mPassword)) {
						showToast("密码输入正确,请输入新密码!");
						ppwv.clearPassword();
						needverify = false;
					} else {
						showToast("错误的密码,请重新输入!");
						ppwv.clearPassword();
						password = "";
					}
				}
			}
		});

		OnClickListener mOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tvSave:
					if (StringUtil.isNotEmpty(password)) {
						ppwv.resetPassWord(password);
						ppwv.clearPassword();
						showToast("密码修改成功,请记住密码.");
						//返回九宫格已设置结果
			            Intent data=new Intent();  
			            data.putExtra("SetPassWord", true);   
			            //请求代码可以自己设置，这里设置成20  
			            setResult(20, data);
						finish();
					} else {
						ppwv.clearPassword();
						showToast("密码不能为空,请输入密码.");
					}
					break;
				case R.id.tvReset:
					ppwv.clearPassword();
					break;
				}
			}
		};
		Button buttonSave = (Button) this.findViewById(R.id.tvSave);
		buttonSave.setOnClickListener(mOnClickListener);
		Button tvReset = (Button) this.findViewById(R.id.tvReset);
		tvReset.setOnClickListener(mOnClickListener);
		// 如果密码为空,直接输入密码
		if (ppwv.isPasswordEmpty()) {
			this.needverify = false;
			showToast("请输入密码");
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

}

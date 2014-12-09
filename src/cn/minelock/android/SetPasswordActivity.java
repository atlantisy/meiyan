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

import cn.minelock.android.R;

public class SetPasswordActivity extends Activity {
	private PatternPassWordView ppwv;
	private String password;
	private boolean needverify = true;
	private Toast toast;
	
	public static final String PREFS = "lock_pref";//pref�ļ���
	public static final String BOOLIDPATH = "wallpaper_idorpath";//Ӧ����or���ֽbool��prefֵ����,trueΪID��falseΪpath
	public static final String WALLPAPERID = "wallpaper_id";//Ӧ���ڱ�ֽ��ԴID��prefֵ����
	public static final String WALLPAPERPATH = "wallpaper_path";//Ӧ�����ֽPath��prefֵ����
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setpassword);
		RelativeLayout setPasswordLayout = (RelativeLayout)findViewById(R.id.SetPasswordLayout);
		//��ȡprefֵ
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
		boolean bIdOrPath = settings.getBoolean(BOOLIDPATH, true);
		int wallpaperId = settings.getInt(WALLPAPERID, R.drawable.wallpaper00);
		String wallpaperPath = settings.getString(WALLPAPERPATH, "");	
		if(bIdOrPath==true)//���ñ�ֽ			
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
						showToast("ͨ���������������벢����");
						ppwv.clearPassword();
						needverify = false;
					} else {
						showToast("����������������룡");
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
						showToast("��ϲ�����óɹ�");
						//���ؾŹ��������ý��
			            Intent data=new Intent();  
			            data.putExtra("SetPassWord", true);   
			            //�����������Լ����ã��������ó�20  
			            setResult(20, data);
						finish();
					} else {
						ppwv.clearPassword();
						showToast("����Ϊ�գ����������룡");
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
		// �������Ϊ��,ֱ����������
		if (ppwv.isPasswordEmpty()) {
			this.needverify = false;
			showToast("û�����룬������");
		}
	}
	
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
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	

}

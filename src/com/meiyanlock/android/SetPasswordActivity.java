package com.meiyanlock.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.meiyanlock.widget.PatternPassWordView;
import com.meiyanlock.widget.PatternPassWordView.OnCompleteListener;
import com.meiyanlock.util.StringUtil;

public class SetPasswordActivity extends Activity {
	private PatternPassWordView ppwv;
	private String password;
	private boolean needverify = true;
	private Toast toast;

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
		ppwv = (PatternPassWordView) this.findViewById(R.id.mPatternPassWordView);
		ppwv.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(String mPassword) {
				password = mPassword;
				if (needverify) {
					if (ppwv.verifyPassword(mPassword)) {
						showToast("����������ȷ,������������!");
						ppwv.clearPassword();
						needverify = false;
					} else {
						showToast("���������,����������!");
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
						showToast("�����޸ĳɹ�,���ס����.");
						//���ؾŹ��������ý��
			            Intent data=new Intent();  
			            data.putExtra("SetPassWord", true);   
			            //�����������Լ����ã��������ó�20  
			            setResult(20, data);
						finish();
					} else {
						ppwv.clearPassword();
						showToast("���벻��Ϊ��,����������.");
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
			showToast("����������");
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

package com.meiyanlock.android;

import java.util.ArrayList;
import java.util.List;

import com.meiyanlock.widget.AbstractSpinerAdapter;
import com.meiyanlock.widget.CustemObject;
import com.meiyanlock.widget.CustemSpinerAdapter;
import com.meiyanlock.widget.SpinerPopWindow;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class EditVerseActivity extends Activity implements OnClickListener, AbstractSpinerAdapter.IOnItemSelectListener{

	private Button mBtnDropDown;//������ť
	private List<CustemObject> nameList = new ArrayList<CustemObject>();
	private AbstractSpinerAdapter mAdapter;
	private EditText verse_edit = null;

	public static final String PREFS = "lock_pref";//pref�ļ���
	public static final String VERSE = "verse";//������ʽprefֵ����

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editverse);
		
		//��ȡ���������			
		SharedPreferences settings = getSharedPreferences(PREFS, 0);  
		String verse = settings.getString(VERSE, "ÿʱÿ�� ��������");
		//����Ĭ������
		verse_edit = (EditText)findViewById(R.id.edit_verse);	
		verse_edit.setText(verse.trim().toCharArray(), 0, verse.trim().length());
		
		//�������Ա༭���
		ImageButton editok_btn = (ImageButton)findViewById(R.id.edit_ok);
		editok_btn.setOnClickListener(editOkOnClickListener);
		
		// �Զ�������ѡ��
		customOptionSetup();
	}
	
	private OnClickListener editOkOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
/*			verse_edit.clearFocus();
			verse_edit.setCursorVisible(false);*/
			//��ȡ�Զ�������
			String verse = verse_edit.getText().toString();						
			//�ж��Ƿ���Ҫ��չ����
			int len = verse.length();
			if(len<9)
				for(int i=0; i<=9-len; i++)
					verse+=" ";
			//�����Դ���SharedPreferences
			SharedPreferences setting = getSharedPreferences(PREFS, 0);  
			SharedPreferences.Editor editor = setting.edit();  
			editor.putString(VERSE, verse);  
			editor.commit();
			
			startActivity(new Intent(EditVerseActivity.this, HomeActivity.class));
			finish();
		}
	};

	private void customOptionSetup(){
		mBtnDropDown = (Button) findViewById(R.id.edit_label);
		mBtnDropDown.setOnClickListener(this);
				
		String[] names = getResources().getStringArray(R.array.customize);
		for(int i = 0; i < names.length; i++){
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
		switch(view.getId()){
		case R.id.edit_label:
			showSpinWindow();
			break;
		}
	}
		
	private SpinerPopWindow mSpinerPopWindow;
	
	private void showSpinWindow(){
		Log.e("", "showSpinWindow");
		mSpinerPopWindow.setWidth(mBtnDropDown.getWidth());
		mSpinerPopWindow.showAsDropDown(mBtnDropDown);
	}
	
	@Override
	public void onItemClick(int pos) {
		setCustom(pos);
	}
	
	private void setCustom(int pos){
		if (pos >= 0 && pos <= nameList.size()){
			CustemObject value = nameList.get(pos);
		
			mBtnDropDown.setText(value.toString());
		}
	}
	
}

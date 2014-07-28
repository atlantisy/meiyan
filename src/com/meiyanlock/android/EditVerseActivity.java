package com.meiyanlock.android;

import java.util.ArrayList;
import java.util.List;

import com.meiyanlock.widget.AbstractSpinerAdapter;
import com.meiyanlock.widget.CustemObject;
import com.meiyanlock.widget.CustemSpinerAdapter;
import com.meiyanlock.widget.SpinerPopWindow;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditVerseActivity extends Activity implements OnClickListener, AbstractSpinerAdapter.IOnItemSelectListener{

	private Button mBtnDropDown;//������ť
	private List<CustemObject> nameList = new ArrayList<CustemObject>();
	private AbstractSpinerAdapter mAdapter;

	public static final String PREFS = "lock_pref";//pref�ļ���
	public static final String VERSE = "verse";//������ʽprefֵ����
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editverse);
		
		//��ȡ�Զ�������
		EditText verse_text = (EditText)findViewById(R.id.custom_verse);
		String verse = verse_text.getText().toString();
		//verse_text.setText(verse.toCharArray(), 0, verse.length());//��verse�ַ�����verse_text 

		//�ж��Ƿ���Ҫ��չ����
		int len = verse.length();
		if(len<9)
			for(int i=0; i<=9-len; i++)
				verse+="";
		//�����Դ���SharedPreferences
		SharedPreferences setting = getSharedPreferences(PREFS, 0);  
		SharedPreferences.Editor editor = setting.edit();  
		editor.putString(VERSE, verse);  
		editor.commit();
		
		// �Զ�������ѡ��
		customOptionSetup();
	}
	
    private void customOptionSetup(){

		mBtnDropDown = (Button) findViewById(R.id.textedit_view);
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
		case R.id.textedit_view:
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

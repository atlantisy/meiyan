package cn.minelock.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import cn.minelock.android.R;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);		
		// ����
		ImageButton return_btn = (ImageButton) findViewById(R.id.about_return);
		return_btn.setOnClickListener(returnOnClickListener);		
		// �������
		Button feedback_btn = (Button) findViewById(R.id.about_feedback);
		feedback_btn.setOnClickListener(feedbackOnClickListener);
		// ΢��
		Button weibo_btn = (Button) findViewById(R.id.about_weibo);
		weibo_btn.setOnClickListener(weiboOnClickListener);
		// ΢��
		Button wechat_btn = (Button) findViewById(R.id.about_wechat);
		wechat_btn.setOnClickListener(wechatOnClickListener);
	}
	// �������
	private OnClickListener feedbackOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			joinQQGroup("YY5Tm4TItvMDDVFJkkqDBRVB4JXeiwla");
		}
	};
	// ΢��
	private OnClickListener weiboOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://dwz.cn/minelock0"));   
			//i.setClassName("com.android.browser", "com.android.browser.BrowserActivity");   
			startActivity(i);  
		}
	};	
	// ΢��
	private OnClickListener wechatOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://dwz.cn/minelock3"));   
			//i.setClassName("com.android.browser", "com.android.browser.BrowserActivity");   
			startActivity(i);  
		}
	};
	// ���ذ�ť
	private OnClickListener returnOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
/*			startActivity(new Intent(AboutActivity.this, SettingActivity.class));
			overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);*/
			finish();
		}
	};
	/****************
	*
	* �������Ⱥ���̡�Ⱥ�ţ�������������Ⱥ(176225597) �� key Ϊ�� YY5Tm4TItvMDDVFJkkqDBRVB4JXeiwla
	* ���� joinQQGroup(YY5Tm4TItvMDDVFJkkqDBRVB4JXeiwla) ���ɷ�����Q�ͻ��������Ⱥ ������������Ⱥ(176225597)
	*
	* @param key �ɹ������ɵ�key
	* @return ����true��ʾ������Q�ɹ�������fals��ʾ����ʧ��
	******************/
	public boolean joinQQGroup(String key) {
	    Intent intent = new Intent();
	    intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
	    // ��Flag�ɸ��ݾ����Ʒ��Ҫ�Զ��壬�����ã����ڼ�Ⱥ���水���أ�������Q�����棬�����ã������ػ᷵�ص������Ʒ����    
	    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	    try {
	        startActivity(intent);
	        return true;
	    } catch (Exception e) {
	        // δ��װ��Q��װ�İ汾��֧��
	        return false;
	    }
	}

}

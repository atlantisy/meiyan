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
		// 返回
		ImageButton return_btn = (ImageButton) findViewById(R.id.about_return);
		return_btn.setOnClickListener(returnOnClickListener);		
		// 意见反馈
		Button feedback_btn = (Button) findViewById(R.id.about_feedback);
		feedback_btn.setOnClickListener(feedbackOnClickListener);
		// 微博
		Button weibo_btn = (Button) findViewById(R.id.about_weibo);
		weibo_btn.setOnClickListener(weiboOnClickListener);
		// 微信
		Button wechat_btn = (Button) findViewById(R.id.about_wechat);
		wechat_btn.setOnClickListener(wechatOnClickListener);
	}
	// 意见反馈
	private OnClickListener feedbackOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(!joinQQGroup("YY5Tm4TItvMDDVFJkkqDBRVB4JXeiwla")){
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://dwz.cn/minelockwx20140926"));  
				startActivity(i);  
			}
		}
	};
	// 微博
	private OnClickListener weiboOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://dwz.cn/minelock0"));   
			//i.setClassName("com.android.browser", "com.android.browser.BrowserActivity");   
			startActivity(i);  
		}
	};	
	// 微信
	private OnClickListener wechatOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			//Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://dwz.cn/minelockwx20140926"));  
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.minelock.com"));   
			//i.setClassName("com.android.browser", "com.android.browser.BrowserActivity");   
			startActivity(i);  
		}
	};
	// 返回按钮
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
	* 发起添加群流程。群号：美言锁屏讨论群(176225597) 的 key 为： YY5Tm4TItvMDDVFJkkqDBRVB4JXeiwla
	* 调用 joinQQGroup(YY5Tm4TItvMDDVFJkkqDBRVB4JXeiwla) 即可发起手Q客户端申请加群 美言锁屏讨论群(176225597)
	*
	* @param key 由官网生成的key
	* @return 返回true表示呼起手Q成功，返回fals表示呼起失败
	******************/
	public boolean joinQQGroup(String key) {
	    Intent intent = new Intent();
	    intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
	    // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    
	    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	    try {
	        startActivity(intent);
	        return true;
	    } catch (Exception e) {
	        // 未安装手Q或安装的版本不支持
	        return false;
	    }
	}

}

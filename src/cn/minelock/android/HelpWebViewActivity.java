package cn.minelock.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

public class HelpWebViewActivity extends Activity {
	private WebView mWebView;
	//private Handler mHandler = new Handler(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_helpwebview);
		
		mWebView = (WebView)findViewById(R.id.helpwebview);
        // 如果访问的页面中有Javascript，则webview必须设置支持Javascript。
		mWebView.getSettings().setJavaScriptEnabled(true);      
/*        mWebView.addJavascriptInterface(new Object() {       
            public void clickOnAndroid() {       
                mHandler.post(new Runnable() {       
                    public void run() {       
                        mWebView.loadUrl("javascript:wave()");       
                    }       
                });       
            }       
        }, "demo");*/       
        mWebView.loadUrl("http://dwz.cn/minelockwx20150123");		
		// 点击链接继续在当前browser中响应
		mWebView.setWebViewClient(new WebViewClient(){       
            public boolean shouldOverrideUrlLoading(WebView view, String url) {       
                view.loadUrl(url);       
                return true;       
            }       
		}); 
		// 返回
		ImageButton back = (ImageButton)findViewById(R.id.helpwebview_return);
		back.setOnClickListener(returnOnClickListener);
	}
	
	// 返回按钮
	private OnClickListener returnOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

			finish();
		}
	};		
}

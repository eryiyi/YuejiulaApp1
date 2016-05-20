package com.liangxun.yuejiula.ui;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/3
 * Time: 20:48
 * 类的功能、说明写在此处.
 */
public class AboutJHActivity extends BaseActivity implements View.OnClickListener {
    private WebView webview_aboutjh;
    private ImageView aboutjh_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutjh_xml);
        webview_aboutjh = (WebView) this.findViewById(R.id.webview_aboutjh);
        //设置WebView属性，能够执行Javascript脚本
        webview_aboutjh.getSettings().setJavaScriptEnabled(true);
        //加载需要显示的网页
        webview_aboutjh.loadUrl("http://www.baidu.com");
        //设置Web视图
        webview_aboutjh.setWebViewClient(new HelloWebViewClient());
        aboutjh_back = (ImageView) this.findViewById(R.id.aboutjh_back);
        aboutjh_back.setOnClickListener(this);
    }
    //Web视图
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aboutjh_back:
                finish();
                break;
        }
    }
}

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
 * Date: 2015/2/6
 * Time: 22:01
 * 类的功能、说明写在此处.
 */
public class FindTvActivity extends BaseActivity implements View.OnClickListener {
    private ImageView notice_detail_menu;
    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_tv);
        initView();
        webview = (WebView) findViewById(R.id.webview);
        //设置WebView属性，能够执行Javascript脚本
        webview.getSettings().setJavaScriptEnabled(true);
        //加载需要显示的网页
        webview.loadUrl("http://www.letv.com/");
        //设置Web视图
        webview.setWebViewClient(new HelloWebViewClient());
    }

    //Web视图
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private void initView() {
        notice_detail_menu = (ImageView) this.findViewById(R.id.notice_detail_menu);
        notice_detail_menu.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.notice_detail_menu:
                finish();
                break;
        }
    }
}

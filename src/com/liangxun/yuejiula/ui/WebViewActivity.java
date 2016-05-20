package com.liangxun.yuejiula.ui;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;

/**
 * Created by Administrator on 2015/5/14.
 */
public class WebViewActivity extends BaseActivity implements View.OnClickListener {
    private WebView detail_webview;
    private ImageView menu;
    private String strurl;

    private static final String APP_CACAHE_DIRNAME = "/webcache";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        strurl = getIntent().getExtras().getString("strurl");
        initView();
        detail_webview.getSettings().setJavaScriptEnabled(true);


//        detail_webview.getSettings().setJavaScriptEnabled(true);
//        detail_webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
//        detail_webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);  //设置 缓存模式
//        // 开启 DOM storage API 功能
//        detail_webview.getSettings().setDomStorageEnabled(true);
//        //开启 database storage API 功能
//        detail_webview.getSettings().setDatabaseEnabled(true);
//        String cacheDirPath = getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME;
////      String cacheDirPath = getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
//        //设置数据库缓存路径
//        detail_webview.getSettings().setDatabasePath(cacheDirPath);
//        //设置  Application Caches 缓存目录
//        detail_webview.getSettings().setAppCachePath(cacheDirPath);
//        //开启 Application Caches 功能
//        detail_webview.getSettings().setAppCacheEnabled(true);

        detail_webview.loadUrl(strurl);
        detail_webview.setWebViewClient(new HelloWebViewClient());

    }

    private void initView() {
        menu = (ImageView) this.findViewById(R.id.menu);
        menu.setOnClickListener(this);
        detail_webview = (WebView) this.findViewById(R.id.detail_webview);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.menu:
                finish();
                break;
        }
    }
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause ()
    {
        detail_webview.reload ();
        super.onPause ();
    }


}

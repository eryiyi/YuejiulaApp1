package com.liangxun.yuejiula.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.util.Constants;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * Created by Administrator on 2015/5/14.
 */
public class WebViewActivity extends BaseActivity implements View.OnClickListener {
    private WebView detail_webview;
    private ImageView menu;
    private String strurl;

    private static final String APP_CACAHE_DIRNAME = "/webcache";


    UMSocialService mController;
    String shareCont = "";//内容
    String shareUrl = "";
    String sharePic = "";//分享图片
    String shareParams = "";
    String appID = "wx198fc23a0fae697a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        strurl = getIntent().getExtras().getString("strurl");
        initView();
        detail_webview.getSettings().setJavaScriptEnabled(true);
        shareUrl = strurl;

        detail_webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        detail_webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);  //设置 缓存模式
        // 开启 DOM storage API 功能
        detail_webview.getSettings().setDomStorageEnabled(true);
        //开启 database storage API 功能
        detail_webview.getSettings().setDatabaseEnabled(true);
        String cacheDirPath = getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME;
//      String cacheDirPath = getCacheDir()getCacheDir.getAbsolutePath()+Constant.APP_DB_DIRNAME;
        //设置数据库缓存路径
        detail_webview.getSettings().setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录
        detail_webview.getSettings().setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
        detail_webview.getSettings().setAppCacheEnabled(true);

        detail_webview.loadUrl(strurl);
        detail_webview.setWebViewClient(new HelloWebViewClient());



    }

    private void initView() {
        menu = (ImageView) this.findViewById(R.id.menu);
        menu.setOnClickListener(this);
        detail_webview = (WebView) this.findViewById(R.id.detail_webview);
        this.findViewById(R.id.close).setOnClickListener(this);
        this.findViewById(R.id.detail_share).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.menu:
                onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN,200));
                break;
            case R.id.close:
                finish();
                break;
            case R.id.detail_share:
                //分享
                shareUrl = detail_webview.getUrl();
                shareCont = "老年大学我们的家";
                sharePic = getGson().fromJson(getSp().getString(Constants.EMPCOVER, ""), String.class) ;
                mController = UMServiceFactory.getUMSocialService(DetailPageAcitvity.class.getName(), RequestType.SOCIAL);
                mController.setShareMedia(new UMImage(this, sharePic));//设置分享图片
                shareParams = "" ;//设置分享链接
                mController.setShareContent(shareCont + "," + shareUrl + shareParams);//设置分享内容


                //新浪微博
                mController.getConfig().setSsoHandler(new SinaSsoHandler());
                //腾讯微博
                mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
                //1.添加QQ空间分享
                QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, "1104297339", "pbodVVpCHwKwm7W9");
                qZoneSsoHandler.setTargetUrl(shareUrl + shareParams);
                qZoneSsoHandler.addToSocialSDK();
                //2.添加QQ好友分享
                UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, "1104297339", "pbodVVpCHwKwm7W9");
                qqSsoHandler.setTargetUrl(shareUrl + shareParams);
                qqSsoHandler.addToSocialSDK();
                // 添加微信平台
                UMWXHandler wxHandler = new UMWXHandler(this, appID);
                wxHandler.addToSocialSDK();
                //支持微信朋友圈
                UMWXHandler wxCircleHandler = new UMWXHandler(this, appID);
                wxCircleHandler.setToCircle(true);
                wxCircleHandler.addToSocialSDK();
                //单独设置微信分享
                WeiXinShareContent xinShareContent = new WeiXinShareContent();
                xinShareContent.setShareContent(shareCont);
                xinShareContent.setTitle(shareCont);
                xinShareContent.setShareImage(new UMImage(this, sharePic));
                xinShareContent.setTargetUrl(shareUrl + shareParams);
                mController.setShareMedia(xinShareContent);
                CircleShareContent circleMedia = new CircleShareContent();
                circleMedia.setShareContent(shareCont);
                circleMedia.setTitle(shareCont);
                circleMedia.setShareImage(new UMImage(this, sharePic));
                circleMedia.setTargetUrl(shareUrl + shareParams);
                mController.setShareMedia(circleMedia);
                mController.openShare(this, false);
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
    // 设置回退
    // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && detail_webview.canGoBack()) {
            detail_webview.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            detail_webview.loadData("", "text/html; charset=UTF-8", null);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode,event);
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

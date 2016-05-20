package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.News;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.popview.DeletePopWindow;
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

import java.util.HashMap;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/1
 * Time: 11:31
 * 类的功能、说明写在此处.
 */
public class DetailNewsActivity extends BaseActivity implements View.OnClickListener {
    private ImageView news_detail_menu;
    private ImageView detail_news_share;
    private TextView part_news_title;
    private WebView partdetail_webview;

    private News news;

    UMSocialService mController;
    String shareCont = "";//内容
    String shareUrl = InternetURL.SHARE_NEWS_URL;
    String shareParams = "";
    String appID = Constants.social_wx_key;
    String sharePic = "";
    private String typeId = "";
    private DeletePopWindow deleteWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail);
        initView();
        typeId = getGson().fromJson(getSp().getString(Constants.EMPTYPE, ""), String.class);
        news = (News) getIntent().getExtras().get(Constants.NEWS);
//        part_news_title.setText(news.getTitle());
        //设置WebView属性，能够执行Javascript脚本
        partdetail_webview.getSettings().setJavaScriptEnabled(true);
        //加载需要显示的网页
        partdetail_webview.loadUrl(InternetURL.GET_NEWS_DETAIL_URL + "?newsId=" + news.getId() + "&publishType=" + news.getPublishType());
        //设置Web视图
        partdetail_webview.setWebViewClient(new HelloWebViewClient());
        shareCont = news.getTitle();
        mController = UMServiceFactory.getUMSocialService(DetailPageAcitvity.class.getName(), RequestType.SOCIAL);
        mController.setShareContent(shareCont);
        sharePic = news.getPic();
        //设置分享图片
        mController.setShareMedia(new UMImage(DetailNewsActivity.this, sharePic));
        shareParams = "?newsId=" + news.getId() + "&publishType= " + news.getPublishType();
        //新浪微博
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        //腾讯微博
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
        //1.添加QQ空间分享
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, Constants.social_qq_id, Constants.social_qq_key);
        qZoneSsoHandler.setTargetUrl(shareUrl + shareParams);
        qZoneSsoHandler.addToSocialSDK();
        //2.添加QQ好友分享
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, Constants.social_qq_id, Constants.social_qq_key);
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
        xinShareContent.setShareImage(new UMImage(DetailNewsActivity.this, sharePic));
        xinShareContent.setTargetUrl(shareUrl + shareParams);
        mController.setShareMedia(xinShareContent);
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(shareCont);
        circleMedia.setTitle(shareCont);
        circleMedia.setShareImage(new UMImage(DetailNewsActivity.this, sharePic));
        circleMedia.setTargetUrl(shareUrl + shareParams);
        mController.setShareMedia(circleMedia);
    }

    private void initView() {
        detail_news_share = (ImageView) this.findViewById(R.id.detail_news_share);
        detail_news_share.setOnClickListener(this);
        news_detail_menu = (ImageView) this.findViewById(R.id.news_detail_menu);
        news_detail_menu.setOnClickListener(this);
        part_news_title = (TextView) this.findViewById(R.id.part_news_title);
        partdetail_webview = (WebView) this.findViewById(R.id.partdetail_webview);
        part_news_title.setOnClickListener(this);

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
            case R.id.news_detail_menu://返回
                finish();
                break;
            case R.id.detail_news_share:
                mController.openShare(this, false);
                break;
            case R.id.part_news_title:
                if (typeId.equals("1")) {
                    showSelectImageDialog();
                }
                break;
        }
    }

    // 选择是否删除
    private void showSelectImageDialog() {
        deleteWindow = new DeletePopWindow(DetailNewsActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(DetailNewsActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //删除方法
    private void delete() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.DELETE_NEWS_UUID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(DetailNewsActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                                //调用广播，刷新主页
                                Intent intent1 = new Intent(Constants.SEND_NEWS_DELETE_SUCCESS);
                                sendBroadcast(intent1);
                                finish();
                            } else {
                                Toast.makeText(DetailNewsActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailNewsActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailNewsActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("newsId", news.getId());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        getRequestQueue().add(request);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_sure:
                    delete();
                    break;
                default:
                    break;
            }
        }

    };
}

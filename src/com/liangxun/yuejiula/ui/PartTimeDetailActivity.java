package com.liangxun.yuejiula.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.FavoursDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.PartTime;
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
 * Date: 2015/2/7
 * Time: 15:52
 * 类的功能、说明写在此处.
 */
public class PartTimeDetailActivity extends BaseActivity implements View.OnClickListener {
    private ImageView part_detail_menu;
    private ImageView detail_parttype_share;//分享按钮
    private WebView partdetail_webview;

    private LinearLayout part_detail_qq;
    private LinearLayout part_detail_tel;
    private LinearLayout detail_part_report;//举报

    private PartTime record;
    private String emp_id = "";//当前登陆者UUID
    private String schoolId = "";
    private String emp_type = "";

    private LinearLayout detail_part_delete;
    private DeletePopWindow deleteWindow;
    UMSocialService mController;
    String shareCont = "";//内容
    String shareUrl = InternetURL.GET_PARTTIME_DETAIL_URL;
    String shareParams = "";
    String appID = "wx198fc23a0fae697a";
    String sharePic = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.part_detail);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        record = (PartTime) getIntent().getExtras().get(Constants.PART_INFO);
        emp_type = getGson().fromJson(getSp().getString(Constants.EMPTYPE, ""), String.class);
        initView();
        //设置WebView属性，能够执行Javascript脚本
        partdetail_webview.getSettings().setJavaScriptEnabled(true);
        //加载需要显示的网页
        partdetail_webview.loadUrl(InternetURL.GET_PARTTIME_DETAIL_URL + "?id=" + record.getId());
        //设置Web视图
        partdetail_webview.setWebViewClient(new HelloWebViewClient());

        shareCont = record.getTitle();
        mController = UMServiceFactory.getUMSocialService(DetailPageAcitvity.class.getName(), RequestType.SOCIAL);
        mController.setShareContent(shareCont);
        sharePic = record.getTypeCover();
        //设置分享图片
        mController.setShareMedia(new UMImage(PartTimeDetailActivity.this, sharePic));
        shareParams = "?id=" + record.getId();
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
        xinShareContent.setShareImage(new UMImage(PartTimeDetailActivity.this, sharePic));
        xinShareContent.setTargetUrl(shareUrl + shareParams);
        mController.setShareMedia(xinShareContent);
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(shareCont);
        circleMedia.setTitle(shareCont);
        circleMedia.setShareImage(new UMImage(PartTimeDetailActivity.this, sharePic));
        circleMedia.setTargetUrl(shareUrl + shareParams);
        mController.setShareMedia(circleMedia);
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
        part_detail_menu = (ImageView) this.findViewById(R.id.part_detail_menu);
        part_detail_menu.setOnClickListener(this);
        partdetail_webview = (WebView) this.findViewById(R.id.partdetail_webview);
        part_detail_qq = (LinearLayout) this.findViewById(R.id.part_detail_qq);
        part_detail_tel = (LinearLayout) this.findViewById(R.id.part_detail_tel);
        part_detail_tel.setOnClickListener(this);
        part_detail_qq.setOnClickListener(this);
        detail_part_report = (LinearLayout) this.findViewById(R.id.detail_part_report);
        detail_part_report.setVisibility(View.GONE);
        detail_part_report.setOnClickListener(this);
        detail_parttype_share = (ImageView) this.findViewById(R.id.detail_parttype_share);
        detail_parttype_share.setOnClickListener(this);
        detail_part_delete = (LinearLayout) this.findViewById(R.id.detail_part_delete);
        detail_part_delete.setVisibility(View.GONE);
        detail_part_delete.setOnClickListener(this);
        if (record != null && (emp_type.equals("1") || emp_type.equals("3") || emp_id.equals(record.getEmpId()))) {
            //如果是管理员  代理商  或者商家自己
            detail_part_delete.setVisibility(View.VISIBLE);
        } else {
            detail_part_report.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.part_detail_menu:
                finish();
                break;
            case R.id.part_detail_qq:
                String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + record.getQq();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                break;
            case R.id.part_detail_tel:
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + record.getMobile()));
                PartTimeDetailActivity.this.startActivity(intent);
                break;
            case R.id.detail_part_report:
                showJubao();
                break;
            case R.id.detail_parttype_share:
                mController.openShare(this, false);
                break;
            case R.id.detail_part_delete:
                showSelectImageDialog();
                break;
        }
    }

    // 举报
    private void showJubao() {
        final Dialog picAddDialog = new Dialog(PartTimeDetailActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(this, R.layout.jubao_dialog, null);
        TextView jubao_sure = (TextView) picAddInflate.findViewById(R.id.jubao_sure);
        final EditText jubao_cont = (EditText) picAddInflate.findViewById(R.id.jubao_cont);
        //举报提交
        jubao_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contreport = jubao_cont.getText().toString();
                if (StringUtil.isNullOrEmpty(contreport)) {
                    Toast.makeText(PartTimeDetailActivity.this, R.string.report_answer, Toast.LENGTH_LONG).show();
                    return;
                }
                report(contreport);
                picAddDialog.dismiss();
            }
        });
        //举报取消
        TextView jubao_cancle = (TextView) picAddInflate.findViewById(R.id.jubao_cancle);
        jubao_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picAddDialog.dismiss();
            }
        });
        picAddDialog.setContentView(picAddInflate);
        picAddDialog.show();
    }

    public void report(final String contReport) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.ADD_REPORT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            FavoursDATA data = getGson().fromJson(s, FavoursDATA.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(PartTimeDetailActivity.this, R.string.report_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PartTimeDetailActivity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PartTimeDetailActivity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(PartTimeDetailActivity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empOne", emp_id);
                params.put("empTwo", record.getEmpId());
                params.put("typeId", Constants.REPORT_TYPE_tWO);
                params.put("cont", contReport);
                params.put("xxid", record.getId());
                params.put("schoolId", schoolId);
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

    // 选择是否删除
    private void showSelectImageDialog() {
        deleteWindow = new DeletePopWindow(PartTimeDetailActivity.this, itemsOnClickOne);
        //显示窗口
        deleteWindow.showAtLocation(PartTimeDetailActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClickOne = new View.OnClickListener() {

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

    //删除方法
    private void delete() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.DELETE_PARTTIME_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(PartTimeDetailActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                                //调用广播，刷新主页
                                Intent intent1 = new Intent(Constants.SEND_PART_SUCCESS);
                                sendBroadcast(intent1);
                                finish();
                            } else {
                                Toast.makeText(PartTimeDetailActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PartTimeDetailActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(PartTimeDetailActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", record.getId());
                params.put("type", "1");
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

}

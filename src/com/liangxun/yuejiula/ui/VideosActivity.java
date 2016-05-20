package com.liangxun.yuejiula.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.ItemVideosAdapter;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.RecordDATA;
import com.liangxun.yuejiula.data.VideosData;
import com.liangxun.yuejiula.entity.VideoPlayer;
import com.liangxun.yuejiula.entity.Videos;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/15.
 */
public class VideosActivity extends BaseActivity implements View.OnClickListener,OnClickContentItemListener {

    private PullToRefreshListView lstv;
    private List<Videos> list = new ArrayList<Videos>();
    private ItemVideosAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private String emp_id = "";//当前登陆者UUID
    private int tmpId;

    UMSocialService mController;
    String shareCont = "";//内容
    String shareUrl = InternetURL.SHARE_VIDEOS;
    String sharePic = "";//分享图片
    String shareParams = "";
    String appID = "wx198fc23a0fae697a";

    private String time_is = "1";
    private String favour_is = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
        setContentView(R.layout.videos_activity);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        initView();
        progressDialog = new ProgressDialog(this );

        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        initData();
    }

    private void initView() {
        lstv = (PullToRefreshListView) this.findViewById(R.id.lstv);
        adapter = new ItemVideosAdapter(list, VideosActivity.this);
        lstv.setMode(PullToRefreshBase.Mode.BOTH);
        lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(VideosActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(VideosActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData();
            }
        });
        lstv.setAdapter(adapter);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Videos tmpVideos = list.get(position - 1);
                Intent detailView = new Intent(VideosActivity.this, DetailVideosActivity.class);
                detailView.putExtra(Constants.INFO, tmpVideos);
                startActivity(detailView);
            }
        });
        adapter.setOnClickContentItemListener(this);
        this.findViewById(R.id.liner_one).setOnClickListener(this);
        this.findViewById(R.id.liner_two).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.liner_one:
                progressDialog = new ProgressDialog(this );

                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                IS_REFRESH = true;
                pageIndex = 1;
                time_is = "1";
                favour_is = "0";
                initData();
                break;
            case R.id.liner_two:
                progressDialog = new ProgressDialog(this );

                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                IS_REFRESH = true;
                pageIndex = 1;
                favour_is = "1";
                time_is = "0";
                initData();
                break;
        }
    }

    Videos tmpVideos;
    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        tmpVideos = list.get(position);
        tmpId = position;
        switch (flag){
            case 1:
                //评论
            {
                Intent comment = new Intent(VideosActivity.this, PublishVideoCommentAcitvity.class);
                comment.putExtra(Constants.FATHER_PERSON_NAME, "");
                comment.putExtra(Constants.FATHER_UUID, "0");
                comment.putExtra(Constants.RECORD_UUID, tmpVideos.getId());
                comment.putExtra("fplempid", "");
                startActivity(comment);
            }
                break;
            case 2:
                //分享
            {
                mController = UMServiceFactory.getUMSocialService(DetailPageAcitvity.class.getName(), RequestType.SOCIAL);
                mController.setShareMedia(new UMImage(this, sharePic));//设置分享图片
                shareParams = "?id=" + tmpVideos.getId();//设置分享链接
                mController.setShareContent(shareCont + "," + shareUrl + shareParams);//设置分享内容

//        //新浪微博
                mController.getConfig().setSsoHandler(new SinaSsoHandler());
//        //腾讯微博
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

                UMVideo umVideo = new UMVideo(tmpVideos.getVideoUrl());
                umVideo.setMediaUrl(tmpVideos.getVideoUrl());
                umVideo.setThumb(sharePic);
                umVideo.setTitle(tmpVideos.getTitle());
                umVideo.setTargetUrl(shareUrl + shareParams);
                mController.setShareMedia(umVideo);
                //打开分享面板
                mController.openShare(this, false);
            }
                break;
            case 3:
                //赞
            {
                progressDialog = new ProgressDialog(this );

                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                zan_click(tmpVideos);
            }
                break;
            case 4:
                //播放
                String videoUrl = tmpVideos.getVideoUrl();
                Intent intent = new Intent(VideosActivity.this, VideoPlayerActivity2.class);
                VideoPlayer video = new VideoPlayer(videoUrl);
                intent.putExtra(Constants.EXTRA_LAYOUT, "0");
                intent.putExtra(VideoPlayer.class.getName(), video);
                startActivity(intent);
                break;
        }
    }


    //赞
    private void zan_click(final Videos record) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.PUBLISH_VIDEO_FAVOUR_RECORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            RecordDATA data = getGson().fromJson(s, RecordDATA.class);
                            if (data.getCode() == 200) {
                                //赞+1
                                Toast.makeText(VideosActivity.this, R.string.zan_success, Toast.LENGTH_SHORT).show();
                                list.get(tmpId).setZanNum(String.valueOf(Integer.parseInt((list.get(tmpId).getZanNum() == null ? "0" : list.get(tmpId).getZanNum())) + 1));
                                adapter.notifyDataSetChanged();
                            }
                            if (data.getCode() == 1) {
                                Toast.makeText(VideosActivity.this, R.string.zan_error_one, Toast.LENGTH_SHORT).show();

                            }
                            if (data.getCode() == 2) {
                                Toast.makeText(VideosActivity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(VideosActivity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();

                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(VideosActivity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordId", record.getId());
                params.put("empId", emp_id);
                params.put("sendEmpId", "");
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


    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_VIDEOS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            VideosData data = getGson().fromJson(s, VideosData.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    list.clear();
                                }
                                list.addAll(data.getData());
                                lstv.onRefreshComplete();
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(VideosActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(VideosActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(VideosActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex));
                params.put("time_is", time_is);
                params.put("favour_is", favour_is);
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

    public void back(View view){
        finish();
    }

    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.SEND_COMMENT_SUCCESS_VIDEO)) {
                //刷新内容
                list.get(tmpId).setPlNum(String.valueOf((Integer.parseInt(list.get(tmpId).getPlNum() == null ? "0" : list.get(tmpId).getPlNum()) + 1)));//评论加1
                adapter.notifyDataSetChanged();
            }
        }
    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SEND_COMMENT_SUCCESS_VIDEO);//评论成功，刷新评论列表
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

}

package com.liangxun.yuejiula.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.AnimateFirstDisplayListener;
import com.liangxun.yuejiula.adapter.DetailCommentAdapter;
import com.liangxun.yuejiula.adapter.DetailFavourAdapter;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.CommentContentDATA;
import com.liangxun.yuejiula.data.FavoursDATA;
import com.liangxun.yuejiula.data.RecordDATA;
import com.liangxun.yuejiula.entity.CommentContent;
import com.liangxun.yuejiula.entity.Favour;
import com.liangxun.yuejiula.entity.VideoPlayer;
import com.liangxun.yuejiula.entity.Videos;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.ContentListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
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
import com.yixia.camera.demo.UniversityApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/15.
 */
public class DetailVideosActivity extends BaseActivity implements View.OnClickListener, ContentListView.OnRefreshListener,
        ContentListView.OnLoadListener, OnClickContentItemListener {
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    List<CommentContent> commentContents;
    private Videos record;//传参
    private ContentListView detail_lstv;
    private ImageView detail_back;//返回按钮
    private ImageView detail_share;//分享按钮
    private LinearLayout commentLayout;//头部

    private LinearLayout detail_like_liner;//赞区域
    private LinearLayout detail_comment_liner;//评论区域

    private TextView title;
    private TextView content;
    private ImageView picone;
    private ImageView picplay;

    private PullToRefreshListView detail_comment_lstv;//评论列表
    private DetailCommentAdapter adapter;
    private int pageIndex = 1;

    private GridView gridView;
    private List<Favour> itemList = new ArrayList<Favour>();
    private DetailFavourAdapter adaptertwo;

    private List<Favour> itemListtwo = new ArrayList<Favour>();//赞列表用

    private RelativeLayout detail_like_liner_layout;//赞区域
    private String emp_id = "";//当前登陆者UUID

    UMSocialService mController;
    String shareCont = "";//内容
    String shareUrl = InternetURL.SHARE_VIDEOS;
    String sharePic = "";//分享图片
    String shareParams = "";
    String appID = "wx198fc23a0fae697a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_video_page);
        registerBoradcastReceiver();
        record = (Videos) getIntent().getExtras().get(Constants.INFO);//传递过来的值
        commentContents = new ArrayList<CommentContent>();
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        initView();
        initData();

        mController = UMServiceFactory.getUMSocialService(DetailPageAcitvity.class.getName(), RequestType.SOCIAL);
        mController.setShareMedia(new UMImage(this, sharePic));//设置分享图片
        shareParams = "?id=" + record.getId();//设置分享链接
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

        UMVideo umVideo = new UMVideo(record.getVideoUrl());
        umVideo.setMediaUrl(record.getVideoUrl());
        umVideo.setThumb(sharePic);
        umVideo.setTitle(record.getTitle());
        umVideo.setTargetUrl(shareUrl + shareParams);
        mController.setShareMedia(umVideo);

    }

    private void initView() {
        commentLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.detail_video_header, null);
        detail_back = (ImageView) this.findViewById(R.id.detail_back);
        detail_back.setOnClickListener(this);
        detail_share = (ImageView) this.findViewById(R.id.detail_share);
        detail_share.setOnClickListener(this);
        detail_lstv = (ContentListView) this.findViewById(R.id.detail_lstv);

        detail_like_liner = (LinearLayout) this.findViewById(R.id.detail_like_liner);
        detail_comment_liner = (LinearLayout) this.findViewById(R.id.detail_comment_liner);
        detail_comment_liner.setOnClickListener(this);
        detail_like_liner.setOnClickListener(this);

        title = (TextView) commentLayout.findViewById(R.id.title);
        content = (TextView) commentLayout.findViewById(R.id.content);
        picone = (ImageView) commentLayout.findViewById(R.id.picone);
        picplay = (ImageView) commentLayout.findViewById(R.id.picplay);
        picplay.setOnClickListener(this);
        adapter = new DetailCommentAdapter(this, commentContents);
        adapter.setOnClickContentItemListener(this);
        detail_lstv.addHeaderView(commentLayout);//添加头部
        detail_lstv.setAdapter(adapter);
        detail_lstv.setOnRefreshListener(this);
        detail_lstv.setOnLoadListener(this);

        detail_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommentContent commentContent = (CommentContent) parent.getAdapter().getItem(position);
                if (commentContent != null) {
                    Intent comment = new Intent(DetailVideosActivity.this, PublishVideoCommentAcitvity.class);
                    comment.putExtra(Constants.FATHER_PERSON_NAME, commentContent.getNickName());
                    comment.putExtra(Constants.FATHER_UUID, commentContent.getId());
                    comment.putExtra(Constants.RECORD_UUID, record.getId());
                    comment.putExtra(Constants.FATHER_PERSON_UUID, "");
                    comment.putExtra("fplempid", commentContent.getEmpId());
                    startActivity(comment);
                }
            }
        });

        detail_like_liner_layout = (RelativeLayout) commentLayout.findViewById(R.id.detail_like_liner_layout);
        detail_like_liner_layout.setVisibility(View.GONE);

        gridView = (GridView) commentLayout.findViewById(R.id.gridView);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    private void initData() {
        //视频
        imageLoader.displayImage(record.getPicUrl(), picone, UniversityApplication.videofailed, animateFirstListener);
        title.setText(record.getTitle() == null ? "" : record.getTitle());
        content.setText(record.getContent()==null?"":record.getContent());
        loadData(ContentListView.REFRESH);
        getFavour();
        if(!StringUtil.isNullOrEmpty(record.getTitle())){
            shareCont = record.getTitle();
        }else {
            shareCont = "我在，你在哪里，快到童心堂来！";
        }
        if (!StringUtil.isNullOrEmpty(record.getPicUrl())) {
            sharePic = record.getPicUrl();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_back://返回按钮
                finish();
                break;
            case R.id.detail_share://分享按钮
                mController.openShare(this, false);
                break;
            case R.id.detail_comment_liner://评论
                Intent comment = new Intent(DetailVideosActivity.this, PublishVideoCommentAcitvity.class);
                comment.putExtra(Constants.FATHER_PERSON_NAME, "");
                comment.putExtra(Constants.FATHER_UUID, "0");
                comment.putExtra(Constants.RECORD_UUID, record.getId());
                comment.putExtra("fplempid", "");
                startActivity(comment);
                break;
            case R.id.detail_like_liner://点赞
                progressDialog = new ProgressDialog(this );
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                detail_like_liner.setClickable(false);
                zan_click(record);
                break;

            case R.id.picplay://视频播放按钮
                String videoUrl = record.getVideoUrl();
                Intent intent = new Intent(DetailVideosActivity.this, VideoPlayerActivity2.class);
                VideoPlayer video = new VideoPlayer(videoUrl);
                intent.putExtra(Constants.EXTRA_LAYOUT, "0");
                intent.putExtra(VideoPlayer.class.getName(), video);
                startActivity(intent);
                break;

        }
    }

    private void loadData(final int currentid) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.GET_VIDEOS_PL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        detail_lstv.onRefreshComplete();
                        detail_lstv.onLoadComplete();
                        if (StringUtil.isJson(s)) {
                            CommentContentDATA data = getGson().fromJson(s, CommentContentDATA.class);
                            if (data.getCode() == 200) {
                                if (ContentListView.REFRESH == currentid) {
                                    commentContents.clear();
                                    commentContents.addAll(data.getData());
                                    detail_lstv.setResultSize(data.getData().size());
                                    adapter.notifyDataSetChanged();
                                }
                                if (ContentListView.LOAD == currentid) {
                                    commentContents.addAll(data.getData());
                                    detail_lstv.setResultSize(data.getData().size());
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(DetailVideosActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailVideosActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        detail_lstv.onRefreshComplete();
                        detail_lstv.onLoadComplete();
                        Toast.makeText(DetailVideosActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordId", record.getId());
                params.put("page", String.valueOf(pageIndex));
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


    private void getFavour() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.GET_VIDEOS_FAVOUR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            FavoursDATA data = getGson().fromJson(s, FavoursDATA.class);
                            if (data.getCode() == 200) {
                                itemListtwo.clear();
                                itemListtwo = data.getData();
                                itemList.clear();
                                if (itemListtwo.size() > 5) {
                                    for (int i = 0; i < 6; i++) {
                                        itemList.add(itemListtwo.get(i));
                                    }
                                } else {
                                    itemList.addAll(itemListtwo);
                                }
                                if (itemList.size() > 0) {//当存在赞数据的时候
                                    detail_like_liner_layout.setVisibility(View.VISIBLE);
                                }

                                adaptertwo = new DetailFavourAdapter(itemList, DetailVideosActivity.this , itemListtwo.size());
                                gridView.setAdapter(adaptertwo);
                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        if (position == (itemList.size() - 1)) {
//                                            Intent favour = new Intent(DetailVideosActivity.this, DetailFavourActivity.class);
//                                            favour.putExtra(Constants.RECORD_UUID, record.getRecordId());
//                                            startActivity(favour);
                                        }
                                    }
                                });
                                if (itemList.size() > 0) {//当存在赞数据的时候
                                    detail_like_liner_layout.setVisibility(View.VISIBLE);
                                }
                                adaptertwo.notifyDataSetChanged();
                            } else {
                                Toast.makeText(DetailVideosActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailVideosActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailVideosActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", record.getId());
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

    /**
     * 加载数据监听实现
     */
    @Override
    public void onLoad() {
        pageIndex++;
        loadData(ContentListView.LOAD);
    }

    /**
     * 刷新数据监听实现
     */
    @Override
    public void onRefresh() {
        pageIndex = 1;
        loadData(ContentListView.REFRESH);
    }


    CommentContent comt;
    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        comt = commentContents.get(position);
        switch (flag) {
            case 1:
                if (!emp_id.equals(comt.getEmpId())) {
                    Intent mine = new Intent(DetailVideosActivity.this, ProfilePersonalActivity.class);
                    mine.putExtra(Constants.EMPID, comt.getEmpId());
                    startActivity(mine);
                } else {
                    Intent mine = new Intent(DetailVideosActivity.this, UpdateProfilePersonalActivity.class);
                    startActivity(mine);
                }
                break;
        }
    }

    //赞
    private void zan_click(final Videos record) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.PUBLISH_VIDEO_FAVOUR_RECORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            RecordDATA data = getGson().fromJson(s, RecordDATA.class);
                            if (data.getCode() == 200) {
                                //赞+1
                                Toast.makeText(DetailVideosActivity.this, R.string.zan_success, Toast.LENGTH_SHORT).show();
                                //调用广播，刷新详细页评论
                                Intent intent1 = new Intent(Constants.SEND_FAVOUR_SUCCESS_VIDEO);
                                sendBroadcast(intent1);
                                detail_like_liner.setClickable(true);
                            }
                            if (data.getCode() == 1) {
                                Toast.makeText(DetailVideosActivity.this, R.string.zan_error_one, Toast.LENGTH_SHORT).show();
                                detail_like_liner.setClickable(true);
                            }
                            if (data.getCode() == 2) {
                                Toast.makeText(DetailVideosActivity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();
                                detail_like_liner.setClickable(true);
                            }
                        } else {
                            Toast.makeText(DetailVideosActivity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();
                            detail_like_liner.setClickable(true);
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailVideosActivity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        detail_like_liner.setClickable(true);
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


    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.SEND_COMMENT_SUCCESS_VIDEO)) {
                //刷新内容
                pageIndex = 1;
                loadData(ContentListView.REFRESH);
            }
            if (action.equals(Constants.SEND_FAVOUR_SUCCESS_VIDEO)) {
                getFavour();
            }
        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SEND_COMMENT_SUCCESS_VIDEO);//评论成功，刷新评论列表
        myIntentFilter.addAction(Constants.SEND_FAVOUR_SUCCESS_VIDEO);//点赞成功，刷新赞列表
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }


}

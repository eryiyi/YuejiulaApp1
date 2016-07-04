package com.liangxun.yuejiula.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.MainActivity;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.*;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.*;
import com.liangxun.yuejiula.entity.*;
import com.liangxun.yuejiula.face.FaceConversionUtil;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.PicUtil;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.ContentListView;
import com.liangxun.yuejiula.widget.PictureGridview;
import com.liangxun.yuejiula.widget.popview.DeletePopWindow;
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
 * author: ${zhanghailong}
 * Date: 2015/1/31
 * Time: 14:36
 * 类的功能、说明写在此处.
 */
public class DetailPageAcitvity extends BaseActivity implements View.OnClickListener, ContentListView.OnRefreshListener,
        ContentListView.OnLoadListener, OnClickContentItemListener {
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    List<CommentContent> commentContents;

    private Record record;//传参
    private ContentListView detail_lstv;
    private ImageView detail_back;//返回按钮
    private ImageView detail_share;//分享按钮
    private TextView detail_title;//标题
    private LinearLayout detail_like_liner;//赞区域
    private LinearLayout detail_comment_liner;//评论区域
    private LinearLayout detail_report_liner;//分享区域
    private LinearLayout detail_delete_liner;//删除区域

    private LinearLayout commentLayout;//头部
    private ImageView detail_photo;//头像
    private TextView detail_nickname;//昵称
    private TextView detail_time;//时间
    private TextView home_item_school;//学校
    private TextView detail_content;//详细内容
    private PictureGridview detail_grideview;//grideview
    private ImageView detail_video_pic;//视频图片
    private ImageView detail_player_icon_video;//视频播放按钮
    private ImageView picone;//单一图片的时候使用
    private TextView money;//价格

    private ImageView detail_ad_image;//广告图片
    private TextView pl_text;//评论数量
    private PullToRefreshListView detail_comment_lstv;//评论列表
    private DetailCommentAdapter adapter;
    private int pageIndex = 1;

    private GridView gridView;
    private List<Favour> itemList = new ArrayList<Favour>();
    private DetailFavourAdapter adaptertwo;

    private List<Favour> itemListtwo = new ArrayList<Favour>();//赞列表用

    private RelativeLayout detail_like_liner_layout;//赞区域
    private String schoolId = "";
    private String emp_type = "";
    private String emp_id = "";//当前登陆者UUID

    private Ad ad;

    UMSocialService mController;
    String shareCont = "";//内容
    String shareUrl = InternetURL.SHARE_RECORD;
    String sharePic = "";//分享图片
    String shareParams = "";
    String appID = "wx198fc23a0fae697a";
    private DeletePopWindow deleteWindow;

    private TextView text_comment;

    private LinearLayout liner_jp;
    private TextView detail_jp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.detail_page);
        registerBoradcastReceiver();
        record = (Record) getIntent().getExtras().get(Constants.INFO);//传递过来的值
        commentContents = new ArrayList<CommentContent>();
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        emp_type = getGson().fromJson(getSp().getString(Constants.EMPTYPE, ""), String.class);
        initView();
        initData();

        mController = UMServiceFactory.getUMSocialService(DetailPageAcitvity.class.getName(), RequestType.SOCIAL);
        mController.setShareMedia(new UMImage(this, sharePic));//设置分享图片
        shareParams = "?recordId=" + record.getRecordId();//设置分享链接
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

        //视频的分享设置
        if (record.getRecordType().equals("2")) {
            UMVideo umVideo = new UMVideo(record.getRecordVideo());
            umVideo.setMediaUrl(record.getRecordVideo());
            umVideo.setThumb(sharePic);
            umVideo.setTitle(record.getRecordCont());
            umVideo.setTargetUrl(shareUrl + shareParams);
            mController.setShareMedia(umVideo);
        }

        detail_grideview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    private void initView() {
        commentLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.detail_header, null);
        liner_jp = (LinearLayout) commentLayout.findViewById(R.id.liner_jp);
        detail_jp = (TextView) commentLayout.findViewById(R.id.detail_jp);
        detail_jp.setOnClickListener(this);
        detail_back = (ImageView) this.findViewById(R.id.detail_back);
        text_comment = (TextView) this.findViewById(R.id.text_comment);
        detail_back.setOnClickListener(this);
        detail_share = (ImageView) this.findViewById(R.id.detail_share);
        detail_share.setOnClickListener(this);
        detail_title = (TextView) this.findViewById(R.id.detail_title);
        detail_lstv = (ContentListView) this.findViewById(R.id.detail_lstv);
        detail_like_liner = (LinearLayout) this.findViewById(R.id.detail_like_liner);
        detail_comment_liner = (LinearLayout) this.findViewById(R.id.detail_comment_liner);
        detail_report_liner = (LinearLayout) this.findViewById(R.id.detail_report_liner);
        detail_delete_liner = (LinearLayout) this.findViewById(R.id.detail_delete_liner);
        detail_report_liner.setOnClickListener(this);
        detail_comment_liner.setOnClickListener(this);
        detail_like_liner.setOnClickListener(this);
        detail_delete_liner.setOnClickListener(this);

        boolean flagMine = false;
        if(MainActivity.contractSchools != null){
            for(ContractSchool contractSchool:MainActivity.contractSchools){
                if(record.getRecordSchoolId().equals(contractSchool.getSchoolId())){
                    //如果包括
                    flagMine = true;
                    break;
                }
            }
        }
        if (emp_type.equals("1") || flagMine) {
            //是管理员 或者是这个学校的承包商
            detail_delete_liner.setVisibility(View.VISIBLE);
            detail_report_liner.setVisibility(View.GONE);
        }

        detail_photo = (ImageView) commentLayout.findViewById(R.id.detail_photo);
        detail_photo.setOnClickListener(this);
        detail_nickname = (TextView) commentLayout.findViewById(R.id.detail_nickname);
        detail_nickname.setOnClickListener(this);
        detail_time = (TextView) commentLayout.findViewById(R.id.detail_time);
        home_item_school= (TextView) commentLayout.findViewById(R.id.home_item_school);
        detail_content = (TextView) commentLayout.findViewById(R.id.detail_content);
        detail_content.setVisibility(View.GONE);
        detail_grideview = (PictureGridview) commentLayout.findViewById(R.id.gridview_detail_picture);
        detail_video_pic = (ImageView) commentLayout.findViewById(R.id.detail_video_pic);
        detail_player_icon_video = (ImageView) commentLayout.findViewById(R.id.detail_player_icon_video);
        picone = (ImageView) commentLayout.findViewById(R.id.picone);
        detail_player_icon_video.setOnClickListener(this);
        money = (TextView) commentLayout.findViewById(R.id.money);
//        commentLayoutfoot = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.detail_foot, null);
        detail_ad_image = (ImageView) commentLayout.findViewById(R.id.detail_ad_image);
        detail_ad_image.setOnClickListener(this);

        pl_text = (TextView) commentLayout.findViewById(R.id.pl_text);
//        detail_comment_lstv = (PullToRefreshListView) commentLayout.findViewById(R.id.detail_comment_lstv);

        detail_grideview.setVisibility(View.GONE);
        detail_video_pic.setVisibility(View.GONE);
        picone.setVisibility(View.GONE);
        detail_player_icon_video.setVisibility(View.GONE);

        adapter = new DetailCommentAdapter(this, commentContents);
        adapter.setOnClickContentItemListener(this);
        detail_lstv.addHeaderView(commentLayout);//添加头部
//        detail_lstv.addFooterView(commentLayoutfoot);
        detail_lstv.setAdapter(adapter);
        detail_lstv.setOnRefreshListener(this);
        detail_lstv.setOnLoadListener(this);
//        detail_lstv.setLoadEnable(true);
        detail_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommentContent commentContent = (CommentContent) parent.getAdapter().getItem(position);
                if (commentContent != null) {
                    Intent comment = new Intent(DetailPageAcitvity.this, PublishCommentAcitvity.class);
                    comment.putExtra(Constants.FATHER_PERSON_NAME, commentContent.getNickName());
                    comment.putExtra(Constants.FATHER_UUID, commentContent.getId());
                    comment.putExtra(Constants.RECORD_UUID, record.getRecordId());
                    comment.putExtra(Constants.FATHER_PERSON_UUID, record.getRecordEmpId());
                    comment.putExtra("fplempid", commentContent.getEmpId());
                    startActivity(comment);
                }
            }
        });


        detail_like_liner_layout = (RelativeLayout) commentLayout.findViewById(R.id.detail_like_liner_layout);
        detail_like_liner_layout.setVisibility(View.GONE);

        gridView = (GridView) commentLayout.findViewById(R.id.gridView);


    }

    private void initData() {
        imageLoader.displayImage(record.getEmpCover(), detail_photo, UniversityApplication.txOptions, animateFirstListener);
        detail_nickname.setText(record.getEmpName());
        detail_time.setText(record.getDateLine());
        home_item_school.setText(record.getSchoolName());
        String urlStr = "  >>网页链接";
        if (!StringUtil.isNullOrEmpty(record.getRecordCont())) {
            detail_content.setVisibility(View.VISIBLE);
            String strcont = record.getRecordCont();//内容
            int textsize = (int) detail_content.getTextSize();
            textsize = StringUtil.dp2px(getBaseContext(), textsize+25);
            if (strcont.contains("http")) {
                //如果包含http
                String strhttp = strcont.substring(strcont.indexOf("http"), strcont.length());
                strcont = strcont.replaceAll(strhttp, "");
                detail_content.setText(FaceConversionUtil.getInstace().getExpressionString(DetailPageAcitvity.this, strcont + urlStr,textsize));
                detail_content.setOnClickListener(this);
            }else {
                detail_content.setText(FaceConversionUtil.getInstace().getExpressionString(DetailPageAcitvity.this, strcont,textsize));
            }
//            detail_content.setText(FaceConversionUtil.getInstace().getExpressionString(DetailPageAcitvity.this, record.getRecordCont()));
        }
        pl_text.setText("评论  " + record.getPlNum());
        //说说并且图片要有
        if (record.getRecordType().equals("0") && !StringUtil.isNullOrEmpty(record.getRecordPicUrl())) {
            final String[] pics;
            if (record.getRecordPicUrl().contains(",")) {
                //对账图片
                pics = record.getRecordPicUrl().split(",");
                if(pics != null && pics.length >1){
                    detail_grideview.setAdapter(new ImageGridViewAdapter(pics, this));
                    detail_grideview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            startImageActivity(pics, position);
                        }
                    });
                    detail_grideview.setVisibility(View.VISIBLE);
                }else{
                    picone.setVisibility(View.VISIBLE);
                    imageLoader.displayImage(pics[0], picone, UniversityApplication.options, animateFirstListener);
                }

            } else {
                //单一图片
                pics = new String[]{record.getRecordPicUrl()};
                picone.setVisibility(View.VISIBLE);
                imageLoader.displayImage(pics[0], picone, UniversityApplication.options, animateFirstListener);
            }

            picone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startImageActivity(pics, 0);
                }
            });

        }
        //视频
        if (record.getRecordType().equals("2")) {
            detail_video_pic.setVisibility(View.VISIBLE);
            detail_player_icon_video.setVisibility(View.VISIBLE);
            imageLoader.displayImage(record.getRecordPicUrl(), detail_video_pic, UniversityApplication.videofailed, animateFirstListener);
        }
        //广告
        getSmallAd();
        loadData(ContentListView.REFRESH);
        getFavour();
        if(!StringUtil.isNullOrEmpty(record.getRecordCont())){
            shareCont = record.getRecordCont();
        }else {
            shareCont = "我在，你在哪里，快到童心堂来！";
        }
        if (!StringUtil.isNullOrEmpty(record.getRecordPicUrl())) {
            if (record.getRecordPicUrl().contains(",")) {
                sharePic = record.getRecordPicUrl().substring(0, record.getRecordPicUrl().indexOf(","));
            } else {
                sharePic = record.getRecordPicUrl();
            }
        }else{
            sharePic = record.getEmpCover();
        }
        if("1".equals(record.getIs_paimai())){
            //说明是拍卖
            liner_jp.setVisibility(View.VISIBLE);
            money.setText("竞拍价："+record.getMoney());
            text_comment.setText("出价");
        }else {
            liner_jp.setVisibility(View.GONE);
            text_comment.setText("评论");
        }
    }

    private void startImageActivity(String[] urls, int position) {
        if (!PicUtil.hasSDCard()) {
            Toast.makeText(this, R.string.sd_card_is_in, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(DetailPageAcitvity.this, GalleryUrlActivity.class);
        intent.putExtra(Constants.IMAGE_URLS, urls);
        intent.putExtra(Constants.IMAGE_POSITION, position);
        DetailPageAcitvity.this.startActivity(intent);
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
            case R.id.detail_report_liner://举报
                showJubao();
                break;
            case R.id.detail_comment_liner:
                if("1".equals(record.getIs_paimai())){
                    //出价
                    Intent recordV = new Intent(DetailPageAcitvity.this, AddRecordJpActivity.class);
                    recordV.putExtra("record", record);
                    startActivity(recordV);
                }else {
                    //评论
                    Intent comment = new Intent(DetailPageAcitvity.this, PublishCommentAcitvity.class);
                    comment.putExtra(Constants.FATHER_PERSON_NAME, "");
                    comment.putExtra(Constants.FATHER_UUID, "0");
                    comment.putExtra(Constants.RECORD_UUID, record.getRecordId());
                    comment.putExtra(Constants.FATHER_PERSON_UUID, record.getRecordEmpId());
                    comment.putExtra("fplempid", "");
                    startActivity(comment);
                }

                break;
            case R.id.detail_like_liner://点赞
                progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                detail_like_liner.setClickable(false);
                zan_click(record);
                break;
            case R.id.detail_photo://头像
                if (!emp_id.equals(record.getRecordEmpId())) {
                    Intent profile = new Intent(DetailPageAcitvity.this, ProfilePersonalActivity.class);
                    profile.putExtra(Constants.EMPID, record.getRecordEmpId());
                    startActivity(profile);
                } else {
                    Intent profile = new Intent(DetailPageAcitvity.this, UpdateProfilePersonalActivity.class);
                    startActivity(profile);
                }
                break;
            case R.id.detail_nickname://昵称
                if (!emp_id.equals(record.getRecordEmpId())) {
                    Intent profile = new Intent(DetailPageAcitvity.this, ProfilePersonalActivity.class);
                    profile.putExtra(Constants.EMPID, record.getRecordEmpId());
                    startActivity(profile);
                } else {
                    Intent profile = new Intent(DetailPageAcitvity.this, UpdateProfilePersonalActivity.class);
                    startActivity(profile);
                }
                break;
            case R.id.detail_player_icon_video://视频播放按钮
                String videoUrl = record.getRecordVideo();
                Intent intent = new Intent(DetailPageAcitvity.this, VideoPlayerActivity2.class);
                VideoPlayer video = new VideoPlayer(videoUrl);
                intent.putExtra(Constants.EXTRA_LAYOUT, "0");
                intent.putExtra(VideoPlayer.class.getName(), video);
                startActivity(intent);

                break;
            case R.id.detail_ad_image://广告图片
                if (ad != null) {
                    Intent webView = new Intent(DetailPageAcitvity.this, WebViewActivity.class);
                    webView.putExtra("strurl", ad.getAdUrl());
                    startActivity(webView);
                }
                break;
            case R.id.detail_delete_liner:
                //删除
                showSelectImageDialog();
                break;
            case R.id.detail_content:
            {
                String strcont = record.getRecordCont();//内容
                if (strcont.contains("http")){
                    //如果包含http
                    String strhttp = strcont.substring(strcont.indexOf("http"), strcont.length());
                    Intent webView = new Intent(DetailPageAcitvity.this, WebViewActivity.class);
                    webView.putExtra("strurl", strhttp);
                    startActivity(webView);
                }
            }
                break;
            case R.id.detail_jp:
                Intent jpV = new Intent(DetailPageAcitvity.this, DetailJingpaiActivity.class);
                jpV.putExtra("record", record);
                startActivity(jpV);
                break;

        }
    }

    private void loadData(final int currentid) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_DETAIL_PL_URL,
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
                                Toast.makeText(DetailPageAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailPageAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        detail_lstv.onRefreshComplete();
                        detail_lstv.onLoadComplete();
                        Toast.makeText(DetailPageAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordId", record.getRecordId());
                params.put("page", String.valueOf(pageIndex));
                params.put("pageSize", "5");
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

    private void getSmallAd() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_SMALLAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            AdDATA data = getGson().fromJson(s, AdDATA.class);
                            if (data.getCode() == 200) {
                                ad = data.getData();
                                if (ad != null && !StringUtil.isNullOrEmpty(ad.getAdPic())) {
                                    detail_ad_image.setVisibility(View.VISIBLE);
                                    imageLoader.displayImage(ad.getAdPic(), detail_ad_image, UniversityApplication.adOptions, animateFirstListener);
                                } else {
                                    detail_ad_image.setVisibility(View.GONE);
                                }

                            } else {
                                Toast.makeText(DetailPageAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailPageAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailPageAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
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

    private void getFavour() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_FAVOUR_URL,
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

                                adaptertwo = new DetailFavourAdapter(itemList, DetailPageAcitvity.this , itemListtwo.size());
                                gridView.setAdapter(adaptertwo);
                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                            Intent favour = new Intent(DetailPageAcitvity.this, DetailFavourActivity.class);
                                            favour.putExtra(Constants.RECORD_UUID, record.getRecordId());
                                            startActivity(favour);

                                    }
                                });
                                if (itemList.size() > 0) {//当存在赞数据的时候
                                    detail_like_liner_layout.setVisibility(View.VISIBLE);
                                }
                                adaptertwo.notifyDataSetChanged();
                            } else {
                                Toast.makeText(DetailPageAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailPageAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailPageAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordId", record.getRecordId());
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
                    Intent mine = new Intent(DetailPageAcitvity.this, ProfilePersonalActivity.class);
                    mine.putExtra(Constants.EMPID, comt.getEmpId());
                    startActivity(mine);
                } else {
                    Intent mine = new Intent(DetailPageAcitvity.this, UpdateProfilePersonalActivity.class);
                    startActivity(mine);
                }
                break;
        }
    }

    //赞
    private void zan_click(final Record record) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.CLICK_LIKE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            RecordDATA data = getGson().fromJson(s, RecordDATA.class);
                            if (data.getCode() == 200) {
                                //赞+1
                                Toast.makeText(DetailPageAcitvity.this, R.string.zan_success, Toast.LENGTH_SHORT).show();
                                //调用广播，刷新详细页评论
                                Intent intent1 = new Intent(Constants.SEND_FAVOUR_SUCCESS);
                                sendBroadcast(intent1);
                            }
                            if (data.getCode() == 1) {
                                Toast.makeText(DetailPageAcitvity.this, R.string.zan_error_one, Toast.LENGTH_SHORT).show();
                                detail_like_liner.setClickable(true);
                            }
                            if (data.getCode() == 2) {
                                Toast.makeText(DetailPageAcitvity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();
                                detail_like_liner.setClickable(true);
                            }
                        } else {
                            Toast.makeText(DetailPageAcitvity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(DetailPageAcitvity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();
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
                params.put("recordId", record.getRecordId());
                params.put("empId", emp_id);
                params.put("sendEmpId", record.getRecordEmpId());
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

    // 举报
    private void showJubao() {
        final Dialog picAddDialog = new Dialog(DetailPageAcitvity.this, R.style.dialog);
        View picAddInflate = View.inflate(this, R.layout.jubao_dialog, null);
        TextView jubao_sure = (TextView) picAddInflate.findViewById(R.id.jubao_sure);
        final EditText jubao_cont = (EditText) picAddInflate.findViewById(R.id.jubao_cont);
        //举报提交
        jubao_sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String contreport = jubao_cont.getText().toString();
                if (StringUtil.isNullOrEmpty(contreport)) {
                    Toast.makeText(DetailPageAcitvity.this, R.string.report_answer, Toast.LENGTH_LONG).show();
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
                                Toast.makeText(DetailPageAcitvity.this, R.string.report_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DetailPageAcitvity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailPageAcitvity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailPageAcitvity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empOne", emp_id);
                params.put("empTwo", record.getRecordEmpId());
                params.put("typeId", Constants.REPORT_TYPE_ZERRO);
                params.put("cont", contReport);
                params.put("xxid", record.getRecordId());
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

    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.SEND_COMMENT_SUCCESS)) {
                //刷新内容
                pageIndex = 1;
                loadData(ContentListView.REFRESH);
            }
            if (action.equals(Constants.SEND_FAVOUR_SUCCESS)) {
                getFavour();
            }
            if (action.equals("record_jp_success")) {
                String money1 =  intent.getExtras().getString("money");
                record.setMoney(String.valueOf(Integer.parseInt(record.getMoney()) + Integer.parseInt(money1)));
                money.setText("竞拍价："+ record.getMoney());
            }
        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SEND_COMMENT_SUCCESS);//评论成功，刷新评论列表
        myIntentFilter.addAction(Constants.SEND_FAVOUR_SUCCESS);//点赞成功，刷新赞列表
        myIntentFilter.addAction("record_jp_success");
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    // 选择是否删除
    private void showSelectImageDialog() {
        deleteWindow = new DeletePopWindow(DetailPageAcitvity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(DetailPageAcitvity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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

    //删除方法
    private void delete() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.DELETE_RECORDS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(DetailPageAcitvity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                                //调用广播，刷新主页
                                Intent intent1 = new Intent(Constants.SEND_DELETE_RECORD_SUCCESS);
                                intent1.putExtra("recordId",record.getRecordId());
                                sendBroadcast(intent1);
                                finish();
                            } else {
                                Toast.makeText(DetailPageAcitvity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailPageAcitvity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailPageAcitvity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordId", record.getRecordId());
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

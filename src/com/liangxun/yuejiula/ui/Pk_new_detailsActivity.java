package com.liangxun.yuejiula.ui;

import android.app.Dialog;
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
import android.view.animation.Animation;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.*;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.*;
import com.liangxun.yuejiula.entity.PKComment;
import com.liangxun.yuejiula.entity.PKWork;
import com.liangxun.yuejiula.entity.PkZan;
import com.liangxun.yuejiula.entity.VideoPlayer;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.PicUtil;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.ContentListView;
import com.liangxun.yuejiula.widget.CustomProgressDialog;
import com.liangxun.yuejiula.widget.popview.DeletePopWindow;
import com.liangxun.yuejiula.widget.popview.IsSurePrizesWindow;
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

public class Pk_new_detailsActivity extends BaseActivity implements View.OnClickListener, ContentListView.OnRefreshListener,
        ContentListView.OnLoadListener, OnClickContentItemListener {
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private int pageIndex=1;//分页
    private PKWork pkwork;//传递过来的参数

    private ImageView detail_pk_back;//返回
    private ImageView detail_pk_share;//分享
    private LinearLayout pk_comment_liner;//评论区域
    private LinearLayout pk_report_liner;//举报区域
    private LinearLayout pk_favour_liner;//投票区域
    private ContentListView pk_new_lstv;//分页列表
    private PkWordAdapter adapter;//适配器
    private List<PKComment> lists = new ArrayList<PKComment>();
    private LinearLayout headView;//头部文件
    private ImageView detail_pk_cover;//头像
    private TextView pk_detail_nickname;//昵称
    private TextView pk_detail_time;//时间
    private TextView pk_detail_content;//内容
    private GridView pk_gridView;//赞列表
    private TextView pk_pl_text;//评论
    private TextView pk_favour_text;//赞
    private List<String> listPic = new ArrayList<String>();//顶部图片集合
    private DetailPkFavourAdapter adaptertwo;
    private List<PkZan> itemListtwo = new ArrayList<PkZan>();//赞列表用
    private List<PkZan> itemList = new ArrayList<PkZan>();
    private RelativeLayout pk_like_liner_layout;//投票列表区域
    private Map<String, Object> mapData = new HashMap<String, Object>();//接收查询作品投票数据
    public static int favourCount;//赞的数量
    private String emp_id;//当前登陆者UUID
    private String schoolId;
    private ImageView pk_delete_img;//删除中间的图片
    private LinearLayout pk_delete_liner;//删除中间的图片
    private DeletePopWindow deleteWindow;
    private IsSurePrizesWindow issureWindow;
    private TextView pk_detail_colleages;//圈子
    private RelativeLayout video_pic;//视频部分
    private ImageView video_pic_url;
    private ImageView video_pic_img;
    private ImageView pk_jubao_img;//举报中间的图片

    UMSocialService mController;
    private Animation operatingAnim;
    String shareCont = "";//内容
    String shareUrl = InternetURL.GET_VIEW_PK;
    String sharePic = "";//分享图片
    String shareParams = "";
    String appID = "wx198fc23a0fae697a";

    private GridView gridview_detail_picture;//图片集合

    private ImageView prizes_issure;//是否冠军 是否领奖
    private ImageView picOne;//单一图片的时候用
    private String championId = "";

    private int tmpFavourId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.pk_new_detail);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        registerBoradcastReceiver();
        pkwork = (PKWork) getIntent().getExtras().get(Constants.PK_WORD_INFO);
        championId = getIntent().getExtras().getString(Constants.PK_CHAMPION_UUID);
        //实例化对象
        initView();
        //获得评论数据列表
//        onRefresh();
        getData(ContentListView.REFRESH);
        //获得投票数据列表
        getFavourData();

        shareCont = pkwork.getTitle();
        sharePic = pkwork.getEmpCover();
        mController = UMServiceFactory.getUMSocialService(DetailPageAcitvity.class.getName(), RequestType.SOCIAL);
        mController.setShareContent(shareCont);//设置分享内容
        mController.setShareMedia(new UMImage(this, sharePic));//设置分享图片
        shareParams = "?zpId=" + pkwork.getId();//设置分享链接
        //视频的分享设置
        if (pkwork.getType().equals("2")) {
            UMVideo umVideo = new UMVideo(pkwork.getVideoUrl());
            umVideo.setMediaUrl(pkwork.getVideoUrl());
            umVideo.setThumb(sharePic);
            umVideo.setTitle(pkwork.getTitle());
            umVideo.setTargetUrl(shareUrl + shareParams);
            mController.setShareMedia(umVideo);
        }
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
    }

    private void initView() {

        detail_pk_back = (ImageView) this.findViewById(R.id.detail_pk_back);
        detail_pk_share = (ImageView) this.findViewById(R.id.detail_pk_share);
        pk_comment_liner = (LinearLayout) this.findViewById(R.id.pk_comment_liner);
        pk_report_liner = (LinearLayout) this.findViewById(R.id.pk_report_liner);
        pk_favour_liner = (LinearLayout) this.findViewById(R.id.pk_favour_liner);
        pk_new_lstv = (ContentListView) this.findViewById(R.id.pk_new_lstv);
        adapter = new PkWordAdapter(lists, Pk_new_detailsActivity.this);
        pk_new_lstv.setAdapter(adapter);
        pk_new_lstv.setOnRefreshListener(this);
        pk_new_lstv.setOnLoadListener(this);
        pk_new_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PKComment commentContent = (PKComment) parent.getAdapter().getItem(position);
                if (commentContent != null) {
                    Intent comment = new Intent(Pk_new_detailsActivity.this, PkCommentActivity.class);
                    comment.putExtra(Constants.FATHER_PERSON_NAME, commentContent.getNickName());
                    comment.putExtra(Constants.FATHER_UUID, commentContent.getId());
                    comment.putExtra(Constants.RECORD_UUID, pkwork.getId());
                    comment.putExtra(Constants.FATHER_PERSON_UUID, pkwork.getEmpId());//评论的谁的作品
                    comment.putExtra("fplempid", commentContent.getEmpId());//父评论者
                    startActivity(comment);
                }
            }
        });

        pk_comment_liner.setOnClickListener(this);
        pk_report_liner.setOnClickListener(this);
        pk_favour_liner.setOnClickListener(this);
        detail_pk_back.setOnClickListener(this);
        detail_pk_share.setOnClickListener(this);
        //head
        headView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.pk_new_detail_header, null);
        detail_pk_cover = (ImageView) headView.findViewById(R.id.detail_pk_cover);
        picOne = (ImageView) headView.findViewById(R.id.picOne);
        picOne.setVisibility(View.GONE);
        detail_pk_cover.setOnClickListener(this);
        pk_detail_nickname = (TextView) headView.findViewById(R.id.pk_detail_nickname);
        pk_detail_time = (TextView) headView.findViewById(R.id.pk_detail_time);
        pk_detail_content = (TextView) headView.findViewById(R.id.pk_detail_content);
        //图片
        gridview_detail_picture = (GridView) headView.findViewById(R.id.gridview_detail_picture);
        gridview_detail_picture.setSelector(new ColorDrawable(Color.TRANSPARENT));

        //喜欢赞区域
        pk_gridView = (GridView) headView.findViewById(R.id.pk_gridView);
        pk_gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));

        picOne.setOnClickListener(this);
        pk_like_liner_layout = (RelativeLayout) headView.findViewById(R.id.pk_like_liner_layout);
        pk_pl_text = (TextView) headView.findViewById(R.id.pk_pl_text);
        pk_favour_text = (TextView) headView.findViewById(R.id.pk_favour_text);
        pk_detail_colleages = (TextView) headView.findViewById(R.id.pk_detail_colleage);
        prizes_issure = (ImageView) headView.findViewById(R.id.prizes_issure);
        prizes_issure.setVisibility(View.GONE);
        prizes_issure.setOnClickListener(this);

        //赋值
        imageLoader.displayImage(pkwork.getEmpCover(), detail_pk_cover, UniversityApplication.txOptions, animateFirstListener);
        pk_detail_nickname.setText(pkwork.getEmpName());
        pk_detail_time.setText(pkwork.getDateline());
        pk_detail_content.setText(pkwork.getTitle()==null?"":pkwork.getTitle());
        pk_pl_text.setText(pkwork.getPlNum()==null?"0":pkwork.getPlNum());
        pk_favour_text.setText(pkwork.getZanNum()==null?"0":pkwork.getZanNum());
        pk_detail_colleages.setText(pkwork.getSchoolName()==null?"":pkwork.getSchoolName());

        if (pkwork.getType().equals("0")) {
            //文字
        }
        if (pkwork.getType().equals("1")) {
            final String[] pics;
            if (pkwork.getPicUrl().contains(",")) {
                pics = pkwork.getPicUrl().split(",");
                if(pics.length > 1){
                    //多张图片
                    gridview_detail_picture.setVisibility(View.VISIBLE);
                    gridview_detail_picture.setAdapter(new ImageGridViewAdapter(pics, this));
                    gridview_detail_picture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            startImageActivity(pics, position);
                        }
                    });
                }else {
                    //一张图片
                    picOne.setVisibility(View.VISIBLE);
                    imageLoader.displayImage(pkwork.getPicUrl(), picOne, UniversityApplication.options, animateFirstListener);
                }
            } else {
                //一张图片
                pics = new String[]{pkwork.getPicUrl()};
                picOne.setVisibility(View.VISIBLE);
                imageLoader.displayImage(pkwork.getPicUrl(), picOne, UniversityApplication.options, animateFirstListener);
            }
            //单张图片点点击放大事件
            picOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startImageActivity(pics, 0);
                }
            });
        }

        if (pkwork.getType().equals("2")) {
            //视频
            video_pic = (RelativeLayout) headView.findViewById(R.id.video_pic);
            video_pic.setVisibility(View.VISIBLE);
            video_pic_url = (ImageView) headView.findViewById(R.id.video_pic_url);
            video_pic_img = (ImageView) headView.findViewById(R.id.video_pic_img);
            video_pic_img.setOnClickListener(this);
            imageLoader.displayImage(pkwork.getPicUrl(), video_pic_url, UniversityApplication.tpOptions, animateFirstListener);
        }
        if (pkwork.getIsChampion() != null && "1".equals(pkwork.getIsChampion()) && pkwork.getEmpId().equals(emp_id)) {
            //是冠军
            prizes_issure.setVisibility(View.VISIBLE);
            if ("0".equals(pkwork.getIsSure())) {
                //未领奖
                prizes_issure.setImageResource(R.drawable.prizes_issure_one);
            } else if ("1".equals(pkwork.getIsSure())) {
                //已领奖
                prizes_issure.setImageResource(R.drawable.prizes_isure);
            }
        } else {
            prizes_issure.setVisibility(View.GONE);
        }
        //底部按钮
        pk_jubao_img = (ImageView) this.findViewById(R.id.pk_jubao_img);
        pk_delete_img = (ImageView) this.findViewById(R.id.pk_delete_img);
        pk_delete_liner = (LinearLayout) this.findViewById(R.id.pk_delete_liner);
        pk_delete_liner.setOnClickListener(this);
        if (emp_id.equals(pkwork.getEmpId())) {
            //是自己的作品
            if(pkwork.getIsChampion() != null && "1".equals(pkwork.getIsChampion())) {
                pk_delete_img.setVisibility(View.GONE);
                pk_delete_liner.setVisibility(View.GONE);
            }else{
                pk_delete_img.setVisibility(View.VISIBLE);
                pk_delete_liner.setVisibility(View.VISIBLE);
            }
        } else {
            pk_jubao_img.setVisibility(View.VISIBLE);
            pk_report_liner.setVisibility(View.VISIBLE);
        }
        //添加头部
        pk_new_lstv.addHeaderView(headView);
    }

    private void startImageActivity(String[] urls, int position) {
        if (!PicUtil.hasSDCard()) {
            Toast.makeText(this, R.string.sd_card_is_in, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Pk_new_detailsActivity.this, GalleryUrlActivity.class);
        intent.putExtra(Constants.IMAGE_URLS, urls);
        intent.putExtra(Constants.IMAGE_POSITION, position);
        startActivity(intent);
    }

    //查询评论列表
    private void getData(final int currentid) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.GET_DETAIL_PK_PL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            PkCommentDATA data = getGson().fromJson(s, PkCommentDATA.class);
                            if (data.getCode() == 200) {
                                if (ContentListView.REFRESH == currentid) {
                                    pk_new_lstv.onRefreshComplete();
                                    lists.clear();
                                    lists.addAll(data.getData());
                                    pk_new_lstv.setResultSize(data.getData().size());
                                    adapter.notifyDataSetChanged();
                                }
                                if (ContentListView.LOAD == currentid) {
                                    pk_new_lstv.onLoadComplete();
                                    lists.addAll(data.getData());
                                    pk_new_lstv.setResultSize(data.getData().size());
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(Pk_new_detailsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Pk_new_detailsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(Pk_new_detailsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("zpId", pkwork.getId());
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

    //查询赞列表
    private void getFavourData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.GET_PK_FAVOUR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            PkZanDATA data = getGson().fromJson(s, PkZanDATA.class);
                            if (data.getCode() == 200) {
                                PkZanA pkZanA = data.getData();
                                itemListtwo.clear();
                                itemListtwo = pkZanA.getList();//获得赞的集合
                                favourCount = pkZanA.getCount();//获得投票总数
                                itemList.clear();
                                if (itemListtwo.size() > 5) {
                                    for (int i = 0; i < 6; i++) {
                                        itemList.add(itemListtwo.get(i));
                                    }
                                } else {
                                    itemList.addAll(itemListtwo);
                                }
                                if (itemList.size() > 0) {//当存在赞数据的时候
                                    pk_like_liner_layout.setVisibility(View.VISIBLE);
                                }

                                adaptertwo = new DetailPkFavourAdapter(itemList, Pk_new_detailsActivity.this, itemListtwo.size() );
                                pk_gridView.setAdapter(adaptertwo);
                                pk_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                        if (position == (itemList.size() - 1)) {
                                            Intent favour = new Intent(Pk_new_detailsActivity.this, DetailPkFavourActivity.class);
                                            favour.putExtra(Constants.PK_WORD_INFO_UUID, pkwork.getId());
                                            startActivity(favour);
//                                        } else {
//                                            PkZan pkZan = itemList.get(position);
//                                            if (emp_id.equals(pkZan.getEmpId())) {
//                                                Intent profileView = new Intent(Pk_new_detailsActivity.this, UpdateProfilePersonalActivity.class);
//                                                startActivity(profileView);
//                                            } else {
//                                                Intent profileView = new Intent(Pk_new_detailsActivity.this, ProfilePersonalActivity.class);
//                                                profileView.putExtra(Constants.EMPID, pkZan.getEmpId());
//                                                startActivity(profileView);
//                                            }
//                                        }
                                    }
                                });

                                adaptertwo.notifyDataSetChanged();
                                //
                                if(tmpFavourId == 0){
                                    pkwork.setZanNum(String.valueOf(Integer.parseInt(pkwork.getZanNum()==null?"0":pkwork.getZanNum())));
                                }else {
                                    pkwork.setZanNum(String.valueOf(Integer.parseInt(pkwork.getZanNum()==null?"0":pkwork.getZanNum()) + 1));
                                }

                                pk_favour_text.setText(pkwork.getZanNum()==null?"0":pkwork.getZanNum());
                                //发通知 更新列表
                                Intent intent1 = new Intent(Constants.PK_SEND_FAVOUR_SUCCESS);
                                intent1.putExtra("pkId", pkwork.getId());
                                sendBroadcast(intent1);

                            } else {
                                Toast.makeText(Pk_new_detailsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Pk_new_detailsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(Pk_new_detailsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("zpId", pkwork.getId());
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
            if (action.equals(Constants.PK_SEND_COMMENT_SUCCESS)) {
                //todo
                pkwork.setPlNum(String.valueOf(Integer.parseInt(pkwork.getPlNum()==null?"0":pkwork.getPlNum()) + 1));
                pk_pl_text.setText(pkwork.getPlNum()==null?"0":pkwork.getPlNum());
                pageIndex = 1;
                getData(ContentListView.REFRESH);
            }

        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.PK_SEND_COMMENT_SUCCESS);//pk评论成功
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }


    @Override
    public void onClickContentItem(int position, int flag, Object object) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detail_pk_back:
                finish();
                break;
            case R.id.detail_pk_share:
                mController.openShare(this, false);
                break;
            case R.id.pk_comment_liner:
                Intent comment = new Intent(Pk_new_detailsActivity.this, PkCommentActivity.class);
                comment.putExtra(Constants.FATHER_PERSON_NAME, "");
                comment.putExtra(Constants.FATHER_UUID, "0");
                comment.putExtra(Constants.RECORD_UUID, pkwork.getId());
                comment.putExtra(Constants.FATHER_PERSON_UUID, pkwork.getEmpId());//评论的谁的作品
                comment.putExtra("fplempid","");//父评论者
                startActivity(comment);
                break;
            case R.id.pk_report_liner:
                showJubao();
                break;
            case R.id.pk_favour_liner:
                progressDialog = new CustomProgressDialog(Pk_new_detailsActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                pk_favour_liner.setClickable(false);
                zan_click();
                break;
            case R.id.detail_pk_cover:
                //点击头像
                if (emp_id.equals(pkwork.getEmpId())) {
                    Intent profileView = new Intent(Pk_new_detailsActivity.this, UpdateProfilePersonalActivity.class);
                    startActivity(profileView);
                } else {
                    Intent profileView = new Intent(Pk_new_detailsActivity.this, ProfilePersonalActivity.class);
                    profileView.putExtra(Constants.EMPID, pkwork.getEmpId());
                    startActivity(profileView);
                }
                break;
            case R.id.pk_delete_liner:
                //点击删除
                showSelectImageDialog();
                break;
            case R.id.video_pic: {
                String videoUrl = pkwork.getVideoUrl();
                Intent intent = new Intent(this, VideoPlayerActivity2.class);
                VideoPlayer video = new VideoPlayer(videoUrl);
                intent.putExtra(Constants.EXTRA_LAYOUT, "0");
                intent.putExtra(VideoPlayer.class.getName(), video);
                startActivity(intent);
            }
            break;
            case R.id.video_pic_img: {
                String videoUrl = pkwork.getVideoUrl();
                Intent intent = new Intent(this, VideoPlayerActivity2.class);
                VideoPlayer video = new VideoPlayer(videoUrl);
                intent.putExtra(Constants.EXTRA_LAYOUT, "0");
                intent.putExtra(VideoPlayer.class.getName(), video);
                startActivity(intent);
            }
            break;
            case R.id.prizes_issure:
                //确认领奖
                if ("1".equals(pkwork.getIsSure())) {
                    Toast.makeText(Pk_new_detailsActivity.this, R.string.pk_is_sure_prizes, Toast.LENGTH_SHORT).show();
                    return;
                }
                showSelectImageDialogTwo();
                break;
        }
    }

    // 选择是否删除
    private void showSelectImageDialog() {
        deleteWindow = new DeletePopWindow(Pk_new_detailsActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(Pk_new_detailsActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    // 选择是否删除
    private void showSelectImageDialogTwo() {
        issureWindow = new IsSurePrizesWindow(Pk_new_detailsActivity.this, itemsOnClickTwo);
        //显示窗口
        issureWindow.showAtLocation(Pk_new_detailsActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //删除方法
    private void delete() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.PK_DELETE_ZP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(Pk_new_detailsActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                                //删除成功，更新列表页
                                Intent intent1 = new Intent(Constants.PK_SEND_SUCCESS_URL);
                                sendBroadcast(intent1);
                                finish();
                            } else {
                                Toast.makeText(Pk_new_detailsActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Pk_new_detailsActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(Pk_new_detailsActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("zpId", pkwork.getId());
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

    //确认领取奖品
    private void sure_prize() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.SURE_PRIZES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(Pk_new_detailsActivity.this, R.string.pk_is_sure_prizes_true, Toast.LENGTH_SHORT).show();
                                prizes_issure.setImageResource(R.drawable.prizes_isure);
                                //发广播
//                                Intent intent = new Intent(Constants.PK_SEARCH_ALL_OR_MINE);
//                                sendBroadcast(intent);
                            } else if (data.getCode() == 1) {
                                Toast.makeText(Pk_new_detailsActivity.this, R.string.pk_is_sure_prizes_errorone, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Pk_new_detailsActivity.this, R.string.pk_is_sure_prizes_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Pk_new_detailsActivity.this, R.string.pk_is_sure_prizes_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(Pk_new_detailsActivity.this, R.string.pk_is_sure_prizes_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("championId", championId);
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
    private View.OnClickListener itemsOnClickTwo = new View.OnClickListener() {

        public void onClick(View v) {
            issureWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_sure:
                    sure_prize();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 加载数据监听实现
     */
    @Override
    public void onLoad() {
        pageIndex++;
        getData(ContentListView.LOAD);
    }

    /**
     * 刷新数据监听实现
     */
    @Override
    public void onRefresh() {
        pageIndex = 1;
        getData(ContentListView.REFRESH);
    }

    //投票
    private void zan_click() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.PK_CLICK_LIKE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                //赞+1
                                Toast.makeText(Pk_new_detailsActivity.this, R.string.pk_zan_success, Toast.LENGTH_SHORT).show();
                                tmpFavourId = 1;
                                getFavourData();
                            } else if (data.getCode() == 2) {
                                Toast.makeText(Pk_new_detailsActivity.this, R.string.pk_zan_error_one, Toast.LENGTH_SHORT).show();
                                pk_favour_liner.setClickable(true);
                            } else if (data.getCode() == 3) {
                                Toast.makeText(Pk_new_detailsActivity.this, R.string.pk_zan_error_three, Toast.LENGTH_SHORT).show();
                                pk_favour_liner.setClickable(true);
                            } else {
                                Toast.makeText(Pk_new_detailsActivity.this, R.string.pk_zan_error_two, Toast.LENGTH_SHORT).show();
                                pk_favour_liner.setClickable(true);
                            }
                        } else {
                            Toast.makeText(Pk_new_detailsActivity.this, R.string.pk_zan_error_two, Toast.LENGTH_SHORT).show();
                            pk_favour_liner.setClickable(true);
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(Pk_new_detailsActivity.this, R.string.pk_zan_error_two, Toast.LENGTH_SHORT).show();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        pk_favour_liner.setClickable(true);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("zpId", pkwork.getId());
                params.put("empId", emp_id);
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
        final Dialog picAddDialog = new Dialog(Pk_new_detailsActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(this, R.layout.jubao_dialog, null);
        TextView jubao_sure = (TextView) picAddInflate.findViewById(R.id.jubao_sure);
        final EditText jubao_cont = (EditText) picAddInflate.findViewById(R.id.jubao_cont);
        //举报提交
        jubao_sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String contreport = jubao_cont.getText().toString();
                if (StringUtil.isNullOrEmpty(contreport)) {
                    Toast.makeText(Pk_new_detailsActivity.this, R.string.report_answer, Toast.LENGTH_LONG).show();
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
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.ADD_REPORT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            FavoursDATA data = getGson().fromJson(s, FavoursDATA.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(Pk_new_detailsActivity.this, R.string.report_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Pk_new_detailsActivity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Pk_new_detailsActivity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(Pk_new_detailsActivity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empOne", emp_id);
                params.put("empTwo", pkwork.getEmpId());
                params.put("typeId", Constants.REPORT_TYPE_FOUR);
                params.put("cont", contReport);
                params.put("xxid", pkwork.getId());
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
}

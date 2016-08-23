package com.liangxun.yuejiula.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.liangxun.yuejiula.adapter.*;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.*;
import com.liangxun.yuejiula.db.DBHelper;
import com.liangxun.yuejiula.db.ShoppingCart;
import com.liangxun.yuejiula.entity.*;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.DateUtil;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.ContentListView;
import com.liangxun.yuejiula.widget.popview.DeletePopWindow;
import com.liangxun.yuejiula.widget.popview.GoodsPopMenu;
import com.liangxun.yuejiula.widget.popview.GoodsTelPopWindow;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
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
import com.yixia.camera.demo.UniversityApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/22.
 */
public class DetailPaopaoGoodsActivity extends BaseActivity implements OnClickContentItemListener,View.OnClickListener, GoodsPopMenu.OnItemClickListener,ContentListView.OnRefreshListener, ContentListView.OnLoadListener {
    private ImageView detail_goods_back;//返回
    private Button foot_cart;//加入购物车
    private Button foot_order;//立即购买
    private TextView foot_goods;//购物车
    private TextView button_favour;//收藏按钮

    private PaopaoGoods goods;

    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID
    private String emp_type = "";//当前登陆者类别

    UMSocialService mController;
    String shareCont = "";//内容
    String shareUrl = "";
    String shareParams = "";
    String appID = Constants.social_wx_key;
    String sharePic = "";
    Resources res;
    Drawable img_favour;
    private GoodsTelPopWindow goodsTelPopWindow;
    private DeletePopWindow deleteWindow;

    //下拉菜单
    private GoodsPopMenu menu;
    List<String> arrayMenu = new ArrayList<>();
    private ScrollView headView;

    String topPosition;
    ContentListView lstv;
    private GoodsCommentAdapter adapter;
    private List<GoodsComment> comments;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    //
    private RelativeLayout relative_video;
    private ImageView img_video;
    private ImageView img_video_play;
    private TextView title;
    private ImageView head;
    private TextView shuliang;
    private TextView nickname;
    private TextView zixun;
    private TextView money_one;
    private TextView money_two;
    private TextView money_three;
    private TextView content;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    //导航
    private PaopaoGoodsViewPagerAdapter adapterAd;
    private ImageView dot, dots[];
    private Runnable runnable;
    private int autoChangeTime = 5000;
    private List<String> lists = new ArrayList<String>();
    String titleSlide = "";//幻灯片标题
    private ViewPager viewpager;
    private LinearLayout viewGroup;
    private TextView article_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_paopao_goods_xml);
        res=getResources();
        shareUrl =  getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.SHARE_GOODS;
        img_favour = res.getDrawable(R.drawable.favour_p);
        img_favour.setBounds(0, 0, img_favour.getMinimumWidth(), img_favour.getMinimumHeight());
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        emp_type = getGson().fromJson(getSp().getString(Constants.EMPTYPE, ""), String.class);
        goods = (PaopaoGoods) getIntent().getExtras().get(Constants.GOODS);
        topPosition = goods.getGoodsPosition();
        initView();
        initData();
    }
    public  static  Boolean flagR = false;
    private List<DailiObj> dailis = new ArrayList<DailiObj>();
    private void isDaili() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.LIST_DAILI_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            DailiObjData data = getGson().fromJson(s, DailiObjData.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    dailis.clear();
                                }
                                dailis.addAll(data.getData());
                                if(dailis != null && dailis.size() > 0){
                                    //说明是代理关系
                                    flagR = true;
                                    money_three.setVisibility(View.VISIBLE);
                                }else {
                                    money_three.setVisibility(View.GONE);
                                }
                                if(goods.getEmpId().equals(getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class))){
                                    money_three.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", goods.getEmpId());
                params.put("emp_id_d", getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class));
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

    // 选择相册，相机
    private void ShowPickDialog() {
        goodsTelPopWindow = new GoodsTelPopWindow(DetailPaopaoGoodsActivity.this, itemsOnClick);
        //显示窗口
        goodsTelPopWindow.showAtLocation(DetailPaopaoGoodsActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            goodsTelPopWindow.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + goods.getTel()));
                    DetailPaopaoGoodsActivity.this.startActivity(intent);
                }
                break;
                case R.id.mapstorage: {
                    if (!emp_id.equals(goods.getEmpId())) {
                        Intent profile = new Intent(DetailPaopaoGoodsActivity.this, ProfilePersonalActivity.class);
                        profile.putExtra(Constants.EMPID, goods.getEmpId());
                        startActivity(profile);
                    } else {
                        Intent profile = new Intent(DetailPaopaoGoodsActivity.this, UpdateProfilePersonalActivity.class);
                        startActivity(profile);
                    }
                }
                break;
                case R.id.qq:
                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + goods.getQq();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    break;
                default:
                    break;
            }
        }
    };


    private void initView() {
        detail_goods_back = (ImageView) this.findViewById(R.id.detail_goods_back);
        detail_goods_back.setOnClickListener(this);
        button_favour = (TextView) this.findViewById(R.id.button_favour);
        button_favour.setOnClickListener(this);
        foot_cart = (Button) this.findViewById(R.id.foot_cart);
        foot_order = (Button) this.findViewById(R.id.foot_order);
        foot_goods = (TextView) this.findViewById(R.id.foot_goods);
        foot_cart.setOnClickListener(this);
        foot_order.setOnClickListener(this);
        foot_goods.setOnClickListener(this);

        arrayMenu.add("分享");
        if (emp_type.equals("0")) {
            // 普通会员 只能举报
            arrayMenu.add("举报");
        }else if (emp_type.equals("1")) {
            //管理员直接删除商品
            arrayMenu.add("删除");
            arrayMenu.add("置顶");
        }else if (emp_type.equals("3")) {
            //代理商 直接删除商品
            arrayMenu.add("删除");
            arrayMenu.add("置顶");
        }else if (emp_type.equals("2") && goods.getEmpId().equals(emp_id)) {
            //如果是商家 商品是自己的 可以直接删除
            arrayMenu.add("删除");
        }else{
            arrayMenu.add("举报");
        }
        //顶部右侧按钮
        menu = new GoodsPopMenu(DetailPaopaoGoodsActivity.this, arrayMenu);
        menu.setOnItemClickListener(this);


        mController = UMServiceFactory.getUMSocialService(DetailPageAcitvity.class.getName(), RequestType.SOCIAL);
        //设置分享内容
        shareCont = goods.getName();
        mController.setShareContent(shareCont);
        sharePic = goods.getEmpCover();
        //设置分享图片
        mController.setShareMedia(new UMImage(DetailPaopaoGoodsActivity.this, sharePic));
        shareParams = "?goodsId=" + goods.getId();
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
        xinShareContent.setShareImage(new UMImage(DetailPaopaoGoodsActivity.this, sharePic));
        xinShareContent.setTargetUrl(shareUrl + shareParams);
        mController.setShareMedia(xinShareContent);
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(shareCont);
        circleMedia.setTitle(shareCont);
        circleMedia.setShareImage(new UMImage(DetailPaopaoGoodsActivity.this, sharePic));
        circleMedia.setTargetUrl(shareUrl + shareParams);
        mController.setShareMedia(circleMedia);

        //评论
        comments = new ArrayList<>();
        lstv = (ContentListView) this.findViewById(R.id.lstv);
        headView =  (ScrollView) LayoutInflater.from(DetailPaopaoGoodsActivity.this).inflate(R.layout.paopao_head, null);

        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        headView.setLayoutParams(layoutParams);

        relative_video = (RelativeLayout) headView.findViewById(R.id.relative_video);
        img_video = (ImageView) headView.findViewById(R.id.img_video);
        img_video_play = (ImageView) headView.findViewById(R.id.img_video_play);
        img_video_play.setOnClickListener(this);
        head = (ImageView) headView.findViewById(R.id.head);
        head.setOnClickListener(this);
        title = (TextView) headView.findViewById(R.id.title);
        shuliang = (TextView) headView.findViewById(R.id.shuliang);
        nickname = (TextView) headView.findViewById(R.id.nickname);
        zixun = (TextView) headView.findViewById(R.id.zixun);
        zixun.setOnClickListener(this);
        money_one = (TextView) headView.findViewById(R.id.money_one);
        money_two = (TextView) headView.findViewById(R.id.money_two);
        money_three = (TextView) headView.findViewById(R.id.money_three);
        content = (TextView) headView.findViewById(R.id.content);
        money_two.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG); //中划线

        imageLoader.displayImage(goods.getEmpCover(), head, UniversityApplication.options);
        if("0".equals(goods.getIs_video())){
            //没有视频
            relative_video.setVisibility(View.GONE);
        }else{
            //有视频
            relative_video.setVisibility(View.VISIBLE);
            imageLoader.displayImage(goods.getVideourl(), img_video, UniversityApplication.options);
        }
        title.setText(goods.getName());
        shuliang.setText("数量："+goods.getCount());
        nickname.setText(goods.getNickName());
        money_one.setText("价格："+goods.getSellPrice());
        money_two.setText("市场价："+goods.getMarketPrice());
        if(!StringUtil.isNullOrEmpty(goods.getDaili_price())){
            money_three.setText("代理价："+goods.getDaili_price());
        }else {
            money_three.setText("代理价：暂无");
        }

        content.setText(goods.getCont());

        adapter = new GoodsCommentAdapter(comments, DetailPaopaoGoodsActivity.this);
        adapter.setOnClickContentItemListener(this);
        lstv.setOnRefreshListener(this);
        lstv.setOnLoadListener(this);
        lstv.addHeaderView(headView);
        lstv.setAdapter(adapter);
//        lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                String label = DateUtils.formatDateTime(DetailPaopaoGoodsActivity.this, System.currentTimeMillis(),
//                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
//
//                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
//                IS_REFRESH = true;
//                pageIndex = 1;
//                initData();
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                String label = DateUtils.formatDateTime(DetailPaopaoGoodsActivity.this, System.currentTimeMillis(),
//                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
//
//                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
//                IS_REFRESH = false;
//                pageIndex++;
//                initData();
//            }
//        });
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsComment comment1 = comments.get(position - 1);
                Intent comment = new Intent(DetailPaopaoGoodsActivity.this, PublishGoodCommentActivity.class);
                comment.putExtra(Constants.GOODS_FATHER_PERSON_NAME, comment1.getNickName());
                comment.putExtra(Constants.GOODS_FATHER_UUID, comment1.getId());
                comment.putExtra(Constants.GOODS_RECORD_UUID, comment1.getGoodsId());
                comment.putExtra(Constants.GOODS_PERSON_UUID, goods.getEmpId());
                comment.putExtra("fplempid", comment1.getEmpId());
                startActivity(comment);
            }
        });

        String picurls = goods.getCover();
        if(!StringUtil.isNullOrEmpty(picurls)){
            String[] arrs = picurls.split(",");
            for(String str:arrs){
                lists.add(str);
            }
        }

        initViewPager();
        isDaili();//是否代理
    }

    //评论获取
    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.GET_GOODS_COMMENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            GoodsCommentDATA data = getGson().fromJson(s, GoodsCommentDATA.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    comments.clear();
                                    lstv.onRefreshComplete();
                                    comments.addAll(data.getData());
                                    lstv.setResultSize(data.getData().size());
                                } else {
                                    lstv.onLoadComplete();
                                    lstv.setResultSize(data.getData().size());
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                        lstv.onRefreshComplete();
                        lstv.onLoadComplete();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        lstv.onRefreshComplete();
                        lstv.onLoadComplete();
                        Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("goodsId", goods.getId());
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

//    class Contact {
        // 添加一个对象, 让JS可以访问该对象的方法, 该对象中可以调用JS中的方法
        public void showDialog(){
            goodsTelPopWindow = new GoodsTelPopWindow(DetailPaopaoGoodsActivity.this, itemsOnClick);
            //显示窗口
            goodsTelPopWindow.showAtLocation(DetailPaopaoGoodsActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_goods_back://返回
                finish();
                break;
            case R.id.head://头像
                if (!emp_id.equals(goods.getEmpId())) {
                    Intent profile = new Intent(DetailPaopaoGoodsActivity.this, ProfilePersonalActivity.class);
                    profile.putExtra(Constants.EMPID, goods.getEmpId());
                    startActivity(profile);
                } else {
                    Intent profile = new Intent(DetailPaopaoGoodsActivity.this, UpdateProfilePersonalActivity.class);
                    startActivity(profile);
                }
                break;
//            case R.id.detail_goods_mobile://电话
//                ShowPickDialog();
//                break;
//            case R.id.comment_count://评论查看全部
//                Intent commentAll = new Intent(DetailPaopaoGoodsActivity.this, GoodsCommentActivity.class);
//                commentAll.putExtra(Constants.GOODS, goods);
//                startActivity(commentAll);
//                break;
//            case R.id.foot_comment://评论
//                Intent comment = new Intent(this, PublishGoodCommentActivity.class);
//                comment.putExtra(Constants.GOODS_FATHER_PERSON_NAME, "");
//                comment.putExtra(Constants.GOODS_FATHER_UUID, "");
//                comment.putExtra(Constants.GOODS_RECORD_UUID, goods.getId());
//                comment.putExtra(Constants.GOODS_PERSON_UUID, goods.getEmpId());
//                comment.putExtra("fplempid", "");
//                startActivity(comment);
//                break;
            case R.id.foot_cart:
                //购物车
                //判断是否直营 是否允许直营
                if("1".equals(goods.getIs_zhiying())){
                    if("0".equals(goods.getIs_youhuo())){
                        showMsg(DetailPaopaoGoodsActivity.this, "暂时无货，请联系商家！");
                        return;
                    }
                }
                //先查询是否已经存在该商品了
                if(DBHelper.getInstance(DetailPaopaoGoodsActivity.this).isSaved(goods.getId())){
                    //如果已经加入购物车了
                    Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.add_cart_is, Toast.LENGTH_SHORT).show();
                }else{
                    ShoppingCart shoppingCart = new ShoppingCart();
                    shoppingCart.setCartid(StringUtil.getUUID());
                    shoppingCart.setGoods_id(goods.getId());
                    shoppingCart.setEmp_id(goods.getEmpId());
                    shoppingCart.setGoods_name(goods.getName());
                    shoppingCart.setGoods_cover(goods.getCover());
                    shoppingCart.setSell_price(goods.getSellPrice());
                    shoppingCart.setGoods_count("1");
                    shoppingCart.setDateline(DateUtil.getCurrentDateTime());
                    shoppingCart.setIs_select("0");//默认选中
                    shoppingCart.setEmp_name(goods.getNickName());
                    shoppingCart.setEmp_cover(goods.getEmpCover());
                    DBHelper.getInstance(DetailPaopaoGoodsActivity.this).addShoppingToTable(shoppingCart);
                    Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.add_cart_success, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.foot_order:
                //订单
                Intent orderMakeView = new Intent(DetailPaopaoGoodsActivity.this, OrderMakeActivity.class);
                ArrayList<ShoppingCart> arrayList = new ArrayList<>();

                ShoppingCart shoppingCart = new ShoppingCart();
                shoppingCart.setCartid(StringUtil.getUUID());
                shoppingCart.setGoods_id(goods.getId());
                shoppingCart.setEmp_id(goods.getEmpId());
                shoppingCart.setGoods_name(goods.getName());
                shoppingCart.setGoods_cover(goods.getCover());
                shoppingCart.setSell_price(goods.getSellPrice());
                shoppingCart.setGoods_count("1");
                shoppingCart.setDateline(DateUtil.getCurrentDateTime());
                shoppingCart.setIs_select("0");//默认选中
                shoppingCart.setEmp_name(goods.getNickName());
                shoppingCart.setEmp_cover(goods.getEmpCover());
                arrayList.add(shoppingCart);
                if(arrayList !=null && arrayList.size() > 0){
                    orderMakeView.putExtra("listsgoods",arrayList);
                    startActivity(orderMakeView);
                }else{
                    Toast.makeText(DetailPaopaoGoodsActivity.this,R.string.cart_error_one,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.foot_goods:
                //进入购物车
                Intent cartView = new Intent(DetailPaopaoGoodsActivity.this, MineCartActivity.class);
                startActivity(cartView);
                break;
////            case R.id.button_favour:
////                //收藏
////                favour();
//                break;
            case R.id.zixun:
            {
                showDialog();
            }
                break;
            case R.id.img_video_play:
            {
                String videoUrl = goods.getVideourl();
                Intent intent = new Intent(DetailPaopaoGoodsActivity.this, VideoPlayerActivity2.class);
                VideoPlayer video = new VideoPlayer(videoUrl);
                intent.putExtra(Constants.EXTRA_LAYOUT, "0");
                intent.putExtra(VideoPlayer.class.getName(), video);
                startActivity(intent);
            }
                break;
        }
    }


    // 举报
    private void showJubao() {
        final Dialog picAddDialog = new Dialog(DetailPaopaoGoodsActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(this, R.layout.jubao_dialog, null);
        TextView jubao_sure = (TextView) picAddInflate.findViewById(R.id.jubao_sure);
        final EditText jubao_cont = (EditText) picAddInflate.findViewById(R.id.jubao_cont);
        //举报提交
        jubao_sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String contreport = jubao_cont.getText().toString();
                if (StringUtil.isNullOrEmpty(contreport)) {
                    Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.report_answer, Toast.LENGTH_LONG).show();
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
                                Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.report_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empOne", emp_id);
                params.put("empTwo", goods.getEmpId());
                params.put("typeId", Constants.REPORT_TYPE_ONE);
                params.put("cont", contReport);
                params.put("xxid", goods.getId());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // 选择是否删除
    private void showSelectImageDialog() {
        deleteWindow = new DeletePopWindow(DetailPaopaoGoodsActivity.this, itemsOnClickOne);
        //显示窗口
        deleteWindow.showAtLocation(DetailPaopaoGoodsActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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

    //删除商品方法
    private void delete() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.DELETE_GOODS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                                //删除之后  发广播
                                Intent intent1 = new Intent(Constants.SEND_GOOD_SUCCESS);
                                sendBroadcast(intent1);
                                finish();
                            } else {
                                Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", goods.getId());
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

    //弹出顶部主菜单
    public void onTopMenuPopupButtonClick(View view) {
        menu.showAsDropDown(view);
    }

    @Override
    public void onItemClick(int index) {
        switch (index){
            case 0:
                mController.openShare(this, false);
                break;
            case 1:
                if (emp_type.equals("0")) {
                    // 普通会员 只能举报
                    showJubao();
                }else if (emp_type.equals("1")) {
                    //管理员直接删除商品
                    showSelectImageDialog();
                }else if (emp_type.equals("3")) {
                    //代理商 直接删除商品
                    showSelectImageDialog();
                }else if (emp_type.equals("2") && goods.getEmpId().equals(emp_id)) {
                    //如果是商家 商品是自己的 可以直接删除
                    showSelectImageDialog();
                }else{
                    showJubao();
                }
                break;
            case 2:
                //置顶
                if ("0".equals(goods.getGoodsPosition())){
                    topPosition = "1";
                }else {
                    topPosition = "0";
                }
                makeTop();
                break;
        }
    }

    public void makeTop() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.UPDATE_POSTION_GOODS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                if (topPosition.equals("1")) {
                                    //置顶成功
                                    Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.update_top_done_success, Toast.LENGTH_SHORT).show();
                                    goods.setGoodsPosition("1");
//                                    goods_foot_top.setImageDrawable(res.getDrawable(R.drawable.top_dis_foot));
                                } else {
                                    Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.update_cancle_top_success, Toast.LENGTH_SHORT).show();
                                    goods.setGoodsPosition("0");
//                                    goods_foot_top.setImageDrawable(res.getDrawable(R.drawable.top_foot));
                                }
                            } else {
                                Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", goods.getId());
                params.put("position", topPosition);
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


    //收藏
    private void favour() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.SAVE_FAVOUR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                button_favour.setCompoundDrawables(null, img_favour, null, null);
                                Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.goods_favour_success, Toast.LENGTH_SHORT).show();
                            }else if(data.getCode() == 2){
                                button_favour.setCompoundDrawables(null, img_favour, null, null);
                                Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.goods_favour_error_one, Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.goods_favour_error_two, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.goods_favour_error_two, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailPaopaoGoodsActivity.this, R.string.goods_favour_error_two, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("goods_id", goods.getId());
                params.put("emp_id_favour", emp_id);
                params.put("emp_id_goods", goods.getEmpId());
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


    private void initViewPager() {
        adapterAd = new PaopaoGoodsViewPagerAdapter(DetailPaopaoGoodsActivity.this);
        adapterAd.change(getList());
        adapterAd.setOnClickContentItemListener(this);
        viewpager = (ViewPager) headView.findViewById(R.id.viewpager);
        viewpager.setAdapter(adapterAd);
        viewpager.setOnPageChangeListener(myOnPageChangeListener);
        initDot();
        runnable = new Runnable() {
            @Override
            public void run() {
                int next = viewpager.getCurrentItem()+1;
                if (next > adapterAd.getCount()) {
                    next = 0;
                }
                viewHandler.sendEmptyMessage(next);
            }
        };
        viewHandler.postDelayed(runnable, autoChangeTime);
    }

    private List<String> getList() {
        return lists;
    }



    // 初始化dot视图
    private void initDot() {
        viewGroup = (LinearLayout) headView.findViewById(R.id.viewGroup);
        article_title = (TextView) headView.findViewById(R.id.article_title);
//        if(lists.size()!= 0) {
//            article_title.setText(lists.get(0).getMm_ad_title());//初始化新闻标题显示
//        }else{
//            article_title.setText("");
//        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                30, 30);
        layoutParams.setMargins(4, 3, 4, 3);

        dots = new ImageView[adapterAd.getCount()];
        for (int i = 0; i < adapterAd.getCount(); i++) {

            dot = new ImageView(DetailPaopaoGoodsActivity.this);
            dot.setLayoutParams(layoutParams);
            dots[i] = dot;
            dots[i].setTag(i);
            dots[i].setOnClickListener(onClick);

            if (i == 0) {
                dots[i].setBackgroundResource(R.drawable.dotc);
            } else {
                dots[i].setBackgroundResource(R.drawable.dotn);
            }

            viewGroup.addView(dots[i]);
        }
    }

    ViewPager.OnPageChangeListener myOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            setCurDot(arg0);
            viewHandler.removeCallbacks(runnable);
            viewHandler.postDelayed(runnable, autoChangeTime);
        }

    };
    // 实现dot点击响应功能,通过点击事件更换页面
    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            setCurView(position);
        }

    };

    /**
     * 设置当前的引导页
     */
    private void setCurView(int position) {
        if (position < 0 || position > adapterAd.getCount()) {
            return;
        }
        viewpager.setCurrentItem(position);
        if(position >= lists.size()){
            return;
        }else{
//            if (!StringUtil.isNullOrEmpty(lists.get(position).getMm_ad_title())){
//                titleSlide = lists.get(position).getMm_ad_title();
//                if(titleSlide.length() > 13){
//                    titleSlide = titleSlide.substring(0,12);
//                    article_title.setText(titleSlide);//当前新闻标题显示
//                }else{
//                    article_title.setText(titleSlide);//当前新闻标题显示
//                }
//            }
        }
    }

    /**
     * 选中当前引导小点
     */
    private void setCurDot(int position) {
        for (int i = 0; i < dots.length; i++) {
            if (position == i) {
                dots[i].setBackgroundResource(R.drawable.dotc);
            } else {
                dots[i].setBackgroundResource(R.drawable.dotn);
            }
        }
    }

    /**
     * 每隔固定时间切换广告栏图片
     */
    @SuppressLint("HandlerLeak")
    private final Handler viewHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setCurView(msg.what);
        }

    };


    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        String str = (String) object;
        if("1001".equals(str)){
            switch (flag){
                case 1:
                    final String[] picUrls = goods.getCover().split(",");//图片链接切割
                    Intent intent = new Intent(DetailPaopaoGoodsActivity.this, GalleryUrlActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    intent.putExtra(Constants.IMAGE_URLS, picUrls);
                    intent.putExtra(Constants.IMAGE_POSITION, position);
                    startActivity(intent);
                    break;
            }
        }

    }

    @Override
    public void onLoad() {
        IS_REFRESH = false;
        pageIndex++;
        initData();
    }

    @Override
    public void onRefresh() {
        IS_REFRESH = true;
        pageIndex = 1;
        initData();
    }


}

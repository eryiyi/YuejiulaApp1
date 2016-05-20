package com.liangxun.yuejiula.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
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
import com.liangxun.yuejiula.adapter.GoodsCommentAdapter;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.FavoursDATA;
import com.liangxun.yuejiula.data.GoodsCommentDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.db.DBHelper;
import com.liangxun.yuejiula.db.ShoppingCart;
import com.liangxun.yuejiula.entity.GoodsComment;
import com.liangxun.yuejiula.entity.PaopaoGoods;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.DateUtil;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.popview.DeletePopWindow;
import com.liangxun.yuejiula.widget.popview.GoodsPopMenu;
import com.liangxun.yuejiula.widget.popview.GoodsTelPopWindow;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/7
 * Time: 9:06
 * 类的功能、说明写在此处.
 */
public class DetailGoodsActivity extends BaseActivity implements OnClickContentItemListener,
        View.OnClickListener, GoodsPopMenu.OnItemClickListener {
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
    String shareUrl = InternetURL.SHARE_GOODS;
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

    private WebView webview;

    String topPosition;
    private ImageView top;
    PullToRefreshListView lstv;
    private GoodsCommentAdapter adapter;
    private List<GoodsComment> comments;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private TextView select_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res=getResources();
        img_favour = res.getDrawable(R.drawable.favour_p);
        img_favour.setBounds(0, 0, img_favour.getMinimumWidth(), img_favour.getMinimumHeight());
        setContentView(R.layout.detail_goods_xml);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        emp_type = getGson().fromJson(getSp().getString(Constants.EMPTYPE, ""), String.class);
        goods = (PaopaoGoods) getIntent().getExtras().get(Constants.GOODS);
        topPosition = goods.getGoodsPosition();
        initView();
    }

    // 选择相册，相机
    private void ShowPickDialog() {
        goodsTelPopWindow = new GoodsTelPopWindow(DetailGoodsActivity.this, itemsOnClick);
        //显示窗口
        goodsTelPopWindow.showAtLocation(DetailGoodsActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            goodsTelPopWindow.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + goods.getTel()));
                    DetailGoodsActivity.this.startActivity(intent);
                }
                break;
                case R.id.mapstorage: {
                    if (!emp_id.equals(goods.getEmpId())) {
                        Intent profile = new Intent(DetailGoodsActivity.this, ProfilePersonalActivity.class);
                        profile.putExtra(Constants.EMPID, goods.getEmpId());
                        startActivity(profile);
                    } else {
                        Intent profile = new Intent(DetailGoodsActivity.this, UpdateProfilePersonalActivity.class);
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

    //Web视图
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private void initView() {
        select_comment = (TextView) this.findViewById(R.id.select_comment);
        select_comment.setOnClickListener(this);
        top = (ImageView) this.findViewById(R.id.top);
        webview = (WebView) this.findViewById(R.id.webview);
        //设置WebView属性，能够执行Javascript脚本
        webview.getSettings().setJavaScriptEnabled(true);
        //加载需要显示的网页
        webview.loadUrl(InternetURL.DETAIL_GOODS_URL + "?id=" + goods.getId());
        //设置Web视图
        webview.setWebViewClient(new HelloWebViewClient());
        webview.addJavascriptInterface(new Contact(), "contact");
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String openUrl) {
                if ("protocol://lianxi".equals(openUrl)) { //伪代码。判断是否是需要过滤的url,是的话，就返回不处理
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, openUrl);
            }
        });

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
        menu = new GoodsPopMenu(DetailGoodsActivity.this, arrayMenu);
        menu.setOnItemClickListener(this);
        if("0".equals(goods.getGoodsPosition())){
            top.setVisibility(View.GONE);
        }else {
            top.setVisibility(View.VISIBLE);
        }

        mController = UMServiceFactory.getUMSocialService(DetailPageAcitvity.class.getName(), RequestType.SOCIAL);
        //设置分享内容
        shareCont = goods.getName();
        mController.setShareContent(shareCont);
        sharePic = goods.getEmpCover();
        //设置分享图片
        mController.setShareMedia(new UMImage(DetailGoodsActivity.this, sharePic));
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
        xinShareContent.setShareImage(new UMImage(DetailGoodsActivity.this, sharePic));
        xinShareContent.setTargetUrl(shareUrl + shareParams);
        mController.setShareMedia(xinShareContent);
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(shareCont);
        circleMedia.setTitle(shareCont);
        circleMedia.setShareImage(new UMImage(DetailGoodsActivity.this, sharePic));
        circleMedia.setTargetUrl(shareUrl + shareParams);
        mController.setShareMedia(circleMedia);

        //评论
        comments = new ArrayList<>();
        lstv = (PullToRefreshListView) this.findViewById(R.id.lstv);
        adapter = new GoodsCommentAdapter(comments, DetailGoodsActivity.this);
        adapter.setOnClickContentItemListener(this);
        lstv.setAdapter(adapter);
        lstv.setMode(PullToRefreshBase.Mode.BOTH);
        lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(DetailGoodsActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(DetailGoodsActivity.this, System.currentTimeMillis(),
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
                GoodsComment comment1 = comments.get(position - 1);
                Intent comment = new Intent(DetailGoodsActivity.this, PublishGoodCommentActivity.class);
                comment.putExtra(Constants.GOODS_FATHER_PERSON_NAME, comment1.getNickName());
                comment.putExtra(Constants.GOODS_FATHER_UUID, comment1.getId());
                comment.putExtra(Constants.GOODS_RECORD_UUID, comment1.getGoodsId());
                comment.putExtra(Constants.GOODS_PERSON_UUID, goods.getEmpId());
                comment.putExtra("fplempid", comment1.getEmpId());
                startActivity(comment);
            }
        });
    }

    //评论获取
    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_GOODS_COMMENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            GoodsCommentDATA data = getGson().fromJson(s, GoodsCommentDATA.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    comments.clear();
                                }
                                comments.addAll(data.getData());
                                lstv.onRefreshComplete();
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(DetailGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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

    class Contact {
        // 添加一个对象, 让JS可以访问该对象的方法, 该对象中可以调用JS中的方法
        public void showDialog(){
            goodsTelPopWindow = new GoodsTelPopWindow(DetailGoodsActivity.this, itemsOnClick);
            //显示窗口
            goodsTelPopWindow.showAtLocation(DetailGoodsActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_goods_back://返回
                finish();
                break;
            case R.id.detail_goods_cover://头像
                if (!emp_id.equals(goods.getEmpId())) {
                    Intent profile = new Intent(DetailGoodsActivity.this, ProfilePersonalActivity.class);
                    profile.putExtra(Constants.EMPID, goods.getEmpId());
                    startActivity(profile);
                } else {
                    Intent profile = new Intent(DetailGoodsActivity.this, UpdateProfilePersonalActivity.class);
                    startActivity(profile);
                }
                break;
            case R.id.detail_goods_mobile://电话
                ShowPickDialog();
                break;
            case R.id.comment_count://评论查看全部
                Intent commentAll = new Intent(DetailGoodsActivity.this, GoodsCommentActivity.class);
                commentAll.putExtra(Constants.GOODS, goods);
                startActivity(commentAll);
                break;
            case R.id.foot_comment://评论
                Intent comment = new Intent(this, PublishGoodCommentActivity.class);
                comment.putExtra(Constants.GOODS_FATHER_PERSON_NAME, "");
                comment.putExtra(Constants.GOODS_FATHER_UUID, "");
                comment.putExtra(Constants.GOODS_RECORD_UUID, goods.getId());
                comment.putExtra(Constants.GOODS_PERSON_UUID, goods.getEmpId());
                comment.putExtra("fplempid", "");
                startActivity(comment);
                break;
            case R.id.foot_cart:
                //购物车
                //先查询是否已经存在该商品了
                if(DBHelper.getInstance(DetailGoodsActivity.this).isSaved(goods.getId())){
                    //如果已经加入购物车了
                    Toast.makeText(DetailGoodsActivity.this, R.string.add_cart_is, Toast.LENGTH_SHORT).show();
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
                    DBHelper.getInstance(DetailGoodsActivity.this).addShoppingToTable(shoppingCart);
                    Toast.makeText(DetailGoodsActivity.this, R.string.add_cart_success, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.foot_order:
                //订单
                Intent orderMakeView = new Intent(DetailGoodsActivity.this, OrderMakeActivity.class);
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
                    Toast.makeText(DetailGoodsActivity.this,R.string.cart_error_one,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.foot_goods:
                //进入购物车
                Intent cartView = new Intent(DetailGoodsActivity.this, MineCartActivity.class);
                startActivity(cartView);
                break;
            case R.id.button_favour:
                //收藏
                favour();
                break;
            case R.id.select_comment:
                //评论
                Intent commentView = new Intent(DetailGoodsActivity.this, GoodsCommentActivity.class);
                commentView.putExtra(Constants.GOODS, goods);
                startActivity(commentView);
                break;
        }
    }

    @Override
    public void onClickContentItem(int position, int flag, Object object) {

    }

    // 举报
    private void showJubao() {
        final Dialog picAddDialog = new Dialog(DetailGoodsActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(this, R.layout.jubao_dialog, null);
        TextView jubao_sure = (TextView) picAddInflate.findViewById(R.id.jubao_sure);
        final EditText jubao_cont = (EditText) picAddInflate.findViewById(R.id.jubao_cont);
        //举报提交
        jubao_sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String contreport = jubao_cont.getText().toString();
                if (StringUtil.isNullOrEmpty(contreport)) {
                    Toast.makeText(DetailGoodsActivity.this, R.string.report_answer, Toast.LENGTH_LONG).show();
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
                                Toast.makeText(DetailGoodsActivity.this, R.string.report_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DetailGoodsActivity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailGoodsActivity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailGoodsActivity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
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
        deleteWindow = new DeletePopWindow(DetailGoodsActivity.this, itemsOnClickOne);
        //显示窗口
        deleteWindow.showAtLocation(DetailGoodsActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
                InternetURL.DELETE_GOODS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(DetailGoodsActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                                //删除之后  发广播
                                Intent intent1 = new Intent(Constants.SEND_GOOD_SUCCESS);
                                sendBroadcast(intent1);
                                finish();
                            } else {
                                Toast.makeText(DetailGoodsActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailGoodsActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailGoodsActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
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
                InternetURL.UPDATE_POSTION_GOODS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                if (topPosition.equals("1")) {
                                    //置顶成功
                                    Toast.makeText(DetailGoodsActivity.this, R.string.update_top_done_success, Toast.LENGTH_SHORT).show();
                                    goods.setGoodsPosition("1");
//                                    goods_foot_top.setImageDrawable(res.getDrawable(R.drawable.top_dis_foot));
                                } else {
                                    Toast.makeText(DetailGoodsActivity.this, R.string.update_cancle_top_success, Toast.LENGTH_SHORT).show();
                                    goods.setGoodsPosition("0");
//                                    goods_foot_top.setImageDrawable(res.getDrawable(R.drawable.top_foot));
                                }
                                if("0".equals(goods.getGoodsPosition())){
                                    top.setVisibility(View.GONE);
                                }else {
                                    top.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(DetailGoodsActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailGoodsActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailGoodsActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
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
                InternetURL.SAVE_FAVOUR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                button_favour.setCompoundDrawables(null, img_favour, null, null);
                                Toast.makeText(DetailGoodsActivity.this, R.string.goods_favour_success, Toast.LENGTH_SHORT).show();
                            }else if(data.getCode() == 2){
                                button_favour.setCompoundDrawables(null, img_favour, null, null);
                                Toast.makeText(DetailGoodsActivity.this, R.string.goods_favour_error_one, Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(DetailGoodsActivity.this, R.string.goods_favour_error_two, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailGoodsActivity.this, R.string.goods_favour_error_two, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailGoodsActivity.this, R.string.goods_favour_error_two, Toast.LENGTH_SHORT).show();
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
}

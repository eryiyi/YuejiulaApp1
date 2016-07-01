package com.liangxun.yuejiula.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
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
import com.liangxun.yuejiula.entity.AdObj;
import com.liangxun.yuejiula.entity.ManagerInfo;
import com.liangxun.yuejiula.entity.PaopaoGoods;
import com.liangxun.yuejiula.entity.SchoolThreeTingtaiBd;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/5/23.
 */
public class DianpuDetailActivity extends BaseActivity implements View.OnClickListener,OnClickContentItemListener{
    //下拉刷新
    private PullToRefreshListView lstv ;
    private SearchGoodsAdapter adapterGoods;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private List<PaopaoGoods> listgoods = new ArrayList<PaopaoGoods>();

    private LinearLayout headView;

    //导航
    private AdViewPagerAdapter adapterAd;
    private ImageView dot, dots[];
    private Runnable runnable;
    private int autoChangeTime = 5000;
    private List<AdObj> lists = new ArrayList<AdObj>();
    String titleSlide = "";//幻灯片标题
    private ViewPager viewpager;
    private LinearLayout viewGroup;
    private TextView article_title;

    private String emp_id;//当前用户id

    private TextView title;
    private ImageView head;
    private TextView nickname;
    private TextView tel;
    private TextView address;
    private TextView zixun;
    private TextView favour;
    private TextView yingyetime;
    private TextView youhui;
    private TextView content;
    private TextView location;

    private RelativeLayout detail_like_liner_layout;//赞区域
    private GridView gridView;
    private List<SchoolThreeTingtaiBd> itemList = new ArrayList<SchoolThreeTingtaiBd>();
    private DetailDianpuThreePtAdapter adaptertwo;

    private ManagerInfo managerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dianpu_acitivity);
        this.findViewById(R.id.detail_back).setOnClickListener(this);
        emp_id = getIntent().getExtras().getString("emp_id");

        //头部文件
        headView = (LinearLayout) LayoutInflater.from(DianpuDetailActivity.this).inflate(R.layout.dianpu_head, null);

        initView();
        lstv = (PullToRefreshListView) this.findViewById(R.id.lstv);//列表

        ListView listView = lstv.getRefreshableView();

        listView.addHeaderView(headView);

        adapterGoods = new SearchGoodsAdapter(listgoods, DianpuDetailActivity.this);
        lstv.setAdapter(adapterGoods);
        lstv.setMode(PullToRefreshBase.Mode.BOTH);
        lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(DianpuDetailActivity.this.getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(DianpuDetailActivity.this.getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData();
            }
        });
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    PaopaoGoods good = listgoods.get(position-2);
                    if (good != null) {
                        Intent detail = new Intent(DianpuDetailActivity.this, DetailGoodsActivity.class);
                        detail.putExtra(Constants.GOODS, good);
                        startActivity(detail);
                    }
                } catch (Exception e) {

                }
            }
        });

        //查询个人店铺信息
        getManagerInfo();
        //个人商品
        initData();

    }

    void initView(){
        title = (TextView) headView.findViewById(R.id.title);
        head = (ImageView) headView.findViewById(R.id.head);
        nickname = (TextView) headView.findViewById(R.id.nickname);
        tel = (TextView) headView.findViewById(R.id.tel);
        address = (TextView) headView.findViewById(R.id.address);
        zixun = (TextView) headView.findViewById(R.id.zixun);
        favour = (TextView) headView.findViewById(R.id.favour);
        yingyetime = (TextView) headView.findViewById(R.id.yingyetime);
        youhui = (TextView) headView.findViewById(R.id.youhui);
        content = (TextView) headView.findViewById(R.id.content);
        location = (TextView) headView.findViewById(R.id.location);
        location.setOnClickListener(this);
        zixun.setOnClickListener(this);
        favour.setOnClickListener(this);
        detail_like_liner_layout = (RelativeLayout) headView.findViewById(R.id.detail_like_liner_layout);
        gridView = (GridView) headView.findViewById(R.id.gridView);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        getFavour();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.detail_back:
                finish();
                break;
            case R.id.zixun:
                //咨询
                if(!StringUtil.isNullOrEmpty(managerInfo.getCompany_tel())){
                    //传入服务， parse（）解析号码
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + managerInfo.getCompany_tel()));
                    //通知activtity处理传入的call服务
                    DianpuDetailActivity.this.startActivity(intent);
                }
                break;
            case R.id.favour:
                //收藏本店
            {
                saveFavour();
            }
                break;
            case R.id.location:
                //导航
                if(managerInfo != null && !StringUtil.isNullOrEmpty(managerInfo.getLat_company())  && !StringUtil.isNullOrEmpty(managerInfo.getLng_company()) && !StringUtil.isNullOrEmpty(UniversityApplication.lat) && !StringUtil.isNullOrEmpty(UniversityApplication.lng)){
                    Intent naviV = new Intent(DianpuDetailActivity.this, GPSNaviActivity.class);
                    naviV.putExtra("lat_end", managerInfo.getLat_company());
                    naviV.putExtra("lng_end", managerInfo.getLng_company());
                    startActivity(naviV);
                }
                break;
        }
    }

        private void initViewPager() {
        adapterAd = new AdViewPagerAdapter(DianpuDetailActivity.this);
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
                if (next > adaptertwo.getCount()) {
                    next = 0;
                }
                viewHandler.sendEmptyMessage(next);
            }
        };
        viewHandler.postDelayed(runnable, autoChangeTime);
    }

    private List<AdObj> getList() {
        return lists;
    }

    /**
     * 获取幻灯片
     */
    private void getSlide() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_DIANPU_ADS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            AdObjData data = getGson().fromJson(s, AdObjData.class);
                            if (data.getCode() == 200) {
                                lists.addAll(data.getData());
//                                adapterAd.notifyDataSetChanged();
                                initViewPager();
                            } else {
                                Toast.makeText(DianpuDetailActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DianpuDetailActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DianpuDetailActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", emp_id);
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

    // 初始化dot视图
    private void initDot() {
        viewGroup = (LinearLayout) headView.findViewById(R.id.viewGroup);
        article_title = (TextView) headView.findViewById(R.id.article_title);
        if(lists.size()!= 0) {
            article_title.setText(lists.get(0).getMm_ad_title());//初始化新闻标题显示
        }else{
            article_title.setText("");
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                30, 30);
        layoutParams.setMargins(4, 3, 4, 3);

        dots = new ImageView[adapterAd.getCount()];
        for (int i = 0; i < adapterAd.getCount(); i++) {

            dot = new ImageView(DianpuDetailActivity.this);
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
        if (position < 0 || position > adaptertwo.getCount()) {
            return;
        }
        viewpager.setCurrentItem(position);
        if(position >= lists.size()){
            return;
        }else{
            if (!StringUtil.isNullOrEmpty(lists.get(position).getMm_ad_title())){
                titleSlide = lists.get(position).getMm_ad_title();
                if(titleSlide.length() > 13){
                    titleSlide = titleSlide.substring(0,12);
                    article_title.setText(titleSlide);//当前新闻标题显示
                }else{
                    article_title.setText(titleSlide);//当前新闻标题显示
                }
            }
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
        AdObj slide = lists.get(position);
        switch (flag){
            case 1:
//                    Intent adView =  new Intent(DianpuDetailActivity.this, WebViewActivity.class);
//                    adView.putExtra("strurl", slide.getMm_ad_url());
//                    startActivity(adView);
                break;
        }
    }

    //查询平台
    private void getFavour() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_THREE_PT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SchoolThreeTingtaiBdData data = getGson().fromJson(s, SchoolThreeTingtaiBdData.class);
                            if (data.getCode() == 200) {
                                itemList.clear();
                                itemList = data.getData();

                                adaptertwo = new DetailDianpuThreePtAdapter(itemList, DianpuDetailActivity.this , itemList.size());
                                gridView.setAdapter(adaptertwo);
                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        SchoolThreeTingtaiBd schoolThreeTingtaiBd = itemList.get(position);
                                        if(schoolThreeTingtaiBd != null){
                                            Intent adView =  new Intent(DianpuDetailActivity.this, WebViewActivity.class);
                                            adView.putExtra("strurl", schoolThreeTingtaiBd.getPingtai_url());
                                            startActivity(adView);
                                        }
                                    }
                                });
                                if (itemList.size() > 0) {//当存在赞数据的时候
                                    detail_like_liner_layout.setVisibility(View.VISIBLE);
                                }else {
                                    detail_like_liner_layout.setVisibility(View.GONE);
                                }
                                adaptertwo.notifyDataSetChanged();
                            } else {
                                Toast.makeText(DianpuDetailActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DianpuDetailActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DianpuDetailActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", emp_id);
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


    private void getManagerInfo() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_DIANPU_MSG_DETAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            ManagerInfoData data = getGson().fromJson(s, ManagerInfoData.class);
                            if (data.getCode() == 200) {
                               managerInfo = data.getData();
                                //
                                initDataProfile();
                            } else {
                                Toast.makeText(DianpuDetailActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DianpuDetailActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DianpuDetailActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", emp_id);
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

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    void initDataProfile(){
        imageLoader.displayImage(managerInfo.getEmp_cover(), head, UniversityApplication.options, animateFirstListener);
        title.setText(managerInfo.getCompany_name());
        nickname.setText(managerInfo.getCompany_person());
        tel.setText(managerInfo.getCompany_tel());
        address.setText(managerInfo.getCompany_address());
        yingyetime.setText("营业时间:"+managerInfo.getYingye_time_start() + "-" + managerInfo.getYingye_time_end());
        youhui.setText("优惠承诺:"+managerInfo.getShouhui());
        content.setText(managerInfo.getCompany_detail());
        //AdObj
        AdObj adObj = new AdObj();
        adObj.setEmp_id(managerInfo.getEmp_id());
        adObj.setMm_ad_id("");
        adObj.setMm_ad_url("");
        adObj.setMm_ad_title("");
        adObj.setMm_ad_pic(managerInfo.getCompany_pic());
        lists.add(adObj);
        //查询个人广告位
        getSlide();

    }

    //获得商品列表
    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_GOODS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            GoodsDATA data = getGson().fromJson(s, GoodsDATA.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    listgoods.clear();
                                }
                                listgoods.addAll(data.getData());
                                adapterGoods.notifyDataSetChanged();
                                lstv.onRefreshComplete();
                            } else {
                                Toast.makeText(DianpuDetailActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DianpuDetailActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DianpuDetailActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cont", "");
                params.put("schoolId", "");
                params.put("page", String.valueOf(pageIndex));
                params.put("typeId", "");
                params.put("type", "0");
                params.put("empId", emp_id);
                params.put("isMine", "");
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

    void saveFavour(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SAVE_FAVOUR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                showMsg(DianpuDetailActivity.this, "收藏店铺成功！");
                            } else if(data.getCode() == 2){
                                showMsg(DianpuDetailActivity.this, "已经收藏了！");
                            }
                            else {
                                Toast.makeText(DianpuDetailActivity.this, "收藏店铺失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DianpuDetailActivity.this, "收藏店铺失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DianpuDetailActivity.this, "收藏店铺失败", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", emp_id);
                params.put("emp_id_favour", getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class));
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

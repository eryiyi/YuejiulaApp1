package com.liangxun.yuejiula.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.ImageAdapter;
import com.liangxun.yuejiula.adapter.SearchGoodsAdapter;
import com.liangxun.yuejiula.base.BaseFragment;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.GoodSingleDATA;
import com.liangxun.yuejiula.data.GoodsDATA;
import com.liangxun.yuejiula.data.GoodsTypeDATA;
import com.liangxun.yuejiula.entity.Goodstype;
import com.liangxun.yuejiula.entity.PaopaoGoods;
import com.liangxun.yuejiula.ui.DetailGoodsActivity;
import com.liangxun.yuejiula.ui.SearchGoodsActivity;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.ClassifyGridview;
import com.liangxun.yuejiula.widget.ContentListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商城
 */
public class GoodsFragment extends BaseFragment implements ContentListView.OnRefreshListener, ContentListView.OnLoadListener,View.OnClickListener {
    //下拉刷新
    private ContentListView lstv ;
    private SearchGoodsAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private List<PaopaoGoods> listgoods = new ArrayList<PaopaoGoods>();

    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID
    private String typeId = "";
    private String type = "";//登陆者类别

    private LinearLayout headView;
    private ClassifyGridview parttimetyupeGridview;//定义一个gridview
    private List<Goodstype> goodstypeList = new ArrayList<Goodstype>();
    private ImageAdapter adaptertype;

//    private EditText searchText;//搜索框
//    private ImageView soubtn;
//    private String content = "";

    //导航
//    private ViewPagerAdapter adapterAd;
//    private ImageView dot, dots[];
//    private Runnable runnable;
//    private int autoChangeTime = 5000;
//    private List<Slide> lists = new ArrayList<Slide>();
//    String titleSlide = "";//幻灯片标题
//    private ViewPager viewpager;
//    private LinearLayout viewGroup;
//    private TextView article_title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goods_fragment, null);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        initView(view);
//        initViewPager();
        getType();
        initData();
//        getSlide();
        return view;
    }

    private void initView(View view) {
        //头部文件
        headView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.goodshead, null);
//        searchText = (EditText) headView.findViewById(R.id.searchText);
//        soubtn = (ImageView) headView.findViewById(R.id.soubtn);
//        soubtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                content = searchText.getText().toString();//要搜的内容
//                IS_REFRESH = true;
//                pageIndex = 1;
//                initData();
//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//            }
//        });

        parttimetyupeGridview = (ClassifyGridview) headView.findViewById(R.id.moreparttimetyupeGridview);
        parttimetyupeGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Goodstype goodstype = goodstypeList.get(position);
                typeId = goodstype.getTypeId();
                String typeName = goodstype.getTypeName();
                Intent search = new Intent(getActivity(), SearchGoodsActivity.class);
                search.putExtra("typeId", typeId);
                search.putExtra("typeName", typeName);
                startActivity(search);
            }
        });
        adaptertype = new ImageAdapter(goodstypeList,getActivity());
        parttimetyupeGridview.setAdapter(adaptertype);
        parttimetyupeGridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        lstv = (ContentListView) view.findViewById(R.id.detail_lstv);//列表
        adapter = new SearchGoodsAdapter(listgoods, getActivity());
        lstv.setOnRefreshListener(this);
        lstv.setOnLoadListener(this);
        lstv.addHeaderView(headView);
        lstv.setAdapter(adapter);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    PaopaoGoods good = listgoods.get(position-2);
                    if (good != null) {
                        Intent detail = new Intent(getActivity(), DetailGoodsActivity.class);
                        detail.putExtra(Constants.GOODS, good);
                        startActivity(detail);
                    }
                } catch (Exception e) {

                }
            }
        });
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    //获得商品列表
    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +InternetURL.GET_GOODS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            GoodsDATA data = getGson().fromJson(s, GoodsDATA.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    listgoods.clear();
                                    lstv.onRefreshComplete();
                                    listgoods.addAll(data.getData());
                                    lstv.setResultSize(data.getData().size());
                                } else {
                                    lstv.onLoadComplete();
                                    listgoods.addAll(data.getData());
                                    lstv.setResultSize(data.getData().size());
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cont", "");
                params.put("schoolId", schoolId);
                params.put("page", String.valueOf(pageIndex));
                params.put("typeId", typeId);
                params.put("type", "0");
                params.put("empId", "");
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


//    @Override
//    public void onClickContentItem(int position, int flag, Object object) {
//        Slide slide = lists.get(position);
//        switch (flag){
//            case 1:
//                if("1".equals(slide.getType())){
//                    //pk
//                    Intent pk = new Intent(getActivity(), PkActivity.class);
//                    startActivity(pk);
//                }
//                if("2".equals(slide.getType())){
//                    //shangpinxiangqing
//                    getGoodsByUUID(slide.getGoodsId());
//                }
//                if("3".equals(slide.getType())){
//                    //guanggao
//                    Intent adView =  new Intent(getActivity(), WebViewActivity.class);
//                    adView.putExtra("strurl", slide.getPicUrl());
//                    startActivity(adView);
//                }
//                break;
//        }
//
//    }

    //根据商品UUID
    private void getGoodsByUUID(final String goodsid) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +InternetURL.GET_GOODS_DETAIL_BYUUID_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            if (StringUtil.isJson(s)) {
                                GoodSingleDATA data = getGson().fromJson(s, GoodSingleDATA.class);
                                if (data.getCode() == 200) {
                                    Intent goodsdetail = new Intent(getActivity(), DetailGoodsActivity.class);
                                    goodsdetail.putExtra(Constants.GOODS, data.getData());
                                    startActivity(goodsdetail);
                                } else {
                                    Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", goodsid);
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


    //获得商品类别
    void getType(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +InternetURL.GET_GOODSTYPE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            GoodsTypeDATA data = getGson().fromJson(s, GoodsTypeDATA.class);
                            if (data.getCode() == 200) {
                                goodstypeList.clear();
                                goodstypeList.addAll(data.getData());
                                adaptertype.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
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


//    private void initViewPager() {
//        adapterAd = new ViewPagerAdapter(getActivity());
//        adapterAd.change(getList());
//        adapterAd.setOnClickContentItemListener(this);
//        viewpager = (ViewPager) headView.findViewById(R.id.viewpager);
//        viewpager.setAdapter(adapterAd);
//        viewpager.setOnPageChangeListener(myOnPageChangeListener);
//        initDot();
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                int next = viewpager.getCurrentItem()+1;
//                if (next > adapter.getCount()) {
//                    next = 0;
//                }
//                viewHandler.sendEmptyMessage(next);
//            }
//        };
//        viewHandler.postDelayed(runnable, autoChangeTime);
//    }
//
//    private List<Slide> getList() {
//        return lists;
//    }

//    /**
//     * 获取幻灯片
//     */
//    private void getSlide() {
//        StringRequest request = new StringRequest(
//                Request.Method.POST,
//                InternetURL.SLIDENEWS_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        if (StringUtil.isJson(s)) {
//                            SlideData data = getGson().fromJson(s, SlideData.class);
//                            if (data.getCode() == 200) {
//                                lists.addAll(data.getData());
//                                adapterAd.notifyDataSetChanged();
//                            } else {
//                                Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("schoolId", schoolId);
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                return params;
//            }
//        };
//        getRequestQueue().add(request);
//    }
//
//    // 初始化dot视图
//    private void initDot() {
//        viewGroup = (LinearLayout) headView.findViewById(R.id.viewGroup);
//        article_title = (TextView) headView.findViewById(R.id.article_title);
//        if(lists.size()!= 0) {
//            article_title.setText(lists.get(0).getDesc());//初始化新闻标题显示
//        }else{
//            article_title.setText("");
//        }
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                30, 30);
//        layoutParams.setMargins(4, 3, 4, 3);
//
//        dots = new ImageView[adapter.getCount()];
//        for (int i = 0; i < adapter.getCount(); i++) {
//
//            dot = new ImageView(getActivity());
//            dot.setLayoutParams(layoutParams);
//            dots[i] = dot;
//            dots[i].setTag(i);
//            dots[i].setOnClickListener(onClick);
//
//            if (i == 0) {
//                dots[i].setBackgroundResource(R.drawable.dotc);
//            } else {
//                dots[i].setBackgroundResource(R.drawable.dotn);
//            }
//
//            viewGroup.addView(dots[i]);
//        }
//    }
//
//    ViewPager.OnPageChangeListener myOnPageChangeListener = new ViewPager.OnPageChangeListener() {
//
//        @Override
//        public void onPageScrollStateChanged(int arg0) {
//        }
//
//        @Override
//        public void onPageScrolled(int arg0, float arg1, int arg2) {
//        }
//
//        @Override
//        public void onPageSelected(int arg0) {
//            setCurDot(arg0);
//            viewHandler.removeCallbacks(runnable);
//            viewHandler.postDelayed(runnable, autoChangeTime);
//        }
//
//    };
//    // 实现dot点击响应功能,通过点击事件更换页面
//    View.OnClickListener onClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            int position = (Integer) v.getTag();
//            setCurView(position);
//        }
//
//    };
//
//    /**
//     * 设置当前的引导页
//     */
//    private void setCurView(int position) {
//        if (position < 0 || position > adapter.getCount()) {
//            return;
//        }
//        viewpager.setCurrentItem(position);
//        if(position >= lists.size()){
//            return;
//        }else{
//            if (!StringUtil.isNullOrEmpty(lists.get(position).getDesc())){
//                titleSlide = lists.get(position).getDesc();
//                if(titleSlide.length() > 13){
//                    titleSlide = titleSlide.substring(0,12);
//                    article_title.setText(titleSlide);//当前新闻标题显示
//                }else{
//                    article_title.setText(titleSlide);//当前新闻标题显示
//                }
//            }
//        }
//    }
//
//    /**
//     * 选中当前引导小点
//     */
//    private void setCurDot(int position) {
//        for (int i = 0; i < dots.length; i++) {
//            if (position == i) {
//                dots[i].setBackgroundResource(R.drawable.dotc);
//            } else {
//                dots[i].setBackgroundResource(R.drawable.dotn);
//            }
//        }
//    }
//
//    /**
//     * 每隔固定时间切换广告栏图片
//     */
//    @SuppressLint("HandlerLeak")
//    private final Handler viewHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            setCurView(msg.what);
//        }
//
//    };



}

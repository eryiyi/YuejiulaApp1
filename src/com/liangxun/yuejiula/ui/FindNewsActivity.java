package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.NewsAdapter;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.NewsDATA;
import com.liangxun.yuejiula.entity.News;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/1
 * Time: 10:46
 * 类的功能、说明写在此处.
 */
public class FindNewsActivity extends BaseActivity implements View.OnClickListener, OnClickContentItemListener {
    private ImageView news_menu;//返回
    private PullToRefreshListView news_lstv;
    private NewsAdapter adapter;
    private List<News> news;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private RequestQueue mRequestQueue;

    private String schoolId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schoolnews);
        mRequestQueue = Volley.newRequestQueue(this);
        initView();
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        initData(pageIndex);
    }

    private void initView() {
        news = new ArrayList<News>();
        news_menu = (ImageView) this.findViewById(R.id.news_menu);
        news_menu.setOnClickListener(this);
        news_lstv = (PullToRefreshListView) this.findViewById(R.id.news_lstv);

        adapter = new NewsAdapter(news, FindNewsActivity.this);
        adapter.setOnClickContentItemListener(this);
        news_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        news_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(FindNewsActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData(pageIndex);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(FindNewsActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData(pageIndex);
            }
        });
        news_lstv.setAdapter(adapter);
        news_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detail = new Intent(FindNewsActivity.this, DetailNewsActivity.class);
                News record = news.get(position - 1);
                detail.putExtra(Constants.NEWS, record);
                startActivity(detail);
            }
        });
    }

    private void initData(final int pageIndex) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_NEWS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            NewsDATA data = getGson().fromJson(s, NewsDATA.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    news.clear();
                                }
                                news.addAll(data.getData());
                                news_lstv.onRefreshComplete();
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(FindNewsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(FindNewsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(FindNewsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("news_school_id", schoolId);
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
        mRequestQueue.add(request);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.news_menu:
                finish();
                break;
        }
    }

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        switch (flag) {
            case 1:
                Toast.makeText(this, "发布者", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

package com.liangxun.yuejiula.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.ItemDianpuAdapter;
import com.liangxun.yuejiula.adapter.ItemFindAdapter;
import com.liangxun.yuejiula.base.BaseFragment;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.DianpuData;
import com.liangxun.yuejiula.data.SchoolFindData;
import com.liangxun.yuejiula.entity.EmpDianpu;
import com.liangxun.yuejiula.entity.SchoolFind;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.ui.*;
import com.liangxun.yuejiula.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发现
 */
public class FindFragment extends BaseFragment implements View.OnClickListener {
    private TextView find_jianzhi;//兼职
    private TextView find_news;//新闻
    private TextView find_notice;//告示
    private TextView find_pk;//pk
    private TextView find_tv;
    private TextView find_videos;
//    private ImageView find_pk_img;
    private LinearLayout headView;

    //下拉刷新
    private PullToRefreshListView lstv ;
    private ItemFindAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private List<SchoolFind> listgoods = new ArrayList<SchoolFind>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find, null);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        //头部文件
        headView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.find_header, null);
        find_jianzhi = (TextView) headView.findViewById(R.id.find_jianzhi);
        find_jianzhi.setOnClickListener(this);
        find_news = (TextView) headView.findViewById(R.id.find_news);
        find_news.setOnClickListener(this);
        find_notice = (TextView) headView.findViewById(R.id.find_notices);
        find_notice.setOnClickListener(this);
        find_pk = (TextView) headView.findViewById(R.id.find_pk);
        find_tv = (TextView) headView.findViewById(R.id.find_tv);
        find_videos = (TextView) headView.findViewById(R.id.find_videos);
        find_pk.setOnClickListener(this);
        find_videos.setOnClickListener(this);
        find_tv.setOnClickListener(this);
//        find_pk_img = (ImageView) headView.findViewById(R.id.find_pk_img);
//        find_pk_img.setOnClickListener(this);

        lstv = (PullToRefreshListView) view.findViewById(R.id.lstv);//列表

        ListView listView = lstv.getRefreshableView();

        listView.addHeaderView(headView);

        adapter = new ItemFindAdapter(listgoods, getActivity());
        lstv.setAdapter(adapter);
        lstv.setMode(PullToRefreshBase.Mode.BOTH);
        lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                lstv.onRefreshComplete();
//                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                lstv.onRefreshComplete();
//                initData();
            }
        });
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    SchoolFind dianpu = listgoods.get(position-2);
                    if (dianpu != null) {
                        Intent detail = new Intent(getActivity(), WebViewActivity.class);
                        detail.putExtra("strurl", dianpu.getWww_url());
                        startActivity(detail);
                    }
                } catch (Exception e) {

                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.find_jianzhi:
                Intent part = new Intent(getActivity(), PartsActivity.class);
                startActivity(part);
                break;
            case R.id.find_news:
                Intent news = new Intent(getActivity(), NewsFragmentActivtiy.class);
                startActivity(news);
                break;
            case R.id.find_notices:
                Intent notice = new Intent(getActivity(), NoticeActivity.class);
                startActivity(notice);
                break;
            case R.id.find_pk:
//            case R.id.find_pk_img:
                Intent pk = new Intent(getActivity(), PkActivity.class);
                startActivity(pk);
                break;
            case R.id.find_tv:
                Intent find_tv = new Intent(getActivity(), FindVideosActivity.class);
                startActivity(find_tv);
                break;
            case R.id.find_videos:
                //视频
                Intent videoView =  new Intent(getActivity(), VideosActivity.class);
                startActivity(videoView);
                break;
        }
    }

    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.FIND_WWW_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SchoolFindData data = getGson().fromJson(s, SchoolFindData.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    listgoods.clear();
                                }
                                listgoods.addAll(data.getData());
                                adapter.notifyDataSetChanged();
                                lstv.onRefreshComplete();
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

}

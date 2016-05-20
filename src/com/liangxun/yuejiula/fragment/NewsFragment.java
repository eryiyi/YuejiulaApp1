package com.liangxun.yuejiula.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
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
import com.liangxun.yuejiula.adapter.NewsAdapter;
import com.liangxun.yuejiula.base.BaseNewsFragment;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.NewsDATA;
import com.liangxun.yuejiula.entity.News;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.ui.DetailNewsActivity;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewsFragment extends BaseNewsFragment {
    public ProgressDialog progressDialog;
    Activity activity;
    ArrayList<News> newsList = new ArrayList<News>();
    PullToRefreshListView mListView;
    NewsAdapter mAdapter;
    String typeId;
    String typeName;

    ImageView detail_loading;
    public final static int SET_NEWSLIST = 0;
    private TextView item_textview;
    private static boolean IS_REFRESH = true;
    private int pageIndex = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle args = getArguments();
        registerBoradcastReceiver();
        typeId = args != null ? args.getString(Constants.NEWS_TYPEID_UUID) : "";
        typeName = args != null ? args.getString(Constants.NEWS_TYPEID_NAME) : "";
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.news_fragment_lstv, null);
        initView(view);
        item_textview.setText(typeName);
        initData();
        return view;
    }

    private void initView(View view) {
        mListView = (PullToRefreshListView) view.findViewById(R.id.mListView);
        item_textview = (TextView) view.findViewById(R.id.item_textview);
        detail_loading = (ImageView) view.findViewById(R.id.detail_loading);
        mAdapter = new NewsAdapter(newsList, activity);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                progressDialog = new ProgressDialog(getActivity() );

                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                Resources res = getActivity().getBaseContext().getResources();
                String message = res.getString(R.string.check_publish).toString();
                progressDialog = new ProgressDialog(getActivity() );

                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                initData();
            }
        });
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent detail = new Intent(getActivity(), DetailNewsActivity.class);
                News record = newsList.get(position - 1);
                detail.putExtra(Constants.NEWS, record);
                startActivity(detail);

//                try {
//                    Intent videoPlayer = new Intent(getActivity(), VideoPalyerActivity.class);
//                    News videos = newsList.get(position - 1);
//                    videoPlayer.putExtra(Constants.VIDEO_URL, videos.getLink_url());
//                    Intent intent = new Intent();
//                    intent.setAction("android.intent.action.VIEW");
//                    Uri content_url = Uri.parse(videos.getLink_url());
//                    intent.setData(content_url);
//                    startActivity(intent);
//                } catch (Exception e) {
//
//                }

            }
        });

    }

    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_NEWS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            NewsDATA data = getGson().fromJson(s, NewsDATA.class);
                            if (data.getCode() == 200) {
                                newsList.addAll(data.getData());
                                if (IS_REFRESH) {
                                    newsList.clear();
                                }
                                newsList.addAll(data.getData());
                                mListView.onRefreshComplete();
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("typeId", typeId);
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

    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.SEND_NEWS_DELETE_SUCCESS)) {
                progressDialog = new ProgressDialog(getActivity());

                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                initData();
            }
        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SEND_NEWS_DELETE_SUCCESS);//设置下拉按钮的广播事件
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

}

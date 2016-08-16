package com.liangxun.yuejiula.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.ChampionAdapter;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.base.BasePkFragment;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.ChampionsDATA;
import com.liangxun.yuejiula.entity.Champion;
import com.liangxun.yuejiula.entity.PKWork;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.ui.Pk_new_detailsActivity;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.CustomProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/6.
 * 冠军榜
 */
public class PkWinnerFragment extends BasePkFragment implements View.OnClickListener, OnClickContentItemListener {
    //动态listview
    private PullToRefreshListView home_lstv;
    //动态适配器
    private ChampionAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    List<Champion> lists = new ArrayList<Champion>();
    private String schoolId = "";
    private String empId = "";
    private String type_pk = "0";//查询的时候  区分是查询全部的还是自己学校的
    private ImageView search_null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pk_guanjun, null);
        empId = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        type_pk = getGson().fromJson(getSp().getString(Constants.PK_SEARCH_ALL_OR_MINE, ""), String.class);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void onClickContentItem(int position, int flag, Object object) {

    }

    @Override
    public void onClick(View view) {

    }

    public void initView(View view) {
        home_lstv = (PullToRefreshListView) view.findViewById(R.id.home_lstv);
        search_null = (ImageView) view.findViewById(R.id.search_null);
        adapter = new ChampionAdapter(lists, getActivity());
        home_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        home_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
                progressDialog = new ProgressDialog(getActivity() );

                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                initData();
            }
        });
        home_lstv.setAdapter(adapter);
        home_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detail = new Intent(getActivity(), Pk_new_detailsActivity.class);
                PKWork pkWork = new PKWork();
                Champion champion = lists.get(position - 1);
                pkWork.setId(champion.getZpId());
                pkWork.setEmpName(champion.getEmpName());
                pkWork.setEmpCover(champion.getEmpCover());
                pkWork.setTitle(champion.getZpContent());
                pkWork.setType(champion.getZpType());
                pkWork.setPicUrl(champion.getPicUrl());
                pkWork.setVideoUrl(champion.getVideoUrl());
                pkWork.setDateline(champion.getDateline());
                pkWork.setEmpId(champion.getEmpId());
                pkWork.setZanNum(champion.getZanNum());
                pkWork.setPlNum(champion.getPlNum());
                pkWork.setSchoolName(champion.getSchoolName());
                pkWork.setSchoolId(champion.getSchoolId());
                pkWork.setZtId(champion.getThemeId());
                pkWork.setIsChampion("1");//是冠军
                if (StringUtil.isNullOrEmpty(champion.getIsSure())) {
                    pkWork.setIsSure("0");
                } else {
                    pkWork.setIsSure(champion.getIsSure());
                }
                detail.putExtra(Constants.PK_WORD_INFO, pkWork);
                detail.putExtra(Constants.PK_CHAMPION_UUID, champion.getId());
                startActivity(detail);
            }
        });

    }

    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.PK_GET_CHAMPION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            ChampionsDATA data = getGson().fromJson(s, ChampionsDATA.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    lists.clear();
                                }
                                lists.addAll(data.getData());
                                home_lstv.onRefreshComplete();
                                adapter.notifyDataSetChanged();
                                if (lists.size() == 0) {
                                    search_null.setVisibility(View.VISIBLE);
                                    home_lstv.setVisibility(View.GONE);
                                } else {
                                    search_null.setVisibility(View.GONE);
                                    home_lstv.setVisibility(View.VISIBLE);
                                }
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
                if (type_pk != null) {
                    if (type_pk.equals(Constants.MOOD_TYPE)) {
                        //全部的
                        params.put("schoolId", "");
                    }
                    if (type_pk.equals(Constants.RECORD_TYPE)) {
                        //我的学校
                        params.put("schoolId", schoolId);
                    }
                }
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
            if (action.equals(Constants.PK_SEARCH_ALL_OR_MINE)) {
                String pk_switch = intent.getExtras().getString(Constants.PK_SWITCH);
                type_pk = pk_switch;
                //刷新内容
                IS_REFRESH = true;
                pageIndex = 1;
                progressDialog =  new CustomProgressDialog(getActivity(), "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.show();
                initData();
            }
        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.PK_SEARCH_ALL_OR_MINE);
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }
}

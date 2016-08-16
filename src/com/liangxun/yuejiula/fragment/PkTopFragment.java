package com.liangxun.yuejiula.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.adapter.PkNewAdapter;
import com.liangxun.yuejiula.base.BasePkFragment;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.PkNewDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.PKWork;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.ui.Pk_new_detailsActivity;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.CustomProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/6.
 */
public class PkTopFragment extends BasePkFragment implements View.OnClickListener, OnClickContentItemListener {
    private PkNewAdapter adapter;
    private EditText searchText;//搜索框
    private ImageView deleteContSearch;//清除
    private String content = "";
    private String schoolId = "";
    private String empId = "";
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private PullToRefreshListView pklstv;

    ArrayList<PKWork> lists;
    private String type_pk = "0";//查询的时候  区分是查询全部的还是自己学校的
    private int tmpPosition;
    private ImageView search_null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pk_zuixin, null);
        empId = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        type_pk = getGson().fromJson(getSp().getString(Constants.PK_SEARCH_ALL_OR_MINE, ""), String.class);
        initView(view);
        getData();
        return view;
    }

    private void initView(View view) {
        lists = new ArrayList<PKWork>();
        pklstv = (PullToRefreshListView) view.findViewById(R.id.pk_new_lstv);
        searchText = (EditText) view.findViewById(R.id.searchText);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                content = searchText.getText().toString();//要搜的内容
                if (!StringUtil.isNullOrEmpty(content)) {
                    deleteContSearch.setVisibility(View.VISIBLE);
                } else {
                    deleteContSearch.setVisibility(View.GONE);
                }
                progressDialog =  new CustomProgressDialog(getActivity(), "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.show();
                getData();
            }
        });
        deleteContSearch = (ImageView) view.findViewById(R.id.deleteContSearch);
        deleteContSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText.setText("");
            }
        });
        deleteContSearch.setVisibility(View.GONE);
        adapter = new PkNewAdapter(lists, getActivity());
        adapter.setOnClickContentItemListener(this);
        pklstv.setMode(PullToRefreshBase.Mode.BOTH);
        pklstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                progressDialog =  new CustomProgressDialog(getActivity(), "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.show();
                getData();
            }
        });
        pklstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detail = new Intent(getActivity(), Pk_new_detailsActivity.class);
                PKWork pkWork = lists.get(position - 1);
                detail.putExtra(Constants.PK_WORD_INFO, pkWork);
                startActivity(detail);
            }
        });
        pklstv.setAdapter(adapter);
        search_null = (ImageView) view.findViewById(R.id.search_null);
    }

    PKWork pkWork;

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        pkWork = lists.get(position);
        tmpPosition = position;
        switch (flag) {
            case 1:
                progressDialog = new ProgressDialog(getActivity() );

                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                zan_click();
                break;
        }
    }

    //投票
    private void zan_click() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.PK_CLICK_LIKE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                //赞+1
                                Toast.makeText(getActivity(), R.string.pk_zan_success, Toast.LENGTH_SHORT).show();
//                                pkWork.setZanNum(String.valueOf(Integer.parseInt(pkWork.getZanNum()) + 1 ));
                                lists.get(tmpPosition).setZanNum(String.valueOf(Integer.parseInt(pkWork.getZanNum()) + 1));
                                adapter.notifyDataSetChanged();
                            } else if (data.getCode() == 2) {
                                Toast.makeText(getActivity(), R.string.pk_zan_error_one, Toast.LENGTH_SHORT).show();
                            } else if (data.getCode() == 3) {
                                Toast.makeText(getActivity(), R.string.pk_zan_error_three, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), R.string.pk_zan_error_two, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.pk_zan_error_two, Toast.LENGTH_SHORT).show();
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), R.string.pk_zan_error_two, Toast.LENGTH_SHORT).show();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("zpId", pkWork.getId());
                params.put("empId", empId);
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
    public void onClick(View view) {
    }

    private void getData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.GET_NEW_PK_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            PkNewDATA data = getGson().fromJson(s, PkNewDATA.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    lists.clear();
                                }
                                lists.addAll(data.getData());
                                pklstv.onRefreshComplete();
                                adapter.notifyDataSetChanged();
                                if (lists.size() == 0) {
                                    search_null.setVisibility(View.VISIBLE);
                                    pklstv.setVisibility(View.GONE);
                                } else {
                                    search_null.setVisibility(View.GONE);
                                    pklstv.setVisibility(View.VISIBLE);
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
                params.put("empId", "");
                params.put("zan", "1");
                params.put("keyWords", content);
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
                getData();
            }
            if (action.equals(Constants.PK_SEND_SUCCESS_URL)) {
                //刷新内容
                IS_REFRESH = true;
                pageIndex = 1;
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                getData();
            }
            if (action.equals(Constants.PK_SEND_COMMENT_SUCCESS)) {
                //刷新内容,评论+1
                String recordId =  intent.getExtras().getString("pkId");
                for(PKWork record:lists){
                    if(record.getId().equals(recordId)){
                        record.setPlNum(String.valueOf(Integer.parseInt(record.getPlNum())+1));
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
            if (action.equals(Constants.PK_SEND_FAVOUR_SUCCESS)) {
                //刷新赞,赞+1
                String recordId =  intent.getExtras().getString("pkId");
                for(PKWork record:lists){
                    if(record.getId().equals(recordId)){
                        record.setZanNum(String.valueOf(Integer.parseInt(record.getZanNum())+1));
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.PK_SEARCH_ALL_OR_MINE);
        myIntentFilter.addAction(Constants.PK_SEND_SUCCESS_URL);
        myIntentFilter.addAction(Constants.PK_SEND_FAVOUR_SUCCESS);//pk赞成功
        myIntentFilter.addAction(Constants.PK_SEND_COMMENT_SUCCESS);//pk评论成功
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }
}



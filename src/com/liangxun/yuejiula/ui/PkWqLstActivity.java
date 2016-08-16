package com.liangxun.yuejiula.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.adapter.PkNewAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.PkNewDATA;
import com.liangxun.yuejiula.entity.PKTheme;
import com.liangxun.yuejiula.entity.PKWork;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.CustomProgressDialog;
import com.liangxun.yuejiula.widget.popview.PkFootPopWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/16.
 */
public class PkWqLstActivity extends BaseActivity implements View.OnClickListener, OnClickContentItemListener {
    private PullToRefreshListView pklstv;
    private ImageView pk_title_back;
    private PkNewAdapter adapter;
    private EditText searchText;
    private ImageView deleteContSearch;
    List<PKWork> lists = new ArrayList<PKWork>();//从后台取得数据集合
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private String content = "";
    private String schoolId = "";
    private String empId = "";//当前登陆者的ID

    private int tmpPosition;
    private ImageView search_null;
    private String type_pk = "0";//查询的时候  区分是查询全部的还是自己学校的
    private PKTheme pkTheme;
    private PkFootPopWindow deleteWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
        empId = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        type_pk = getGson().fromJson(getSp().getString(Constants.PK_SEARCH_ALL_OR_MINE, ""), String.class);
        pkTheme = (PKTheme) getIntent().getExtras().get(Constants.PK_ADD_PRIZE_THEME_WQ);
        setContentView(R.layout.pk_wqlstv);
        initView();
        progressDialog = new CustomProgressDialog(PkWqLstActivity.this, "正在加载中",R.anim.custom_dialog_frame);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        getData();
    }

    private void initView() {
        pklstv = (PullToRefreshListView) this.findViewById(R.id.pk_new_lstv);
        pklstv.setOnClickListener(this);
        pk_title_back = (ImageView) this.findViewById(R.id.pk_title_back);
        pk_title_back.setOnClickListener(this);
        searchText = (EditText) this.findViewById(R.id.searchText);
        deleteContSearch = (ImageView) this.findViewById(R.id.deleteContSearch);
        deleteContSearch.setOnClickListener(this);
        deleteContSearch.setVisibility(View.GONE);
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
                progressDialog = new CustomProgressDialog(PkWqLstActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                getData();
            }
        });
        adapter = new PkNewAdapter(lists, PkWqLstActivity.this);
        adapter.setOnClickContentItemListener(this);
        pklstv.setMode(PullToRefreshBase.Mode.BOTH);
        pklstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;

                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;

                getData();
            }
        });
        //跳转到详细页面
        pklstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detail = new Intent(PkWqLstActivity.this, Pk_new_detailsActivity.class);
                PKWork pkWork = lists.get(position - 1);
                detail.putExtra(Constants.PK_WORD_INFO, pkWork);
                startActivity(detail);
            }
        });
        pklstv.setAdapter(adapter);
        search_null = (ImageView) this.findViewById(R.id.search_null);
        search_null.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pk_title_back:
                finish();
                break;
            case R.id.deleteContSearch:
                searchText.setText("");
                break;
        }
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
                                Toast.makeText(PkWqLstActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PkWqLstActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PkWqLstActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                params.put("zan", "1");
                params.put("themeId", pkTheme.getId());
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

    //底部菜单
    public void onFootMenuPopupButtonClick(View view) {
        showSelectImageDialog();
    }

    private void showSelectImageDialog() {
        deleteWindow = new PkFootPopWindow(PkWqLstActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(PkWqLstActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.pk_foot_all: {
                    //全部的
                    save(Constants.PK_SEARCH_ALL_OR_MINE, Constants.MOOD_TYPE);
                    //调用广播，刷新主页
                    Intent intent = new Intent(Constants.PK_SEARCH_ALL_OR_MINE);
                    intent.putExtra(Constants.PK_SWITCH, Constants.MOOD_TYPE);
                    sendBroadcast(intent);
                }
                break;
                case R.id.pk_foot_mine: {
                    //我的
                    save(Constants.PK_SEARCH_ALL_OR_MINE, Constants.RECORD_TYPE);
                    //调用广播，刷新主页
                    Intent intent = new Intent(Constants.PK_SEARCH_ALL_OR_MINE);
                    intent.putExtra(Constants.PK_SWITCH, Constants.RECORD_TYPE);
                    sendBroadcast(intent);
                }
                break;
                default:
                    break;
            }
        }

    };


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
                progressDialog =  new CustomProgressDialog(PkWqLstActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                getData();

            }

        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.PK_SEARCH_ALL_OR_MINE);
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
        if (!"1".equals(pkTheme.getIsUse())) {
            //如果不是正在进行的
            Toast.makeText(PkWqLstActivity.this, R.string.pk_error_wq, Toast.LENGTH_SHORT).show();
            return;
        }
    }
}

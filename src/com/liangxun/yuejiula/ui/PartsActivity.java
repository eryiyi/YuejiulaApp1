package com.liangxun.yuejiula.ui;

import android.app.ProgressDialog;
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
import com.liangxun.yuejiula.adapter.PartTimeAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.PartTimeDATA;
import com.liangxun.yuejiula.data.PartTimeTypeDATA;
import com.liangxun.yuejiula.entity.PartTime;
import com.liangxun.yuejiula.entity.PartTimeType;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.CustomProgressDialog;
import com.liangxun.yuejiula.widget.popview.MorePartTypePopWindow;
import com.liangxun.yuejiula.widget.popview.PublishGoodsInfoPopWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/3/7
 * Time: 9:11
 * 类的功能、说明写在此处.
 */
public class PartsActivity extends BaseActivity implements View.OnClickListener, OnClickContentItemListener {
    private ImageView pull_down_button;
    private ImageView back;//返回
    private ImageView search_null;
    private EditText searchText;//搜索框
    private ImageView deleteContSearch;//清除
    private PullToRefreshListView parttime_lstv;//列表
    private PartTimeAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private String content = "";
    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID
    private List<PartTime> goods;
    private ImageView foot_part_button;//底部点击按钮
    private MorePartTypePopWindow deleteWindow;
    private List<PartTimeType> goodstypeList = new ArrayList<PartTimeType>();
    ;
    private String typeid = "";

    private String type = "";//登陆者类别
    private PublishGoodsInfoPopWindow infoWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
        setContentView(R.layout.parttimetype);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        type = getGson().fromJson(getSp().getString(Constants.EMPTYPE, ""), String.class);
        initView();
        progressDialog = new CustomProgressDialog(PartsActivity.this, "正在加载中",R.anim.custom_dialog_frame);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        initData();
        getTypeData();

    }

    private void initView() {
        goods = new ArrayList<PartTime>();
        search_null = (ImageView) this.findViewById(R.id.search_null);
        back = (ImageView) this.findViewById(R.id.parttype_back);
        back.setOnClickListener(this);
        searchText = (EditText) this.findViewById(R.id.searchText);
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
                initData();
            }
        });

        deleteContSearch = (ImageView) this.findViewById(R.id.deleteContSearch);
        deleteContSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText.setText("");
            }
        });
        deleteContSearch.setVisibility(View.GONE);
        parttime_lstv = (PullToRefreshListView) this.findViewById(R.id.parttime_lstv);
        adapter = new PartTimeAdapter(goods, PartsActivity.this);
        parttime_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        parttime_lstv.setAdapter(adapter);
        parttime_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(PartsActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
//                foot_part_button.setVisibility(View.GONE);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(PartsActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
//                foot_part_button.setVisibility(View.GONE);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData();
            }
        });

        parttime_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detail = new Intent(PartsActivity.this, PartTimeDetailActivity.class);
                PartTime record = goods.get(position - 1);
                detail.putExtra(Constants.PART_INFO, record);
                startActivity(detail);
            }
        });
        foot_part_button = (ImageView) this.findViewById(R.id.foot_part_button);
        foot_part_button.setOnClickListener(this);
        pull_down_button = (ImageView) this.findViewById(R.id.pull_down_button);
        pull_down_button.setOnClickListener(this);
    }

    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.GET_PARTTIME_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            PartTimeDATA data = getGson().fromJson(s, PartTimeDATA.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    goods.clear();
                                }
                                goods.addAll(data.getData());
                                if (goods.size() == 0) {
                                    search_null.setVisibility(View.VISIBLE);
                                } else {
                                    search_null.setVisibility(View.GONE);
                                }
                                parttime_lstv.onRefreshComplete();
                                adapter.notifyDataSetChanged();
//                                foot_part_button.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(PartsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PartsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PartsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("typeId", typeid);
                params.put("keyWords", content);
                params.put("schoolId", schoolId);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parttype_back:
                finish();
                break;
            case R.id.foot_part_button:
                deleteWindow = new MorePartTypePopWindow(PartsActivity.this, goodstypeList);
                deleteWindow.setOnClickContentItemListener(this);
                deleteWindow.showAtLocation(PartsActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.pull_down_button:
                if (type.equals("0")) {
                    showInfoDialog();
                } else {
                    Intent part = new Intent(PartsActivity.this, PublishPartTimeActivity.class);
                    startActivity(part);
                }

                break;
        }
    }

    // 提示
    private void showInfoDialog() {
        infoWindow = new PublishGoodsInfoPopWindow(PartsActivity.this, itemsOnClicktwo);
        //显示窗口
        infoWindow.showAtLocation(PartsActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private View.OnClickListener itemsOnClicktwo = new View.OnClickListener() {
        public void onClick(View v) {
            infoWindow.dismiss();
            switch (v.getId()) {
                case R.id.button_add: {
                    Intent intent = new Intent(PartsActivity.this, PublishPartTimeActivity.class);
                    startActivity(intent);
                }
                break;
                case R.id.button_kaitong://开通商家权限   先去查看所有代理商
                {
                    Intent intent = new Intent(PartsActivity.this, ChengbaoshangActivity.class);
                    startActivity(intent);
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
            if (action.equals(Constants.SEND_PART_SUCCESS)) {
                //刷新内容
//                schoolId = "";
                pageIndex = 1;
                IS_REFRESH = true;
                initData();
            }
        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SEND_PART_SUCCESS);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    PartTimeType parttype;

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        parttype = goodstypeList.get(position);
        if(parttype.getId().equals("0")){
            typeid = "";
        }else {
            typeid = parttype.getId();
        }
        pageIndex = 1;
        initData();
        if(deleteWindow != null){
            deleteWindow.dismiss();
        }

    }

    private void getTypeData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.GET_PARTTIMETYPE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            PartTimeTypeDATA data = getGson().fromJson(s, PartTimeTypeDATA.class);
                            if (data.getCode() == 200) {
                                goodstypeList.clear();
                                goodstypeList.add(new PartTimeType("0","全部","picurl"));
                                goodstypeList.addAll(data.getData());
                            } else {
                                Toast.makeText(PartsActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PartsActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PartsActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
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

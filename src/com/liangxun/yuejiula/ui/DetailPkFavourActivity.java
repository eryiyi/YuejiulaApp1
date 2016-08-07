package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.PkZanAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.PkZanDATA;
import com.liangxun.yuejiula.entity.PkZan;
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
 * Date: 2015/2/6
 * Time: 13:50
 * Pk的赞列表
 */
public class DetailPkFavourActivity extends BaseActivity implements View.OnClickListener {
    private PullToRefreshListView detail_favour_lstv;//列表
    private ImageView detail_favour_back;//返回
    private TextView detail_favour_title;//标题
    private PkZanAdapter adapter;
    List<PkZan> favours;
    private static boolean IS_REFRESH = true;
    private int pageIndex = 1;
    private String recordId = "";
    private String emp_id = "";//当前登陆者UUID
    private Map<String, Object> mapData = new HashMap<String, Object>();//接收查询作品投票数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_favour_xml);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        recordId = getIntent().getExtras().getString(Constants.PK_WORD_INFO_UUID);
        initView();
        getFavour();
    }

    private void initView() {
        favours = new ArrayList<PkZan>();
        detail_favour_lstv = (PullToRefreshListView) this.findViewById(R.id.detail_favour_lstv);
        detail_favour_back = (ImageView) this.findViewById(R.id.detail_favour_back);
        detail_favour_back.setOnClickListener(this);
        detail_favour_title = (TextView) this.findViewById(R.id.detail_favour_title);
        adapter = new PkZanAdapter(favours, this);
//        adapter.setOnClickContentItemListener(this);
        detail_favour_lstv.setAdapter(adapter);
        detail_favour_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        detail_favour_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(DetailPkFavourActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                getFavour();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(DetailPkFavourActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                getFavour();
            }
        });

        detail_favour_lstv.setAdapter(adapter);
        detail_favour_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PkZan favour = favours.get(i-1);
                if (!emp_id.equals(favour.getEmpId())) {
                    Intent profile = new Intent(DetailPkFavourActivity.this, ProfilePersonalActivity.class);
                    profile.putExtra(Constants.EMPID, favour.getEmpId());
                    startActivity(profile);
                } else {
                    Intent profile = new Intent(DetailPkFavourActivity.this, UpdateProfilePersonalActivity.class);
                    startActivity(profile);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_favour_back:
                finish();
                break;
        }
    }

    private void getFavour() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.GET_PK_FAVOUR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            PkZanDATA data = getGson().fromJson(s, PkZanDATA.class);
                            if (data.getCode() == 200) {
//                                if (IS_REFRESH) {
                                    favours.clear();
//                                }
                                favours.addAll(data.getData().getList());
                                detail_favour_lstv.onRefreshComplete();
                                detail_favour_title.setText(data.getData().getCount() + "人给TA投票");
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(DetailPkFavourActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailPkFavourActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailPkFavourActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("zpId", recordId);
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
}

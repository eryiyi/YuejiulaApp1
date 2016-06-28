package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
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
import com.liangxun.yuejiula.adapter.ItemFavourDianpuAdapter;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.DianPuFavourData;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.DianPuFavour;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.popview.DeletePopWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/11.
 * 我的收藏
 */
public class MineFavoursDianpuActivity extends BaseActivity implements View.OnClickListener, OnClickContentItemListener {
    private ImageView minegoods_back;
    private ImageView search_null;

    private PullToRefreshListView classtype_lstv;//列表
    private ItemFavourDianpuAdapter adapter;
    private List<DianPuFavour> goods;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID

    private int tmpSelected;//暂时存UUID  删除用

    private DeletePopWindow deleteWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_favours_xml);
        initView();
        initData();
    }

    private void initView() {
        goods = new ArrayList<DianPuFavour>();
        search_null = (ImageView) this.findViewById(R.id.search_null);
        minegoods_back = (ImageView) this.findViewById(R.id.minegoods_back);
        minegoods_back.setOnClickListener(this);

        classtype_lstv = (PullToRefreshListView) this.findViewById(R.id.minegoods_lstv);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        adapter = new ItemFavourDianpuAdapter(goods, MineFavoursDianpuActivity.this);

        classtype_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        classtype_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MineFavoursDianpuActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MineFavoursDianpuActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData();
            }
        });
        classtype_lstv.setAdapter(adapter);
        adapter.setOnClickContentItemListener(this);
        classtype_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DianPuFavour record = goods.get(position - 1);
                Intent deitalV = new Intent(MineFavoursDianpuActivity.this, DianpuDetailActivity.class);
                deitalV.putExtra("emp_id", record.getEmp_id());
                startActivity(deitalV);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.minegoods_back:
                finish();
                break;
        }
    }

    //收藏商品列表
    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.APP_GET_FAVOUR_DIANPU_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            DianPuFavourData data = getGson().fromJson(s, DianPuFavourData.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    goods.clear();
                                }
                                goods.addAll(data.getData());
                                classtype_lstv.onRefreshComplete();
                                if (goods.size() == 0) {
                                    search_null.setVisibility(View.VISIBLE);
                                } else {
                                    search_null.setVisibility(View.GONE);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MineFavoursDianpuActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineFavoursDianpuActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineFavoursDianpuActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
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

    DianPuFavour good;

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        good = goods.get(position);
        switch (flag) {
            case 1:
                //删除
                tmpSelected = position;
                showSelectImageDialog();
                break;
        }
    }

    // 选择是否删除
    private void showSelectImageDialog() {
        deleteWindow = new DeletePopWindow(MineFavoursDianpuActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(MineFavoursDianpuActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void delete() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.DELETE_DIANPU_FAVOUR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(MineFavoursDianpuActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                                goods.remove(tmpSelected);
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MineFavoursDianpuActivity.this, "取消收藏失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineFavoursDianpuActivity.this, "取消收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineFavoursDianpuActivity.this,"取消收藏失败", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("favour_no", good.getFavour_no());
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

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_sure:
                    delete();
                    break;
                default:
                    break;
            }
        }

    };



}

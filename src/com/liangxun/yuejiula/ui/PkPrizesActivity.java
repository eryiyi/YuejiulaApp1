package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.MinePrizesAdapter;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.PkPrizesData;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.PkPrize;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.CustomProgressDialog;
import com.liangxun.yuejiula.widget.popview.DeletePopWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/11.
 * 根据圈主ID查找所有设置的奖品
 */
public class PkPrizesActivity extends BaseActivity implements View.OnClickListener, OnClickContentItemListener {
    private PullToRefreshListView prize_lstv;
    private ImageView pk_menu;
    private MinePrizesAdapter adapter;
    List<PkPrize> lists = new ArrayList<PkPrize>();
    private String emp_id = "";
    private String typeId = "";
    private String schoolId = "";


    private static boolean IS_REFRESH = true;
    private int page = 1;
    private ImageView search_null;
    private DeletePopWindow deleteWindow;
    private int tmpSelected;
    private PkPrize pkPrize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pk_prizes_lst);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        typeId = getGson().fromJson(getSp().getString(Constants.EMPTYPE, ""), String.class);
        initView();
        progressDialog = new CustomProgressDialog(PkPrizesActivity.this, "正在加载中",R.anim.custom_dialog_frame);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        getPrize();
    }

    private void initView() {
        prize_lstv = (PullToRefreshListView) this.findViewById(R.id.prize_lstv);
        pk_menu = (ImageView) this.findViewById(R.id.pk_menu);
        pk_menu.setOnClickListener(this);
        adapter = new MinePrizesAdapter(lists, PkPrizesActivity.this);
        search_null = (ImageView) this.findViewById(R.id.search_null);
        search_null.setVisibility(View.GONE);
        prize_lstv.setAdapter(adapter);
        adapter.setOnClickContentItemListener(this);
        prize_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        prize_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                page = 1;

                getPrize();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                page++;

                getPrize();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pk_menu:
                finish();
                break;
        }
    }

    //查询奖品
    private void getPrize() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.PK_GET_PRIZES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            PkPrizesData data = getGson().fromJson(s, PkPrizesData.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    lists.clear();
                                }
                                lists.addAll(data.getData());
                                prize_lstv.onRefreshComplete();
                                if (lists.size() == 0) {
                                    search_null.setVisibility(View.VISIBLE);
                                    prize_lstv.setVisibility(View.GONE);
                                } else {
                                    search_null.setVisibility(View.GONE);
                                    prize_lstv.setVisibility(View.VISIBLE);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(PkPrizesActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PkPrizesActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(PkPrizesActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empId", emp_id);
                params.put("page", String.valueOf(page));
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
    public void onClickContentItem(int position, int flag, Object object) {
        pkPrize = lists.get(position);
        tmpSelected = position;
        switch (flag) {
            case 1:
                //点击了删除按钮
                showSelectImageDialog();
                break;
        }
    }

    // 选择是否删除
    private void showSelectImageDialog() {
        deleteWindow = new DeletePopWindow(PkPrizesActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(PkPrizesActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //删除商品方法
    private void delete() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.PK_DELETE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(PkPrizesActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                                lists.remove(tmpSelected);
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(PkPrizesActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PkPrizesActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(PkPrizesActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", pkPrize.getId());
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

    public void addPic(View view){
        Intent intent = new Intent(PkPrizesActivity.this, PkTitleActivity.class);
        startActivity(intent);
    }

}

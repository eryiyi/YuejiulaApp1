package com.liangxun.yuejiula.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.MainActivity;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.FenghaofengqAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.EmpsDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.ContractSchool;
import com.liangxun.yuejiula.entity.Emp;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/6/11.
 */
public class FenghaoFengqunActivity extends BaseActivity implements View.OnClickListener {
    private TextView text_one;
    private TextView text_two;
    private PullToRefreshListView lstv;
    private FenghaofengqAdapter adapter;
    public static boolean IS_REFRESH = true;

    private List<Emp> lists = new ArrayList<Emp>();
    private String type="0";//0是封号 1是封群
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fenghaofengqun_activity);
        initView();
        initData();
    }

    private void initView() {
        text_one = (TextView) this.findViewById(R.id.text_one);
        text_two = (TextView) this.findViewById(R.id.text_two);

        text_one.setOnClickListener(this);
        text_two.setOnClickListener(this);
        lstv = (PullToRefreshListView) this.findViewById(R.id.lstv);
        adapter = new FenghaofengqAdapter(lists, FenghaoFengqunActivity.this);
        lstv.setMode(PullToRefreshBase.Mode.BOTH);
        lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            }
        });
        lstv.setAdapter(adapter);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Emp emp = lists.get(position - 1);
                if (emp != null) {

                    if ("0".equals(type)) {
                        //封号 解封
                        showMsgFenghao(emp.getEmpId(), "0");
                    }
                    if ("1".equals(type)) {
                        //封群 解封
                        showMsgFenghao(emp.getEmpId(), "1");
                    }
                }
            }
        });
    }

    private void showMsgFenghao(final String emp_id,final String type) {
        final Dialog picAddDialog = new Dialog(FenghaoFengqunActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(this, R.layout.msg_mine_dialog, null);
        TextView jubao_sure = (TextView) picAddInflate.findViewById(R.id.jubao_sure);
        final TextView content = (TextView) picAddInflate.findViewById(R.id.content);
        content.setText("确定解封该会员？");
        jubao_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("0".equals(type)){
                    updateFh(emp_id);
                }
                if("1".equals(type)) {
                    updateFq(emp_id);
                }
                picAddDialog.dismiss();
            }
        });

        TextView jubao_cancle = (TextView) picAddInflate.findViewById(R.id.jubao_cancle);
        jubao_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picAddDialog.dismiss();
            }
        });
        picAddDialog.setContentView(picAddInflate);
        picAddDialog.show();
    }

    //获得封号封群的组
    public void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_FENGHAOFENGQUNS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            EmpsDATA data = getGson().fromJson(s, EmpsDATA.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    lists.clear();
                                }
                                lists.addAll(data.getData());
                                lstv.onRefreshComplete();
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(FenghaoFengqunActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(FenghaoFengqunActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                        if(progressDialog != null){
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(progressDialog != null){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(FenghaoFengqunActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class))){
                    params.put("emp_id", getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class));
                }
                if(MainActivity.contractSchools != null){
                    String schoolds = "";
                    for(ContractSchool con:MainActivity.contractSchools){
                        schoolds +=con.getSchoolId() +",";
                    }
                    params.put("schoolds", schoolds);//承包的学校ID
                }
                params.put("type", type);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(request);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.text_one:
                text_one.setTextColor(getResources().getColor(R.color.button_color_orange_p));
                text_two.setTextColor(getResources().getColor(R.color.black_text_color));
                type = "0";
                IS_REFRESH = true;
                initData();
                break;
            case R.id.text_two:
                text_one.setTextColor(getResources().getColor(R.color.black_text_color));
                text_two.setTextColor(getResources().getColor(R.color.button_color_orange_p));
                type = "1";
                IS_REFRESH = true;
                initData();
                break;
        }
    }

    public void back(View view){
        finish();
    }

//    @Override
//    public void onClickContentItem(int position, int flag, Object object) {
//        switch (flag){
//            case 1:
//                //
//                Emp emp = lists.get(position-1);
//
//                break;
//        }
//    }


    private void updateFh(final String empT) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_FENGHAO_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                showMsg(FenghaoFengqunActivity.this , "解除封号操作成功！");
                                type = "0";
                                IS_REFRESH = true;
                                initData();
                            } else {
                                Toast.makeText(FenghaoFengqunActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(FenghaoFengqunActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(FenghaoFengqunActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", empT);
                params.put("is_fenghao", "0");
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
    private void updateFq(final String empT) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_FENGQUN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                showMsg(FenghaoFengqunActivity.this , "解除封群操作成功！");
                                type = "1";
                                IS_REFRESH = true;
                                initData();
                            } else {
                                Toast.makeText(FenghaoFengqunActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(FenghaoFengqunActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(FenghaoFengqunActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", empT);
                params.put("is_fengqun", "0");
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

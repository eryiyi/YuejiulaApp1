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
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.adapter.ReportAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.*;
import com.liangxun.yuejiula.entity.Report;
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
 * author: ${zhanghailong}
 * Date: 2015/2/11
 * Time: 8:36
 * 类的功能、说明写在此处.
 */
public class ReportActivity extends BaseActivity implements View.OnClickListener, OnClickContentItemListener {
    private ImageView report_menu;//举报返回
    private PullToRefreshListView report_lstv;
    private ReportAdapter adapter;
    List<Report> reportList;

    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID

    private int tmpId;
    private DeletePopWindow deleteWindow;
    private String tmpReportId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_lstv);
        initView();
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        progressDialog = new CustomProgressDialog(ReportActivity.this, "正在加载中",R.anim.custom_dialog_frame);

        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        initData();
    }

    private void initView() {
        reportList = new ArrayList<Report>();
        report_menu = (ImageView) this.findViewById(R.id.report_menu);
        report_menu.setOnClickListener(this);
        report_lstv = (PullToRefreshListView) this.findViewById(R.id.report_lstv);
        adapter = new ReportAdapter(reportList, this);
        adapter.setOnClickContentItemListener(this);
        report_lstv.setAdapter(adapter);
        report_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        report_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(ReportActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                progressDialog = new CustomProgressDialog(ReportActivity.this, "正在加载中",R.anim.custom_dialog_frame);

                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(ReportActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                progressDialog =  new CustomProgressDialog(ReportActivity.this, "正在加载中",R.anim.custom_dialog_frame);

                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                initData();
            }
        });
        report_lstv.setAdapter(adapter);
        report_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.report_menu:
                finish();
                break;
        }
    }

    Report report;

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        report = reportList.get(position);
        switch (flag) {
            case 1:
                Intent prorileone = new Intent(this, ProfilePersonalActivity.class);
                prorileone.putExtra(Constants.EMPID, report.getEmpOne());
                startActivity(prorileone);
                break;
            case 2:
                Intent proriletwo = new Intent(this, ProfilePersonalActivity.class);
                proriletwo.putExtra(Constants.EMPID, report.getEmpTwo());
                startActivity(proriletwo);
                break;
            case 3:
                //举报类别
                //0   动态  1   商品
                //2   兼职  3    举报个人   4举报作品

                if (report.getTypeId().equals(Constants.REPORT_TYPE_ZERRO)) {
                    getRecordByUUID(report.getXxid());
                }
                if (report.getTypeId().equals(Constants.REPORT_TYPE_ONE)) {
                    getGoodsByUUID(report.getXxid());
                }
                if (report.getTypeId().equals(Constants.REPORT_TYPE_tWO)) {
                    getPartByUUID(report.getXxid());
                }
                if (report.getTypeId().equals(Constants.REPORT_TYPE_THREE)) {
                    getEmp(report.getXxid());
                }
                if (report.getTypeId().equals(Constants.REPORT_TYPE_FOUR)) {
                    getPkdetail(report.getXxid());
                }
                break;
            case 4://cancle
                tmpId = position;
                tmpReportId = report.getId();
                showCancel();
                break;
            case 5://done
                Intent reportdone = new Intent(this, ReportDoneActivity.class);
                reportdone.putExtra(Constants.REPORT_INFO, report);
                startActivity(reportdone);
                break;
        }
    }

    // 选择是否删除
    private void showCancel() {
        deleteWindow = new DeletePopWindow(ReportActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(ReportActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.GET_REPORT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            ReportDATA data = getGson().fromJson(s, ReportDATA.class);
                            if (data.getCode() == 200) {
                                reportList.clear();
                                reportList.addAll(data.getData());
                                report_lstv.onRefreshComplete();
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("schoolId", "");
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

    //根据动态UUID
    private void getRecordByUUID(final String xxid) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.GET_RECORD_DETAIL_BYUUID_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            RecordSingleDATA data = getGson().fromJson(s, RecordSingleDATA.class);
                            if (data.getCode() == 200) {
                                Intent pagedetail = new Intent(ReportActivity.this, DetailPageAcitvity.class);
                                pagedetail.putExtra(Constants.INFO, data.getData());
                                startActivity(pagedetail);
                            } else {
                                Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordId", xxid);
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


    //根据商品UUID
    private void getGoodsByUUID(final String xxid) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.GET_GOODS_DETAIL_BYUUID_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            if (StringUtil.isJson(s)) {
                                GoodSingleDATA data = getGson().fromJson(s, GoodSingleDATA.class);
                                if (data.getCode() == 200) {
                                    Intent goodsdetail = new Intent(ReportActivity.this, DetailGoodsActivity.class);
                                    goodsdetail.putExtra(Constants.GOODS, data.getData());
                                    startActivity(goodsdetail);
                                } else {
                                    Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", xxid);
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

    //根据兼职UUID
    private void getPartByUUID(final String xxid) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.GET_PART_DETAIL_BYUUID_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            if (StringUtil.isJson(s)) {
                                PartSingleDATA data = getGson().fromJson(s, PartSingleDATA.class);
                                if (data.getCode() == 200) {
                                    Intent goodsdetail = new Intent(ReportActivity.this, PartTimeDetailActivity.class);
                                    goodsdetail.putExtra(Constants.PART_INFO, data.getData());
                                    startActivity(goodsdetail);
                                } else {
                                    Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("partId", xxid);
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

    //忽略举报
    private void cancleReport() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.CANCLE_REPORT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
//                                reportList.get(tmpId).setIsDel("1");//管理员已经处理举报
                                reportList.remove(tmpId);
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ReportActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ReportActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ReportActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reportId", tmpReportId);
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
                    cancleReport();
                    break;
                default:
                    break;
            }
        }

    };

    //查询Pk detail
    private void getPkdetail(final String xxid) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.GET_PK_DETAIL_BYUUID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            if (StringUtil.isJson(s)) {
                                PkNewSingleDATA data = getGson().fromJson(s, PkNewSingleDATA.class);
                                if (data.getCode() == 200) {
                                    Intent goodsdetail = new Intent(ReportActivity.this, Pk_new_detailsActivity.class);
                                    goodsdetail.putExtra(Constants.PK_WORD_INFO, data.getData());
                                    startActivity(goodsdetail);
                                } else {
                                    Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("zpId", xxid);
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

    //查询个人信息
    private void getEmp(final String xxid) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.GET_EMP_DETAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            EmpDATA data = getGson().fromJson(s, EmpDATA.class);
                            if (data.getCode() == 200) {
                                Intent goodsdetail = new Intent(ReportActivity.this, ProfilePersonalActivity.class);
                                goodsdetail.putExtra(Constants.EMPID, data.getData().getEmpId());
                                startActivity(goodsdetail);
                            } else {
                                Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ReportActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empId", xxid);
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

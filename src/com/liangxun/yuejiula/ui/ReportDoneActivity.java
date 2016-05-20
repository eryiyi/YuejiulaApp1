package com.liangxun.yuejiula.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.Report;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.DateBackListener;
import com.liangxun.yuejiula.widget.DateDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/11
 * Time: 9:34
 * 类的功能、说明写在此处.
 */
public class ReportDoneActivity extends BaseActivity implements View.OnClickListener, DateBackListener {
    private ImageView report_done_menu;
    private TextView report_done_button;//提交
    private TextView report_done_title;//标题
    private EditText report_done_editext;//关禁闭时间
    private EditText report_done_editexttwo;//积分
    private TextView report_emp_one_nickname;//被举报人
    private TextView report_emp_two_nickname;//举报人


    private EditText beginET;
    private EditText endET;
    private String endTime;
    private String beginTime;
    private DateDialog dateDialog;

    private String countlong = "";

    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID

    private Report report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_detail);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);

        report = (Report) getIntent().getExtras().get(Constants.REPORT_INFO);

        initView();
        initData();
    }

    private void initView() {
        report_done_menu = (ImageView) this.findViewById(R.id.report_done_menu);
        report_done_menu.setOnClickListener(this);
        report_done_button = (TextView) this.findViewById(R.id.report_done_button);
        report_done_button.setOnClickListener(this);
        report_done_title = (TextView) this.findViewById(R.id.report_done_title);

        report_done_editexttwo = (EditText) this.findViewById(R.id.report_done_editexttwo);
        beginET = (EditText) this.findViewById(R.id.begin_time);
        beginET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog = new DateDialog(ReportDoneActivity.this, R.style.MyAlertDialog, true);
                dateDialog.setDateBackListener(ReportDoneActivity.this);
                dateDialog.show();
            }
        });
        endET = (EditText) this.findViewById(R.id.end_time);
        endET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog = new DateDialog(ReportDoneActivity.this, R.style.MyAlertDialog, false);
                dateDialog.setDateBackListener(ReportDoneActivity.this);
                dateDialog.show();
            }
        });
        report_emp_one_nickname = (TextView) this.findViewById(R.id.report_emp_one_nickname);
        report_emp_two_nickname = (TextView) this.findViewById(R.id.report_emp_two_nickname);

    }

    public void initData() {
        report_done_title.setText(report.getCont());
        report_emp_one_nickname.setText(report.getEmpTwoNickName());//被举报人
        report_emp_two_nickname.setText(report.getEmpOneNickName());//举报人
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.report_done_menu:
                finish();
                break;
            case R.id.report_done_button:
                if (StringUtil.isNullOrEmpty(beginET.getText().toString())) {
                    Toast.makeText(ReportDoneActivity.this, R.string.report_error_start_time, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(endET.getText().toString())) {
                    Toast.makeText(ReportDoneActivity.this, R.string.report_error_end_time, Toast.LENGTH_SHORT).show();
                    return;
                }
                countlong = report_done_editexttwo.getText().toString();
                if (StringUtil.isNullOrEmpty(countlong)) {
                    Toast.makeText(ReportDoneActivity.this, R.string.report_error_four, Toast.LENGTH_SHORT).show();
                    return;
                }
                doneData();
                break;
        }
    }

    private void doneData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.REPORT_JINBI_TIME_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(ReportDoneActivity.this, R.string.report_done_success, Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (data.getCode() == 2) {
                                Toast.makeText(ReportDoneActivity.this, R.string.report_error_oneone, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ReportDoneActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ReportDoneActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ReportDoneActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empId", report.getEmpTwo());
                params.put("admin", emp_id);
                params.put("start", beginET.getText().toString());
                params.put("end", endET.getText().toString());
                params.put("empIdTwo", report.getEmpOne());
                params.put("countJF", countlong);
                params.put("reportId", report.getId());
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
    public void backTime(String date, boolean isStart) {

        if (isStart) {
            beginTime = date;
        } else {
            endTime = date;
        }
        beginET.setText(beginTime);
        endET.setText(endTime);

        if (StringUtil.isNullOrEmpty(beginET.getText().toString())) {
            Toast.makeText(ReportDoneActivity.this, "请先选择开始时间", Toast.LENGTH_SHORT).show();
            endET.setText("");
            return;
        }

        if (!StringUtil.isNullOrEmpty(endET.getText().toString())) {

            if (Integer.parseInt(beginET.getText().toString().replaceAll("-", "")) > Integer.parseInt(endET.getText().toString().replaceAll("-", ""))) {
                Toast.makeText(ReportDoneActivity.this, "请选择正确结束时间", Toast.LENGTH_SHORT).show();
                endET.setText("");
                return;
            }
        }
    }
}

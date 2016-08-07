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
import com.liangxun.yuejiula.data.ReportDATA;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/26
 * Time: 8:01
 * 类的功能、说明写在此处.
 */
public class ReSetPwrActivity extends BaseActivity implements View.OnClickListener {
    private ImageView reset_pwr_menu;//返回按钮
    private EditText yuanshi_pwr;//原始密码
    private EditText new_pwr;//新密码
    private EditText sure_pwr;//确认密码
    private TextView resetPwr_button;//确认按钮

    private String pwrone;
    private String pwrtwo;
    private String pwrthree;

    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_pwr_xml);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        initView();
    }

    private void initView() {
        reset_pwr_menu = (ImageView) this.findViewById(R.id.reset_pwr_menu);
        yuanshi_pwr = (EditText) this.findViewById(R.id.yuanshi_pwr);
        new_pwr = (EditText) this.findViewById(R.id.new_pwr);
        sure_pwr = (EditText) this.findViewById(R.id.sure_pwr);
        resetPwr_button = (TextView) this.findViewById(R.id.resetPwr_button);

        reset_pwr_menu.setOnClickListener(this);
        resetPwr_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_pwr_menu:
                finish();
                break;
            case R.id.resetPwr_button:
                pwrone = yuanshi_pwr.getText().toString();
                pwrtwo = new_pwr.getText().toString();
                pwrthree = sure_pwr.getText().toString();
                if (StringUtil.isNullOrEmpty(pwrone)) {
                    Toast.makeText(ReSetPwrActivity.this, R.string.reset_pwr_error_one, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(pwrtwo)) {
                    Toast.makeText(ReSetPwrActivity.this, R.string.reset_pwr_error_two, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pwrtwo.length() < 6 || pwrtwo.length() > 18) {
                    Toast.makeText(ReSetPwrActivity.this, R.string.reset_pwr_error_five, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(pwrthree)) {
                    Toast.makeText(ReSetPwrActivity.this, R.string.reset_pwr_error_three, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pwrtwo.equals(pwrthree)) {
                    Toast.makeText(ReSetPwrActivity.this, R.string.reset_pwr_error_four, Toast.LENGTH_SHORT).show();
                    return;
                }
                resetPwr();
                break;
        }
    }

    private void resetPwr() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.UPDATE_PWR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            ReportDATA data = getGson().fromJson(s, ReportDATA.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(ReSetPwrActivity.this, R.string.reset_pwr_success, Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (data.getCode() == 1) {
                                Toast.makeText(ReSetPwrActivity.this, R.string.reset_pwr_error_six, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ReSetPwrActivity.this, R.string.reset_pwr_error_seven, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ReSetPwrActivity.this, R.string.reset_pwr_error_seven, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ReSetPwrActivity.this, R.string.reset_pwr_error_seven, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empId", emp_id);
                params.put("pass", pwrone);
                params.put("rePass", pwrtwo);
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

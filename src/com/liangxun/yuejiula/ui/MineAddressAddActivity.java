package com.liangxun.yuejiula.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.CustomProgressDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/9.
 * 新增我的收货地址
 */
public class MineAddressAddActivity extends BaseActivity implements View.OnClickListener {
    private ImageView back;
    private String emp_id = "";//当前登陆者UUID
    //
    private EditText add_nickname;
    private TextView add_address_one;
    private EditText add_tel;
    private EditText add_address_two;
    private Button button_add_address;

    private String cityid;
    private String cityName;
    private String provinceId;
    private String provinceName;
    private String areaId;
    private String areaName;
    private CheckBox checkbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_address_add_four_activity);
        cityid = getIntent().getExtras().getString("cityid");
        cityName = getIntent().getExtras().getString("cityName");
        provinceId = getIntent().getExtras().getString("provinceId");
        provinceName = getIntent().getExtras().getString("provinceName");
        areaId = getIntent().getExtras().getString("areaId");
        areaName = getIntent().getExtras().getString("areaName");
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        initView();
    }
    private void initView() {
        back = (ImageView) this.findViewById(R.id.back);
        back.setOnClickListener(this);

        add_nickname =  (EditText) this.findViewById(R.id.add_nickname);
        add_tel =  (EditText) this.findViewById(R.id.add_tel);
        add_address_one =  (TextView) this.findViewById(R.id.add_address_one);
        add_address_two =  (EditText) this.findViewById(R.id.add_address_two);
        button_add_address =  (Button) this.findViewById(R.id.button_add_address);
        checkbox =  (CheckBox) this.findViewById(R.id.checkbox);

        button_add_address.setOnClickListener(this);
        add_address_one.setText(provinceName+ cityName+ areaName);

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.button_add_address:
                //添加
                if (StringUtil.isNullOrEmpty(add_nickname.getText().toString())) {
                    Toast.makeText(MineAddressAddActivity.this, R.string.add_address_error_one, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(add_tel.getText().toString())) {
                    Toast.makeText(MineAddressAddActivity.this, R.string.add_address_error_two, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (StringUtil.isNullOrEmpty(add_address_two.getText().toString())) {
                    Toast.makeText(MineAddressAddActivity.this, R.string.add_address_error_three, Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog = new CustomProgressDialog(MineAddressAddActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                saveAddress();
                break;
        }
    }

    //保存收货地址
    public void saveAddress(){
        //获得收货地址列表
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.ADD_MINE_ADDRSS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                //成功
                                Intent intent = new Intent("add_address_success");
                                sendBroadcast(intent);
                                finish();
                            } else {
                                Toast.makeText(MineAddressAddActivity.this, R.string.address_error_one, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineAddressAddActivity.this, R.string.address_error_one, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MineAddressAddActivity.this, R.string.address_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accept_name", add_nickname.getText().toString());
                params.put("address", add_address_two.getText().toString());
                params.put("phone", add_tel.getText().toString());
                params.put("emp_id", emp_id);
                params.put("province", provinceId);
                params.put("city", cityid);
                params.put("area", areaId);
                if(checkbox.isChecked()){
                    params.put("is_default", "1");
                }else {
                    params.put("is_default", "0");
                }
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

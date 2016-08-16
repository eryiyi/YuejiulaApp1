package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
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
import com.liangxun.yuejiula.entity.ShoppingAddress;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.CustomProgressDialog;
import com.liangxun.yuejiula.widget.popview.OrderCancelPopWindow;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/12.
 */
public class MineAddressUpdateActivity extends BaseActivity implements View.OnClickListener {
    private ShoppingAddress goodsAddress;
    private EditText add_nickname;
    private EditText add_tel;
    private TextView add_address_one;
    private EditText add_address_two;
    private Button button_delete_address;
    private Button button_add_address;
    private ImageView back;

    private CheckBox checkbox;
    private String is_default = "0";

    private OrderCancelPopWindow orderCancelPopWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_address_update_activity);
        goodsAddress = (ShoppingAddress) getIntent().getExtras().get("goodsAddress");
        is_default = goodsAddress.getIs_default();
        initView();
    }

    private void initView() {
        back = (ImageView) this.findViewById(R.id.back);
        add_nickname = (EditText) this.findViewById(R.id.add_nickname);
        add_tel = (EditText) this.findViewById(R.id.add_tel);
        add_address_one = (TextView) this.findViewById(R.id.add_address_one);
        add_address_two = (EditText) this.findViewById(R.id.add_address_two);
        button_delete_address = (Button) this.findViewById(R.id.button_delete_address);
        button_add_address = (Button) this.findViewById(R.id.button_add_address);
        back.setOnClickListener(this);
        button_add_address.setOnClickListener(this);
        button_delete_address.setOnClickListener(this);
        checkbox = (CheckBox) this.findViewById(R.id.checkbox);

        add_nickname.setText(goodsAddress.getAccept_name());
        add_tel.setText(goodsAddress.getPhone());
        add_address_one.setText(goodsAddress.getProvinceName()+goodsAddress.getCityName()+goodsAddress.getAreaName());
        add_address_two.setText(goodsAddress.getAddress());
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    is_default = "1";
                } else {
                    is_default = "0";
                }
            }
        });
        if (is_default.equals("0")){
            //未选中
            checkbox.setChecked(false);
        }else{
            //选中
            checkbox.setChecked(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.button_add_address:
                //修改地址
                if (StringUtil.isNullOrEmpty(add_nickname.getText().toString())) {
                    Toast.makeText(MineAddressUpdateActivity.this, R.string.add_address_error_one, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(add_tel.getText().toString())) {
                    Toast.makeText(MineAddressUpdateActivity.this, R.string.add_address_error_two, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (StringUtil.isNullOrEmpty(add_address_two.getText().toString())) {
                    Toast.makeText(MineAddressUpdateActivity.this, R.string.add_address_error_three, Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog = new CustomProgressDialog(MineAddressUpdateActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                saveAddress();
                break;
            case R.id.button_delete_address:
                showCancel();
                break;
        }
    }
    //保存收货地址
    public void saveAddress(){
        //获得收货地址列表
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.UPDATE_MINE_ADDRSS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                //成功
                                Intent intent = new Intent("update_address_success");
                                sendBroadcast(intent);
                                finish();
                            } else {
                                Toast.makeText(MineAddressUpdateActivity.this, R.string.address_error_one, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineAddressUpdateActivity.this, R.string.address_error_one, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MineAddressUpdateActivity.this, R.string.address_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accept_name", add_nickname.getText().toString());
                params.put("address", add_address_two.getText().toString());
                params.put("phone", add_tel.getText().toString());
                params.put("address_id", goodsAddress.getAddress_id());
                params.put("emp_id", goodsAddress.getEmp_id());
                params.put("is_default",is_default);
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

    private void showCancel() {
        orderCancelPopWindow = new OrderCancelPopWindow(MineAddressUpdateActivity.this, itemsOnClick);
        //显示窗口
        orderCancelPopWindow.showAtLocation(MineAddressUpdateActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            orderCancelPopWindow.dismiss();
            switch (v.getId()) {
                case R.id.sure:
                {
                    //取消订单
                    cancelAddress();
                }
                break;
            }
        }
    };

    //删除地址
    private void cancelAddress() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.DELETE_MINE_ADDRSS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Intent intent = new Intent("update_address_success");
                                sendBroadcast(intent);
                                finish();
                            } else {
                                Toast.makeText(MineAddressUpdateActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineAddressUpdateActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineAddressUpdateActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("address_id", goodsAddress.getAddress_id());
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

package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.ProvinceAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.ProvinceDATA;
import com.liangxun.yuejiula.entity.Province;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.ClassifyGridview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/3/9
 * Time: 8:18
 * 类的功能、说明写在此处.
 */
public class SelectProvinceActivity extends BaseActivity implements View.OnClickListener {
    private ImageView back;//返回
    private List<Province> provinces = new ArrayList<Province>();
    private ProvinceAdapter provinceAdapter;
    private ClassifyGridview provinceGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_province_activity);
        initView();
        getProvince();
    }

    private void initView() {
        back = (ImageView) this.findViewById(R.id.other_back);
        back.setOnClickListener(this);

        provinceGridView = (ClassifyGridview) this.findViewById(R.id.province);
        provinceAdapter = new ProvinceAdapter(provinces, SelectProvinceActivity.this);
        provinceGridView.setAdapter(provinceAdapter);
        provinceGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent gt = new Intent(SelectProvinceActivity.this, SelectCollegeActivity.class);
                Province province = provinces.get(position);
                gt.putExtra(Constants.OTHER_PROVINCE, province.getProvinceId());//省份UUID
                gt.putExtra(Constants.OTHER_PROVINCE_CONT, "");//内容
                startActivity(gt);
            }
        });
        provinceGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.other_back:
                finish();
                break;
        }
    }

    //获取省份列表
    private void getProvince() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.GET_PROVINCE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            ProvinceDATA data = getGson().fromJson(s, ProvinceDATA.class);
                            if (data.getCode() == 200) {
                                provinces.clear();
                                provinces.addAll(data.getData());
                                provinceAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(SelectProvinceActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SelectProvinceActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(SelectProvinceActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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

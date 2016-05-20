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
public class OtherSchoolOneActivity extends BaseActivity implements View.OnClickListener {
    private ImageView back;//返回
    private String cont;//搜索框内容
    private EditText searchText;//搜索框
    private ImageView deleteContSearch;//清除
    private List<Province> provinces = new ArrayList<Province>();
    private ProvinceAdapter provinceAdapter;
    private ClassifyGridview provinceGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_school_one);
        initView();
        getProvince();
    }

    private void initView() {
        back = (ImageView) this.findViewById(R.id.other_back);
        back.setOnClickListener(this);
        searchText = (EditText) this.findViewById(R.id.searchText);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cont = searchText.getText().toString();//要搜的内容
                if (!StringUtil.isNullOrEmpty(cont)) {
                    deleteContSearch.setVisibility(View.VISIBLE);
                } else {
                    deleteContSearch.setVisibility(View.GONE);
                }
                if (!StringUtil.isNullOrEmpty(cont)) {
                    Intent gt = new Intent(OtherSchoolOneActivity.this, OtherSchoolTwoActivity.class);
                    gt.putExtra(Constants.OTHER_PROVINCE, "");//省份UUID
                    gt.putExtra(Constants.OTHER_PROVINCE_CONT, cont);//内容
                    startActivity(gt);
                }

            }
        });
        deleteContSearch = (ImageView) this.findViewById(R.id.deleteContSearch);
        deleteContSearch.setOnClickListener(this);
        provinceGridView = (ClassifyGridview) this.findViewById(R.id.province);
        provinceAdapter = new ProvinceAdapter(provinces, OtherSchoolOneActivity.this);
        provinceGridView.setAdapter(provinceAdapter);
        provinceGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent gt = new Intent(OtherSchoolOneActivity.this, OtherSchoolTwoActivity.class);
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
            case R.id.deleteContSearch://清除
                searchText.setText("");
                break;
        }
    }

    //获取省份列表
    private void getProvince() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_PROVINCE_URL,
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
                                Toast.makeText(OtherSchoolOneActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(OtherSchoolOneActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(OtherSchoolOneActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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

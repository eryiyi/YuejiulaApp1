package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
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
import com.liangxun.yuejiula.adapter.ItemAreaAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.AreaDATA;
import com.liangxun.yuejiula.entity.Area;
import com.liangxun.yuejiula.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/9.
 * 新增我的收货地址--选择地区
 */
public class MineAddressAddAreaActivity extends BaseActivity implements View.OnClickListener {
    private ImageView back;


    private ListView lstv;
    private ItemAreaAdapter adapter;
    List<Area> provinces = new ArrayList<Area>();
    private String cityid;
    private String cityName;
    private String provinceId;
    private String provinceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_address_add_activity);
        cityid = getIntent().getExtras().getString("cityid");
        cityName = getIntent().getExtras().getString("cityName");
        provinceId = getIntent().getExtras().getString("provinceId");
        provinceName = getIntent().getExtras().getString("provinceName");
        initView();
        getData();
    }
    private void initView() {
        back = (ImageView) this.findViewById(R.id.back);
        back.setOnClickListener(this);
        adapter = new ItemAreaAdapter(provinces, MineAddressAddAreaActivity.this);
        lstv = (ListView) this.findViewById(R.id.lstv);
        lstv.setAdapter(adapter);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Area city = provinces.get(position);
                Intent intent = new Intent(MineAddressAddAreaActivity.this, MineAddressAddActivity.class);
                intent.putExtra("cityid", cityid);
                intent.putExtra("cityName", cityName);
                intent.putExtra("provinceName", provinceName);
                intent.putExtra("provinceId", provinceId);
                intent.putExtra("areaId", city.getAreaid());
                intent.putExtra("areaName", city.getAreaName());
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    //获取省份
    public void getData(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SELECT_AREA_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            AreaDATA data = getGson().fromJson(s, AreaDATA.class);
                            if (data.getCode() == 200) {
                                provinces.addAll(data.getData());
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MineAddressAddAreaActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineAddressAddAreaActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MineAddressAddAreaActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cityid", cityid);
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

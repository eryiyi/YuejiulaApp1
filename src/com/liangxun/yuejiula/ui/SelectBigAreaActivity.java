package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.SelectBigAreaAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.BigAreaObjData;
import com.liangxun.yuejiula.entity.BigAreaObj;
import com.liangxun.yuejiula.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/7.
 */
public class SelectBigAreaActivity extends BaseActivity implements View.OnClickListener {
    private ListView lstv;
    private List<BigAreaObj> list = new ArrayList<BigAreaObj>();
    private SelectBigAreaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_big_area);

        this.findViewById(R.id.back).setOnClickListener(this);
        lstv = (ListView) this.findViewById(R.id.lstv);
        adapter  = new SelectBigAreaAdapter(list, SelectBigAreaActivity.this);
        lstv.setAdapter(adapter);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BigAreaObj bigAreaObj = list.get(i);
                if(bigAreaObj != null){
                    save ("select_big_area", bigAreaObj.getArea_url());
                    save ("select_big_area_name", bigAreaObj.getArea_title());
                    save ("select_big_area_cont", bigAreaObj.getArea_content());
                    save ("select_big_area_pic", bigAreaObj.getArea_pic());
                    Intent intent = new Intent(SelectBigAreaActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        getData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
        }
    }

    private void getData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.DEFAULT_GET_BIG_AREA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            BigAreaObjData data = getGson().fromJson(s, BigAreaObjData.class);
                            if (data.getCode() == 200) {
                                list.clear();
                                list.addAll(data.getData());
                                adapter.notifyDataSetChanged();
                            }else{
                                Intent login = new Intent(SelectBigAreaActivity.this, LoginActivity.class);
                                startActivity(login);

                            }
                        } else {
                            Intent login = new Intent(SelectBigAreaActivity.this, LoginActivity.class);
                            startActivity(login);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Intent login = new Intent(SelectBigAreaActivity.this, LoginActivity.class);
                        startActivity(login);
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

package com.liangxun.yuejiula.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.PartTimeTypeAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.PartTimeTypeDATA;
import com.liangxun.yuejiula.entity.PartTimeType;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.ClassifyGridview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/25
 * Time: 15:50
 * 类的功能、说明写在此处.
 */
public class MorePartTypeActivity extends BaseActivity implements View.OnClickListener {
    private ImageView more_parttime_menu;
    private ClassifyGridview parttimetyupeGridview;//定义一个gridview
    private List<PartTimeType> goodstypeList = new ArrayList<PartTimeType>();
    ;

    private PartTimeTypeAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_part_type);
        initView();
        parttimetyupeGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent gt = new Intent(MorePartTypeActivity.this, PartTimeLstActivity.class);
                PartTimeType goodstype = goodstypeList.get(position);
                gt.putExtra(Constants.PARTTYPE, goodstype.getId());
                startActivity(gt);
            }
        });
        adapter = new PartTimeTypeAdapter(goodstypeList, this);
        parttimetyupeGridview.setAdapter(adapter);
        parttimetyupeGridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        progressDialog = new ProgressDialog(MorePartTypeActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_parttime_menu:
                finish();
                break;
        }
    }

    private void initView() {
        parttimetyupeGridview = (ClassifyGridview) this.findViewById(R.id.moreparttimetyupeGridview);
        more_parttime_menu = (ImageView) this.findViewById(R.id.more_parttime_menu);
        more_parttime_menu.setOnClickListener(this);
    }


    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +InternetURL.GET_PARTTIMETYPE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            PartTimeTypeDATA data = getGson().fromJson(s, PartTimeTypeDATA.class);
                            if (data.getCode() == 200) {
                                goodstypeList.addAll(data.getData());
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MorePartTypeActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MorePartTypeActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MorePartTypeActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
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

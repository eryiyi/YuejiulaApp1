package com.liangxun.yuejiula.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.ItemMineAddressAdapter;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.MineAddressDATA;
import com.liangxun.yuejiula.entity.ShoppingAddress;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/9.
 * 我的收货地址列表
 */
public class MineAddressActivity extends BaseActivity implements View.OnClickListener,OnClickContentItemListener {
    private ListView lstv;
    private ImageView back;
    private ItemMineAddressAdapter adapter;
    private List<ShoppingAddress> lists = new ArrayList<>();

    private String emp_id = "";//当前登陆者UUID
    //新增收货地址
    private Button button_add_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
        setContentView(R.layout.mine_address_activity);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        initView();
        getData();
    }

    private void initView() {
        back = (ImageView) this.findViewById(R.id.back);
        button_add_address = (Button) this.findViewById(R.id.button_add_address);
        button_add_address.setOnClickListener(this);
        back.setOnClickListener(this);
        lstv = (ListView) this.findViewById(R.id.lstv);
        adapter = new ItemMineAddressAdapter(lists, MineAddressActivity.this);
        lstv.setAdapter(adapter);
        adapter.setOnClickContentItemListener(this);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShoppingAddress goodsAddress = lists.get(i);
                Intent updateAddressView = new Intent(MineAddressActivity.this, MineAddressUpdateActivity.class);
                updateAddressView.putExtra("goodsAddress",goodsAddress);
                startActivity(updateAddressView);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.back:
                finish();
                break;
            case R.id.button_add_address:
                Intent addAddressView = new Intent(MineAddressActivity.this, MineAddressAddProvinceActivity.class);
                startActivity(addAddressView);
                break;
        }
    }
    public void getData(){
        //获得收货地址列表
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.MINE_ADDRSS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            MineAddressDATA data = getGson().fromJson(s, MineAddressDATA.class);
                            if (data.getCode() == 200) {
                                lists.clear();
                                lists.addAll(data.getData());
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MineAddressActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineAddressActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineAddressActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empId", emp_id);
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

    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("add_address_success")) {
                //刷新内容
                getData();
            }
            if (action.equals("update_address_success")) {
                //刷新内容
                getData();
            }
        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("add_address_success");
        myIntentFilter.addAction("update_address_success");
        //注册广播
        this.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        switch (flag){
            case 1:
                //进入详细页
                break;
        }
    }
}

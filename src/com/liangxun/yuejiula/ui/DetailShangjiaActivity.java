package com.liangxun.yuejiula.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.liangxun.yuejiula.adapter.AnimateFirstDisplayListener;
import com.liangxun.yuejiula.adapter.MineShangjiaColleageAdapter;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.SellerSchoolListDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.SellerGoods;
import com.liangxun.yuejiula.entity.SellerSchoolList;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.popview.DeletePopWindow;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/3/27
 * Time: 8:55
 * 类的功能、说明写在此处.
 */
public class DetailShangjiaActivity extends BaseActivity implements View.OnClickListener, OnClickContentItemListener {
    private ImageView mine_record_xml_menu;//返回
    private ImageView covers;//商家头像
    private TextView nickname;//昵称
    private ImageView search_null;

    private ListView mine_lstv;
    private List<SellerSchoolList> lists = new ArrayList<SellerSchoolList>();
    private MineShangjiaColleageAdapter adapter;

    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID

    private SellerGoods emp;//商家的资料
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private TextView detail_shangjia_delete;//删除

    private DeletePopWindow deleteWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
        setContentView(R.layout.detail_shangjia);
        emp = (SellerGoods) getIntent().getExtras().get(Constants.SELLERGOODS_INFO);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        initView();
        initData();
    }

    private void initView() {
        mine_record_xml_menu = (ImageView) this.findViewById(R.id.mine_record_xml_menu);
        mine_record_xml_menu.setOnClickListener(this);
        covers = (ImageView) this.findViewById(R.id.cover);
        imageLoader.displayImage(emp.getEmpCover(), covers, UniversityApplication.txOptions, animateFirstListener);
        nickname = (TextView) this.findViewById(R.id.nickname);
        nickname.setText(emp.getEmpName());
        search_null = (ImageView) this.findViewById(R.id.search_null);
        search_null.setVisibility(View.GONE);
        covers.setOnClickListener(this);
        mine_lstv = (ListView) this.findViewById(R.id.mine_lstv);
        adapter = new MineShangjiaColleageAdapter(lists, this);
        mine_lstv.setAdapter(adapter);
        adapter.setOnClickContentItemListener(this);
        mine_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent detail = new Intent(DetailShangjiaActivity.this, DetailShangjiaActivity.class);
//                Emp record = emps.get(position-1);
//                detail.putExtra(Constants.SHANGJIA_UUID, record.getEmpId());
//                startActivity(detail);
            }
        });
        detail_shangjia_delete = (TextView) this.findViewById(R.id.detail_shangjia_delete);
        detail_shangjia_delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_record_xml_menu:
                finish();
                break;
            case R.id.cover:
                Intent intent = new Intent(DetailShangjiaActivity.this, ProfilePersonalActivity.class);
                intent.putExtra(Constants.EMPID, emp.getEmpId());
                startActivity(intent);
                break;
            case R.id.detail_shangjia_delete:
                showSelectImageDialog();
                break;
        }
    }

    // 选择是否删除
    private void showSelectImageDialog() {
        deleteWindow = new DeletePopWindow(DetailShangjiaActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(DetailShangjiaActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_sure:
                    delete();
                    break;
                default:
                    break;
            }
        }

    };

    //查询商家的圈子
    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.GET_SHOOLS_BYSJ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SellerSchoolListDATA data = getGson().fromJson(s, SellerSchoolListDATA.class);
                            if (data.getCode() == 200) {
                                lists.clear();
                                lists.addAll(data.getData());
                                if (lists.size() == 0) {
                                    search_null.setVisibility(View.VISIBLE);
                                } else {
                                    search_null.setVisibility(View.GONE);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(DetailShangjiaActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailShangjiaActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailShangjiaActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("contractId", emp_id);
                params.put("empId", emp.getEmpId());
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

    //删除方法
    private void delete() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.DELETE_SHAGNJIA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(DetailShangjiaActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                                //发广播，刷新我的商家列表
                                Intent intent1 = new Intent(Constants.UPDATE_SHANGJIA_INFOS);
                                sendBroadcast(intent1);
                                finish();
                            } else {
                                Toast.makeText(DetailShangjiaActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailShangjiaActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailShangjiaActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empId", emp.getEmpId());
                params.put("contractId", emp_id);
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

    SellerSchoolList sellerSchoolList;

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        sellerSchoolList = lists.get(position);
        switch (flag) {
            case 1:
                Intent update = new Intent(DetailShangjiaActivity.this, UpdateShangjiaActivity.class);
                update.putExtra(Constants.SELLERSCHOOLLIST, sellerSchoolList);
                startActivity(update);
                break;
        }
    }

    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.SELLERSCHOOLLIST_UPDATE)) {
                initData();
            }
        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SELLERSCHOOLLIST_UPDATE);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}

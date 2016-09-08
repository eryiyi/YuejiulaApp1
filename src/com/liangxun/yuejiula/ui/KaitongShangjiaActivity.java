package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.AnimateFirstDisplayListener;
import com.liangxun.yuejiula.adapter.ManageColleageSjAdapter;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.base.ActivityTack;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.MineShangjiasDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.ContractSchool;
import com.liangxun.yuejiula.entity.Emp;
import com.liangxun.yuejiula.entity.SellerGoods;
import com.liangxun.yuejiula.entity.SellerGoodsForm;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.DateBackListener;
import com.liangxun.yuejiula.widget.DateDialog;
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
 * Time: 9:56
 * 类的功能、说明写在此处.
 */
public class KaitongShangjiaActivity extends BaseActivity implements View.OnClickListener, OnClickContentItemListener, DateBackListener {
    private Emp emp;//传递过来的商家UUID
    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID

    private ImageView mine_record_xml_menu;//返回
    private ImageView cover;//头像
    private TextView nickname;//昵称

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private ListView mine_lstv;//列表项
    private List<ContractSchool> lists = new ArrayList<ContractSchool>();
    private ManageColleageSjAdapter adapter;
    private TextView publish_comment_run;//设置商家按钮

    private List<SellerGoods> listgoods = new ArrayList<SellerGoods>();

    private SellerGoodsForm SGform = new SellerGoodsForm();
    private DateDialog dateDialog;
    private int tmpPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kaitong_shangjia);
        emp = (Emp) getIntent().getExtras().get(Constants.INFO);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        initView();
        getData();
    }

    private void initView() {
        mine_record_xml_menu = (ImageView) this.findViewById(R.id.mine_record_xml_menu);
        mine_record_xml_menu.setOnClickListener(this);
        cover = (ImageView) this.findViewById(R.id.cover);
        nickname = (TextView) this.findViewById(R.id.nickname);
        cover.setOnClickListener(this);
        nickname.setText(emp.getEmpName());
        imageLoader.displayImage(emp.getEmpCover(), cover, UniversityApplication.txOptions, animateFirstListener);
        mine_lstv = (ListView) this.findViewById(R.id.mine_lstv);
        adapter = new ManageColleageSjAdapter(lists, this);
        mine_lstv.setAdapter(adapter);
        adapter.setOnClickContentItemListener(this);
        mine_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        publish_comment_run = (TextView) this.findViewById(R.id.publish_comment_run);
        publish_comment_run.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_record_xml_menu:
                finish();
                break;
            case R.id.cover:
                Intent intent = new Intent(KaitongShangjiaActivity.this, ProfilePersonalActivity.class);
                intent.putExtra(Constants.EMPID, emp.getEmpId());
                startActivity(intent);
                break;
            case R.id.publish_comment_run:
                if (emp.getEmpId().equals(emp_id)) {
                    Toast.makeText(KaitongShangjiaActivity.this, R.string.kaitong_error_one, Toast.LENGTH_SHORT).show();
                    return;
                }
                //设置商家
                initData();
                break;
        }
    }

    ContractSchool info;

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        info = lists.get(position);
        tmpPosition = position;
        switch (flag) {
            case 1:
                break;
            case 2://日期
                dateDialog = new DateDialog(KaitongShangjiaActivity.this, R.style.MyAlertDialog, true);
                dateDialog.setDateBackListener(KaitongShangjiaActivity.this);
                dateDialog.show();
                break;
            case 3://开通
                lists.get(tmpPosition).setIsOpen("0");
                break;
            case 4://关闭
                lists.get(tmpPosition).setIsOpen("1");
                break;
        }
    }

    //查询该代理商下面的圈子
    private void getData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.GET_SCHOOLS_BY_JXS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            MineShangjiasDATA data = getGson().fromJson(s, MineShangjiasDATA.class);
                            if (data.getCode() == 200) {
                                lists.clear();
                                lists.addAll(data.getData());
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(KaitongShangjiaActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(KaitongShangjiaActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(KaitongShangjiaActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
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

    //设置商家
    private void setSJ() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.SET_SHAGNJIA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(KaitongShangjiaActivity.this, R.string.report_done_success, Toast.LENGTH_SHORT).show();
                                //发广播，刷新我的商家列表
                                Intent intent1 = new Intent(Constants.UPDATE_SHANGJIA_INFOS);
                                sendBroadcast(intent1);
                                ActivityTack.getInstanse().popUntilActivity(MineShangjiaActivity.class);
                            } else if (data.getCode() == 2) {
                                Toast.makeText(KaitongShangjiaActivity.this, R.string.report_error_twoone, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(KaitongShangjiaActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(KaitongShangjiaActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(KaitongShangjiaActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("list", new Gson().toJson(SGform));
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

    public void initData() {
        for (int i = 0; i < lists.size(); i++) {
            ContractSchool ctr = lists.get(i);
            if (!StringUtil.isNullOrEmpty(ctr.getIsOpen())) {
                if (ctr.getIsOpen().equals("0")) {//0的也是开通的   1的是关闭的
                    listgoods.add(new SellerGoods("", emp.getEmpId(), ctr.getEmpId(), ctr.getSchoolId(), ctr.getEndTime(), ""));
                }
            } else {//isopen默认是null的默认是开通
                listgoods.add(new SellerGoods("", emp.getEmpId(), ctr.getEmpId(), ctr.getSchoolId(), ctr.getEndTime(), ""));
            }
        }
        //如果集合不为空
        if (listgoods.size() > 0) {
            SGform.setList(listgoods);
        } else {
            Toast.makeText(KaitongShangjiaActivity.this, "请选择开通一个圈子", Toast.LENGTH_SHORT).show();
            return;
        }
        setSJ();
    }

    @Override
    public void backTime(String date, boolean isStart) {
        lists.get(tmpPosition).setEndTime(date);
        adapter.notifyDataSetChanged();
    }
}

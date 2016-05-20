package com.liangxun.yuejiula.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.ItemMineOrderSjAdapter;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.OrdersVoDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.OrderVo;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.popview.OrderCancelPopWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/15.
 * 我的订单--商家
 */
public class MineOrdersMngActivity extends BaseActivity implements View.OnClickListener,OnClickContentItemListener {
    private String order_no;

    private PullToRefreshListView classtype_lstv;//列表
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private String emp_id = "";//当前登陆者UUID

    private ItemMineOrderSjAdapter adapter;
    private List<OrderVo> orderVos = new ArrayList<>();
    private TextView text_one;
    private TextView text_two;
    private TextView text_three;
    private TextView text_four;
    private String status="";

    private OrderCancelPopWindow orderCancelPopWindow;//删除订单
    private OrderCancelPopWindow orderCancelPopWindowTwo;//确认收货
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_order_activity);
        initView();
        initData();
    }

    private void initView() {
        classtype_lstv = (PullToRefreshListView) this.findViewById(R.id.lstv);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        adapter = new ItemMineOrderSjAdapter(orderVos,MineOrdersMngActivity.this);
        adapter.setOnClickContentItemListener(this);

        classtype_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        classtype_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MineOrdersMngActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MineOrdersMngActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData();
            }
        });
        classtype_lstv.setAdapter(adapter);
        classtype_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderVo orderVo = orderVos.get(position-1);
                Intent detailView = new Intent(MineOrdersMngActivity.this, DetailOrderMngActivity.class);
                detailView.putExtra("orderVo",orderVo);
                startActivity(detailView);
            }
        });

        text_one = (TextView) this.findViewById(R.id.text_one);
        text_two = (TextView) this.findViewById(R.id.text_two);
        text_three = (TextView) this.findViewById(R.id.text_three);
        text_four = (TextView) this.findViewById(R.id.text_four);
        text_one.setOnClickListener(this);
        text_two.setOnClickListener(this);
        text_three.setOnClickListener(this);
        text_four.setOnClickListener(this);
    }

    public void back(View view){
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.text_one:
                text_one.setTextColor(getResources().getColor(R.color.button_color_orange_p));
                text_two.setTextColor(getResources().getColor(R.color.black_text_color));
                text_three.setTextColor(getResources().getColor(R.color.black_text_color));
                text_four.setTextColor(getResources().getColor(R.color.black_text_color));
                status = "";
                initData();
                break;
            case R.id.text_two:
                text_one.setTextColor(getResources().getColor(R.color.black_text_color));
                text_two.setTextColor(getResources().getColor(R.color.button_color_orange_p));
                text_three.setTextColor(getResources().getColor(R.color.black_text_color));
                text_four.setTextColor(getResources().getColor(R.color.black_text_color));
                status = "1";
                initData();
                break;
            case R.id.text_three:
                text_one.setTextColor(getResources().getColor(R.color.black_text_color));
                text_two.setTextColor(getResources().getColor(R.color.black_text_color));
                text_three.setTextColor(getResources().getColor(R.color.button_color_orange_p));
                text_four.setTextColor(getResources().getColor(R.color.black_text_color));
                status = "2";
                initData();
                break;
            case R.id.text_four:
                text_one.setTextColor(getResources().getColor(R.color.black_text_color));
                text_two.setTextColor(getResources().getColor(R.color.black_text_color));
                text_three.setTextColor(getResources().getColor(R.color.black_text_color));
                text_four.setTextColor(getResources().getColor(R.color.button_color_orange_p));
                status = "5";
                initData();
                break;
        }
    }


    OrderVo orderVoTmp;
    int tmpPosition;
    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        orderVoTmp = orderVos.get(position);
        //1已发货  2 已收货 3取消  4 发送货物
        tmpPosition = position;
        switch (flag){
            case 1:
                break;
            case 2:
                break;
            case 3:
                //取消订单
                showCancel();
                break;
            case 4:
                //确认发货
                showSure();
                break;
            case 5:
                //删除订单
                showDelete();
                break;
        }
    }
    //取订单
    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.MINE_ORDERS_SJ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            OrdersVoDATA data = getGson().fromJson(s, OrdersVoDATA.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    orderVos.clear();
                                }
                                orderVos.addAll(data.getData());
                                classtype_lstv.onRefreshComplete();
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MineOrdersMngActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineOrdersMngActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineOrdersMngActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex));
                params.put("empId", emp_id);//卖家--
                params.put("status", status);
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



    private void showDelete() {
        orderCancelPopWindow = new OrderCancelPopWindow(MineOrdersMngActivity.this, itemsOnClick);
        //显示窗口
        orderCancelPopWindow.showAtLocation(MineOrdersMngActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    private void showSure() {
        orderCancelPopWindowTwo = new OrderCancelPopWindow(MineOrdersMngActivity.this, itemsOnClickTwo);
        //显示窗口
        orderCancelPopWindowTwo.showAtLocation(MineOrdersMngActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClickTwo = new View.OnClickListener() {

        public void onClick(View v) {
            orderCancelPopWindowTwo.dismiss();
            switch (v.getId()) {
                case R.id.sure:
                {
                    //确认收货
                    sureOrder();
                }
                break;
            }
        }
    };
    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            orderCancelPopWindow.dismiss();
            switch (v.getId()) {
                case R.id.sure:
                {
                    //删除订单
                    deleteOrder();
                }
                break;
            }
        }
    };

    //确认发货
    private void sureOrder() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                orderVos.get(tmpPosition).setStatus("6");
                                orderVos.get(tmpPosition).setDistribution_status("1");
                                adapter.notifyDataSetChanged();
                                Toast.makeText(MineOrdersMngActivity.this, R.string.send_order_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MineOrdersMngActivity.this, R.string.send_order_error_one, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineOrdersMngActivity.this, R.string.send_order_error_one, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineOrdersMngActivity.this, R.string.send_order_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("order_no", orderVoTmp.getOrder_no());
                params.put("status", "6");//6物流运输中（卖家确认订单）
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


    //取消订单弹框，需要卖家说明取消原因
    private void showCancel() {
        final Dialog picAddDialog = new Dialog(MineOrdersMngActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(this, R.layout.order_cancel_dialog, null);
        TextView jubao_sure = (TextView) picAddInflate.findViewById(R.id.jubao_sure);
        final EditText jubao_cont = (EditText) picAddInflate.findViewById(R.id.jubao_cont);
        //举报提交
        jubao_sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String cont = jubao_cont.getText().toString();
                if (StringUtil.isNullOrEmpty(cont)) {
                    Toast.makeText(MineOrdersMngActivity.this, R.string.cancel_order_error_two, Toast.LENGTH_LONG).show();
                    return;
                }
                cancel(cont);
                picAddDialog.dismiss();
            }
        });

        //举报取消
        TextView jubao_cancle = (TextView) picAddInflate.findViewById(R.id.jubao_cancle);
        jubao_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picAddDialog.dismiss();
            }
        });
        picAddDialog.setContentView(picAddInflate);
        picAddDialog.show();
    }

    //取消订单
    public void cancel(final String cont) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.CANCEL_ORDERS_SJ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                orderVos.get(tmpPosition).setStatus("3");
                                adapter.notifyDataSetChanged();
                                Toast.makeText(MineOrdersMngActivity.this, R.string.cancel_order_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MineOrdersMngActivity.this, R.string.cancel_order_error_one, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineOrdersMngActivity.this, R.string.cancel_order_error_one, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineOrdersMngActivity.this, R.string.cancel_order_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("seller_emp_id", orderVoTmp.getSeller_emp_id());
                params.put("emp_id", orderVoTmp.getEmp_id());
                params.put("order_no", orderVoTmp.getOrder_no());
                params.put("cont", cont);
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

    //删除订单
    public void deleteOrder() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                orderVos.remove(tmpPosition);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(MineOrdersMngActivity.this, R.string.delete_order_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MineOrdersMngActivity.this, R.string.cancel_order_error_one, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineOrdersMngActivity.this, R.string.cancel_order_error_one, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineOrdersMngActivity.this, R.string.cancel_order_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("order_no", orderVoTmp.getOrder_no());
                params.put("status", "4");//4作废订单
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

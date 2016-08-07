package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.AnimateFirstDisplayListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.GoodSingleDATA;
import com.liangxun.yuejiula.data.ShoppingAddressSingleDATA;
import com.liangxun.yuejiula.entity.OrderVo;
import com.liangxun.yuejiula.entity.ShoppingAddress;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/15.
 * 订单详情
 */
public class DetailOrderMngActivity extends BaseActivity implements View.OnClickListener {
    private OrderVo orderVo;//传递过来的值
    private ImageView back;
    private TextView order_status;

    //收货地址
    private TextView order_name;
    private TextView order_tel;
    private TextView order_location;
    //买家信息
    private ImageView item_head;
    private TextView item_nickname;

    //订单信息
    private ImageView item_pic;
    private TextView item_content;
    private TextView item_prices;
    private TextView item_money;
    private TextView item_count;

    //功能按钮
    private Button button_one;
    private Button button_two;

    private RelativeLayout relative_one;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private TextView order_dateline;//订单编号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_order_activity);
        orderVo = (OrderVo) getIntent().getExtras().get("orderVo");
        initView();
        //填充数据
        initData();
        //获得收货地址
        getAddressById();
    }

    private void initView() {
        back = (ImageView) this.findViewById(R.id.back);
        order_status = (TextView) this.findViewById(R.id.order_status);
        order_name = (TextView) this.findViewById(R.id.order_name);
        order_tel = (TextView) this.findViewById(R.id.order_tel);
        order_location = (TextView) this.findViewById(R.id.order_location);
        item_head = (ImageView) this.findViewById(R.id.item_head);
        item_nickname = (TextView) this.findViewById(R.id.item_nickname);
        item_pic = (ImageView) this.findViewById(R.id.item_pic);
        item_content = (TextView) this.findViewById(R.id.item_content);
        item_prices = (TextView) this.findViewById(R.id.item_prices);
        item_money = (TextView) this.findViewById(R.id.item_money);
        item_count = (TextView) this.findViewById(R.id.item_count);
        button_one = (Button) this.findViewById(R.id.button_one);
        button_two = (Button) this.findViewById(R.id.button_two);
        relative_one = (RelativeLayout) this.findViewById(R.id.relative_one);
        order_dateline = (TextView) this.findViewById(R.id.order_dateline);

        button_one.setOnClickListener(this);
        button_two.setOnClickListener(this);
        item_head.setOnClickListener(this);
        item_nickname.setOnClickListener(this);
        back.setOnClickListener(this);
        relative_one.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.relative_one:
                getGoodsByUUID();
                break;
            case R.id.item_head:{
                Intent profileView = new Intent(DetailOrderMngActivity.this, ProfilePersonalActivity.class);
                profileView.putExtra(Constants.EMPID, orderVo.getEmp_id());
                startActivity(profileView);
            }
                break;
            case R.id.item_nickname:
            {
                Intent profileView = new Intent(DetailOrderMngActivity.this, ProfilePersonalActivity.class);
                profileView.putExtra(Constants.EMPID, orderVo.getEmp_id());
                startActivity(profileView);

            }
                break;
            case R.id.button_one:
            {
                Intent profileView = new Intent(DetailOrderMngActivity.this, ProfilePersonalActivity.class);
                profileView.putExtra(Constants.EMPID, orderVo.getEmp_id());
                startActivity(profileView);
            }
                break;
            case R.id.button_two:
            {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + orderVo.getEmpMobile()));
                this.startActivity(intent);
            }
                break;
        }
    }

    void initData(){
        StringBuilder datetime = new StringBuilder();
        datetime.append("订单编号:" + orderVo.getOrder_no());
        switch (Integer.parseInt(orderVo.getStatus())){
            //1生成订单,2支付订单,3取消订单,4作废订单,5完成订单', 6物流运输中（卖家确认订单）
            case 1:
                order_status.setText("等待买家付款");
                if(!StringUtil.isNullOrEmpty(orderVo.getCreate_time())){
                    datetime.append("\n" + "创建时间:"+orderVo.getCreate_time());
                }
                break;
            case 2:
                order_status.setText("等待卖家发货");
                if(!StringUtil.isNullOrEmpty(orderVo.getCreate_time())){
                    datetime.append("\n" + "创建时间:"+orderVo.getCreate_time());
                }
                if(!StringUtil.isNullOrEmpty(orderVo.getPay_time())) {
                    datetime.append("\n" + "付款时间:"+orderVo.getPay_time());
                }
                break;
            case 3:
                order_status.setText("订单已取消");
                break;
            case 4:
                order_status.setText("订单已作废");
                break;
            case 5:
                order_status.setText("订单已完成");
                if(!StringUtil.isNullOrEmpty(orderVo.getCreate_time())){
                    datetime.append("\n" + "创建时间:" + orderVo.getCreate_time());
                }
                if(StringUtil.isNullOrEmpty(orderVo.getPay_time())){
                    datetime.append("\n" + "付款时间:" + orderVo.getPay_time());
                }
                if(!StringUtil.isNullOrEmpty(orderVo.getSend_time())){
                    datetime.append("\n" + "发货时间:" + orderVo.getSend_time());
                }
                if(!StringUtil.isNullOrEmpty(orderVo.getAccept_time())){
                    datetime.append("\n" + "收货时间:" + orderVo.getAccept_time());
                }
                if(!StringUtil.isNullOrEmpty(orderVo.getCompletion_time())){
                    datetime.append("\n" + "完成时间:" + orderVo.getCompletion_time());
                }
                break;
            case 6:
                order_status.setText("物流运输中");
                if(!StringUtil.isNullOrEmpty(orderVo.getCreate_time())){
                    datetime.append("\n" + "创建时间:" + orderVo.getCreate_time());
                }
                if(!StringUtil.isNullOrEmpty(orderVo.getPay_time())){
                    datetime.append("\n" + "付款时间:" + orderVo.getPay_time());
                }
                if(!StringUtil.isNullOrEmpty(orderVo.getSend_time())){
                    datetime.append("\n" + "发货时间:" + orderVo.getSend_time());
                }
                break;
        }
        imageLoader.displayImage(orderVo.getEmpCover(), item_head, UniversityApplication.txOptions, animateFirstListener);
        imageLoader.displayImage(orderVo.getGoodsCover(), item_pic, UniversityApplication.txOptions, animateFirstListener);
        item_nickname.setText(orderVo.getEmpName());
        item_content.setText(orderVo.getGoodsTitle());
        item_prices.setText(getResources().getString(R.string.money) + orderVo.getGoodsPrice());
        item_count.setText(String.format(getResources().getString(R.string.item_count_adapter), orderVo.getGoods_count()));
        item_money.setText(String.format(getResources().getString(R.string.item_money_adapter), Double.valueOf(orderVo.getPayable_amount())));
        order_dateline.setText(datetime);
    }

    void getAddressById(){
        //收货地址获得
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.GET_ADDRESS_BYID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            ShoppingAddressSingleDATA data = getGson().fromJson(s, ShoppingAddressSingleDATA.class);
                            if (data.getCode() == 200) {
                               //收货地址
                                initAddress(data.getData());
                            } else {
                                Toast.makeText(DetailOrderMngActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailOrderMngActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailOrderMngActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("address_id", orderVo.getAddress_id());
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

    void initAddress(ShoppingAddress shoppingAddress){
        //收货地址
        order_name.setText(shoppingAddress.getAccept_name());
        order_tel.setText(shoppingAddress.getPhone());
        order_location.setText(shoppingAddress.getProvinceName()+shoppingAddress.getCityName()+shoppingAddress.getAreaName()+shoppingAddress.getAddress());
    }

    //根据商品UUID
    private void getGoodsByUUID() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.GET_GOODS_DETAIL_BYUUID_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            if (StringUtil.isJson(s)) {
                                GoodSingleDATA data = getGson().fromJson(s, GoodSingleDATA.class);
                                if (data.getCode() == 200) {
                                    Intent goodsView = new Intent(DetailOrderMngActivity.this, DetailGoodsActivity.class);
                                    goodsView.putExtra(Constants.GOODS, data.getData());
                                    startActivity(goodsView);
                                } else {
                                    Toast.makeText(DetailOrderMngActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(DetailOrderMngActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailOrderMngActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailOrderMngActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", orderVo.getGoods_id());
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

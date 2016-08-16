package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import com.alipay.sdk.app.PayTask;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.ItemCartAdapter;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.GoodSingleDATA;
import com.liangxun.yuejiula.data.OrderInfoAndSignDATA;
import com.liangxun.yuejiula.data.ShoppingAddressSingleDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.db.DBHelper;
import com.liangxun.yuejiula.db.ShoppingCart;
import com.liangxun.yuejiula.entity.Order;
import com.liangxun.yuejiula.entity.OrdersForm;
import com.liangxun.yuejiula.entity.ShoppingAddress;
import com.liangxun.yuejiula.order.OrderInfoAndSign;
import com.liangxun.yuejiula.order.PayResult;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.CustomProgressDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/2.
 * 生成订单
 */
public class OrderMakeActivity  extends BaseActivity implements View.OnClickListener,OnClickContentItemListener {
    private String out_trade_no;

    //---------------------------------支付开始----------------------------------------
    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
//                        Toast.makeText(OrderMakeActivity.this, "支付成功",
//                                Toast.LENGTH_SHORT).show();
                        //更新订单状态
                        updateMineOrder();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(OrderMakeActivity.this, "支付结果确认中",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(OrderMakeActivity.this, "支付失败",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(OrderMakeActivity.this, "检查结果为：" + msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        };
    };
    //------------------------------------------------------------------------------------
    private ImageView back;

    private TextView order_name;//收货人
    private TextView order_tel;//电话
    private TextView order_location;//地址
    private ImageView select_location;//进入选择地址
    private TextView no_address;//收货地址暂无

    private Button order_sure;//确定按钮
    private TextView order_count;//价格合计
    private List<ShoppingCart> lists;//购物车集合
    private ItemCartAdapter adapter;
    private ListView lstv;
    private ShoppingCart shoppingCart;

    private String emp_id;//当前登陆者  ，买家
    private String schoolId;
    private ShoppingAddress shoppingAddress;

    private OrdersForm SGform = new OrdersForm();
    private List<Order> listOrders = new ArrayList<Order>();//订单集合 --传给服务器


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_make_activity);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        lists = (List<ShoppingCart>) getIntent().getExtras().get("listsgoods");
        initView();

        progressDialog = new CustomProgressDialog(OrderMakeActivity.this, "正在加载中",R.anim.custom_dialog_frame);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        toCalculate();
        getMorenAddress();
    }

    private void initView() {
        back = (ImageView) this.findViewById(R.id.back);
        order_sure = (Button) this.findViewById(R.id.order_sure);
        order_count = (TextView) this.findViewById(R.id.order_count);
        order_name = (TextView) this.findViewById(R.id.order_name);
        order_tel = (TextView) this.findViewById(R.id.order_tel);
        order_location = (TextView) this.findViewById(R.id.order_location);
        select_location = (ImageView) this.findViewById(R.id.select_location);

        back.setOnClickListener(this);
        select_location.setOnClickListener(this);
        order_sure.setOnClickListener(this);
        lstv = (ListView) this.findViewById(R.id.lstv);
        adapter = new ItemCartAdapter(lists , OrderMakeActivity.this, false);
        adapter.setOnClickContentItemListener(this);
        lstv.setAdapter(adapter);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                shoppingCart = lists.get(position);
//                if (shoppingCart != null) {
//                    //查询商品信息，根据商品ID
//                    getGoodsById();
//                }
            }
        });
        no_address = (TextView) this.findViewById(R.id.no_address);
        no_address.setVisibility(View.GONE);
        no_address.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.back:
                finish();
                break;
            case R.id.order_sure:
                //todo
                //判断是否在营业时间
                order_sure.setClickable(false);
                if(shoppingAddress != null){
                    //先传值给服务端
                    if(lists != null && lists.size() > 0){
                        for(int i=0;i<lists.size();i++){
                            ShoppingCart shoppingCart = lists.get(i);
                            if(shoppingCart!=null && shoppingCart.getIs_select().equals("0")){
                                Double payable_amount = Double.valueOf(shoppingCart.getSell_price())*Integer.parseInt(shoppingCart.getGoods_count());
                                listOrders.add(new Order(shoppingCart.getGoods_id(), emp_id, shoppingCart.getEmp_id()
                                        ,shoppingAddress.getAddress_id(), shoppingCart.getGoods_count(), String.valueOf(payable_amount)
                                        ,"0","0","","","","",shoppingAddress.getProvince(),shoppingAddress.getCity(),shoppingAddress.getArea(), schoolId));
                            }
                        }
                    }
                    SGform.setList(listOrders);
                    if(listOrders!=null && listOrders.size() > 0){
                        //传值给服务端
                        sendOrderToServer();
                    }
                }else{
                    order_sure.setClickable(true);
                    Toast.makeText(OrderMakeActivity.this, R.string.no_address_error, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.select_location:
            case R.id.no_address:
                Intent selectAddressView = new Intent(OrderMakeActivity.this, SelectAddressActivity.class);
                if(shoppingAddress != null){
                    selectAddressView.putExtra("address_id", shoppingAddress.getAddress_id());
                }else {
                    selectAddressView.putExtra("address_id", "0");
                }
                startActivityForResult(selectAddressView, 0);
                break;
        }
    }

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        switch (flag){
            case 1:
                //左侧选择框按钮
                if("0".equals(lists.get(position).getIs_select())){
                    lists.get(position).setIs_select("1");
                }else {
                    lists.get(position).setIs_select("0");
                }
                adapter.notifyDataSetChanged();
                toCalculate();
                break;
            case 2:
                //加号
                lists.get(position).setGoods_count(String.valueOf((Integer.parseInt(lists.get(position).getGoods_count()) + 1)));
                adapter.notifyDataSetChanged();
                toCalculate();
                break;
            case 3:
                //减号
                int selectNum = Integer.parseInt(lists.get(position).getGoods_count());
                if(selectNum == 0){
                    Toast.makeText(OrderMakeActivity.this, R.string.select_zero,Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    lists.get(position).setGoods_count(String.valueOf((Integer.parseInt(lists.get(position).getGoods_count()) - 1)));
                    adapter.notifyDataSetChanged();
                }
                toCalculate();
                break;
            case 4:
                //删除
                DBHelper.getInstance(OrderMakeActivity.this).deleteShoppingByGoodsId(lists.get(position).getCartid());
                lists.remove(position);
                adapter.notifyDataSetChanged();
                toCalculate();
                break;
        }
    }

    //计算金额总的
    void toCalculate(){
        DecimalFormat df = new DecimalFormat("0.00");
        if (lists != null){
            Double doublePrices = 0.0;
            for(int i=0; i<lists.size() ;i++){
                ShoppingCart shoppingCart = lists.get(i);
                if(shoppingCart.getIs_select() .equals("0")){
                    //默认是选中的
                    doublePrices = doublePrices + Double.parseDouble(shoppingCart.getSell_price()) * Double.parseDouble(shoppingCart.getGoods_count());
                }
            }
            order_count.setText(getResources().getString(R.string.countPrices) + df.format(doublePrices).toString());
        }
    }

    public void getGoodsById(){
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
                                    Intent goodsdetail = new Intent(OrderMakeActivity.this, DetailGoodsActivity.class);
                                    goodsdetail.putExtra(Constants.GOODS, data.getData());
                                    startActivity(goodsdetail);
                                } else {
                                    Toast.makeText(OrderMakeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(OrderMakeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(OrderMakeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(OrderMakeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", shoppingCart.getGoods_id());
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

    //获得默认收货地址
    public void getMorenAddress(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.GET_MOREN_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            if (StringUtil.isJson(s)) {
                                ShoppingAddressSingleDATA data = getGson().fromJson(s, ShoppingAddressSingleDATA.class);
                                if (data.getCode() == 200) {
                                    //获得默认收货地址
                                    shoppingAddress = data.getData();
                                    if(shoppingAddress != null){
                                        initData();
                                    }else{
                                        //没有收货地址的话
                                        no_address.setVisibility(View.VISIBLE);
                                        order_name.setVisibility(View.GONE);
                                        order_tel.setVisibility(View.GONE);
                                        order_location.setVisibility(View.GONE);
                                        if (progressDialog != null) {
                                            progressDialog.dismiss();
                                        }
                                    }
                                } else {
                                    if (progressDialog != null) {
                                        progressDialog.dismiss();
                                    }
                                    Toast.makeText(OrderMakeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(OrderMakeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(OrderMakeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(OrderMakeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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

    void initData(){
        no_address.setVisibility(View.GONE);
        order_name.setVisibility(View.VISIBLE);
        order_tel.setVisibility(View.VISIBLE);
        order_location.setVisibility(View.VISIBLE);
        order_name.setText(shoppingAddress.getAccept_name());
        order_tel.setText(shoppingAddress.getPhone());
        order_location.setText(shoppingAddress.getProvinceName() + shoppingAddress.getCityName() + shoppingAddress.getAreaName() + shoppingAddress.getAddress());
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:
                shoppingAddress = (ShoppingAddress) data.getExtras().get("select_address");
                if(shoppingAddress != null){
                    initData();
                }
                break;
        }
    }

    //传order给服务器
    private void sendOrderToServer() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.SEND_ORDER_TOSERVER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            OrderInfoAndSignDATA data = getGson().fromJson(s, OrderInfoAndSignDATA.class);
                            if (data.getCode() == 200) {
                                //删除购物车商品
                                deleteCart();
                                //已经生成订单，等待支付，下面去支付
                                out_trade_no= data.getData().getOut_trade_no();
//                                pay(data.getData());//调用支付接口
                                updateMineOrder();
                            }else if(data.getCode() == 2){
                                Toast.makeText(OrderMakeActivity.this, R.string.order_error_three, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else {
                                Toast.makeText(OrderMakeActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(OrderMakeActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(OrderMakeActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
                        finish();
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

    //---------------------------------------------------------支付宝------------------------------------------

    /**
     * call alipay sdk pay. 调用SDK支付
     *
     */
    public void pay(OrderInfoAndSign orderInfoAndSign) {

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfoAndSign.getOrderInfo() + "&sign=\"" + orderInfoAndSign.getSign() + "\"&"
                + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(OrderMakeActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     *
     */
    public void check(View v) {
        Runnable checkRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(OrderMakeActivity.this);
                // 调用查询接口，获取查询结果
                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();

    }

    /**
     * get the sdk version. 获取SDK版本号
     *
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }


    //更新订单状态
    void updateMineOrder(){
            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.UPDATE_ORDER_TOSERVER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            if (StringUtil.isJson(s)) {
                                SuccessData data = getGson().fromJson(s, SuccessData.class);
                                if (data.getCode() == 200) {
                                    Toast.makeText(OrderMakeActivity.this, R.string.order_success, Toast.LENGTH_SHORT).show();
                                    //跳转到订单列表
                                    Intent orderView =  new Intent(OrderMakeActivity.this, MineOrdersActivity.class);
                                    startActivity(orderView);
                                    finish();
                                } else {
                                    Toast.makeText(OrderMakeActivity.this, R.string.order_error_two, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(OrderMakeActivity.this, R.string.order_error_two, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(OrderMakeActivity.this, R.string.order_error_two, Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("out_trade_no",  out_trade_no);
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
    //清空购物车
    void deleteCart(){
        DBHelper.getInstance(OrderMakeActivity.this).deleteShopping();
        Intent clear_cart = new Intent("cart_clear");
        sendBroadcast(clear_cart);
    }
}

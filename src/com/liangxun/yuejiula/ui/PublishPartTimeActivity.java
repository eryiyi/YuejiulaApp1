package com.liangxun.yuejiula.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.PartTimeTypeDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.PartTimeType;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.CustomProgressDialog;
import com.liangxun.yuejiula.widget.popview.CustomerSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/7
 * Time: 16:12
 * 类的功能、说明写在此处.
 */
public class PublishPartTimeActivity extends BaseActivity implements View.OnClickListener {
    private final static int SELECT_LOCAL_PHOTO = 110;
    private static final String TAG = PublishGoodsActivity.class.getSimpleName();

    private ImageView publis_part_back;//返回
    private TextView publish_parttime_run;//发布
    private ImageView publish_part_imv;//添加图片

    private EditText publish_parttimetitle;//发布标题
    private EditText publish_part_number;//发布人数
    private EditText publish_part_money;//薪金待遇
    private EditText publish_part_person;//联系人
    private EditText publish_part_tel;//电话号码
    private EditText publish_part_address;//地址
    private EditText publish_part_content;//招募详情
    private EditText publish_part_qq;//QQ

    private String title;
    private String number;
    private String money;
    private String person;
    private String tel;
    private String address;
    private String content;
    private String qq;

    private CustomerSpinner publish_part_type;//类别

    private ArrayAdapter<String> adapterspin;
    private ArrayList<String> goodstypes = new ArrayList<String>();
    private ArrayList<PartTimeType> goodstypeslst = new ArrayList<PartTimeType>();

    private String typeId = "";
    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID

    private String type = "";//登陆者类别

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_parttime);
        initView();
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        type = getGson().fromJson(getSp().getString(Constants.EMPTYPE, ""), String.class);
        initData();
    }

    private void initView() {
        publis_part_back = (ImageView) this.findViewById(R.id.publis_part_back);
        publis_part_back.setOnClickListener(this);
        publish_parttime_run = (TextView) this.findViewById(R.id.publish_parttime_run);
        publish_parttime_run.setOnClickListener(this);
        publish_parttimetitle = (EditText) this.findViewById(R.id.publish_parttimetitle);
        publish_part_number = (EditText) this.findViewById(R.id.publish_part_number);
        publish_part_money = (EditText) this.findViewById(R.id.publish_part_money);
        publish_part_person = (EditText) this.findViewById(R.id.publish_part_person);
        publish_part_tel = (EditText) this.findViewById(R.id.publish_part_tel);
        publish_part_address = (EditText) this.findViewById(R.id.publish_part_address);
        publish_part_content = (EditText) this.findViewById(R.id.publish_part_content);

        publish_part_type = (CustomerSpinner) this.findViewById(R.id.publish_part_type);
        publish_part_qq = (EditText) this.findViewById(R.id.publish_part_qq);

        adapterspin = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, goodstypes);
        publish_part_type.setList(goodstypes);
        publish_part_type.setAdapter(adapterspin);
        publish_part_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, position + "");
                if (position > 0) {
                    typeId = goodstypeslst.get(position - 1).getId();//获得类别UUID
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publis_part_back:
                finish();
                break;
            case R.id.publish_parttime_run:
                if (!(type.equals("2") || type.equals("3"))) {
                    Toast.makeText(PublishPartTimeActivity.this, R.string.publishgoods_error_nineone, Toast.LENGTH_SHORT).show();
                    return;
                }
                title = publish_parttimetitle.getText().toString();
                number = publish_part_number.getText().toString();
                money = publish_part_money.getText().toString();
                person = publish_part_person.getText().toString();
                tel = publish_part_tel.getText().toString();
                address = publish_part_address.getText().toString();
                content = publish_part_content.getText().toString();
                qq = publish_part_qq.getText().toString();
                if (StringUtil.isNullOrEmpty(title)) {
                    Toast.makeText(PublishPartTimeActivity.this, R.string.publishpart_error_one, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (title.length() > 100) {
                    Toast.makeText(PublishPartTimeActivity.this, R.string.publishpart_error_onetwo, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(number)) {
                    Toast.makeText(PublishPartTimeActivity.this, R.string.publishpart_error_two, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (number.length() > 50) {
                    Toast.makeText(PublishPartTimeActivity.this, R.string.publishpart_error_twotwo, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(money)) {
                    Toast.makeText(PublishPartTimeActivity.this, R.string.publishpart_error_three, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (money.length() > 50) {
                    Toast.makeText(PublishPartTimeActivity.this, R.string.publishpart_error_threetwo, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(person)) {
                    Toast.makeText(PublishPartTimeActivity.this, R.string.publishpart_error_fourthree, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (person.length() > 50) {
                    Toast.makeText(PublishPartTimeActivity.this, R.string.publishpart_error_fourthreetwo, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(tel)) {
                    Toast.makeText(PublishPartTimeActivity.this, R.string.publishpart_error_five, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tel.length() > 25) {
                    Toast.makeText(PublishPartTimeActivity.this, R.string.publishpart_error_fivetwo, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(qq)) {
                    Toast.makeText(PublishPartTimeActivity.this, R.string.publishpart_error_eight, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (qq.length() > 50) {
                    Toast.makeText(PublishPartTimeActivity.this, R.string.publishpart_error_eighttwo, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(address)) {
                    Toast.makeText(PublishPartTimeActivity.this, R.string.publishpart_error_six, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (address.length() > 500) {
                    Toast.makeText(PublishPartTimeActivity.this, R.string.publishpart_error_sixtwo, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(content)) {
                    Toast.makeText(PublishPartTimeActivity.this, R.string.publishpart_error_seven, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (content.length() > 4000) {
                    Toast.makeText(PublishPartTimeActivity.this, R.string.publishpart_error_seventwo, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(typeId)) {
                    Toast.makeText(PublishPartTimeActivity.this, R.string.publishpart_error_night, Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog = new CustomProgressDialog(PublishPartTimeActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                publishAll();
                break;
        }
    }


    private void publishAll() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.PUBLISH_PARTTIME_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(PublishPartTimeActivity.this, R.string.publish_success, Toast.LENGTH_SHORT).show();
                                //调用广播，刷新主页
                                Intent intent1 = new Intent(Constants.SEND_PART_SUCCESS);
                                sendBroadcast(intent1);
                                finish();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(PublishPartTimeActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("typeId", typeId);
                params.put("title", title);
                params.put("cont", content);
                params.put("peopleNumber", number);
                params.put("money", money);
                params.put("contact", person);
                params.put("mobile", tel);
                params.put("qq", qq);
                params.put("address", address);
                params.put("empId", emp_id);
                params.put("schoolId", schoolId);
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


    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.GET_PARTTIMETYPE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            PartTimeTypeDATA data = getGson().fromJson(s, PartTimeTypeDATA.class);
                            if (data.getCode() == 200) {
                                goodstypes.clear();
                                goodstypes.add("选择招募类别");
                                goodstypeslst.addAll(data.getData());
                                for (PartTimeType goodstype1 : goodstypeslst) {
                                    goodstypes.add(goodstype1.getName());
                                }
                                adapterspin.notifyDataSetChanged();
                            } else {
                                Toast.makeText(PublishPartTimeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PublishPartTimeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(PublishPartTimeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}

package com.liangxun.yuejiula.ui;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.receiver.SMSBroadcastReceiver;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.CustomProgressDialog;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/26
 * Time: 8:01
 * 类的功能、说明写在此处.
 */
public class ReSetMobileActivity extends BaseActivity implements View.OnClickListener {
    private ImageView reset_mobile_menu;//返回按钮
//    private EditText reset_mobile_two;//新手机号
    private TextView reset_mobile_button_two;//确认按钮

//    private String mobile_one;
//    private String mobile_two;


//    private String schoolId = "";
//    private String emp_id = "";//当前登陆者UUID
    //
    private EditText mm_emp_mobile;
    private EditText code;
    private Button btn_code;

    //mob短信
    // 填写从短信SDK应用后台注册得到的APPKEY
    private static String APPKEY = InternetURL.APP_MOB_KEY;//"69d6705af33d";0d786a4efe92bfab3d5717b9bc30a10d
    // 填写从短信SDK应用后台注册得到的APPSECRET
    private static String APPSECRET = InternetURL.APP_MOB_SCRECT;
    public String phString;//手机号码

    //短信读取
    private SMSBroadcastReceiver mSMSBroadcastReceiver;
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_mobile_xml);
        res = getResources();
        //mob短信无GUI
        SMSSDK.initSDK(this, APPKEY, APPSECRET, true);
        EventHandler eh=new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eh);
        //生成广播处理
        mSMSBroadcastReceiver = new SMSBroadcastReceiver();
        //实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter(ACTION);
        intentFilter.setPriority(Integer.MAX_VALUE);
        //注册广播
        this.registerReceiver(mSMSBroadcastReceiver, intentFilter);
        mSMSBroadcastReceiver.setOnReceivedMessageListener(new SMSBroadcastReceiver.MessageListener() {
            @Override
            public void onReceived(String message) {
                //花木通的验证码：8469【掌淘科技】
                if(!StringUtil.isNullOrEmpty(message)){
                    String codestr = StringUtil.valuteNumber(message);
                    if(!StringUtil.isNullOrEmpty(codestr)){
                        code.setText(codestr);
                    }
                }
            }
        });
        initView();
    }

    private void initView() {
        mm_emp_mobile = (EditText) this.findViewById(R.id.mm_emp_mobile);
        code = (EditText) this.findViewById(R.id.code);
        btn_code = (Button) this.findViewById(R.id.btn_code);
        btn_code.setOnClickListener(this);
        reset_mobile_menu = (ImageView) this.findViewById(R.id.reset_mobile_menu);
//        reset_mobile_two = (EditText) this.findViewById(R.id.reset_mobile_two);
        reset_mobile_button_two = (TextView) this.findViewById(R.id.reset_mobile_button_two);

        reset_mobile_menu.setOnClickListener(this);
        reset_mobile_button_two.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_code:
                //验证码
                if(!TextUtils.isEmpty(mm_emp_mobile.getText().toString()) && mm_emp_mobile.getText().toString().length() == 11){
                    SMSSDK.getVerificationCode("86", mm_emp_mobile.getText().toString());//发送请求验证码，手机10s之内会获得短信验证码
                    phString=mm_emp_mobile.getText().toString();
                    btn_code.setClickable(false);//不可点击
                    MyTimer myTimer = new MyTimer(60000,1000);
                    myTimer.start();
                }else {
                    showMsg(ReSetMobileActivity.this, res.getString(R.string.pwr_error_seven));
                    return;
                }
                break;
            case R.id.reset_mobile_menu:
                finish();
                break;
            case R.id.reset_mobile_button_two:
//                mobile_two = reset_mobile_two.getText().toString();
                if (StringUtil.isNullOrEmpty(mm_emp_mobile.getText().toString())) {
                    Toast.makeText(ReSetMobileActivity.this, R.string.reset_mobile_error_two, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mm_emp_mobile.getText().toString().trim().length() != 11) {
                    Toast.makeText(ReSetMobileActivity.this, R.string.register_error_two, Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog = new CustomProgressDialog(ReSetMobileActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                SMSSDK.submitVerificationCode("86", phString, code.getText().toString());
//                resetMobile();
                break;
        }
    }
    class MyTimer extends CountDownTimer {

        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            btn_code.setText(res.getString(R.string.daojishi_three));
            btn_code.setClickable(true);//可点击
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btn_code.setText(res.getString(R.string.daojishi_one) + millisUntilFinished / 1000 + res.getString(R.string.daojishi_two));
        }
    }

    Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                System.out.println("--------result"+event);
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
//                    Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
                    resetMobile();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //已经验证
                    Toast.makeText(getApplicationContext(), R.string.code_msg_one, Toast.LENGTH_SHORT).show();
                }

            } else {
//				((Throwable) data).printStackTrace();
                Toast.makeText(ReSetMobileActivity.this, R.string.code_msg_two, Toast.LENGTH_SHORT).show();
//					Toast.makeText(MainActivity.this, "123", Toast.LENGTH_SHORT).show();
                int status = 0;
                try {
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;

                    JSONObject object = new JSONObject(throwable.getMessage());
                    String des = object.optString("detail");
                    status = object.optInt("status");
                    if (!TextUtils.isEmpty(des)) {
                        Toast.makeText(ReSetMobileActivity.this, des, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    SMSLog.getInstance().w(e);
                }
            }
        };
    };

    public void onDestroy() {
        super.onPause();
        SMSSDK.unregisterAllEventHandler();
        //注销短信监听广播
        this.unregisterReceiver(mSMSBroadcastReceiver);
    };



    private void resetMobile() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.UPDATE_MOBILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(ReSetMobileActivity.this, R.string.reset_mobile_success, Toast.LENGTH_SHORT).show();
                                save(Constants.EMPMOBILE, mm_emp_mobile.getText().toString());
                                finish();
                            } else if (data.getCode() == 1) {
                                Toast.makeText(ReSetMobileActivity.this, R.string.reset_mobile_error_three, Toast.LENGTH_SHORT).show();
                            } else if (data.getCode() == 2) {
                                Toast.makeText(ReSetMobileActivity.this, R.string.reset_mobile_error_four, Toast.LENGTH_SHORT).show();
                            } else if (data.getCode() == 3) {
                                Toast.makeText(ReSetMobileActivity.this, R.string.reset_mobile_error_five, Toast.LENGTH_SHORT).show();
                            } else if (data.getCode() == 4) {
                                Toast.makeText(ReSetMobileActivity.this, R.string.reset_mobile_error_six, Toast.LENGTH_SHORT).show();
                            } else if (data.getCode() == 6) {
                                Toast.makeText(ReSetMobileActivity.this, R.string.reset_mobile_error_seven, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ReSetMobileActivity.this, R.string.reset_pwr_error_seven, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ReSetMobileActivity.this, R.string.reset_pwr_error_seven, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ReSetMobileActivity.this, R.string.reset_pwr_error_seven, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", getGson().fromJson(getSp().getString(Constants.EMPMOBILE, ""), String.class));
                params.put("reMobile", mm_emp_mobile.getText().toString().trim());
                params.put("empId", getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class));
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

package com.liangxun.yuejiula.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.Record;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhl on 2016/6/12.
 */
public class AddRecordJpActivity extends BaseActivity implements View.OnClickListener {
    private Record record;
    private ImageView back;

    private TextView money_one;
    private EditText money;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_jp_activity);
        record = (Record) getIntent().getExtras().get("record");

        initView();
    }

    private void initView() {
        back = (ImageView) this.findViewById(R.id.back);
        back.setOnClickListener(this);
        this.findViewById(R.id.cancel).setOnClickListener(this);
        this.findViewById(R.id.chaojia).setOnClickListener(this);
        money_one = (TextView) this.findViewById(R.id.money_one);
        money = (EditText) this.findViewById(R.id.money);
        money_one.setText("当前价格："+record.getMoney());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.cancel:
                finish();
                break;
            case R.id.chaojia:
                if(StringUtil.isNullOrEmpty(money.getText().toString())){
                    showMsg(AddRecordJpActivity.this, "请输入超价金额！");
                    return;
                }
                progressDialog = new ProgressDialog(AddRecordJpActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.show();
                addChaojia();
                break;
        }
    }


    private void addChaojia() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +   InternetURL.SAVE_RECORDJP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                showMsg(AddRecordJpActivity.this ,"加价成功！");
                                Intent intent1 = new Intent("record_jp_success");
                                intent1.putExtra("money" , money.getText().toString());
                                intent1.putExtra("record_id" , record.getRecordId());
                                sendBroadcast(intent1);
                            }else {
                                Toast.makeText(AddRecordJpActivity.this, "加价失败！", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AddRecordJpActivity.this,  "加价失败！", Toast.LENGTH_SHORT).show();
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(AddRecordJpActivity.this,  "加价失败！",  Toast.LENGTH_SHORT).show();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("record_id", record.getRecordId());
                params.put("record_emp_id", record.getRecordEmpId());
                params.put("emp_id", getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class));
                params.put("money", money.getText().toString());
                params.put("moneyY", record.getMoney());
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

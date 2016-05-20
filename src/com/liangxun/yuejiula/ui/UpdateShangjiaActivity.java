package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.liangxun.yuejiula.entity.SellerSchoolList;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.DateBackListener;
import com.liangxun.yuejiula.widget.DateDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/3/29.
 */
public class UpdateShangjiaActivity extends BaseActivity implements View.OnClickListener, DateBackListener {
    private SellerSchoolList sellerSchoolList;
    private ImageView profile_personal_back;
    private TextView update;
    private TextView datetime;
    private TextView schoolname;
    private DateDialog dateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_shangjia);
        sellerSchoolList = (SellerSchoolList) getIntent().getExtras().get(Constants.SELLERSCHOOLLIST);
        initView();
    }

    private void initView() {
        profile_personal_back = (ImageView) this.findViewById(R.id.profile_personal_back);
        profile_personal_back.setOnClickListener(this);
        update = (TextView) this.findViewById(R.id.update);
        update.setOnClickListener(this);
        schoolname = (TextView) this.findViewById(R.id.schoolname);
        datetime = (TextView) this.findViewById(R.id.datetime);
        datetime.setOnClickListener(this);
        schoolname.setText(sellerSchoolList.getSchoolName());
        datetime.setText(sellerSchoolList.getEndTime());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_personal_back:
                finish();
                break;
            case R.id.update:
                if (StringUtil.isNullOrEmpty(datetime.getText().toString())) {
                    Toast.makeText(UpdateShangjiaActivity.this, R.string.error_goods_updatesj_three, Toast.LENGTH_SHORT).show();
                    return;
                }
                saveData();
                break;
            case R.id.datetime:
                dateDialog = new DateDialog(UpdateShangjiaActivity.this, R.style.MyAlertDialog, true);
                dateDialog.setDateBackListener(UpdateShangjiaActivity.this);
                dateDialog.show();
                break;
        }
    }

    @Override
    public void backTime(String date, boolean isStart) {
        datetime.setText(date);
    }

    public void saveData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_SHAGNJIA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(UpdateShangjiaActivity.this, R.string.success_goods_updatesj, Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(Constants.SELLERSCHOOLLIST_UPDATE);
                                sendBroadcast(intent1);
                                finish();
                            } else if (data.getCode() == 1) {
                                Toast.makeText(UpdateShangjiaActivity.this, R.string.error_goods_updatesj_one, Toast.LENGTH_SHORT).show();
                            } else if (data.getCode() == 2) {
                                Toast.makeText(UpdateShangjiaActivity.this, R.string.error_goods_updatesj_two, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(UpdateShangjiaActivity.this, R.string.reset_pwr_error_seven, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(UpdateShangjiaActivity.this, R.string.reset_pwr_error_seven, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", sellerSchoolList.getId());
                params.put("empId", sellerSchoolList.getEmpId());
                params.put("contractId", sellerSchoolList.getContractId());
                params.put("schoolId", sellerSchoolList.getSchoolId());
                params.put("endTime", datetime.getText().toString());
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

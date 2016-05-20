package com.liangxun.yuejiula.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
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
import com.liangxun.yuejiula.adapter.AnimateFirstDisplayListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.RecordDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.Record;
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
 * Created by Administrator on 2015/3/31.
 */
public class MineTuiguangActivity extends BaseActivity implements View.OnClickListener {
    ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private ImageView tg_cover;
    private TextView tg_cont;
    private ImageView tg_menu;
    private ImageView tg_right;
    private ImageView tg_delete;
    private String emp_id;
    private List<Record> recordAds = new ArrayList<Record>();
    private DeletePopWindow deleteWindow;
    private TextView tg_dateline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
        setContentView(R.layout.mine_tuiguang);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        initView();
        getData();
    }

    private void initView() {
        tg_cover = (ImageView) this.findViewById(R.id.tg_cover);
        tg_cont = (TextView) this.findViewById(R.id.tg_cont);
        tg_menu = (ImageView) this.findViewById(R.id.tg_menu);
        tg_right = (ImageView) this.findViewById(R.id.tg_right);
        tg_delete = (ImageView) this.findViewById(R.id.tg_delete);
        tg_dateline = (TextView) this.findViewById(R.id.tg_dateline);
        tg_menu.setOnClickListener(this);
        tg_right.setOnClickListener(this);
        tg_delete.setOnClickListener(this);
        tg_delete.setVisibility(View.GONE);
        tg_right.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tg_menu:

                finish();
                break;
            case R.id.tg_right:

                Intent addTg = new Intent(MineTuiguangActivity.this, AddMineTgActivity.class);
                startActivity(addTg);
                break;
            case R.id.tg_delete:

                showSelectImageDialog();
                break;
        }
    }

    private void getData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.TUIGUANG_AD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            RecordDATA data = getGson().fromJson(s, RecordDATA.class);
                            recordAds = data.getData();
                            initData();
                        } else {
                            Toast.makeText(MineTuiguangActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineTuiguangActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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

    public void initData() {
        if (recordAds != null && recordAds.size() > 0) {
            tg_delete.setVisibility(View.VISIBLE);
            tg_right.setVisibility(View.GONE);
            Record recordtmp = recordAds.get(0);
            tg_cont.setText(recordtmp.getRecordCont());
            tg_dateline.setText(recordtmp.getDateLine());
            imageLoader.displayImage(recordtmp.getRecordPicUrl(), tg_cover, UniversityApplication.options, animateFirstListener);
        } else {
            tg_delete.setVisibility(View.GONE);
            tg_right.setVisibility(View.VISIBLE);
        }
    }

    private void showSelectImageDialog() {
        deleteWindow = new DeletePopWindow(MineTuiguangActivity.this, itemsOnClick);
        deleteWindow.showAtLocation(MineTuiguangActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

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

    private void delete() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.DELETE_TUIGUANG_AD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(MineTuiguangActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(MineTuiguangActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineTuiguangActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineTuiguangActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
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

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.UPDATE_TG_SUCCESS)) {
                getData();
            }
        }

    };

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.UPDATE_TG_SUCCESS);

        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}

package com.liangxun.yuejiula.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.base.BaseFragment;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.CustomProgressDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/22.
 */
public class IndexFragment  extends BaseFragment implements View.OnClickListener, OnClickContentItemListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, null);
//        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
//        schoolId = "";
//        schoolIdEmp = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
//        initView(view);
//        String cover = getGson().fromJson(getSp().getString(Constants.EMPCOVER, ""), String.class);
//        imageLoader.displayImage(cover, main_cover, UniversityApplication.txOptions, animateFirstListener);
        progressDialog = new CustomProgressDialog(getActivity(), "正在加载中",R.anim.custom_dialog_frame);
        progressDialog.show();
        initData();
        return view;
    }

    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.RECORD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
//                            VideosData data = getGson().fromJson(s, VideosData.class);
//                            if (data.getCode() == 200) {
//                                if (IS_REFRESH) {
//                                    list.clear();
//                                }
//                                list.addAll(data.getData());
//                                lstv.onRefreshComplete();
//                                adapter.notifyDataSetChanged();
//                            } else {
//                                Toast.makeText(VideosActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                            }
                        } else {
//                            Toast.makeText(VideosActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
//                        Toast.makeText(VideosActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(1));
                params.put("schoolId", "");
                params.put("empId", "");
                params.put("schoolIdEmp", "");
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
    public void onClickContentItem(int position, int flag, Object object) {

    }

    @Override
    public void onClick(View view) {

    }

    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(Constants.SEND_SUCCESS)) {
//                String str = intent.getExtras().getString(Constants.SEND_VALUE_ONE);
//                if ("0".equals(str)) {
//                    schoolId = "";
//                    maintitle.setText("所有圈子");
//                }
//                if ("1".equals(str)) {
//                    schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
//                    maintitle.setText("我的圈子");
//                }
//                IS_REFRESH = true;
//                pageIndex = 1;
//                initData();
//            }
//            if (action.equals(Constants.SEND_PIC_TX_SUCCESS)) {
//                //更改头像的广播事件
//                imageLoader.displayImage(getGson().fromJson(getSp().getString(Constants.EMPCOVER, ""), String.class), main_cover, UniversityApplication.txOptions, animateFirstListener);
//            }
//            if (action.equals(Constants.SEND_COMMENT_RECORD_SUCCESS)) {
//                //刷新内容,评论+1
//                String recordId =  intent.getExtras().getString("recordId");
//                for(Record record:recordList){
//                    if(record.getRecordId().equals(recordId)){
//                        record.setPlNum(String.valueOf(Integer.parseInt(record.getPlNum())+1));
//                        break;
//                    }
//                }
//                adapter.notifyDataSetChanged();
//            }
//            if(action.equals(Constants.SEND_DELETE_RECORD_SUCCESS)){
//                String recordId =  intent.getExtras().getString("recordId");
//                for(Record record:recordList){
//                    if(record.getRecordId().equals(recordId)){
//                        recordList.remove(record);
//                        break;
//                    }
//                }
//                adapter.notifyDataSetChanged();
//            }
//            if(action.equals(Constants.SEND_INDEX_SUCCESS)){
//                Record record1 = (Record) intent.getExtras().get("addRecord");
//                recordList.add(0, record1);
//                adapter.notifyDataSetChanged();
//            }
        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SEND_SUCCESS);//设置下拉按钮的广播事件
        myIntentFilter.addAction(Constants.SEND_PIC_TX_SUCCESS);//设置头像的广播事件
        myIntentFilter.addAction(Constants.SEND_COMMENT_RECORD_SUCCESS);//动态评论添加  更新评论数量
        myIntentFilter.addAction(Constants.SEND_DELETE_RECORD_SUCCESS);//动态详情页删除动态，更新首页
        myIntentFilter.addAction(Constants.SEND_INDEX_SUCCESS);//添加说说和添加视频成功，刷新首页
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }


}

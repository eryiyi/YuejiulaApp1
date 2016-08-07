package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.MineRecordAdapter;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.RecordDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.Record;
import com.liangxun.yuejiula.entity.VideoPlayer;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.popview.DeletePopWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/8
 * Time: 9:03
 * 类的功能、说明写在此处.
 */
public class MineRecordActivity extends BaseActivity implements View.OnClickListener, OnClickContentItemListener {
    private ImageView mine_record_xml_menu;
    private List<Record> recordList;
    private ImageView search_null;
    //动态listview
    private PullToRefreshListView home_lstv;
    //动态适配器
    private MineRecordAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID
    Record recordtmp;//转换用

    private int tmpSelected;//暂时存UUID  删除用
    private DeletePopWindow deleteWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_record_xml);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        initView();
        initData();
    }
    private void initView() {
        search_null = (ImageView) this.findViewById(R.id.search_null);
        home_lstv = (PullToRefreshListView) this.findViewById(R.id.mine_lstv);
        mine_record_xml_menu = (ImageView) this.findViewById(R.id.mine_record_xml_menu);
        mine_record_xml_menu.setOnClickListener(this);
        recordList = new ArrayList<Record>();
        adapter = new MineRecordAdapter(recordList, this);
        adapter.setOnClickContentItemListener(this);
        home_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        home_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MineRecordActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MineRecordActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData();
            }
        });
        home_lstv.setAdapter(adapter);
        home_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detail = new Intent(MineRecordActivity.this, DetailPageAcitvity.class);
                Record record = recordList.get(position - 1);
                detail.putExtra(Constants.INFO, record);
                startActivity(detail);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_record_xml_menu:
                finish();
                break;
        }
    }

    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.RECORD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            RecordDATA data = getGson().fromJson(s, RecordDATA.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    recordList.clear();
                                }
                                recordList.addAll(data.getData());
                                if (recordList.size() == 0) {
                                    search_null.setVisibility(View.VISIBLE);
                                } else {
                                    search_null.setVisibility(View.GONE);
                                }
                                home_lstv.onRefreshComplete();
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MineRecordActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineRecordActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineRecordActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("schoolId", schoolId);
                params.put("empId", emp_id);
                params.put("page", String.valueOf(pageIndex));
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

    Record record;

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        record = recordList.get(position);
        switch (flag) {
            case 1:
                Intent comment = new Intent(MineRecordActivity.this, PublishCommentAcitvity.class);
                comment.putExtra(Constants.FATHER_PERSON_NAME, "");
                comment.putExtra(Constants.FATHER_UUID, "0");
                comment.putExtra(Constants.RECORD_UUID, record.getRecordId());
                comment.putExtra(Constants.FATHER_PERSON_UUID, record.getRecordEmpId());
                comment.putExtra("fplempid", "");
                startActivity(comment);
                break;
            case 2:
                recordtmp = record;//放到中间存储
                zan_click(record);
                break;
            case 3:
                recordtmp = record;//放到中间存储
                tmpSelected = position;
                showSelectImageDialog();
                break;
            case 4:
                break;
            case 5:
                String videoUrl = record.getRecordVideo();
                Intent intent = new Intent(MineRecordActivity.this, VideoPlayerActivity2.class);
                VideoPlayer video = new VideoPlayer(videoUrl);
                intent.putExtra(Constants.EXTRA_LAYOUT, "0");
                intent.putExtra(VideoPlayer.class.getName(), video);
                startActivity(intent);
                break;
            case 7:
                if (record.getRecordType().equals("1"))
                {
                    //是推广
                    Intent webView = new Intent(MineRecordActivity.this, WebViewActivity.class);
                    webView.putExtra("strurl", record.getRecordCont());
                    startActivity(webView);

                }
                break;
            case 8:
                //网址链接
            {
                String strcont = record.getRecordCont();//内容
                if (strcont.contains("http")){
                    //如果包含http
                    String strhttp = strcont.substring(strcont.indexOf("http"), strcont.length());
                    Intent webView = new Intent(MineRecordActivity.this, WebViewActivity.class);
                    webView.putExtra("strurl", strhttp);
                    startActivity(webView);
                }
            }
            break;

        }
    }
    // 选择是否删除
    private void showSelectImageDialog() {
        deleteWindow = new DeletePopWindow(MineRecordActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(MineRecordActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //删除方法
    private void delete() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.DELETE_RECORDS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(MineRecordActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                                recordList.remove(tmpSelected);
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MineRecordActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineRecordActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineRecordActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordId", recordtmp.getRecordId());
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

    //赞
    private void zan_click(final Record record) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.CLICK_LIKE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            RecordDATA data = getGson().fromJson(s, RecordDATA.class);
                            if (data.getCode() == 200) {
                                //赞+1
                                Toast.makeText(MineRecordActivity.this, R.string.zan_success, Toast.LENGTH_SHORT).show();
                                recordtmp.setZanNum(String.valueOf((Integer.valueOf(recordtmp.getZanNum()) + 1)));
                                adapter.notifyDataSetChanged();
                            }
                            if (data.getCode() == 1) {
                                Toast.makeText(MineRecordActivity.this, R.string.zan_error_one, Toast.LENGTH_SHORT).show();
                            }
                            if (data.getCode() == 2) {
                                Toast.makeText(MineRecordActivity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineRecordActivity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineRecordActivity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("sendEmpId", record.getRecordEmpId());
                params.put("recordId", record.getRecordId());
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

    //为弹出窗口实现监听类
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
}

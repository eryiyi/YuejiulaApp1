package com.liangxun.yuejiula.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.GoodsCommentAdapter;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.GoodsCommentDATA;
import com.liangxun.yuejiula.entity.GoodsComment;
import com.liangxun.yuejiula.entity.PaopaoGoods;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/9
 * Time: 14:28
 * 类的功能、说明写在此处.
 */
public class GoodsCommentActivity extends BaseActivity implements View.OnClickListener, OnClickContentItemListener {
    private ImageView detail_comment_back;
    private ImageView search_null;
    private PullToRefreshListView detail_comment_lstv;
    private GoodsCommentAdapter adapter;
    private List<GoodsComment> comments;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private PaopaoGoods goods;
    private TextView goods_comment_title;
    private String emp_id = "";//当前登陆者UUID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
        setContentView(R.layout.comment_lstv_xml);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        goods = (PaopaoGoods) getIntent().getExtras().get(Constants.GOODS);
        initView();
        initData();
    }

    private void initView() {
        comments = new ArrayList<GoodsComment>();
        search_null = (ImageView) this.findViewById(R.id.search_null);
        goods_comment_title = (TextView) this.findViewById(R.id.goods_comment_title);
        detail_comment_back = (ImageView) this.findViewById(R.id.detail_comment_back);
        detail_comment_back.setOnClickListener(this);
        detail_comment_lstv = (PullToRefreshListView) this.findViewById(R.id.detail_comment_lstv);
        adapter = new GoodsCommentAdapter(comments, GoodsCommentActivity.this);
        adapter.setOnClickContentItemListener(this);
        detail_comment_lstv.setAdapter(adapter);
        detail_comment_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        detail_comment_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(GoodsCommentActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(GoodsCommentActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData();
            }
        });
        detail_comment_lstv.setAdapter(adapter);
        detail_comment_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsComment comment1 = comments.get(position - 1);
                Intent comment = new Intent(GoodsCommentActivity.this, PublishGoodCommentActivity.class);
                comment.putExtra(Constants.GOODS_FATHER_PERSON_NAME, comment1.getNickName());
                comment.putExtra(Constants.GOODS_FATHER_UUID, comment1.getId());
                comment.putExtra(Constants.GOODS_RECORD_UUID, comment1.getGoodsId());
                comment.putExtra(Constants.GOODS_PERSON_UUID, goods.getEmpId());
                comment.putExtra("fplempid", comment1.getEmpId());
                startActivity(comment);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_comment_back:
                finish();
                break;
//            case R.id.goods_comment_sub:
//                Intent comment = new Intent(this, PublishGoodCommentActivity.class);
//                comment.putExtra(Constants.GOODS_FATHER_PERSON_NAME, "");
//                comment.putExtra(Constants.GOODS_FATHER_UUID, "");
//                comment.putExtra(Constants.GOODS_RECORD_UUID, goods.getId());
//                comment.putExtra(Constants.GOODS_PERSON_UUID, goods.getEmpId());
//                comment.putExtra("fplempid", "");
//                startActivity(comment);
//                break;
        }
    }


    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        if (!emp_id.equals(goods.getEmpId())) {
            Intent profile = new Intent(GoodsCommentActivity.this, ProfilePersonalActivity.class);
            profile.putExtra(Constants.EMPID, goods.getEmpId());
            startActivity(profile);
        }
    }

    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.GET_GOODS_COMMENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            GoodsCommentDATA data = getGson().fromJson(s, GoodsCommentDATA.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    comments.clear();
                                }
                                comments.addAll(data.getData());
                                goods_comment_title.setText(comments.size() + "人参与评论");
                                detail_comment_lstv.onRefreshComplete();
                                if (comments.size() == 0) {
                                    search_null.setVisibility(View.VISIBLE);
                                } else {
                                    search_null.setVisibility(View.GONE);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(GoodsCommentActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(GoodsCommentActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(GoodsCommentActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("goodsId", goods.getId());
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

    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.SEND_GOODS_COMMENT_SUCCESS)) {
                //刷新内容
                IS_REFRESH = true;
                initData();
            }
        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SEND_GOODS_COMMENT_SUCCESS);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}

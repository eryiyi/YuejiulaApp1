package com.liangxun.yuejiula.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.MainActivity;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.AnimateFirstDisplayListener;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.adapter.RecordAdapter;
import com.liangxun.yuejiula.base.BaseFragment;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.RecordDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.db.DBHelper;
import com.liangxun.yuejiula.entity.Record;
import com.liangxun.yuejiula.entity.VideoPlayer;
import com.liangxun.yuejiula.library.PullToRefreshBase;
import com.liangxun.yuejiula.library.PullToRefreshListView;
import com.liangxun.yuejiula.ui.*;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.HttpUtils;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.CustomProgressDialog;
import com.liangxun.yuejiula.widget.popview.DeletePopWindow;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener, OnClickContentItemListener{
//    private CircleImageView main_cover;
    private TextView maintitle;//主页标题
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageView select_school;
    private String[] mPopupWindowItems = {"所有圈子", "我的圈子", "其他圈子"};
    private PopupWindow mPopupWindow;
    private View mPopView;
    private LinearLayout mTopLayout;
    private ListView mPopDisplay;
    private List<Record> recordList;
    //动态listview
    private PullToRefreshListView home_lstv;
    //动态适配器
    private RecordAdapter adapter;
    private int pageIndex = 1;
    public static boolean IS_REFRESH = true;

    private String schoolId = "";
    private String schoolIdEmp;
    private String emp_id = "";//当前登陆者UUID
    Record recordtmp;//转换用

    private int tmpSelected;//暂时存UUID  删除用
    private DeletePopWindow deleteWindow;
    boolean isMobileNet, isWifiNet;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, null);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        initView(view);
        String cover = getGson().fromJson(getSp().getString(Constants.EMPCOVER, ""), String.class);
        schoolIdEmp = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);

        //判断是否有网
        try {
            isMobileNet = HttpUtils.isMobileDataEnable(getActivity());
            isWifiNet = HttpUtils.isWifiDataEnable(getActivity());
            if (!isMobileNet && !isWifiNet) {
                recordList.addAll(DBHelper.getInstance(getActivity()).getRecordList());
                adapter.notifyDataSetChanged();
            }else {
                progressDialog = new CustomProgressDialog(getActivity(), "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.show();
                initData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void initView(View view) {
        mTopLayout = (LinearLayout) view.findViewById(R.id.home_top_layout);
        select_school = (ImageView) view.findViewById(R.id.select_school);
        mTopLayout.setOnClickListener(this);
        maintitle = (TextView) view.findViewById(R.id.home_maintitle);
        maintitle.setText("所有圈子");
        recordList = new ArrayList<Record>();
        home_lstv = (PullToRefreshListView) view.findViewById(R.id.home_lstv);
        adapter = new RecordAdapter(recordList, getActivity(), emp_id);
        home_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        home_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                //判断是否有网
                try {
                    isMobileNet = HttpUtils.isMobileDataEnable(getActivity());
                    isWifiNet = HttpUtils.isWifiDataEnable(getActivity());
                    if (!isMobileNet && !isWifiNet) {
                        home_lstv.onRefreshComplete();
                    }else {
                        initData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                //判断是否有网
                try {
                    isMobileNet = HttpUtils.isMobileDataEnable(getActivity());
                    isWifiNet = HttpUtils.isWifiDataEnable(getActivity());
                    if (!isMobileNet && !isWifiNet) {
                        home_lstv.onRefreshComplete();
                    }else {
                        initData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        home_lstv.setAdapter(adapter);
        home_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //判断是否有网
                try {
                    isMobileNet = HttpUtils.isMobileDataEnable(getActivity());
                    isWifiNet = HttpUtils.isWifiDataEnable(getActivity());
                    if (!isMobileNet && !isWifiNet) {
                        Toast.makeText(getActivity(), "请检查网络链接", Toast.LENGTH_SHORT).show();
                    }else{
                        Record record = recordList.get(position - 1);
                        if (!record.getRecordType().equals("1")) {
                            //不是推广
                            Intent detail = new Intent(getActivity(), DetailPageAcitvity.class);
                            detail.putExtra(Constants.INFO, record);
                            startActivity(detail);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
        adapter.setOnClickContentItemListener(this);

        mPopView = LayoutInflater.from(getActivity()).inflate(
                R.layout.home_popupwindow, null);
        mPopDisplay = (ListView) mPopView
                .findViewById(R.id.home_popupwindow_display);
        mPopDisplay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(Constants.SEND_SUCCESS);
                        intent.putExtra(Constants.SEND_VALUE_ONE, "0");
                        getActivity().sendBroadcast(intent);
                        break;
                    case 1:
                        Intent intent1 = new Intent(Constants.SEND_SUCCESS);
                        intent1.putExtra(Constants.SEND_VALUE_ONE, "1");
                        getActivity().sendBroadcast(intent1);
                        break;
                    case 2://其他圈子
                        Intent other = new Intent(getActivity(), OtherSchoolOneActivity.class);
                        startActivity(other);
                        break;
                }
                mPopupWindow.dismiss();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_top_layout:
                // 显示菜单
                initPopupWindow();
                break;
        }
    }

    Record record;

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        //判断是否有网
        try {
            isMobileNet = HttpUtils.isMobileDataEnable(getActivity());
            isWifiNet = HttpUtils.isWifiDataEnable(getActivity());
            if (!isMobileNet && !isWifiNet) {
                Toast.makeText(getActivity(), "请检查网络链接", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        record = recordList.get(position);
        switch (flag) {
            case 1:
                Intent comment = new Intent(getActivity(), PublishCommentAcitvity.class);
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
                break;
            case 4:
                if (!emp_id.equals(record.getRecordEmpId())) {
                    Intent profile = new Intent(getActivity(), ProfilePersonalActivity.class);
                    profile.putExtra(Constants.EMPID, record.getRecordEmpId());
                    startActivity(profile);
                } else {
                    Intent profile = new Intent(getActivity(), UpdateProfilePersonalActivity.class);
                    startActivity(profile);
                }
                break;
            case 5:
                String videoUrl = record.getRecordVideo();
                Intent intent = new Intent(getActivity(), VideoPlayerActivity2.class);
                VideoPlayer video = new VideoPlayer(videoUrl);
                intent.putExtra(Constants.EXTRA_LAYOUT, "0");
                intent.putExtra(VideoPlayer.class.getName(), video);
                startActivity(intent);
                break;
            case 6:
                //删除该动态
                recordtmp = record;//放到中间存储
                tmpSelected = position;
                showSelectImageDialog();
                break;
            case 7:
                if (record.getRecordType().equals("1"))
            {
                //是推广
                Intent webView = new Intent(getActivity(), WebViewActivity.class);
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
                    Intent webView = new Intent(getActivity(), WebViewActivity.class);
                    webView.putExtra("strurl", strhttp);
                    startActivity(webView);
                }
            }
                break;
            case 9:
                //点击圈子
//                schoolId = record.getRecordSchoolId();
//                initData();
                break;
        }
    }

    // 选择是否删除
    private void showSelectImageDialog() {
        deleteWindow = new DeletePopWindow(getActivity(), itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(getActivity().findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //删除方法
    private void delete() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.DELETE_RECORDS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(getActivity(), R.string.delete_success, Toast.LENGTH_SHORT).show();
                                recordList.remove(tmpSelected);
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), R.string.delete_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.delete_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), R.string.delete_error, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getActivity(), "点赞成功", Toast.LENGTH_SHORT).show();
                                recordtmp.setZanNum(String.valueOf((Integer.valueOf(recordtmp.getZanNum()) + 1)));
                                adapter.notifyDataSetChanged();
                            }
                            if (data.getCode() == 1) {
                                Toast.makeText(getActivity(), "已经赞过", Toast.LENGTH_SHORT).show();
                            }
                            if (data.getCode() == 2) {
                                Toast.makeText(getActivity(), "点赞失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "点赞失败，请稍后重试", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "点赞失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordId", record.getRecordId());
                params.put("empId", emp_id);
                params.put("sendEmpId", record.getRecordEmpId());
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
            if (action.equals(Constants.SEND_SUCCESS)) {
                String str = intent.getExtras().getString(Constants.SEND_VALUE_ONE);
                if ("0".equals(str)) {
                    schoolId = "";
                    maintitle.setText("所有圈子");
                }
                if ("1".equals(str)) {
                    schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
                    maintitle.setText("我的圈子");
                }
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }
            if (action.equals(Constants.SEND_PIC_TX_SUCCESS)) {
                //更改头像的广播事件
//                imageLoader.displayImage(getGson().fromJson(getSp().getString(Constants.EMPCOVER, ""), String.class), main_cover, UniversityApplication.txOptions, animateFirstListener);
            }
            if (action.equals(Constants.SEND_COMMENT_RECORD_SUCCESS)) {
                //刷新内容,评论+1
                String recordId =  intent.getExtras().getString("recordId");
                for(Record record:recordList){
                    if(record.getRecordId().equals(recordId)){
                        record.setPlNum(String.valueOf(Integer.parseInt(record.getPlNum())+1));
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
            if(action.equals(Constants.SEND_DELETE_RECORD_SUCCESS)){
                String recordId =  intent.getExtras().getString("recordId");
                for(Record record:recordList){
                    if(record.getRecordId().equals(recordId)){
                        recordList.remove(record);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
            if(action.equals(Constants.SEND_INDEX_SUCCESS)){
                Record record1 = (Record) intent.getExtras().get("addRecord");
                recordList.add(0, record1);
                adapter.notifyDataSetChanged();
            }

            if (action.equals("record_jp_success")) {
                String money1 =  intent.getExtras().getString("money");
                String record_id =  intent.getExtras().getString("record_id");
                for(int i=0;i<recordList.size();i++){
                    Record record1 = recordList.get(i);
                    if(record1.getRecordId().equals(record_id)){
                        recordList.get(i).setMoney(String.valueOf(Integer.parseInt(record.getMoney()) + Integer.parseInt(money1)));
                        break;
                    }
                }
                adapter.notifyDataSetChanged();

            }

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
        myIntentFilter.addAction("record_jp_success");
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * 初始化选择菜单
     */
    private void initPopupWindow() {
        PopupWindowAdapter adapter = new PopupWindowAdapter();
        mPopDisplay.setAdapter(adapter);
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(mPopView, mTopLayout.getWidth(),
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            mPopupWindow.showAsDropDown(mTopLayout, 0, -10);
        }
    }

    private class PopupWindowAdapter extends BaseAdapter {

        public int getCount() {
            return mPopupWindowItems.length;
        }

        public Object getItem(int position) {
            return mPopupWindowItems[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.home_popupwindow_item, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView
                        .findViewById(R.id.home_popupwindow_item_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.name.setText(mPopupWindowItems[position]);
            return convertView;
        }

        class ViewHolder {
            TextView name;
        }
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


    public void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.RECORD_URL,
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
                                home_lstv.onRefreshComplete();
                                adapter.notifyDataSetChanged();
                                //处理数据，需要的话保存到数据库
                                if (data != null && data.getData() != null) {
                                    DBHelper dbHelper = DBHelper.getInstance(getActivity());
                                    for (Record record1 : data.getData()) {
                                        if (dbHelper.getRecordById(record1.getRecordId()) == null) {
                                            DBHelper.getInstance(getActivity()).saveRecord(record1);
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                        if(progressDialog != null){
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(progressDialog != null){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                if(!StringUtil.isNullOrEmpty(schoolId)){
                    params.put("schoolId", schoolId);
                }
                params.put("schoolIdEmp", schoolIdEmp);
                params.put("page", String.valueOf(pageIndex));
                if(!StringUtil.isNullOrEmpty(MainActivity.school_record_mood_id)){
                    //
                    params.put("school_record_mood_id", MainActivity.school_record_mood_id);
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(request);
    }

}

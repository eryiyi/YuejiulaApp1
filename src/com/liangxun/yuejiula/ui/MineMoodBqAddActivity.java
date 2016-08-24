package com.liangxun.yuejiula.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.MainActivity;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.MoodAdapter;
import com.liangxun.yuejiula.adapter.MoodsGzAdapter;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.MoodGuanzhuObjData;
import com.liangxun.yuejiula.data.RecordSingleDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.MoodGuanzhuObj;
import com.liangxun.yuejiula.entity.RecordBigType;
import com.liangxun.yuejiula.entity.SchoolRecordMood;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.CustomProgressDialog;
import com.liangxun.yuejiula.widget.popview.CustomerSpinner;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/24.
 */
public class MineMoodBqAddActivity extends BaseActivity implements View.OnClickListener {

    private CustomerSpinner provinceSpinner;
    private ArrayList<RecordBigType> provinces = new ArrayList<RecordBigType>();
    private ArrayList<String> provinces_names = new ArrayList<String>();
    private ArrayAdapter<String> provinceAdapter;
    RecordBigType schoolRecordMood = null;//选中的那个额

    private TextView bianqian;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_moodbq_add_activity);
        bianqian = (TextView) this.findViewById(R.id.bianqian);

        provinceAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, provinces_names);
        provinceSpinner = (CustomerSpinner) findViewById(R.id.provice_select_school);
        provinceSpinner.setList(provinces_names);
        provinceSpinner.setAdapter(provinceAdapter);

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    schoolRecordMood = provinces.get(position-1);
                    showMoodSmallDialog(schoolRecordMood.getId());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("","");
            }

        });

        provinces.clear();
        provinces.addAll(MainActivity.arrayMoodBigType);
        provinces_names.clear();
        provinces_names.add("请选择标签");

        for (RecordBigType pro : provinces) {
            provinces_names.add(pro.getTitle());
        }

        provinceAdapter.notifyDataSetChanged();

        this.findViewById(R.id.btn).setOnClickListener(this);
    }

    List<SchoolRecordMood> arrayMoodTmp = new ArrayList<SchoolRecordMood>();
    String school_record_mood_id = "";

    private void showMoodSmallDialog(String bigType) {
        final Dialog picAddDialog = new Dialog(MineMoodBqAddActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(this, R.layout.mood_dialog, null);
        ListView listView = (ListView) picAddInflate.findViewById(R.id.lstv);
        arrayMoodTmp.clear();
        for(SchoolRecordMood moods:MainActivity.arrayMood){
            if(bigType.equals(moods.getSchool_record_mood_type())){
                arrayMoodTmp.add(moods);
            }
        }
        MoodAdapter adapter = new MoodAdapter(arrayMoodTmp, MineMoodBqAddActivity.this);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SchoolRecordMood schoolRecordMood = arrayMoodTmp.get(i);
                if(schoolRecordMood != null){
                    school_record_mood_id = schoolRecordMood.getSchool_record_mood_id();
                    bianqian.setText(schoolRecordMood.getSchool_record_mood_name());
                    picAddDialog.dismiss();
                }
            }
        });
        picAddDialog.setContentView(picAddInflate);
        picAddDialog.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn:
            {
                //添加
                if(StringUtil.isNullOrEmpty(school_record_mood_id)){
                    showMsg(MineMoodBqAddActivity.this, "请选择标签");
                    return;
                }
                progressDialog = new CustomProgressDialog(MineMoodBqAddActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.show();
                publishAll();
            }
                break;
        }
    }


    private void publishAll() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.SAVE_MOODS_BQ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo  = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    showMsg(MineMoodBqAddActivity.this, "添加成功");
                                    //调用广播，刷新主页
                                    Intent intent1 = new Intent("add_moods_bq_success");
                                    sendBroadcast(intent1);
                                    finish();
                                }else if(Integer.parseInt(code) == 2){
                                    showMsg(MineMoodBqAddActivity.this, "发布失败，最多设置5个标签！");
                                } else if(Integer.parseInt(code) == 3){
                                    showMsg(MineMoodBqAddActivity.this, "发布失败，您已添加了该标签，换个试试！");
                                } else {
                                    showMsg(MineMoodBqAddActivity.this, "发布失败，请稍后重试！");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
                        Toast.makeText(MineMoodBqAddActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class));
                params.put("school_record_mood_id", school_record_mood_id);
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

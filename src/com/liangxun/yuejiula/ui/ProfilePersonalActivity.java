package com.liangxun.yuejiula.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.MainActivity;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.AnimateFirstDisplayListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.EmpDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.ContractSchool;
import com.liangxun.yuejiula.entity.Emp;
import com.liangxun.yuejiula.huanxin.chat.activity.ChatActivity;
import com.liangxun.yuejiula.huanxin.chat.activity.HxAlertDialog;
import com.liangxun.yuejiula.huanxin.chat.activity.HxAlertYanzheng;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.popview.SelectFenghaoPopWindow;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/1
 * Time: 14:18
 * 类的功能、说明写在此处.
 */
public class ProfilePersonalActivity extends BaseActivity implements View.OnClickListener {
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private ImageView profile_personal_back;//返回
    private ImageView profile_personal_cover;//头像
    private TextView profile_personal_nickname;//昵称
    private ImageView profile_personal_sex;//性别
    private TextView profile_personal_qq;//球球号
    private TextView profile_personal_mobile;//手机号
    private TextView profile_count;//积分
    private TextView profile_dengji;//等级
    private TextView profile_sign;//签名
    private TextView profile_personal_school;//学校名字
    private ImageView profile_personal_flag;//标识
    private LinearLayout profile_record;// 动态

    private String empId;//用户UUID
    private String emp_id = "";//当前登陆者UUID
    private Emp emp;//被访问用户的资料
    private String schoolId = "";

    private LinearLayout mobile_status_liner;//手机号区域
    private View mobile_status_liner_line;//手机号上方的割线

    private ImageView select_fenghq;//封号封群操作
    private ImageView biaozhi_one;
    private ImageView biaozhi_two;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_personal_xml);
        empId = getIntent().getExtras().getString(Constants.EMPID);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        initView();
        getData();

    }

    private void initView() {
        profile_personal_back = (ImageView) this.findViewById(R.id.profile_personal_back);
        profile_personal_back.setOnClickListener(this);
        profile_personal_cover = (ImageView) this.findViewById(R.id.profile_personal_cover);
        profile_personal_cover.setOnClickListener(this);
        profile_personal_nickname = (TextView) this.findViewById(R.id.profile_personal_nickname);
        profile_personal_sex = (ImageView) this.findViewById(R.id.profile_personal_sex);
        profile_personal_qq = (TextView) this.findViewById(R.id.profile_personal_qq);
        profile_personal_mobile = (TextView) this.findViewById(R.id.profile_personal_mobile);
        profile_count = (TextView) this.findViewById(R.id.profile_count);
        profile_dengji = (TextView) this.findViewById(R.id.profile_dengji);
        profile_sign = (TextView) this.findViewById(R.id.profile_sign);
        mobile_status_liner = (LinearLayout) this.findViewById(R.id.mobile_status_liner);
        mobile_status_liner_line = this.findViewById(R.id.mobile_status_liner_line);
        profile_personal_school = (TextView) this.findViewById(R.id.profile_personal_school);
        profile_personal_flag = (ImageView) this.findViewById(R.id.profile_personal_flag);
        profile_record= (LinearLayout) findViewById(R.id.profile_record);
        profile_record.setOnClickListener(this);
        select_fenghq = (ImageView) this.findViewById(R.id.select_fenghq);
        select_fenghq.setVisibility(View.GONE);
        select_fenghq.setOnClickListener(this);
        biaozhi_one = (ImageView) this.findViewById(R.id.biaozhi_one);
        biaozhi_two = (ImageView) this.findViewById(R.id.biaozhi_two);
        biaozhi_one.setVisibility(View.GONE);
        biaozhi_two.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_personal_back:
                finish();
                break;
            case R.id.profile_personal_cover:
                //点击头像
                final String[] picUrls = {emp.getEmpCover()};
                Intent intent = new Intent(this, GalleryUrlActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.putExtra(Constants.IMAGE_URLS, picUrls);
                intent.putExtra(Constants.IMAGE_POSITION, 0);
                startActivity(intent);
                break;
            case R.id.profile_record:
                Intent dynamic=new Intent(ProfilePersonalActivity.this,ProfileDynamicActivity.class);
                dynamic.putExtra(Constants.EMPID,empId);//我要查询的那个人的 EMPID
                startActivity(dynamic);
            case R.id.select_fenghq:
                //封号 封群
                ShowPickDialog();
                break;

        }
    }

    private SelectFenghaoPopWindow deleteWindow;
    private void ShowPickDialog() {
        deleteWindow = new SelectFenghaoPopWindow(ProfilePersonalActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(ProfilePersonalActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }
    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    //封号
                    updateFh();
                }
                break;
                case R.id.mapstorage: {
                   //封群
                    updateFq();
                }
                break;
                default:
                    break;
            }
        }
    };



    //根据用户UUID获取用户信息
    private void getData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_EMP_DETAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            EmpDATA data = getGson().fromJson(s, EmpDATA.class);
                            if (data.getCode() == 200) {
                                emp = data.getData();
                                initData(data.getData());
                            } else {
                                Toast.makeText(ProfilePersonalActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfilePersonalActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ProfilePersonalActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empId", empId);
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

    public void initData(Emp emp) {
        imageLoader.displayImage(emp.getEmpCover(), profile_personal_cover, UniversityApplication.txOptions, animateFirstListener);
        profile_personal_nickname.setText(emp.getEmpName());
        if ("0".equals(emp.getEmpSex())) {
            profile_personal_sex.setImageResource(R.drawable.icon_sex_male);
        }
        if ("1".equals(emp.getEmpSex())) {
            profile_personal_sex.setImageResource(R.drawable.icon_sex_female);
        }
        if (!StringUtil.isNullOrEmpty(emp.getEmpQQ())) {
            profile_personal_qq.setText(emp.getEmpQQ());
        } else {
            profile_personal_qq.setText("未填写");
        }

        profile_personal_mobile.setText(emp.getEmpMobile());

        profile_count.setText(emp.getJfcount());
        profile_dengji.setText(emp.getLevelName());
        if (!StringUtil.isNullOrEmpty(emp.getEmpSign())) {
            profile_sign.setText(emp.getEmpSign());
        } else {
            profile_sign.setText(R.string.sign);
        }
        if (emp.getMobileStatus().equals("0")) {//手机号不公开
            mobile_status_liner.setVisibility(View.GONE);
            mobile_status_liner_line.setVisibility(View.GONE);
        }
        if (emp.getMobileStatus().equals("1")) {//手机号公开
            mobile_status_liner.setVisibility(View.VISIBLE);
            mobile_status_liner_line.setVisibility(View.VISIBLE);
        }
        profile_personal_school.setText(emp.getUniversityName());
        if (emp.getEmpTypeId().equals("0")) {
            profile_personal_flag.setImageResource(R.drawable.icon_type_min);
        }
        if (emp.getEmpTypeId().equals("1")) {
            profile_personal_flag.setImageResource(R.drawable.icon_type_guan);
        }
        if (emp.getEmpTypeId().equals("2")) {
            profile_personal_flag.setImageResource(R.drawable.icon_type_shang);
        }
        if (emp.getEmpTypeId().equals("3")) {
            profile_personal_flag.setImageResource(R.drawable.icon_type_official);
        }

        //判断是否禁用群聊和紧贴
        if("1".equals(emp.getIs_fenghao())){
            biaozhi_one.setVisibility(View.VISIBLE);
        }
        if("1".equals(emp.getIs_fengqun())){
            biaozhi_two.setVisibility(View.VISIBLE);
        }

        boolean flagT = false;
        if(MainActivity.contractSchools != null){
            for(ContractSchool contractSchool :MainActivity.contractSchools ){
                if(emp.getSchoolId().equals(contractSchool.getSchoolId())){
                    //当前用户学校id  == 承包商学校id 说明这个用户是我管理的
                    flagT = true;
                    select_fenghq.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }


    }

    public void chat(View v) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("emp", emp);
        startActivity(intent);
    }

    public void add(View v) {
        if (getMyApp().getUserName().equals(emp.getHxUsername())) {
            String str = getResources().getString(R.string.not_add_myself);
            startActivity(new Intent(this, HxAlertDialog.class).putExtra("msg", str));
            return;
        }

        if (getMyApp().getContactList().containsKey(emp.getHxUsername())) {
            String strin = getResources().getString(R.string.This_user_is_already_your_friend);
            startActivity(new Intent(this, HxAlertDialog.class).putExtra("msg", strin));
            return;
        }

        Intent intent = new Intent(this, HxAlertYanzheng.class);
        intent.putExtra("hxUserName", emp.getHxUsername());
        startActivity(intent);
    }

    public void report(View v) {
        showJubao();
    }

    // 举报
    private void showJubao() {
        final Dialog picAddDialog = new Dialog(ProfilePersonalActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(this, R.layout.jubao_dialog, null);
        TextView jubao_sure = (TextView) picAddInflate.findViewById(R.id.jubao_sure);
        final EditText jubao_cont = (EditText) picAddInflate.findViewById(R.id.jubao_cont);
        //举报提交
        jubao_sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String contreport = jubao_cont.getText().toString();
                if (StringUtil.isNullOrEmpty(contreport)) {
                    Toast.makeText(ProfilePersonalActivity.this, R.string.report_answer, Toast.LENGTH_LONG).show();
                    return;
                }
                report(contreport);
                picAddDialog.dismiss();
            }
        });

        //举报取消
        TextView jubao_cancle = (TextView) picAddInflate.findViewById(R.id.jubao_cancle);
        jubao_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picAddDialog.dismiss();
            }
        });
        picAddDialog.setContentView(picAddInflate);
        picAddDialog.show();
    }

    public void report(final String contReport) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.ADD_REPORT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(ProfilePersonalActivity.this, R.string.report_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfilePersonalActivity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfilePersonalActivity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ProfilePersonalActivity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empOne", emp_id);
                params.put("empTwo", empId);
                params.put("typeId", Constants.REPORT_TYPE_THREE);
                params.put("cont", contReport);
                params.put("xxid", empId);
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


    private void updateFh() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_FENGHAO_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                showMsg(ProfilePersonalActivity.this , "禁发帖操作成功！");
                            } else {
                                Toast.makeText(ProfilePersonalActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfilePersonalActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ProfilePersonalActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", empId);
                params.put("is_fenghao", "1");
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
    private void updateFq() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_FENGQUN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                showMsg(ProfilePersonalActivity.this , "禁群聊操作成功！");
                            } else {
                                Toast.makeText(ProfilePersonalActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfilePersonalActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ProfilePersonalActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", empId);
                params.put("is_fengqun", "1");
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

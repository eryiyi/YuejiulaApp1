package com.liangxun.yuejiula.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.AnimateFirstDisplayListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.EmpDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.Emp;
import com.liangxun.yuejiula.util.CompressPhotoUtil;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.SexRadioGroup;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.popview.SelectPhoPopWindow;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.yixia.camera.demo.UniversityApplication;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/1
 * Time: 14:18
 * 类的功能、说明写在此处.
 */
public class UpdateProfilePersonalActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private ImageView profile_personal_back;//返回
    private ImageView profile_personal_cover;//头像

    private EditText profile_personal_nickname;//昵称
    private SexRadioGroup profile_personal_sex;//性别
    private SexRadioGroup mobile_select;//手机号是否公开
    private EditText profile_personal_qq;//球球号
    private TextView profile_personal_mobile;//手机号
    private EditText update_profile_sign;
    private TextView profile_count;//积分
    private TextView profile_dengji;//等级
    private TextView profile_personal_school;//xuexiao
    //    private ImageView profile_personal_sex_image;//性别图标
    private ImageView profile_personal_flag;//图标

    private RadioButton button_one;
    private RadioButton button_two;
    private RadioButton open_button;
    private RadioButton close_button;

    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID
    private String cover = "";//当前登陆者头像
    private String mobile = "";
    private String mobileStatus = "";
    private String nickname = "";
    private String sex = "";
    private String sex_selected = "";
    private String qq = "";
    private String sign = "";

    private TextView update_profile;//更新

    private String pics = "";
    private static final File PHOTO_CACHE_DIR = new File(Environment.getExternalStorageDirectory() + "/liangxun/PhotoCache");


    private String txpic = "";
    private SelectPhoPopWindow deleteWindow;
    AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile_personal_xml);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        initView();
        getData();
    }

    private void initView() {
        profile_personal_back = (ImageView) this.findViewById(R.id.profile_personal_back);
        profile_personal_back.setOnClickListener(this);
        profile_personal_cover = (ImageView) this.findViewById(R.id.profile_personal_cover);
        profile_personal_cover.setOnClickListener(this);
        profile_personal_nickname = (EditText) this.findViewById(R.id.profile_personal_nickname);
        profile_personal_sex = (SexRadioGroup) this.findViewById(R.id.segment_text);
        mobile_select = (SexRadioGroup) this.findViewById(R.id.mobile_select);
        profile_personal_sex.setOnCheckedChangeListener(this);
        mobile_select.setOnCheckedChangeListener(this);
        profile_personal_qq = (EditText) this.findViewById(R.id.profile_personal_qq);
        profile_personal_mobile = (TextView) this.findViewById(R.id.profile_personal_mobile);
        update_profile_sign = (EditText) this.findViewById(R.id.profile_sign_update);
        update_profile = (TextView) this.findViewById(R.id.update);
        update_profile.setOnClickListener(this);
        profile_count = (TextView) this.findViewById(R.id.profile_count);
        profile_dengji = (TextView) this.findViewById(R.id.profile_dengji);
        button_one = (RadioButton) this.findViewById(R.id.button_one);
        button_two = (RadioButton) this.findViewById(R.id.button_two);
        open_button = (RadioButton) this.findViewById(R.id.open_button);
        close_button = (RadioButton) this.findViewById(R.id.close_button);
//        profile_personal_sex_image = (ImageView) this.findViewById(R.id.profile_personal_sex_image);
        profile_personal_school = (TextView) this.findViewById(R.id.profile_personal_school);
        profile_personal_flag = (ImageView) this.findViewById(R.id.profile_personal_flag);
    }

    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == profile_personal_sex) {
            if (checkedId == R.id.button_one) {
                sex_selected = "0";
            } else if (checkedId == R.id.button_two) {
                sex_selected = "1";
            }
        }
        if (group == mobile_select) {
            if (checkedId == R.id.open_button) {
                mobileStatus = "1";
            } else if (checkedId == R.id.close_button) {
                mobileStatus = "0";
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_personal_back:
                finish();
                break;
            case R.id.profile_personal_cover://点击头像 打开相机相册选择框
                ShowPickDialog();
                break;
            case R.id.update:
                nickname = profile_personal_nickname.getText().toString();
                nickname = nickname.trim();
                qq = profile_personal_qq.getText().toString();
                sign = update_profile_sign.getText().toString();

                if (StringUtil.isNullOrEmpty(nickname)) {
                    Toast.makeText(UpdateProfilePersonalActivity.this, R.string.update_error_one, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (nickname.length() > 18 || nickname.length() < 2) {
                    Toast.makeText(UpdateProfilePersonalActivity.this, R.string.update_error_oneone, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(sex)) {
                    Toast.makeText(UpdateProfilePersonalActivity.this, R.string.update_error_two, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (qq.length() > 50) {
                    Toast.makeText(UpdateProfilePersonalActivity.this, R.string.update_error_five, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (sign.length() > 50) {
                    Toast.makeText(UpdateProfilePersonalActivity.this, R.string.update_error_four, Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog = new ProgressDialog(UpdateProfilePersonalActivity.this);

                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                //检查有没有选择图片
                if (StringUtil.isNullOrEmpty(pics)) {//说明没有选择图片
                    //传当前登陆者的头像
                    publishAll(cover, 0);
                } else {
                    //七牛
                    Map<String,String> map = new HashMap<>();
                    map.put("space", "paopao-pic");
                    RequestParams params = new RequestParams(map);
                    client.get(InternetURL.UPLOAD_TOKEN, params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            try {
                                String token = response.getString("data");
                                UploadManager uploadManager = new UploadManager();
                                uploadManager.put(StringUtil.getBytes(pics), StringUtil.getUUID(), token,
                                        new UpCompletionHandler() {
                                            @Override
                                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                                //key
                                                txpic = key;
                                                publishAll(txpic, 1);
                                            }
                                        }, null);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }
                    });
                }
                break;
        }
    }

    // 选择相册，相机
    private void ShowPickDialog() {
        deleteWindow = new SelectPhoPopWindow(UpdateProfilePersonalActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(UpdateProfilePersonalActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 如果是直接从相册获取
            case 1:
                if (data != null) {
                    startPhotoZoom(data.getData());
                }
                break;
            // 如果是调用相机拍照时
            case 2:
                File temp = new File(Environment.getExternalStorageDirectory()
                        + "/ppCover.jpg");
                startPhotoZoom(Uri.fromFile(temp));
                break;
            // 取得裁剪后的图片
            case 3:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            if (photo != null) {
                pics = CompressPhotoUtil.saveBitmap2file(photo, System.currentTimeMillis() + ".jpg", PHOTO_CACHE_DIR);
                profile_personal_cover.setImageBitmap(photo);
            }
        }
    }

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
                                initData(data.getData());
                            } else {
                                Toast.makeText(UpdateProfilePersonalActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(UpdateProfilePersonalActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(UpdateProfilePersonalActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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

    public void initData(Emp emp) {
        imageLoader.displayImage(emp.getEmpCover(), profile_personal_cover, UniversityApplication.txOptions, animateFirstListener);
        profile_personal_nickname.setText(emp.getEmpName());
        if (!StringUtil.isNullOrEmpty(emp.getEmpSex())) {
            sex_selected = emp.getEmpSex();
            if ("1".equals(sex_selected)) {
                button_one.setChecked(false);//男未选中
                button_two.setChecked(true);//女选中
//                profile_personal_sex_image.setImageResource(R.drawable.jiakao_icon_kaoshijilu_girl);
            }
            if ("0".equals(sex_selected)) {
                button_one.setChecked(true);
                button_two.setChecked(false);
//                profile_personal_sex_image.setImageResource(R.drawable.jiakao_icon_kaoshijilu_boy);
            }
        }
        if (!StringUtil.isNullOrEmpty(emp.getMobileStatus())) {
            mobileStatus = emp.getMobileStatus();
            if ("1".equals(mobileStatus)) {
                open_button.setChecked(true);
                close_button.setChecked(false);
            }
            if ("0".equals(mobileStatus)) {
                open_button.setChecked(false);
                close_button.setChecked(true);
            }
        }
        profile_personal_qq.setText(emp.getEmpQQ());
        profile_personal_mobile.setText(emp.getEmpMobile());
        if (!StringUtil.isNullOrEmpty(emp.getEmpSign())) {
            update_profile_sign.setText(emp.getEmpSign());
        } else {
            update_profile_sign.setText(R.string.sign);
        }
        cover = emp.getEmpCover();
        mobile = emp.getEmpMobile();
        nickname = emp.getEmpName();
        sex = emp.getEmpSex();
        qq = emp.getEmpQQ();
        sign = emp.getEmpSign();
        profile_count.setText(emp.getJfcount());
        profile_dengji.setText(emp.getLevelName());
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
    }

    //更新
    private void publishAll(final String uploadpic, final int currentType) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_PROFILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                if (currentType == 1) {
                                    save(Constants.EMPCOVER, InternetURL.QINIU_URL + uploadpic);
                                }
                                save(Constants.EMPNAME, nickname);
                                save(Constants.EMPSEX, sex_selected);
                                Toast.makeText(UpdateProfilePersonalActivity.this, R.string.update_success, Toast.LENGTH_SHORT).show();
                                //去广播
                                Intent intent = new Intent(Constants.SEND_PIC_TX_SUCCESS);
                                UpdateProfilePersonalActivity.this.sendBroadcast(intent);
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                                finish();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(UpdateProfilePersonalActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empId", emp_id);
                if (StringUtil.isNullOrEmpty(sign)) {
                    params.put("empSign", "");
                } else {
                    params.put("empSign", sign);
                }
                params.put("empSex", sex_selected);
                params.put("mobileStatus", mobileStatus);
                params.put("empName", nickname);
                if (StringUtil.isNullOrEmpty(qq)) {
                    params.put("empQQ", "");
                } else {
                    params.put("empQQ", qq);
                }
                if (currentType == 1) {//说明更换了头像，需要后台更新
                    //头像
                    params.put("empCover", uploadpic);
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
        getRequestQueue().add(request);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    Intent camera = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    //下面这句指定调用相机拍照后的照片存储的路径
                    camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                            .fromFile(new File(Environment
                                    .getExternalStorageDirectory(),
                                    "ppCover.jpg")));
                    startActivityForResult(camera, 2);
                }
                break;
                case R.id.mapstorage: {
                    Intent mapstorage = new Intent(Intent.ACTION_PICK, null);
                    mapstorage.setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*");
                    startActivityForResult(mapstorage, 1);
                }
                break;
                default:
                    break;
            }
        }
    };
}

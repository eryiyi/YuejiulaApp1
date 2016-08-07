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
import com.liangxun.yuejiula.adapter.PkColleageAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.MineShangjiasDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.ContractSchool;
import com.liangxun.yuejiula.util.CompressPhotoUtil;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.NoScrollGridView;
import com.liangxun.yuejiula.widget.popview.SelectPhoPopWindow;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/3/31.
 * 设置我的奖品 代理商用
 */
public class AddMinePrizeActivity extends BaseActivity implements View.OnClickListener {
    private ImageView add_mine_prize_menu;
    private ImageView add_mine_prize_cover;
    private TextView add_mine_prize_run;
    private EditText add_mine_prize_cont;

    private SelectPhoPopWindow deleteWindow;
    private String pics = "";
    private String cont = "";
    private static final File PHOTO_CACHE_DIR = new File(Environment.getExternalStorageDirectory() + "/liangxun/PhotoCache");

    private String emp_id = "";
    private String schoolId = "";
    private String pic_url = "";
    private String themeId = "";

    private NoScrollGridView add_prizes_gridview_image;//学校
    private PkColleageAdapter adapter;
    List<ContractSchool> lists = new ArrayList<ContractSchool>();
    private String schoolstr;
    AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_mine_prize);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        themeId = getIntent().getExtras().getString(Constants.PK_ADD_PRIZE_THEME);
        initView();
        Intent mapstorage = new Intent(Intent.ACTION_PICK, null);
        mapstorage.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(mapstorage, 1);
        getData();
    }

    private void initView() {
        add_mine_prize_menu = (ImageView) this.findViewById(R.id.add_mine_prize_menu);
        add_mine_prize_cover = (ImageView) this.findViewById(R.id.add_mine_prize_cover);
        add_mine_prize_cont = (EditText) this.findViewById(R.id.add_mine_prize_cont);
        add_mine_prize_run = (TextView) this.findViewById(R.id.add_mine_prize_run);

        add_mine_prize_cover.setOnClickListener(this);
        add_mine_prize_menu.setOnClickListener(this);
        add_mine_prize_run.setOnClickListener(this);

        add_prizes_gridview_image = (NoScrollGridView) this.findViewById(R.id.add_prizes_gridview_image);
        adapter = new PkColleageAdapter(lists, this);
        add_prizes_gridview_image.setAdapter(adapter);
        add_prizes_gridview_image.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContractSchool cst = lists.get(position);
                if (cst.getIsSelected().equals("0")) {
                    lists.get(position).setIsSelected("1");
                } else {
                    lists.get(position).setIsSelected("0");
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_mine_prize_menu:
                finish();
                break;
            case R.id.add_mine_prize_run:
                //处理选中的学校
                if (lists.size() > 0) {
                    schoolstr = "";
                    for (int i = 0; i < lists.size(); i++) {
                        ContractSchool contractSchool = lists.get(i);
                        if ("1".equals(contractSchool.getIsSelected())) {//0否  1是选中
                            schoolstr = schoolstr + contractSchool.getSchoolId() + ",";
                        }
                    }
                } else {
                    Toast.makeText(this, R.string.pk_add_error_five, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(schoolstr) || schoolstr.equals(",")) {
                    Toast.makeText(this, R.string.pk_add_error_six, Toast.LENGTH_SHORT).show();
                    return;
                }
                cont = add_mine_prize_cont.getText().toString();
                if (StringUtil.isNullOrEmpty(cont)) {
                    Toast.makeText(this, R.string.publishnews_error_two, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(pics)) {
                    Toast.makeText(this, R.string.publishnews_error_four, Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog = new ProgressDialog(this );

                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

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
                                            pic_url = key;
                                            publishAll();
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
                break;
            case R.id.add_mine_prize_cover:
                ShowPickDialog();
                break;
        }
    }

    private void ShowPickDialog() {
        deleteWindow = new SelectPhoPopWindow(AddMinePrizeActivity.this, itemsOnClick);
        deleteWindow.showAtLocation(AddMinePrizeActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    Intent camera = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                            .fromFile(new File(Environment
                                    .getExternalStorageDirectory(),
                                    "ppTG.jpg")));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (data != null) {
                    startPhotoZoom(data.getData());
                }
                break;
            case 2:
                File temp = new File(Environment.getExternalStorageDirectory()
                        + "/ppTG.jpg");
                startPhotoZoom(Uri.fromFile(temp));
                break;
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

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 16);
        intent.putExtra("aspectY", 9);
        intent.putExtra("outputX", 480);
        intent.putExtra("outputY", 270);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    /**
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            if (photo != null) {
                pics = CompressPhotoUtil.saveBitmap2file(photo, System.currentTimeMillis() + ".jpg", PHOTO_CACHE_DIR);
                add_mine_prize_cover.setImageBitmap(photo);
            }
        }
    }

    private void publishAll() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.PK_ADD_PRIZE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(AddMinePrizeActivity.this, R.string.publishnews_error_five, Toast.LENGTH_SHORT).show();
                            }
                            if (data.getCode() == 1) {
                                Toast.makeText(AddMinePrizeActivity.this, R.string.pk_add_error_two, Toast.LENGTH_SHORT).show();
                            }
                            if (data.getCode() == 2) {
                                Toast.makeText(AddMinePrizeActivity.this, R.string.pk_add_error_one, Toast.LENGTH_SHORT).show();
                            }
                            if (data.getCode() == 3) {
                                Toast.makeText(AddMinePrizeActivity.this, R.string.pk_add_error_three, Toast.LENGTH_SHORT).show();
                            }
                            if (data.getCode() == 4) {
                                Toast.makeText(AddMinePrizeActivity.this, R.string.pk_add_error_four, Toast.LENGTH_SHORT).show();
                            }
                        }

                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(AddMinePrizeActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("themeId", themeId);
                params.put("content", cont);
                params.put("pic", pic_url);
                params.put("schoolId", schoolstr);
                params.put("type", "1");
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

    //查询该代理商下面的学校
    private void getData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.GET_SCHOOLS_BY_JXS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            MineShangjiasDATA data = getGson().fromJson(s, MineShangjiasDATA.class);
                            if (data.getCode() == 200) {
                                lists.clear();
                                List<ContractSchool> listtwo = data.getData();
                                if (listtwo != null) {
                                    ContractSchool cst;
                                    for (int i = 0; i < listtwo.size(); i++) {
                                        cst = listtwo.get(i);
                                        cst.setIsSelected("1");
                                        lists.add(cst);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(AddMinePrizeActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AddMinePrizeActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(AddMinePrizeActivity.this, R.string.report_error_two, Toast.LENGTH_SHORT).show();
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

}

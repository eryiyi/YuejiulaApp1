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
import android.widget.EditText;
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
import com.liangxun.yuejiula.util.CompressPhotoUtil;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.CustomProgressDialog;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/3/31.
 * ��设置我的推广  代理商用
 */
public class AddMineTgActivity extends BaseActivity implements View.OnClickListener {
    private ImageView add_mine_tg_menu;
    private ImageView item_add_tg_cover;
    private TextView mine_tg_run;
    private EditText item_mine_tg_content;
    private SelectPhoPopWindow deleteWindow;
    private String pics = "";
    private String cont = "";
    private static final File PHOTO_CACHE_DIR = new File(Environment.getExternalStorageDirectory() + "/liangxun/PhotoCache");

    private String emp_id = "";
    private String schoolId = "";
    private String pic_url = "";
    AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_mine_tg);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        initView();
        Intent mapstorage = new Intent(Intent.ACTION_PICK, null);
        mapstorage.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(mapstorage, 1);
    }

    private void initView() {
        add_mine_tg_menu = (ImageView) this.findViewById(R.id.add_mine_tg_menu);
        item_add_tg_cover = (ImageView) this.findViewById(R.id.item_add_tg_cover);
        item_mine_tg_content = (EditText) this.findViewById(R.id.item_mine_tg_content);
        mine_tg_run = (TextView) this.findViewById(R.id.mine_tg_run);
        add_mine_tg_menu.setOnClickListener(this);
        mine_tg_run.setOnClickListener(this);
        item_add_tg_cover.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_mine_tg_menu:
                finish();
                break;
            case R.id.mine_tg_run:
                cont = item_mine_tg_content.getText().toString();
                if (StringUtil.isNullOrEmpty(cont)) {
                    Toast.makeText(this, R.string.publishnews_error_two, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(pics)) {
                    Toast.makeText(this, R.string.publishnews_error_four, Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog =  new CustomProgressDialog(AddMineTgActivity.this, "正在加载中",R.anim.custom_dialog_frame);

                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                //七牛
                Map<String,String> map = new HashMap<>();
                map.put("space", "paopao-pic");
                RequestParams params = new RequestParams(map);
                client.get(getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +InternetURL.UPLOAD_TOKEN, params, new JsonHttpResponseHandler() {
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
            case R.id.item_add_tg_cover:
                ShowPickDialog();
                break;
        }
    }

    private void ShowPickDialog() {
        deleteWindow = new SelectPhoPopWindow(AddMineTgActivity.this, itemsOnClick);
        deleteWindow.showAtLocation(AddMineTgActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

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
                item_add_tg_cover.setImageBitmap(photo);
            }
        }
    }

    private void publishAll() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +   InternetURL.PUBLIC_MOOD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code =  jo.getString("code");
                                if(Integer.parseInt(code) == 200){
                                    Toast.makeText(AddMineTgActivity.this, R.string.publishnews_error_five, Toast.LENGTH_SHORT).show();
                                    //调用广播，刷新主页
                                    Intent intent1 = new Intent(Constants.UPDATE_TG_SUCCESS);
                                    sendBroadcast(intent1);
                                    finish();
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
                        Toast.makeText(AddMineTgActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordType", Constants.RECORD_TYPE);
                params.put("recordCont", cont);
                params.put("recordEmpId", emp_id);
                params.put("recordSchoolId", schoolId);
                params.put("recordPicUrl", pic_url);
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

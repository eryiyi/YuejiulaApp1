package com.liangxun.yuejiula.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.Publish_mood_GridView_Adapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.NewsTypeDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.NewsClassify;
import com.liangxun.yuejiula.util.*;
import com.liangxun.yuejiula.widget.NoScrollGridView;
import com.liangxun.yuejiula.widget.popview.CustomerSpinner;
import com.liangxun.yuejiula.widget.popview.SelectPhoTwoPopWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/3/12
 * Time: 7:30
 * 类的功能、说明写在此处.
 */
public class PublishNewsActivity extends BaseActivity implements View.OnClickListener {
    private final static int SELECT_LOCAL_PHOTO = 110;
    private static final String TAG = PublishNewsActivity.class.getSimpleName();
    private ImageView publis_news_back;//返回
    private ImageView publish_news_imv;//图片
    private EditText publish_news_title;//标题
    private EditText publish_news_content;//内容
    private CustomerSpinner publish_good_type;//类别
    private TextView publish_news_run;//发表

    private ArrayAdapter<String> adapterspin;
    private ArrayList<String> goodstypes = new ArrayList<String>();
    private ArrayList<NewsClassify> goodstypeslst = new ArrayList<NewsClassify>();

    private String title;
    private String content;
    private String typeId = "";
    private String typeTitle = "";
    private NoScrollGridView publish_moopd_gridview_image;//图片
    private ArrayList<String> dataList = new ArrayList<String>();
    private ArrayList<String> tDataList = new ArrayList<String>();
    private List<String> uploadPaths = new ArrayList<String>();

    private Uri uri;
    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID
    private String emp_typeid = "";//当前登陆者类别
    private Publish_mood_GridView_Adapter adapter;
    private SelectPhoTwoPopWindow deleteWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_news_xml);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        initView();
        initData();
    }

    private void initView() {
        publis_news_back = (ImageView) this.findViewById(R.id.publis_news_back);
        publish_news_imv = (ImageView) this.findViewById(R.id.publish_news_imv);
        publish_news_title = (EditText) this.findViewById(R.id.publish_news_title);
        publish_news_content = (EditText) this.findViewById(R.id.publish_news_content);
        publish_good_type = (CustomerSpinner) this.findViewById(R.id.publish_good_type);
        publish_news_run = (TextView) this.findViewById(R.id.publish_news_run);

        publis_news_back.setOnClickListener(this);
        publish_news_imv.setOnClickListener(this);
        publish_news_run.setOnClickListener(this);

        adapterspin = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, goodstypes);
        publish_good_type.setList(goodstypes);
        publish_good_type.setAdapter(adapterspin);
        publish_good_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, position + "");
                if (position > 0) {
                    typeId = goodstypeslst.get(position - 1).getId();//获得类别UUID
                    typeTitle = goodstypeslst.get(position - 1).getName();//获得类别名称
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        publish_moopd_gridview_image = (NoScrollGridView) this.findViewById(R.id.publish_goods_gridview_image);
        adapter = new Publish_mood_GridView_Adapter(PublishNewsActivity.this, dataList);
        publish_moopd_gridview_image.setAdapter(adapter);
        publish_moopd_gridview_image.setOnItemClickListener(new GridView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String path = dataList.get(position);
                if (path.contains("default") && position == dataList.size() - 1 && dataList.size() - 1 != 9) {
                    showSelectImageDialog();
                } else {
                    Intent intent = new Intent(PublishNewsActivity.this, ImageDelActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("path", dataList.get(position));
                    startActivityForResult(intent, CommonDefine.DELETE_IMAGE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publis_news_back:
                finish();
                break;
            case R.id.publish_news_run:
//                title = publish_news_title.getText().toString();
//                content = publish_news_content.getText().toString();
//                if (StringUtil.isNullOrEmpty(title)) {
//                    Toast.makeText(PublishNewsActivity.this, R.string.publishnews_error_one, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (title.length() > 100) {
//                    Toast.makeText(PublishNewsActivity.this, R.string.publishnews_error_oneone, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (StringUtil.isNullOrEmpty(content)) {
//                    Toast.makeText(PublishNewsActivity.this, R.string.publishnews_error_two, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (content.length() > 5000) {
//                    Toast.makeText(PublishNewsActivity.this, R.string.publishnews_error_twoone, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (StringUtil.isNullOrEmpty(typeId)) {
//                    Toast.makeText(PublishNewsActivity.this, R.string.publishnews_error_three, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                Resources res = getBaseContext().getResources();
//                String message = res.getString(R.string.check_publish).toString();
//                progressDialog = new ProgressDialog(PublishNewsActivity.this);
//                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.setMessage(message);
//                progressDialog.show();
//                //检查有没有选择图片
//                if (dataList.size() == 1 || dataList.size() == 0) {
//                    progressDialog.dismiss();
//                    Toast.makeText(PublishNewsActivity.this, R.string.publishnews_error_four, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                for (int i = 0; i < dataList.size() - 1; i++) {
//                    File file = new File(dataList.get(i));
//                    if (dataList.get(i).equals("camera_default")) {
//                        continue;
//                    }
//                    Map<String, File> files = new HashMap<String, File>();
//                    files.put("file", file);
//                    Map<String, String> params = new HashMap<String, String>();
//                    CommonUtil.addPutUploadFileRequest(
//                            this,
//                            InternetURL.UPLOAD_FILE,
//                            files,
//                            params,
//                            new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String s) {
//                                    if (StringUtil.isJson(s)) {
//                                        SuccessData data = getGson().fromJson(s, SuccessData.class);
//                                        if (data.getCode() == 200) {
//                                            uploadPaths.add(data.getData());
//                                            //说明文件已经上传完毕
//                                            if (uploadPaths.size() == dataList.size() - 1) {
//                                                publishAll();
//                                            }
//                                        } else {
//                                            Toast.makeText(PublishNewsActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
//                                            if (progressDialog != null) {
//                                                progressDialog.dismiss();
//                                            }
//                                        }
//
//                                    }
//                                }
//                            },
//                            new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError volleyError) {
//                                    Toast.makeText(PublishNewsActivity.this, R.string.publish_error_two, Toast.LENGTH_SHORT).show();
//                                    if (progressDialog != null) {
//                                        progressDialog.dismiss();
//                                    }
//                                }
//                            },
//                            null);
//                }
                break;
            case R.id.publish_news_imv:
                //选择照片
                showSelectImageDialog();
                break;
        }
    }

    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_NEWS_TYPE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            NewsTypeDATA data = getGson().fromJson(s, NewsTypeDATA.class);
                            if (data.getCode() == 200) {
                                goodstypes.clear();
                                goodstypes.add("选择类别");
                                goodstypeslst.addAll(data.getData());
                                for (NewsClassify goodstype1 : goodstypeslst) {
                                    goodstypes.add(goodstype1.getName());
                                }
                                adapterspin.notifyDataSetChanged();
                            } else {
                                Toast.makeText(PublishNewsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PublishNewsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(PublishNewsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
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

    // 选择相册，相机
    private void showSelectImageDialog() {
        deleteWindow = new SelectPhoTwoPopWindow(PublishNewsActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(PublishNewsActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    Intent cameraIntent = new Intent();
                    cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    // 根据文件地址创建文件
                    File file = new File(CommonDefine.FILE_PATH);
                    if (file.exists()) {
                        file.delete();
                    }
                    uri = Uri.fromFile(file);
                    // 设置系统相机拍摄照片完成后图片文件的存放地址
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    // 开启系统拍照的Activity
                    startActivityForResult(cameraIntent, CommonDefine.TAKE_PICTURE_FROM_CAMERA);
                }
                break;
                case R.id.mapstorage: {
                    Intent intent = new Intent(PublishNewsActivity.this, AlbumActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("dataList", getIntentArrayList(dataList));
                    bundle.putString("editContent", "");
                    intent.putExtras(bundle);
                    startActivityForResult(intent, CommonDefine.TAKE_PICTURE_FROM_GALLERY);
                }
                break;
                default:
                    break;
            }
        }

    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private ArrayList<String> getIntentArrayList(ArrayList<String> dataList) {

        ArrayList<String> tDataList = new ArrayList<String>();

        for (String s : dataList) {
            if (!s.contains("default")) {
                tDataList.add(s);
            }
        }
        return tDataList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_LOCAL_PHOTO:
                    tDataList = data.getStringArrayListExtra("datalist");
                    if (tDataList != null) {
                        for (int i = 0; i < tDataList.size(); i++) {
                            String string = tDataList.get(i);
                            dataList.add(string);
                        }
                        if (dataList.size() < 9) {
                            dataList.add("camera_default");
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        finish();
                    }
                    break;
                case CommonDefine.TAKE_PICTURE_FROM_CAMERA:
                    String sdStatus = Environment.getExternalStorageState();
                    if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                        return;
                    }
                    Bitmap bitmap = ImageUtils.getUriBitmap(this, uri, 400, 400);
                    String cameraImagePath = FileUtils.saveBitToSD(bitmap, System.currentTimeMillis() + ".jpg");
                    for (int i = 0; i < dataList.size(); i++) {
                        String path = dataList.get(i);
                        if (path.contains("default")) {
                            dataList.remove(dataList.size() - 1);
                        }
                    }
                    dataList.add(cameraImagePath);
                    if (dataList.size() < 9) {
                        dataList.add("camera_default");
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case CommonDefine.TAKE_PICTURE_FROM_GALLERY:
//                    Bundle bundle2 = data.getExtras();
                    tDataList = data.getStringArrayListExtra("datalist");
                    if (tDataList != null) {
                        dataList.clear();
                        for (int i = 0; i < tDataList.size(); i++) {
                            String string = tDataList.get(i);
                            dataList.add(string);
                        }
                        if (dataList.size() < 9) {
                            dataList.add("camera_default");
                        }
                        adapter.notifyDataSetChanged();
                    }

                    break;
                case CommonDefine.DELETE_IMAGE:
                    int position = data.getIntExtra("position", -1);
                    dataList.remove(position);
                    if (dataList.size() < 9) {
                        dataList.add(dataList.size(), "camera_default");
                        for (int i = 0; i < dataList.size(); i++) {
                            String path = dataList.get(i);
                            if (path.contains("default")) {
                                if (i != 8) {
                                    dataList.remove(dataList.size() - 2);
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    break;
            }

        }
    }

    //上传完图片后开始发布
    private void publishAll() {
        final StringBuffer filePath = new StringBuffer();
        for (int i = 0; i < uploadPaths.size(); i++) {
            filePath.append(uploadPaths.get(i));
            if (i != uploadPaths.size() - 1) {
                filePath.append(",");
            }
        }
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.PUBLISH_NEWS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(PublishNewsActivity.this, R.string.publishnews_error_five, Toast.LENGTH_SHORT).show();
                                //调用广播，刷新主页
//                                Intent intent1 = new Intent(Constants.SEND_GOOD_SUCCESS);
//                                getContext().sendBroadcast(intent1);
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
                        Toast.makeText(PublishNewsActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("typeId", typeId);
                params.put("title", title);
                params.put("dateLine", String.valueOf(filePath));
                params.put("content", content);
                params.put("empId", emp_id);
                params.put("publishType", "1");
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

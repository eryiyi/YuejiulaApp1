package com.liangxun.yuejiula.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.MainActivity;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.Publish_mood_GridView_Adapter;
import com.liangxun.yuejiula.base.ActivityTack;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.util.*;
import com.liangxun.yuejiula.widget.NoScrollGridView;
import com.liangxun.yuejiula.widget.popview.PublishPopWindow;
import com.liangxun.yuejiula.widget.popview.SelectPhoTwoPopWindow;
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
 * Created by Administrator on 2015/4/9.
 */
public class PkAddActivity extends BaseActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener, TextureView.SurfaceTextureListener {
    private ImageView pk_add_back;
    private TextView pk_add_sub;
    private String type;
    private ImageView add_pic;
    private LinearLayout rl_bottom;

    private String cont;
    private NoScrollGridView publish_moopd_gridview_image;
    private Publish_mood_GridView_Adapter adapter;
    private ArrayList<String> dataList = new ArrayList<String>();
    private ArrayList<String> tDataList = new ArrayList<String>();
    private List<String> uploadPaths = new ArrayList<String>();
    private SelectPhoTwoPopWindow deleteWindow;
    private Uri uri;
    private final static int SELECT_LOCAL_PHOTO = 110;
    private RelativeLayout preview_video_parent;
    private String path;
    private PublishPopWindow publishPopWindow;
    public static final int VIDEO_CODE = 112;

    private String schoolId = "";
    private String emp_id = "";

    private TextureView surfaceView;
    private MediaPlayer mediaPlayer;
    private ImageView imagePlay;
    private String themeType = "";
    public EditText add_content;
    public TextView add_content_count;

    AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pk_add);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        type = getIntent().getExtras().getString(Constants.PK_ADD_ZUOPIN_TYPE);
        themeType = getIntent().getExtras().getString(Constants.PK_ADD_ZUOPIN_THEME);
        initView();
        if (type.equals("0")) {

            rl_bottom.setVisibility(View.GONE);
        }
        if (type.equals("1")) {

            rl_bottom.setVisibility(View.VISIBLE);
        }
        if (type.equals("2")) {

            rl_bottom.setVisibility(View.GONE);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            surfaceView = (TextureView) findViewById(R.id.preview_video);

            RelativeLayout preview_video_parent = (RelativeLayout) findViewById(R.id.preview_video_parent);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) preview_video_parent
                    .getLayoutParams();
            layoutParams.width = displaymetrics.widthPixels;
            layoutParams.height = displaymetrics.widthPixels;
            preview_video_parent.setLayoutParams(layoutParams);
            preview_video_parent.setVisibility(View.VISIBLE);
            surfaceView.setSurfaceTextureListener(this);
            surfaceView.setOnClickListener(this);

            path = getIntent().getStringExtra("path");

            imagePlay = (ImageView) findViewById(R.id.previre_play);
            imagePlay.setOnClickListener(this);
            imagePlay.setVisibility(View.VISIBLE);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
        }

    }

    private void initView() {
        pk_add_back = (ImageView) this.findViewById(R.id.pk_add_back);
        pk_add_back.setOnClickListener(this);
        pk_add_sub = (TextView) this.findViewById(R.id.pk_add_sub);
        pk_add_sub.setOnClickListener(this);
        add_content = (EditText) this.findViewById(R.id.add_content);
//        add_content.addTextChangedListener(textWatcher);
        add_content.setSelection(add_content.length());//将光标移动最后一个字符后面
        add_content_count = (TextView) findViewById(R.id.count);
        add_pic = (ImageView) this.findViewById(R.id.add_pic);
        add_pic.setOnClickListener(this);
        rl_bottom = (LinearLayout) this.findViewById(R.id.rl_bottom);
        publish_moopd_gridview_image = (NoScrollGridView) this.findViewById(R.id.publish_moopd_gridview_image);
        adapter = new Publish_mood_GridView_Adapter(this, dataList);
        publish_moopd_gridview_image.setAdapter(adapter);
        publish_moopd_gridview_image.setOnItemClickListener(new GridView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String path = dataList.get(position);
                if (path.contains("camera_default") && position == dataList.size() - 1 && dataList.size() - 1 != 9) {
                    showSelectImageDialog();
                } else {
                    Intent intent = new Intent(PkAddActivity.this, ImageDelActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("path", dataList.get(position));
                    startActivityForResult(intent, CommonDefine.DELETE_IMAGE);
                }
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        private CharSequence temp;//监听前的文本
        private int editStart;//光标开始位置
        private int editEnd;//光标结束位置
        private final int charMaxNum = 500;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp = s;
        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = add_content.getSelectionStart();
            editEnd = add_content.getSelectionEnd();
            add_content.removeTextChangedListener(textWatcher);
            if (temp.length() > charMaxNum) {
                s.delete(editStart - 1, editEnd);
                editStart--;
                editEnd--;
            }
            add_content.setText(s);
            add_content.setSelection(editStart);
            add_content.addTextChangedListener(textWatcher);
            add_content_count.setText(String.valueOf(charMaxNum - temp.length()));
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pk_add_back:
                if (!TextUtils.isEmpty(add_content.getText().toString().trim())|| dataList.size()!=0||type.equals("2")) {   //这里trim()作用是去掉首位空格，防止不必要的错误
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(add_content.getWindowToken(), 0); //强制隐藏键盘
                    showSelectPublishDialog();
                } else {
                    finish();
                }
                break;
            case R.id.pk_add_sub:
                cont = add_content.getText().toString();
                if (StringUtil.isNullOrEmpty(cont)) {
                    Toast.makeText(this, R.string.commetn_isnull, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cont.length() > 500) {
                    Toast.makeText(this, R.string.pk_cont_length, Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog = new ProgressDialog(PkAddActivity.this );
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                if (type.equals("0")) {
                    publishAll();
                }
                if (type.equals("1")) {

                    if (dataList.size() == 0) {
                        progressDialog.dismiss();
                        Toast.makeText(this, R.string.check_is_picture, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (int i = 0; i < dataList.size(); i++) {
                        //七牛
                        final String picurl = dataList.get(i);
                        Map<String,String> map = new HashMap<>();
                        map.put("space", "paopao-pic");
                        RequestParams params = new RequestParams(map);
                        client.get(InternetURL.UPLOAD_TOKEN ,params, new JsonHttpResponseHandler(){
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                try {
                                    String token = response.getString("data");
                                    UploadManager uploadManager = new UploadManager();
                                    uploadManager.put(StringUtil.getBytes(picurl), StringUtil.getUUID(), token,
                                            new UpCompletionHandler() {
                                                @Override
                                                public void complete(String key, ResponseInfo info, JSONObject response) {
                                                    //key
                                                    uploadPaths.add(key);
                                                    if (uploadPaths.size() == dataList.size()) {
                                                        publishAllPic();
                                                    }
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
                }
                if (type.equals("2")) {

                    uploadFileVideo();
                }
                break;
            case R.id.add_pic:
                showSelectImageDialog();
                break;
            case R.id.previre_play:
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                imagePlay.setVisibility(View.GONE);
                break;
            case R.id.preview_video:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    imagePlay.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    // 选择是否退出发布
    private void showSelectPublishDialog() {
        publishPopWindow = new PublishPopWindow(PkAddActivity.this, itemOnClick);
        publishPopWindow.showAtLocation(PkAddActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            publishPopWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_sure:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            if (!TextUtils.isEmpty(add_content.getText().toString().trim())|| dataList.size()!=0||type.equals("2")) {
                showSelectPublishDialog();
                return true;
            } else {
                finish();
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private void publishAll() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.PK_ADD_ZUOPIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(PkAddActivity.this, R.string.publish_success, Toast.LENGTH_SHORT).show();
                                //���ù㲥��ˢ����ҳ
                                Intent intent1 = new Intent(Constants.PK_SEND_SUCCESS_URL);
                                sendBroadcast(intent1);
                                finish();
                            }
                            if (data.getCode() == 1) {

                                Toast.makeText(PkAddActivity.this, R.string.publish_error_three, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(PkAddActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ztId", themeType);
                params.put("empId", emp_id);
                params.put("schoolId", schoolId);
                params.put("type", type);
                params.put("title", cont);
                params.put("picUrl", "");
                params.put("videoUrl", "");
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

    private void publishAllPic() {
        final StringBuffer filePath = new StringBuffer();
        for (int i = 0; i < uploadPaths.size(); i++) {
            filePath.append(uploadPaths.get(i));
            if (i != uploadPaths.size() - 1) {
                filePath.append(",");
            }
        }
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.PK_ADD_ZUOPIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(PkAddActivity.this, R.string.publish_success, Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(Constants.PK_SEND_SUCCESS_URL);
                                sendBroadcast(intent1);
                                finish();
                            }
                            if (data.getCode() == 1) {

                                Toast.makeText(PkAddActivity.this, R.string.publish_error_three, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PkAddActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ztId", themeType);
                params.put("empId", emp_id);
                params.put("schoolId", schoolId);
                params.put("type", type);
                params.put("title", cont);
                params.put("picUrl", String.valueOf(filePath));
                params.put("videoUrl", "");
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

    private void uploadFileVideo() {
        if (StringUtil.isNullOrEmpty(path)) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Toast.makeText(PkAddActivity.this, R.string.video_error, Toast.LENGTH_SHORT).show();
            return;
        }
        //七牛
        Map<String,String> map = new HashMap<>();
        map.put("space", "paopao-pic");
        RequestParams params = new RequestParams(map);
        client.get(InternetURL.UPLOAD_TOKEN ,params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String token = response.getString("data");
                    UploadManager uploadManager = new UploadManager();
                    uploadManager.put(StringUtil.getBytes(path), StringUtil.getUUID(), token,
                            new UpCompletionHandler() {
                                @Override
                                public void complete(String key, ResponseInfo info, JSONObject response) {
                                    //key
                                    publishRun(key);
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

    private void publishRun(final String videoPath) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.PK_ADD_ZUOPIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(PkAddActivity.this, R.string.publish_success, Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(Constants.PK_SEND_SUCCESS_URL);
                                sendBroadcast(intent1);
                                finish();
                            }
                            if (data.getCode() == 1) {
                                Toast.makeText(PkAddActivity.this, R.string.publish_error_three, Toast.LENGTH_SHORT).show();
                                finish();
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
                        Toast.makeText(PkAddActivity.this, R.string.publish_error_two, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ztId", themeType);
                params.put("empId", emp_id);
                params.put("schoolId", schoolId);
                params.put("type", type);
                params.put("title", cont);
                params.put("picUrl", "");
                params.put("videoUrl", videoPath);
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


    private void showSelectImageDialog() {
        deleteWindow = new SelectPhoTwoPopWindow(PkAddActivity.this, itemsOnClick);

        deleteWindow.showAtLocation(PkAddActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private ArrayList<String> getIntentArrayList(ArrayList<String> dataList) {

        ArrayList<String> tDataList = new ArrayList<String>();

        for (String s : dataList) {
            tDataList.add(s);
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
                    dataList.add(cameraImagePath);
                    adapter.notifyDataSetChanged();
                    break;
                case CommonDefine.TAKE_PICTURE_FROM_GALLERY:
                    tDataList = data.getStringArrayListExtra("datalist");
                    if (tDataList != null) {
                        dataList.clear();
                        for (int i = 0; i < tDataList.size(); i++) {
                            String string = tDataList.get(i);
                            dataList.add(string);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    break;
                case CommonDefine.DELETE_IMAGE:
                    int position = data.getIntExtra("position", -1);
                    dataList.remove(position);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    Intent cameraIntent = new Intent();
                    cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    File file = new File(CommonDefine.FILE_PATH);
                    if (file.exists()) {
                        file.delete();
                    }
                    uri = Uri.fromFile(file);

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(cameraIntent, CommonDefine.TAKE_PICTURE_FROM_CAMERA);
                }
                break;
                case R.id.mapstorage: {
                    Intent intent = new Intent(PkAddActivity.this, AlbumActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("dataList", getIntentArrayList(dataList));
                    bundle.putString("editContent", add_content.getText().toString());
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
    protected void onStop() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
        super.onStop();
    }

    private void prepare(Surface surface) {
        try {
            if (!StringUtil.isNullOrEmpty(path) && mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                mediaPlayer.setDataSource(path);

                mediaPlayer.setSurface(surface);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            }

        } catch (Exception e) {
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,
                                          int arg2) {
        prepare(new Surface(arg0));
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
                                            int arg2) {

    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture arg0) {

    }

    private void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        ActivityTack.getInstanse().popUntilActivity(MainActivity.class);
    }

    @Override
    public void onBackPressed() {
        stop();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // ��õ�ǰ�õ������View��һ������¾���EditText������������ǹ켣�����ʵ�尸�����ƶ����㣩
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * ����EditText����������û������������Աȣ����ж��Ƿ����ؼ��̣���Ϊ���û����EditTextʱû��Ҫ����
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
                // ���EditText���¼�����������
                return false;
            } else {
                return true;
            }
        }
        // ������㲻��EditText����ԣ������������ͼ�ջ����꣬��һ�����㲻��EditView�ϣ����û��ù켣��ѡ�������Ľ���
        return false;
    }

    /**
     * ������������̷���������һ��?
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
}

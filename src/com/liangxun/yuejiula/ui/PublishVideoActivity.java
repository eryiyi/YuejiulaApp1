package com.liangxun.yuejiula.ui;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableString;
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
import com.liangxun.yuejiula.base.ActivityTack;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.RecordSingleDATA;
import com.liangxun.yuejiula.entity.SchoolRecordMood;
import com.liangxun.yuejiula.face.ChatEmoji;
import com.liangxun.yuejiula.face.FaceAdapter;
import com.liangxun.yuejiula.face.FaceConversionUtil;
import com.liangxun.yuejiula.face.ViewPagerAdapter;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.popview.CustomerSpinner;
import com.liangxun.yuejiula.widget.popview.PublishPopWindow;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhanghl on 2014/11/24.
 * <p/>
 * 发布视频
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class PublishVideoActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, MediaPlayer.OnCompletionListener, TextureView.SurfaceTextureListener {
    private ImageView back;
    private TextView publish;
    //    private TextView filePath;

//    private ImageView videoRecord;
//    private TextView publish_video_record_text;

    private String path;

    public static final int VIDEO_CODE = 112;

    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID
    private PublishPopWindow publishPopWindow;

    /**
     * 表情页的监听事件
     */
    private OnCorpusSelectedListener mListener;

    /**
     * 显示表情页的viewpager
     */
    private ViewPager vp_face;

    /**
     * 表情页界面集合
     */
    private ArrayList<View> pageViews;

    /**
     * 游标显示布局
     */
    private LinearLayout layout_point;

    /**
     * 游标点集合
     */
    private ArrayList<ImageView> pointViews;

    /**
     * 表情集合
     */
    private List<List<ChatEmoji>> emojis;

    /**
     * 表情区域
     */
    public View view;
    public ImageButton send_btn_face_normal;
    public ImageButton send_btn_face_enable;
    /**
     * 输入框
     */
    public EditText et_sendmessage;
    public TextView et_sendmessage_count;
    /**
     * 表情数据填充器
     */
    private List<FaceAdapter> faceAdapters;

    /**
     * 当前表情页
     */
    private int current = 0;

    public void setOnCorpusSelectedListener(OnCorpusSelectedListener listener) {
        mListener = listener;
    }

    /**
     * 表情选择监听
     *
     * @author naibo-liao
     * @时间： 2013-1-15下午04:32:54
     */
    public interface OnCorpusSelectedListener {

        void onCorpusSelected(ChatEmoji emoji);

        void onCorpusDeleted();
    }

    //播放视频部分
    private TextureView surfaceView;
    private MediaPlayer mediaPlayer;
    private ImageView imagePlay;


    private CustomerSpinner provinceSpinner;
    private ArrayList<SchoolRecordMood> provinces = new ArrayList<SchoolRecordMood>();
    private ArrayList<String> provinces_names = new ArrayList<String>();
    private ArrayAdapter<String> provinceAdapter;
    SchoolRecordMood schoolRecordMood;//选中的那个额
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_video_xml);
        initView();
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        //跳转到录制页面
//        openVideo();
        emojis = FaceConversionUtil.getInstace().emojiLists;
        Init_View();
        Init_viewPager();
        Init_Point();
        Init_Data();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        surfaceView = (TextureView) findViewById(R.id.preview_video);

        RelativeLayout preview_video_parent = (RelativeLayout) findViewById(R.id.preview_video_parent);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) preview_video_parent
                .getLayoutParams();
//        layoutParams.width = displaymetrics.widthPixels;
//        layoutParams.height = displaymetrics.widthPixels;
//        preview_video_parent.setLayoutParams(layoutParams);

        surfaceView.setSurfaceTextureListener(this);
        surfaceView.setOnClickListener(this);

        path = getIntent().getStringExtra("path");

        imagePlay = (ImageView) findViewById(R.id.previre_play);
        imagePlay.setOnClickListener(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);

    }

    private void initView() {
        back = (ImageView) findViewById(R.id.publis_video_back);
        publish = (TextView) findViewById(R.id.publish_video_run);
        back.setOnClickListener(this);
        publish.setOnClickListener(this);
        provinceAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, provinces_names);
        provinceSpinner = (CustomerSpinner) findViewById(R.id.provice_select_school);
        provinceSpinner.setList(provinces_names);
        provinceSpinner.setAdapter(provinceAdapter);

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    schoolRecordMood = provinces.get(position );
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        provinces.clear();
        provinces.addAll(MainActivity.arrayMood);
        provinces_names.clear();
        provinces_names.add("请选择标签");
        for (SchoolRecordMood pro : provinces) {
            if("0".equals(pro.getSchool_record_mood_type())){
                provinces_names.add("心情-"+pro.getSchool_record_mood_name());
            }else
            if("1".equals(pro.getSchool_record_mood_type())){
                provinces_names.add("求助-"+pro.getSchool_record_mood_name());
            }else
            if("2".equals(pro.getSchool_record_mood_type())){
                provinces_names.add("拍卖-"+pro.getSchool_record_mood_name());
            }
        }
        provinceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previre_play://播放视频
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                imagePlay.setVisibility(View.GONE);
                break;
            case R.id.preview_video://暂停
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    imagePlay.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.publis_video_back://返回
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_sendmessage.getWindowToken(), 0); //强制隐藏键盘
                    showSelectImageDialog();
                break;
            case R.id.publish_video_run://发布
                if(!StringUtil.isNullOrEmpty(et_sendmessage.getText().toString())){
                    if (et_sendmessage.getText().toString().length() > 500) {
                        Toast.makeText(this, R.string.publish_video_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if(schoolRecordMood == null){
                    Toast.makeText(this, "请选择标签", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog = new ProgressDialog(PublishVideoActivity.this );

                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                uploadFile();
                break;
//            case R.id.publish_video_record:
//                openVideo();
//                break;
//            case R.id.publish_video_record_text:
//                openVideo();
//                break;
            case R.id.send_btn_face_normal:
                // 显示表情选择框
                send_btn_face_normal.setVisibility(View.INVISIBLE);
                send_btn_face_enable.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(et_sendmessage.getWindowToken(), 0); //强制隐藏键盘
                break;
            case R.id.send_btn_face_enable:
                // 隐藏表情选择框
                send_btn_face_enable.setVisibility(View.INVISIBLE);
                send_btn_face_normal.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);
                break;
            case R.id.face_content:
            if (view.getVisibility() == View.VISIBLE) {
                send_btn_face_enable.setVisibility(View.INVISIBLE);
                send_btn_face_normal.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);
            }
                break;
        }
    }

    // 选择是否退出发布
    private void showSelectImageDialog() {
        publishPopWindow = new PublishPopWindow(PublishVideoActivity.this, itemsOnClick);
        publishPopWindow.showAtLocation(PublishVideoActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

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
        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            showSelectImageDialog();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void uploadFile() {
        if (StringUtil.isNullOrEmpty(path)) {
            Toast.makeText(this, R.string.video_error, Toast.LENGTH_SHORT).show();
            return;
        }
        //七牛
        AsyncHttpClient client = new AsyncHttpClient();
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


    private void publishRun(final String key) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.PUBLIC_MOOD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo  = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    RecordSingleDATA data = getGson().fromJson(s, RecordSingleDATA.class);
                                    Toast.makeText(PublishVideoActivity.this, R.string.publish_success, Toast.LENGTH_SHORT).show();
                                    //调用广播，刷新主页
                                    Intent intent1 = new Intent(Constants.SEND_INDEX_SUCCESS);
                                    intent1.putExtra("addRecord", data.getData());
                                    sendBroadcast(intent1);
                                    finish();
                                }else if(Integer.parseInt(code) == 1){
                                    showMsg(PublishVideoActivity.this, "发布失败！");
                                } else if(Integer.parseInt(code) == 2){
                                    showMsg(PublishVideoActivity.this, "发布失败，不能重复发布！");
                                }else if(Integer.parseInt(code) == 3){
                                    showMsg(PublishVideoActivity.this, "发布失败，您已被封号！");
                                }else {
                                    showMsg(PublishVideoActivity.this, "发布失败，请稍后重试！");
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
                        Toast.makeText(PublishVideoActivity.this, R.string.publish_error_two, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordType", Constants.VIDEO_TYPE);
                params.put("recordCont", et_sendmessage.getText().toString());
                params.put("recordPicUrl", "");
                params.put("recordVoice", "");
                params.put("recordVideo", key);
                params.put("recordEmpId", emp_id);
                params.put("recordSchoolId", schoolId);
                if(schoolRecordMood != null && !StringUtil.isNullOrEmpty(schoolRecordMood.getSchool_record_mood_id())){
                    params.put("school_record_mood_id", schoolRecordMood.getSchool_record_mood_id());
                    if("2".equals(schoolRecordMood.getSchool_record_mood_type())){
                        params.put("is_paimai", "1");
                    }
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

    /**
     * 隐藏表情选择框
     */
    public boolean hideFaceView() {
        // 隐藏表情选择框
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    /**
     * 初始化控件
     */
    private void Init_View() {
        vp_face = (ViewPager) findViewById(R.id.vp_contains);
        et_sendmessage = (EditText) findViewById(R.id.face_content);
//        et_sendmessage.addTextChangedListener(textWatcher);
        et_sendmessage.setSelection(et_sendmessage.length());//将光标移动最后一个字符后面
        et_sendmessage_count = (TextView) findViewById(R.id.count);


        layout_point = (LinearLayout) findViewById(R.id.iv_image);

        send_btn_face_normal = (ImageButton) this.findViewById(R.id.send_btn_face_normal);
        send_btn_face_normal.setOnClickListener(this);
        send_btn_face_enable = (ImageButton) this.findViewById(R.id.send_btn_face_enable);
        send_btn_face_enable.setOnClickListener(this);
        view = findViewById(R.id.ll_facechoose);

        et_sendmessage.setOnClickListener(this);

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
            editStart = et_sendmessage.getSelectionStart();
            editEnd = et_sendmessage.getSelectionEnd();
            et_sendmessage.removeTextChangedListener(textWatcher);
            if (temp.length() > charMaxNum) {
                s.delete(editStart - 1, editEnd);
                editStart--;
                editEnd--;
            }
            et_sendmessage.setText(s);
            et_sendmessage.setSelection(editStart);
            et_sendmessage.addTextChangedListener(textWatcher);
            et_sendmessage_count.setText(String.valueOf(charMaxNum - temp.length()));
        }
    };


    /**
     * 初始化显示表情的viewpager
     */
    private void Init_viewPager() {
        pageViews = new ArrayList<View>();
        // 左侧添加空页
        View nullView1 = new View(this);
        // 设置透明背景
        nullView1.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView1);

        // 中间添加表情页

        faceAdapters = new ArrayList<FaceAdapter>();
        for (int i = 0; i < emojis.size(); i++) {
            GridView view = new GridView(this);
            FaceAdapter adapter = new FaceAdapter(this, emojis.get(i));
            view.setAdapter(adapter);
            faceAdapters.add(adapter);
            view.setOnItemClickListener(this);
            view.setNumColumns(7);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setPadding(5, 0, 5, 0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            pageViews.add(view);
        }

        // 右侧添加空页面
        View nullView2 = new View(this);
        // 设置透明背景
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView2);
    }

    /**
     * 初始化游标
     */
    private void Init_Point() {

        pointViews = new ArrayList<ImageView>();
        ImageView imageView;
        for (int i = 0; i < pageViews.size(); i++) {
            imageView = new ImageView(this);
            imageView.setBackgroundResource(R.drawable.d1);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.width = 8;
            layoutParams.height = 8;
            layout_point.addView(imageView, layoutParams);
            if (i == 0 || i == pageViews.size() - 1) {
                imageView.setVisibility(View.GONE);
            }
            if (i == 1) {
                imageView.setBackgroundResource(R.drawable.d2);
            }
            pointViews.add(imageView);

        }
    }

    /**
     * 填充数据
     */
    private void Init_Data() {
        vp_face.setAdapter(new ViewPagerAdapter(pageViews));

        vp_face.setCurrentItem(1);
        current = 0;
        vp_face.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                current = arg0 - 1;
                // 描绘分页点
                draw_Point(arg0);
                // 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
                if (arg0 == pointViews.size() - 1 || arg0 == 0) {
                    if (arg0 == 0) {
                        vp_face.setCurrentItem(arg0 + 1);// 第二屏 会再次实现该回调方法实现跳转.
                        pointViews.get(1).setBackgroundResource(R.drawable.d2);
                    } else {
                        vp_face.setCurrentItem(arg0 - 1);// 倒数第二屏
                        pointViews.get(arg0 - 1).setBackgroundResource(
                                R.drawable.d2);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    /**
     * 绘制游标背景
     */
    public void draw_Point(int index) {
        for (int i = 1; i < pointViews.size(); i++) {
            if (index == i) {
                pointViews.get(i).setBackgroundResource(R.drawable.d2);
            } else {
                pointViews.get(i).setBackgroundResource(R.drawable.d1);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        ChatEmoji emoji = (ChatEmoji) faceAdapters.get(current).getItem(arg2);
        if (emoji.getId() == R.drawable.face_del_icon) {
            int selection = et_sendmessage.getSelectionStart();
            String text = et_sendmessage.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[");
                    int end = selection;
                    et_sendmessage.getText().delete(start, end);
                    return;
                }
                et_sendmessage.getText().delete(selection - 1, selection);
            }
        }
        if (!TextUtils.isEmpty(emoji.getCharacter())) {
            if (mListener != null)
                mListener.onCorpusSelected(emoji);
            SpannableString spannableString = FaceConversionUtil.getInstace()
                    .addFace(this, emoji.getId(), emoji.getCharacter());
            et_sendmessage.append(spannableString);
        }

    }


    //视频部分
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
                // 设置需要播放的视频
                mediaPlayer.setDataSource(path);
                // 把视频画面输出到Surface
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

}

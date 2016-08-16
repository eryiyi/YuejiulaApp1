package com.liangxun.yuejiula.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.face.ChatEmoji;
import com.liangxun.yuejiula.face.FaceAdapter;
import com.liangxun.yuejiula.face.FaceConversionUtil;
import com.liangxun.yuejiula.face.ViewPagerAdapter;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.CustomProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/9.
 */
public class PkCommentActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ImageView publis_comment_back;//返回
    private TextView publish_comment_run;//发布
    private String cont;
    private TextView rebacktext;//父评论者
    private String father_person_name = "";
    private String father_uuid;
    private String record_uuid;

    private String emp_id = "";//当前登陆者UUID
    private String sendEmpId = "";//所有人UUID
    private String fplempid = "";//父评论者



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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_comment_record);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        initView();
        father_person_name = getIntent().getExtras().getString(Constants.FATHER_PERSON_NAME);//父评论者昵称
        father_uuid = getIntent().getExtras().getString(Constants.FATHER_UUID);//父评论UUID
        record_uuid = getIntent().getExtras().getString(Constants.RECORD_UUID);//动态UUID
        sendEmpId = getIntent().getExtras().getString(Constants.FATHER_PERSON_UUID);
        fplempid = getIntent().getExtras().getString("fplempid");

        if (!StringUtil.isNullOrEmpty(father_person_name)) {
            rebacktext.setVisibility(View.VISIBLE);
            rebacktext.setText("回复:" + father_person_name);
        } else {
            rebacktext.setVisibility(View.GONE);
        }

        emojis = FaceConversionUtil.getInstace().emojiLists;
        Init_View();
        Init_viewPager();
        Init_Point();
        Init_Data();
    }

    private void initView() {
        publis_comment_back = (ImageView) this.findViewById(R.id.publis_comment_back);
        publis_comment_back.setOnClickListener(this);
        publish_comment_run = (TextView) this.findViewById(R.id.publish_comment_run);
        publish_comment_run.setOnClickListener(this);
        rebacktext = (TextView) this.findViewById(R.id.rebacktext);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publis_comment_back:
                finish();
                break;
            case R.id.publish_comment_run:
                //发布按钮
                cont = et_sendmessage.getText().toString();//评论内容
                if (StringUtil.isNullOrEmpty(cont)) {
                    Toast.makeText(this, R.string.commetn_isnull, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(cont.length() > 1000){
                    Toast.makeText(this, R.string.pk_cont_length, Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog = new CustomProgressDialog(PkCommentActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                publish_comment();
                break;
            case R.id.send_btn_face_normal:
                // 显示表情选择框
                send_btn_face_normal.setVisibility(View.INVISIBLE);
                send_btn_face_enable.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_sendmessage.getWindowToken(), 0); //强制隐藏键盘
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

    //开始发布
    private void publish_comment() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.PK_PUBLISH_COMMENT_RECORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                Toast.makeText(PkCommentActivity.this, R.string.comment_success, Toast.LENGTH_SHORT).show();
                                //调用广播，刷新详细页评论
                                Intent intent1 = new Intent(Constants.PK_SEND_COMMENT_SUCCESS);
                                intent1.putExtra("pkId", record_uuid);
                                sendBroadcast(intent1);
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
                        Toast.makeText(PkCommentActivity.this, R.string.comment_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("zpId", record_uuid);
                params.put("empId", emp_id);
                params.put("commentCont", cont);
                params.put("fPlid", father_uuid);
                params.put("fplempid", fplempid);//父评论者id
                params.put("sendEmpId", sendEmpId);//作品所有人的id
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
        layout_point = (LinearLayout) findViewById(R.id.iv_image);
        send_btn_face_normal = (ImageButton) this.findViewById(R.id.send_btn_face_normal);
        send_btn_face_normal.setOnClickListener(this);
        send_btn_face_enable = (ImageButton) this.findViewById(R.id.send_btn_face_enable);
        send_btn_face_enable.setOnClickListener(this);
        view = findViewById(R.id.ll_facechoose);

        et_sendmessage.setOnClickListener(this);

    }

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
            FaceAdapter adapter = new FaceAdapter(PkCommentActivity.this, emojis.get(i));
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
}

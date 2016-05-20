package com.liangxun.yuejiula.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.widget.ImageView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.AboutViewPageAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AboutActivity extends BaseActivity {
    private static final int PICTURE_COUNT = 4;

    private static final int[] PICTURE_RESOURCES = {R.drawable.guide_page,
            R.drawable.guide_page1, R.drawable.guide_page2,
            R.drawable.guide_page3,};

    private static final String[] PICTURE_TITLE = {"第一张图片", "第二张图片", "第三张图片",
            "这是最后一页",};
    private JSONArray jsonArray;
    private ViewPager viewPager;
    private AboutViewPageAdapter adapter;
    private ImageView[] circles = new ImageView[PICTURE_RESOURCES.length];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_viewpage);
        initLoadData();
        initView();
    }

    private void initLoadData() {
        jsonArray = new JSONArray();
        for (int i = 0; i < PICTURE_COUNT; i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("resourceId", PICTURE_RESOURCES[i]);
                jsonObject.put("title", PICTURE_TITLE[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewpage);
        adapter = new AboutViewPageAdapter(this, jsonArray);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }
            @Override
            public void onPageSelected(int position) {
                for (int j = 0; j < circles.length; j++) {
                }
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
}

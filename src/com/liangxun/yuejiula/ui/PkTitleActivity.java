package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
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
import com.liangxun.yuejiula.data.PkPrizesData;
import com.liangxun.yuejiula.data.PkThemeDATA;
import com.liangxun.yuejiula.entity.PKTheme;
import com.liangxun.yuejiula.entity.PkPrize;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;
import com.yixia.camera.demo.ui.record.MediaRecorderActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/9.
 */
public class PkTitleActivity extends BaseActivity implements View.OnClickListener {
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private ImageView pk_title_back;
    private TextView pk_title_add_one;
    private TextView pk_title_title;//主题
    private TextView pk_title_mudi;//目的
    private TextView pk_title_time;//时间
    private TextView pk_title_cont;//前言
    private ImageView pk_title_pic;//主题图片
    private TextView pk_title_prize_textone;//总舵奖品说明
    private ImageView pk_title_prize_imgone;//总舵奖品图片
    private TextView pk_title_prize_texttwo;//学校奖品说明
    private ImageView pk_title_prize_imgtwo;//学校奖品图片
    private PKTheme pkTheme;//主题
    private Button mine_prize;//我是代理商 我来设置奖品
    private View mine_prize_line;//代理商上面的横线
    List<PkPrize> pkPrizes = new ArrayList<PkPrize>();
    private String emp_id = "";
    private String typeId = "";
    private String schoolId = "";
    private LinearLayout prize_all;
    private PkPrize pkPrizeZ = new PkPrize();//总舵设置的奖品
    private PkPrize pkPrizeSchool = new PkPrize();//学校设置的奖品
//    private TextView pk_title_prize_texttwo_title;
private LinearLayout pk_title_prize_texttwo_title;
    private LinearLayout searchnull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pk_title);
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        typeId = getGson().fromJson(getSp().getString(Constants.EMPTYPE, ""), String.class);
        initView();
        getData();
    }

    private void initView() {
        pk_title_back = (ImageView) this.findViewById(R.id.pk_title_back);
        pk_title_add_one = (TextView) this.findViewById(R.id.pk_title_add_one);
        pk_title_title = (TextView) this.findViewById(R.id.pk_title_title);
        pk_title_mudi = (TextView) this.findViewById(R.id.pk_title_mudi);
        pk_title_time = (TextView) this.findViewById(R.id.pk_title_time);
        pk_title_pic = (ImageView) this.findViewById(R.id.pk_title_pic);
        pk_title_cont = (TextView) this.findViewById(R.id.pk_title_cont);
        pk_title_prize_textone = (TextView) this.findViewById(R.id.pk_title_prize_textone);
        pk_title_prize_imgone = (ImageView) this.findViewById(R.id.pk_title_prize_imgone);
        pk_title_prize_texttwo = (TextView) this.findViewById(R.id.pk_title_prize_texttwo);
        pk_title_prize_texttwo_title = (LinearLayout) this.findViewById(R.id.pk_title_prize_texttwo_title);
        pk_title_prize_imgtwo = (ImageView) this.findViewById(R.id.pk_title_prize_imgtwo);
        mine_prize = (Button) this.findViewById(R.id.mine_prize);
        mine_prize_line = this.findViewById(R.id.mine_prize_line);
        prize_all = (LinearLayout) this.findViewById(R.id.prize_all);

        searchnull = (LinearLayout) this.findViewById(R.id.searchnull);
        prize_all.setVisibility(View.GONE);

        if (typeId.equals("3")) {
            mine_prize_line.setVisibility(View.VISIBLE);
            mine_prize.setVisibility(View.VISIBLE);
        }
        pk_title_back.setOnClickListener(this);
        pk_title_add_one.setOnClickListener(this);
        mine_prize.setOnClickListener(this);
        pk_title_prize_imgone.setOnClickListener(this);
        pk_title_prize_imgtwo.setOnClickListener(this);
        pk_title_pic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pk_title_add_one:
                //我要参赛
                if (pkTheme != null) {
                    if (pkTheme.getType().equals("0")) {
                        Intent addView = new Intent(PkTitleActivity.this, PkAddActivity.class);
                        addView.putExtra(Constants.PK_ADD_ZUOPIN_TYPE, "0");
                        addView.putExtra(Constants.PK_ADD_ZUOPIN_THEME, pkTheme.getId());
                        startActivity(addView);
                        finish();
                    }
                    if (pkTheme.getType().equals("1")) {
                        Intent addView = new Intent(PkTitleActivity.this, PkAddActivity.class);
                        addView.putExtra(Constants.PK_ADD_ZUOPIN_TYPE, "1");
                        addView.putExtra(Constants.PK_ADD_ZUOPIN_THEME, pkTheme.getId());
                        startActivity(addView);
                        finish();
                    }
                    if (pkTheme.getType().equals("2")) {
                        save(Constants.PK_ADD_VIDEO_TYPE, "1");
                        save(Constants.PK_ADD_ZUOPIN_THEME, pkTheme.getId());
                        Intent intent = new Intent(PkTitleActivity.this, MediaRecorderActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                    }
                } else {
                    Toast.makeText(PkTitleActivity.this, R.string.pk_error_title, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.pk_title_back:
                finish();
                break;
            case R.id.mine_prize:
                if(pkTheme != null){
                    Intent addPrizeView = new Intent(PkTitleActivity.this, AddMinePrizeActivity.class);
                    addPrizeView.putExtra(Constants.PK_ADD_PRIZE_THEME, pkTheme.getId());
                    startActivity(addPrizeView);
                }
                break;
            case R.id.pk_title_pic:
                if(pkTheme != null){
                    final String[] pk_title_pic = {pkTheme.getPicUrl()};
                    Intent intent = new Intent(PkTitleActivity.this, GalleryUrlActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    intent.putExtra(Constants.IMAGE_URLS, pk_title_pic);
                    intent.putExtra(Constants.IMAGE_POSITION, 0);
                    startActivity(intent);
                }
                break;
            case R.id.pk_title_prize_imgone:
                if(pkPrizeZ != null){
                    final String[] pk_title_prize_imgone = {pkPrizeZ.getPic()};
                    Intent intent_one = new Intent(PkTitleActivity.this, GalleryUrlActivity.class);
                    intent_one.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    intent_one.putExtra(Constants.IMAGE_URLS, pk_title_prize_imgone);
                    intent_one.putExtra(Constants.IMAGE_POSITION, 0);
                    startActivity(intent_one);
                }
                break;
            case R.id.pk_title_prize_imgtwo:
                if(pkPrizeSchool != null){
                    final String[] pk_title_prize_imgtwo = {pkPrizeSchool.getPic()};
                    Intent intent_two = new Intent(PkTitleActivity.this, GalleryUrlActivity.class);
                    intent_two.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    intent_two.putExtra(Constants.IMAGE_URLS, pk_title_prize_imgtwo);
                    intent_two.putExtra(Constants.IMAGE_POSITION, 0);
                    startActivity(intent_two);
                }
                break;
        }
    }

    //查询数据
    private void getData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.PK_GET_TITLE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(        s)) {
                            PkThemeDATA data = getGson().fromJson(s, PkThemeDATA.class);
                            if (data.getCode() == 200) {
                                if (data.getData() != null) {
                                    pkTheme = data.getData();
                                    initData();
                                    searchnull.setVisibility(View.GONE);
                                } else {
                                    searchnull.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(PkTitleActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PkTitleActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(PkTitleActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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

    public void initData() {
        pk_title_title.setText(pkTheme.getTitle());
        pk_title_mudi.setText(pkTheme.getMudi());
        pk_title_time.setText(pkTheme.getStartTime() + "--" + pkTheme.getEndTime());
        pk_title_cont.setText(pkTheme.getContent());
        //查询奖品
        getPrize(pkTheme.getId());
        imageLoader.displayImage(pkTheme.getPicUrl(), pk_title_pic, UniversityApplication.tpOptions, animateFirstListener);
    }

    //查询奖品
    private void getPrize(final String themid) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.PK_GET_PRIZES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            PkPrizesData data = getGson().fromJson(s, PkPrizesData.class);
                            if (data.getCode() == 200) {
                                pkPrizes = data.getData();
                                initDataTwo();
                            } else {
                                Toast.makeText(PkTitleActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PkTitleActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(PkTitleActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("themeId", themid);
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

    public void initDataTwo() {

        if (pkPrizes != null) {
            for (int i = 0; i < pkPrizes.size(); i++) {
                if ("0".equals(pkPrizes.get(i).getType())) {
                    pkPrizeZ = pkPrizes.get(i);//是总舵的奖品
                }
                if ("1".equals(pkPrizes.get(i).getType())) {
                    pkPrizeSchool = pkPrizes.get(i);
                }
            }
        }
        if (pkPrizeZ != null) {
            prize_all.setVisibility(View.VISIBLE);
            pk_title_prize_textone.setText(pkPrizeZ.getContent());
            imageLoader.displayImage(pkPrizeZ.getPic(), pk_title_prize_imgone, UniversityApplication.tpOptions, animateFirstListener);
        }
        if (pkPrizeSchool != null && !StringUtil.isNullOrEmpty(pkPrizeSchool.getId())) {
            pk_title_prize_texttwo.setVisibility(View.VISIBLE);
            pk_title_prize_texttwo.setText(pkPrizeSchool.getContent());
            imageLoader.displayImage(pkPrizeSchool.getPic(), pk_title_prize_imgtwo, UniversityApplication.tpOptions, animateFirstListener);
        }
    }
}

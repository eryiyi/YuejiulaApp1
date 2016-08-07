package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
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
import com.liangxun.yuejiula.data.PkThemeDATA;
import com.liangxun.yuejiula.entity.PKTheme;
import com.liangxun.yuejiula.fragment.PkNewFragment;
import com.liangxun.yuejiula.fragment.PkTopFragment;
import com.liangxun.yuejiula.fragment.PkWinnerFragment;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.popview.MainPopMenu;
import com.liangxun.yuejiula.widget.popview.PkFootPopWindow;
import com.yixia.camera.demo.ui.record.MediaRecorderActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/4.
 * pk的主页
 */
public class PkActivity extends BaseActivity implements View.OnClickListener, MainPopMenu.OnItemClickListener {
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;
    private PkTopFragment pkTopFragment;
    private PkNewFragment pkNewFragment;
    private PkWinnerFragment pkWinnerFragment;
    private TextView zuixin;
    private TextView paihang;
    private TextView guanjun;
    public MainPopMenu mainPopMenu;
    private PkFootPopWindow deleteWindow;
    private ImageView menu;

    public static PKTheme pkTheme;//主题

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pk);
        fm = getSupportFragmentManager();
        initView();
        getDataTheme();
        switchFragment(R.id.zuixin);
    }

    private void initView() {
        zuixin = (TextView) findViewById(R.id.zuixin);
        paihang = (TextView) findViewById(R.id.paihang);
        guanjun = (TextView) findViewById(R.id.guanjun);
        zuixin.setOnClickListener(this);
        paihang.setOnClickListener(this);
        guanjun.setOnClickListener(this);
        mainPopMenu = new MainPopMenu(this);
        mainPopMenu.setOnItemClickListener(this);
        menu = (ImageView) this.findViewById(R.id.menu);
        menu.setOnClickListener(this);
    }

    public void switchFragment(int id) {
        fragmentTransaction = fm.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (id) {
            case R.id.zuixin:
                if (pkNewFragment == null) {
                    pkNewFragment = new PkNewFragment();
                    fragmentTransaction.add(R.id.content_frame, pkNewFragment);
                } else {
                    fragmentTransaction.show(pkNewFragment);
                }
                zuixin.setTextColor(getResources().getColor(R.color.nickname_color));
                paihang.setTextColor(getResources().getColor(R.color.title_font_main));
                guanjun.setTextColor(getResources().getColor(R.color.title_font_main));

                break;
            case R.id.paihang:
                if (pkTopFragment == null) {
                    pkTopFragment = new PkTopFragment();
                    fragmentTransaction.add(R.id.content_frame, pkTopFragment);
                } else {
                    fragmentTransaction.show(pkTopFragment);
                }
                zuixin.setTextColor(getResources().getColor(R.color.title_font_main));
                paihang.setTextColor(getResources().getColor(R.color.nickname_color));
                guanjun.setTextColor(getResources().getColor(R.color.title_font_main));
                break;
            case R.id.guanjun:
                if (pkWinnerFragment == null) {
                    pkWinnerFragment = new PkWinnerFragment();
                    fragmentTransaction.add(R.id.content_frame, pkWinnerFragment);
                } else {
                    fragmentTransaction.show(pkWinnerFragment);
                }
                zuixin.setTextColor(getResources().getColor(R.color.title_font_main));
                paihang.setTextColor(getResources().getColor(R.color.title_font_main));
                guanjun.setTextColor(getResources().getColor(R.color.nickname_color));
                break;
        }
        fragmentTransaction.commit();
    }

    private void hideFragments(FragmentTransaction ft) {
        if (pkNewFragment != null) {
            ft.hide(pkNewFragment);
        }
        if (pkTopFragment != null) {
            ft.hide(pkTopFragment);
        }
        if (pkWinnerFragment != null) {
            ft.hide(pkWinnerFragment);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() ==  R.id.menu){
            finish();
            return;
        }else {
            switchFragment(v.getId());
        }
    }

    private void showSelectImageDialog() {
        deleteWindow = new PkFootPopWindow(PkActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(PkActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.pk_foot_all: {
                    //全部的
                    save(Constants.PK_SEARCH_ALL_OR_MINE, Constants.MOOD_TYPE);
                    //调用广播，刷新主页
                    Intent intent = new Intent(Constants.PK_SEARCH_ALL_OR_MINE);
                    intent.putExtra(Constants.PK_SWITCH, Constants.MOOD_TYPE);
                    sendBroadcast(intent);
                }
                break;
                case R.id.pk_foot_mine: {
                    //我的
                    save(Constants.PK_SEARCH_ALL_OR_MINE, Constants.RECORD_TYPE);
                    //调用广播，刷新主页
                    Intent intent = new Intent(Constants.PK_SEARCH_ALL_OR_MINE);
                    intent.putExtra(Constants.PK_SWITCH, Constants.RECORD_TYPE);
                    sendBroadcast(intent);
                }
                break;
                default:
                    break;
            }
        }

    };

    @Override
    public void onItemClick(int index) {
        switch (index) {
            case 0://本期主题
                initData();
                break;
            case 1://规则
//                Intent titleView = new Intent(PkActivity.this, PkTitleActivity.class);
//                startActivity(titleView);
                Intent titleView = new Intent(PkActivity.this, WebViewActivity.class);
                titleView.putExtra("strurl", InternetURL.GET_THEME_URL + "?schoolId="+ getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class));
                startActivity(titleView);
                break;
            case 2://往期Pk
                Intent wq = new Intent(PkActivity.this, PkWangqiActivity.class);
                startActivity(wq);
                break;
            case 3:
                finish();
                break;
        }
    }

    //弹出顶部主菜单
    public void onTopMenuPopupButtonClick(View view) {
        mainPopMenu.showAsDropDown(view);
    }

    //底部菜单
    public void onFootMenuPopupButtonClick(View view) {
        showSelectImageDialog();
    }


    //查询数据--主题
    private void getDataTheme() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) +  InternetURL.PK_GET_TITLE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            PkThemeDATA data = getGson().fromJson(s, PkThemeDATA.class);
                            if (data.getCode() == 200) {
                                if (data.getData() != null) {
                                    pkTheme = data.getData();
                                }
                            } else {
                                Toast.makeText(PkActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PkActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(PkActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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

    void initData(){
        if (pkTheme != null) {
            if (pkTheme.getType().equals("0")) {
                Intent addView = new Intent(PkActivity.this, PkAddActivity.class);
                addView.putExtra(Constants.PK_ADD_ZUOPIN_TYPE, "0");
                addView.putExtra(Constants.PK_ADD_ZUOPIN_THEME, pkTheme.getId());
                startActivity(addView);
            }
            if (pkTheme.getType().equals("1")) {
                Intent addView = new Intent(PkActivity.this, PkAddActivity.class);
                addView.putExtra(Constants.PK_ADD_ZUOPIN_TYPE, "1");
                addView.putExtra(Constants.PK_ADD_ZUOPIN_THEME, pkTheme.getId());
                startActivity(addView);
            }
            if (pkTheme.getType().equals("2")) {
                save(Constants.PK_ADD_VIDEO_TYPE, "1");
                save(Constants.PK_ADD_ZUOPIN_THEME, pkTheme.getId());
                Intent intent = new Intent(PkActivity.this, MediaRecorderActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
            }
        } else {
            Toast.makeText(PkActivity.this, R.string.pk_error_title, Toast.LENGTH_SHORT).show();
        }
    }


}



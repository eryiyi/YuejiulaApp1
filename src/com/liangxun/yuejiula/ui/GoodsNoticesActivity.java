package com.liangxun.yuejiula.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;

/**
 * Created by Administrator on 2015/3/30.
 */
public class GoodsNoticesActivity extends BaseActivity implements View.OnClickListener {
    private ImageView goods_notice_menu;//fanhui

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_notice);
        goods_notice_menu = (ImageView) this.findViewById(R.id.goods_notice_menu);
        goods_notice_menu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goods_notice_menu:
                finish();
                break;
        }
    }
}

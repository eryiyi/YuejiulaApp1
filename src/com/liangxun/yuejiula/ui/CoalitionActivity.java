package com.liangxun.yuejiula.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;

/**
 * Created by Administrator on 2015/5/1.
 */
public class CoalitionActivity extends BaseActivity implements View.OnClickListener {
    private ImageView set_back;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paopao_coalition);
        set_back= (ImageView) findViewById(R.id.set_back);
        set_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        finish();
    }


}
package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;

/**
 * Created by Administrator on 2015/5/7.
 */
public class ExplainActivity extends BaseActivity implements View.OnClickListener {
    private TextView button;
    private TextView jinbitime;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explain);
        initView();
        String time = getIntent().getExtras().getString("time");
        jinbitime.setText("您被关了禁闭"+time+"天");
    }

    private void initView() {
        button= (TextView) findViewById(R.id.button);
        jinbitime= (TextView) findViewById(R.id.jinbitime);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent(ExplainActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}
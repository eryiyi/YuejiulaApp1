package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;

/**
 * Created by Administrator on 2015/3/30.
 * 帮助  代理商列表没有学校管理人  来这儿联系我们约酒啦江湖总舵
 */
public class HelpOneActivity extends BaseActivity implements View.OnClickListener {
    private TextView button_help;//联系总舵
    private ImageView button_back;//返回按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_one_xml);
        button_back = (ImageView) this.findViewById(R.id.button_back);
        button_help = (TextView) this.findViewById(R.id.button_help);
        button_help.setOnClickListener(this);
        button_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_help:
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:0543-5558888"));
                HelpOneActivity.this.startActivity(intent);
                break;
            case R.id.button_back:
                finish();
                break;
        }
    }
}

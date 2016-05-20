package com.liangxun.yuejiula.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/1
 * Time: 10:17
 * 类的功能、说明写在此处.
 */
public class AboutUsActivity extends BaseActivity implements View.OnClickListener {
    private ImageView aboutus_menu;//关于我们
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutus);
        aboutus_menu = (ImageView) this.findViewById(R.id.aboutus_menu);
        aboutus_menu.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aboutus_menu:
                finish();
                break;
        }
    }
}

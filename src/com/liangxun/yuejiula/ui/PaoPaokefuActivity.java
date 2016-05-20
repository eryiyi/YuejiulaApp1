package com.liangxun.yuejiula.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;

/**
 * Created by Administrator on 2015/10/17.
 */
public class PaoPaokefuActivity extends BaseActivity implements View.OnClickListener {
    private ListView lstv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_kefu_activity);

        lstv = (ListView) this.findViewById(R.id.lstv);
    }

    @Override
    public void onClick(View view) {

    }
    public void back(View view){
        finish();
    }

}

package com.liangxun.yuejiula.ui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;

/**
 * Created by Administrator on 2015/5/1.
 */
public class InstructionActivity extends BaseActivity implements View.OnClickListener {
    private TextView coalition;
    private TextView user_protocol;
    private ImageView set_back;
    private TextView banben;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instruction);
        initView();
    }

    private void initView() {
        coalition = (TextView) findViewById(R.id.coalition);
        banben= (TextView) findViewById(R.id.banben);
        user_protocol = (TextView) findViewById(R.id.user_protocol);
        set_back = (ImageView)findViewById(R.id.set_back);
        coalition.setOnClickListener(this);
        set_back.setOnClickListener(this);
        user_protocol.setOnClickListener(this);
        coalition.setOnClickListener(this);
        banben.setText(getVersion());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.coalition:
//                Intent Coalition = new Intent(InstructionActivity.this, CoalitionActivity.class);
//                startActivity(Coalition);
                break;
            case R.id.user_protocol:
//                Intent User = new Intent(InstructionActivity.this, UserProtocolActivity.class);
//                startActivity(User);
                break;
            case R.id.set_back:
                finish();
                break;

        }
    }

    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return this.getString(R.string.version_name) + version;
        } catch (Exception e) {
            e.printStackTrace();
            return this.getString(R.string.can_not_find_version_name);
        }
    }
}
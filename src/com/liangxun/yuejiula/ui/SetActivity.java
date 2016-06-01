package com.liangxun.yuejiula.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.easemob.EMCallBack;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.ActivityTack;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.huanxin.chat.activity.HxSetActivity;
import com.liangxun.yuejiula.util.Constants;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/8
 * Time: 13:25
 * 类的功能、说明写在此处.
 */
public class SetActivity extends BaseActivity implements View.OnClickListener {
    private Button set_quit;//退出
    private ImageView set_back;//设置

    private LinearLayout setzh;//设置账号
    private LinearLayout setpass;//设置密码
    private LinearLayout update;//检查更新
    private LinearLayout chat_set;//聊天设置
    private LinearLayout find_aboutus;//关于我们

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_xml);
        initView();
    }

    private void initView() {
        set_back = (ImageView) this.findViewById(R.id.set_back);
        set_back.setOnClickListener(this);
        set_quit = (Button) this.findViewById(R.id.set_quit);
        set_quit.setOnClickListener(this);
        setzh = (LinearLayout) this.findViewById(R.id.setzh);
        setzh.setOnClickListener(this);
        setpass = (LinearLayout) this.findViewById(R.id.setpass);
        setpass.setOnClickListener(this);
        update = (LinearLayout) this.findViewById(R.id.update);
        update.setOnClickListener(this);
        chat_set = (LinearLayout) this.findViewById(R.id.chat_set);
        chat_set.setOnClickListener(this);
        find_aboutus = (LinearLayout) this.findViewById(R.id.find_aboutus);
        find_aboutus.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back://返回按钮
                finish();
                break;
            case R.id.set_quit://退出
                logout();
                break;
            case R.id.setpass://设置密码
                Intent pwr = new Intent(this, ReSetPwrActivity.class);
                startActivity(pwr);
                break;
            case R.id.setzh://账号
            {
               Intent intent = new Intent(SetActivity.this, ReSetMobileActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.update://更新
                progressDialog = new ProgressDialog(SetActivity.this );

                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                UmengUpdateAgent.forceUpdate(this);
                UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
                    @Override
                    public void onUpdateReturned(int i, UpdateResponse updateResponse) {
                        progressDialog.dismiss();
                        switch (i) {
                            case UpdateStatus.Yes:
                                break;
                            case UpdateStatus.No:
                                Toast.makeText(SetActivity.this, "已是最新版本", Toast.LENGTH_SHORT).show();
                                break;
                            case UpdateStatus.Timeout:
                                Toast.makeText(SetActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
                break;
            case R.id.chat_set:
                Intent chat_set = new Intent(SetActivity.this, HxSetActivity.class);
                startActivity(chat_set);
                break;
            case R.id.find_aboutus:
                Intent about = new Intent(SetActivity.this, InstructionActivity.class);
                startActivity(about);
                break;
        }
    }


    void logout() {
        AlertDialog dialog = new AlertDialog.Builder(SetActivity.this)
                .setIcon(R.drawable.quiticon)
                .setTitle("确定退出童心堂？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog pd = new ProgressDialog(SetActivity.this);
                        String st = getResources().getString(R.string.Are_logged_out);
                        pd.setMessage(st);
                        pd.setCanceledOnTouchOutside(false);
                        //  pd.show();
                        getMyApp().logout(false,new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        pd.dismiss();
                                        save(Constants.EMPPASS, "");
                                        ActivityTack.getInstanse().exit(SetActivity.this);
                                        finish();

                                    }
                                });
                            }

                            @Override
                            public void onProgress(int progress, String status) {

                            }

                            @Override
                            public void onError(int code, String message) {

                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        dialog.show();
    }

}

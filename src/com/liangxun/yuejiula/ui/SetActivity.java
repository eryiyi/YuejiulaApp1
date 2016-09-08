package com.liangxun.yuejiula.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.easemob.EMCallBack;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.ActivityTack;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.huanxin.chat.activity.HxSetActivity;
import com.liangxun.yuejiula.util.Constants;

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
    private LinearLayout qiehuan_school;//切换圈子

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
        qiehuan_school = (LinearLayout) this.findViewById(R.id.qiehuan_school);
        update.setOnClickListener(this);
        chat_set = (LinearLayout) this.findViewById(R.id.chat_set);
        chat_set.setOnClickListener(this);
        find_aboutus = (LinearLayout) this.findViewById(R.id.find_aboutus);
        find_aboutus.setOnClickListener(this);
        qiehuan_school.setOnClickListener(this);
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
//                progressDialog = new ProgressDialog(SetActivity.this );
//
//                progressDialog.setCancelable(false);
//                progressDialog.setIndeterminate(true);
//                progressDialog.show();
//                UmengUpdateAgent.forceUpdate(this);
//                UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
//                    @Override
//                    public void onUpdateReturned(int i, UpdateResponse updateResponse) {
//                        progressDialog.dismiss();
//                        switch (i) {
//                            case UpdateStatus.Yes:
//                                break;
//                            case UpdateStatus.No:
//                                Toast.makeText(SetActivity.this, "已是最新版本", Toast.LENGTH_SHORT).show();
//                                break;
//                            case UpdateStatus.Timeout:
//                                Toast.makeText(SetActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
//                                break;
//                        }
//                    }
//                });
                break;
            case R.id.chat_set:
                Intent chat_set = new Intent(SetActivity.this, HxSetActivity.class);
                startActivity(chat_set);
                break;
            case R.id.find_aboutus:
                Intent about = new Intent(SetActivity.this, InstructionActivity.class);
                startActivity(about);
                break;
            case R.id.qiehuan_school:
            {
                //切换圈子
                showMsg();
            }
                break;
        }
    }


    private void showMsg() {
        final Dialog picAddDialog = new Dialog(SetActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(this, R.layout.msg_mine_dialog, null);
        TextView jubao_sure = (TextView) picAddInflate.findViewById(R.id.jubao_sure);
        final TextView jubao_cont = (TextView) picAddInflate.findViewById(R.id.content);
        jubao_cont.setText("换个兴趣圈子逛逛？");
        //举报提交
        jubao_sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent selectV = new Intent(SetActivity.this, SelectProvinceActivity.class);
                startActivity(selectV);
                picAddDialog.dismiss();
            }
        });

        TextView jubao_cancle = (TextView) picAddInflate.findViewById(R.id.jubao_cancle);
        jubao_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picAddDialog.dismiss();
            }
        });
        picAddDialog.setContentView(picAddInflate);
        picAddDialog.show();
    }


    void logout() {
        AlertDialog dialog = new AlertDialog.Builder(SetActivity.this)
                .setIcon(R.drawable.quiticon)
                .setTitle("确定退出童心堂？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog pd = new ProgressDialog(SetActivity.this);
                        final String st = getResources().getString(R.string.Are_logged_out);
                        pd.setMessage(st);
                        pd.setCanceledOnTouchOutside(false);
                        //  pd.show();
                        getMyApp().logout(false, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        pd.dismiss();
                                        save(Constants.EMPPASS, "");
                                        ActivityTack.getInstanse().exit(SetActivity.this);
                                        finish();
//                                        ActivityTack.getInstanse().popUntilActivity(LoginActivity.class);
//                                        Intent intent = new Intent(SetActivity.this, LoginActivity.class);
//                                        startActivity(intent);

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

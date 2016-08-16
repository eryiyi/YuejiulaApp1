/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liangxun.yuejiula.huanxin.chat.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.easemob.chat.EMContactManager;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.widget.CustomProgressDialog;


public class HxAlertYanzheng extends BaseActivity {
    private EditText editText;

    private String str;
    private String hxUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_yanzheng);
        editText = (EditText) findViewById(R.id.edit);
        editText.setText("我是"+(getGson().fromJson(getSp().getString(Constants.EMPNAME, ""), String.class)));
        hxUserName = getIntent().getStringExtra("hxUserName");
    }

    public void ok(View view) {
        str = editText.getText().toString();
        String stri = getResources().getString(R.string.Is_sending_a_request);
        if (TextUtils.isEmpty(str.trim())) {
            Toast.makeText(this, "请输入验证信息", Toast.LENGTH_LONG).show();
            return;
        } else {
            progressDialog =  new CustomProgressDialog(HxAlertYanzheng.this, "正在加载中",R.anim.custom_dialog_frame);
            progressDialog.setCancelable(true);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            progressDialog.setMessage(stri);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            new Thread(new Runnable() {
                public void run() {
                    try {
                        EMContactManager.getInstance().addContact(hxUserName, str);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                String s1 = getResources().getString(R.string.send_successful);
                                Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (final Exception e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                                Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }).start();
        }
        finish();

    }

    public void cancel(View view) {
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }


}

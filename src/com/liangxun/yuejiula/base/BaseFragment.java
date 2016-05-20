package com.liangxun.yuejiula.base;

import android.app.ProgressDialog;
import android.os.Bundle;

/**
 * Created by zhanghl on 2015/1/17.
 */
public class BaseFragment extends MyBaseFragment {
    public ProgressDialog progressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}

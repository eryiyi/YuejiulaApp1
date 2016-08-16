package com.liangxun.yuejiula.base;

import android.os.Bundle;
import com.liangxun.yuejiula.widget.CustomProgressDialog;

/**
 * Created by zhanghl on 2015/1/17.
 */
public class BaseFragment extends MyBaseFragment {
    public CustomProgressDialog progressDialog;
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

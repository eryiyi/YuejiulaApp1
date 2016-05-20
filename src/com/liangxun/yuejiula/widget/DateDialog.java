package com.liangxun.yuejiula.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import com.liangxun.yuejiula.R;

/**
 * Created by zhanghl on 2014/12/17.
 */
public class DateDialog extends Dialog implements View.OnClickListener {
    private DatePicker datePicker;
    private Button close;
    private Button sure;

    private Context context;
    private boolean isStart;//是否为开始时间

    private DateBackListener dateBackListener;

    public void setDateBackListener(DateBackListener dateBackListener) {
        this.dateBackListener = dateBackListener;
    }

    public DateDialog(Context context, int theme, boolean isStart) {
        super(context, theme);
        this.context = context;
        this.isStart = isStart;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_layout);
        initView();
    }

    private void initView() {
        datePicker = (DatePicker) findViewById(R.id.date_picker);
        close = (Button) this.findViewById(R.id.close);
        sure = (Button) this.findViewById(R.id.sure);

        close.setOnClickListener(this);
        sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure:
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                String dateStr = year + "-";
                if (month < 9) {
                    dateStr += "0" + (month + 1) + "-";
                } else {
                    dateStr += month + 1 + "-";
                }
                if (day < 10) {
                    dateStr += "0" + day;
                } else {
                    dateStr += day;
                }
                dateBackListener.backTime(dateStr, isStart);
                DateDialog.this.dismiss();
                break;
            case R.id.close:
                DateDialog.this.dismiss();
                break;
        }
    }
}

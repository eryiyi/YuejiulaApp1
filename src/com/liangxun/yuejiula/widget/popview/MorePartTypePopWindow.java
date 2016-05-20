package com.liangxun.yuejiula.widget.popview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.adapter.PartTimeTypeAdapter;
import com.liangxun.yuejiula.entity.PartTimeType;
import com.liangxun.yuejiula.widget.ClassifyGridview;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/3/19
 * Time: 20:58
 * 类的功能、说明写在此处.
 */
public class MorePartTypePopWindow extends PopupWindow {
    private ImageView cancel;
    private View mMenuView;
    private ClassifyGridview parttimetyupeGridview;//定义一个gridview
    private List<PartTimeType> mgoodstypeList;

    private PartTimeTypeAdapter adapter;

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public MorePartTypePopWindow(Activity context, List<PartTimeType> goodstypeList) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mgoodstypeList = goodstypeList;
        mMenuView = inflater.inflate(R.layout.item_dialog_more_part_type, null);
        parttimetyupeGridview = (ClassifyGridview) mMenuView.findViewById(R.id.moreparttimetyupeGridview);
        parttimetyupeGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onClickContentItemListener.onClickContentItem(position, 1, null);
            }
        });
        adapter = new PartTimeTypeAdapter(mgoodstypeList, context);
        parttimetyupeGridview.setAdapter(adapter);
        parttimetyupeGridview.setSelector(new ColorDrawable(Color.TRANSPARENT));

        cancel = (ImageView) mMenuView.findViewById(R.id.cancel);

        //取消按钮
        cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //销毁弹出框
                dismiss();
            }
        });

        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

}
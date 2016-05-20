package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.SellerSchoolList;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/6
 * Time: 14:06
 * 类的功能、说明写在此处.
 */
public class MineShangjiaColleageAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<SellerSchoolList> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public MineShangjiaColleageAdapter(List<SellerSchoolList> findEmps, Context mContext) {
        this.findEmps = findEmps;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return findEmps.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_shangjia_colleage, null);
            holder.item_nickname_shangjia = (TextView) convertView.findViewById(R.id.item_nickname_shangjia);
            holder.item_dateline_shangjia = (TextView) convertView.findViewById(R.id.item_dateline_shangjia);
            holder.item_xufei = (TextView) convertView.findViewById(R.id.item_xufei);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final SellerSchoolList favour = findEmps.get(position);
        if (findEmps != null) {
            holder.item_nickname_shangjia.setText(favour.getSchoolName());
            holder.item_dateline_shangjia.setText(favour.getStartTime() + " 至 " + favour.getEndTime());
            //评论
            holder.item_xufei.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickContentItemListener.onClickContentItem(position, 1, null);
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        TextView item_nickname_shangjia;
        TextView item_dateline_shangjia;
        TextView item_xufei;
    }

}
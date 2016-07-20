package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.Emp;
import com.liangxun.yuejiula.entity.Favour;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/6
 * Time: 14:06
 * 类的功能、说明写在此处.
 */
public class BlackListAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<Emp> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public BlackListAdapter(List<Emp> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_black, null);
            holder.detail_favour_item_cover = (ImageView) convertView.findViewById(R.id.detail_favour_item_cover);
            holder.detail_favour_item_nickname = (TextView) convertView.findViewById(R.id.detail_favour_item_nickname);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Emp favour = findEmps.get(position);
        if (findEmps != null) {
            imageLoader.displayImage(favour.getEmpCover(), holder.detail_favour_item_cover, UniversityApplication.txOptions, animateFirstListener);
            holder.detail_favour_item_nickname.setText(favour.getEmpName());
        }

        return convertView;
    }

    class ViewHolder {
        ImageView detail_favour_item_cover;
        TextView detail_favour_item_nickname;
    }

}
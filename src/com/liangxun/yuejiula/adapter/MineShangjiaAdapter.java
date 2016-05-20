package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.SellerGoods;
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
public class MineShangjiaAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<SellerGoods> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public MineShangjiaAdapter(List<SellerGoods> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_shangjia, null);
            holder.item_cover_shangjia = (ImageView) convertView.findViewById(R.id.item_cover_shangjia);
            holder.item_nickname_shangjia = (TextView) convertView.findViewById(R.id.item_nickname_shangjia);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final SellerGoods favour = findEmps.get(position);
        if (findEmps != null) {
            imageLoader.displayImage(favour.getEmpCover(), holder.item_cover_shangjia, UniversityApplication.txOptions, animateFirstListener);
            holder.item_nickname_shangjia.setText(favour.getEmpName());
        }
        holder.item_cover_shangjia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 1, null);
            }
        });
        return convertView;
    }

    class ViewHolder {
        ImageView item_cover_shangjia;
        TextView item_nickname_shangjia;
    }

}
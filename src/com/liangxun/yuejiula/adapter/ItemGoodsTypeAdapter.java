package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.Goodstype;
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
public class ItemGoodsTypeAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<Goodstype> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    public ItemGoodsTypeAdapter(List<Goodstype> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_goods_type_adapter, null);
            holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
            holder.item_pic = (ImageView) convertView.findViewById(R.id.item_pic);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Goodstype favour = findEmps.get(position);
        if (findEmps != null) {
            holder.item_title.setText(favour.getTypeName());
            imageLoader.displayImage(favour.getTypeCover(), holder.item_pic, UniversityApplication.txOptions, animateFirstListener);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView item_pic;
        TextView item_title;
    }

}
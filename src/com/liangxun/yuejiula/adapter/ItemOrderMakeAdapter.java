package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.GoodsCart;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * author: ${zhanghailong}
 * 生成订单
 */
public class ItemOrderMakeAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<GoodsCart> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    Resources res;

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public ItemOrderMakeAdapter(List<GoodsCart> findEmps, Context mContext) {
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
        res = mContext.getResources();
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_order_make, null);
            holder.item_order_pic = (ImageView) convertView.findViewById(R.id.item_order_pic);
            holder.goods_jian = (ImageView) convertView.findViewById(R.id.goods_jian);
            holder.goods_add = (ImageView) convertView.findViewById(R.id.goods_add);
            holder.item_order_title = (TextView) convertView.findViewById(R.id.item_order_title);
            holder.item_order_money = (TextView) convertView.findViewById(R.id.item_order_money);
            holder.item_order_number = (TextView) convertView.findViewById(R.id.item_order_number);
            holder.item_order_number_two = (TextView) convertView.findViewById(R.id.item_order_number_two);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final GoodsCart favour = findEmps.get(position);
        if (findEmps != null) {
            holder.item_order_title.setText(favour.getGoodstitle());
            holder.item_order_money.setText(favour.getGoodsmoney());
            holder.item_order_number.setText(favour.getGoodsnumber());
            holder.item_order_number_two.setText(favour.getGoodsnumber());
            holder.item_order_pic.setImageDrawable(res.getDrawable(favour.getGoodspic()));
        }
        holder.goods_jian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 2, null);
            }
        });
        holder.goods_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 1, null);
            }
        });
        return convertView;
    }

    class ViewHolder {
        ImageView item_order_pic;
        TextView item_order_title;
        TextView item_order_money;
        TextView item_order_number;
        TextView item_order_number_two;
        ImageView goods_jian;
        ImageView goods_add;
    }

}
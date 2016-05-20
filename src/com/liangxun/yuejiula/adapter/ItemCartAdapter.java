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
import com.liangxun.yuejiula.db.ShoppingCart;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/6
 * Time: 14:06
 * 购物车
 */
public class ItemCartAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<ShoppingCart> findEmps;
    private Context mContext;
    private boolean flag;
    Resources res;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public ItemCartAdapter(List<ShoppingCart> findEmps, Context mContext, boolean flag) {
        this.findEmps = findEmps;
        this.mContext = mContext;
        this.flag = flag;
        res = mContext.getResources();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_cart_adapter, null);
            holder.item_cart_select = (ImageView) convertView.findViewById(R.id.item_cart_select);
            holder.item_cart_pic = (ImageView) convertView.findViewById(R.id.item_cart_pic);
            holder.item_cart_add = (ImageView) convertView.findViewById(R.id.item_cart_add);
            holder.item_cart_cover = (ImageView) convertView.findViewById(R.id.item_cart_cover);
            holder.item_cart_jian = (ImageView) convertView.findViewById(R.id.item_cart_jian);
            holder.item_cart_cont = (TextView) convertView.findViewById(R.id.item_cart_cont);
            holder.item_cart_price = (TextView) convertView.findViewById(R.id.item_cart_price);
            holder.item_cart_num = (TextView) convertView.findViewById(R.id.item_cart_num);
            holder.item_cart_nickname = (TextView) convertView.findViewById(R.id.item_cart_nickname);
            holder.item_cart_edit = (ImageView) convertView.findViewById(R.id.item_cart_edit);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ShoppingCart favour = findEmps.get(position);
        String[] arrs = favour.getGoods_cover().split(",");
        if (findEmps != null) {
            imageLoader.displayImage(arrs[0], holder.item_cart_pic, UniversityApplication.txOptions, animateFirstListener);
            imageLoader.displayImage(favour.getEmp_cover(), holder.item_cart_cover, UniversityApplication.txOptions, animateFirstListener);
            holder.item_cart_cont.setText(favour.getGoods_name());
            holder.item_cart_price.setText(res.getString(R.string.prices) + favour.getSell_price());
            holder.item_cart_num.setText(favour.getGoods_count());
            if("0".equals(favour.getIs_select())){
                holder.item_cart_select.setImageResource(R.drawable.cart_selected);
            }else{
                holder.item_cart_select.setImageResource(R.drawable.cart_selectno);
            }
            holder.item_cart_nickname.setText(favour.getEmp_name());
        }
        holder.item_cart_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 1, null);
            }
        });
        holder.item_cart_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 2, null);
            }
        });
        holder.item_cart_jian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 3, null);
            }
        });
        holder.item_cart_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 4, null);
            }
        });
        return convertView;
    }

    class ViewHolder {
        ImageView item_cart_select;
        ImageView item_cart_pic;
        ImageView item_cart_add;
        ImageView item_cart_jian;
        TextView item_cart_cont;
        TextView item_cart_price;
        TextView item_cart_num;

        ImageView item_cart_cover;
        TextView item_cart_nickname;
        ImageView item_cart_edit;
    }

}
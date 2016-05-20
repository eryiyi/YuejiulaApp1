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
import com.liangxun.yuejiula.entity.ShoppingAddress;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/6
 * Time: 14:06
 * 我的收货地址
 */
public class ItemMineAddressAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<ShoppingAddress> lists;
    private Context mContext;
    Resources res;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public ItemMineAddressAdapter(List<ShoppingAddress> lists, Context mContext) {
        this.lists = lists;
        this.mContext = mContext;
        res = mContext.getResources();
    }

    @Override
    public int getCount() {
        return lists.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_address_adapter, null);
            holder.item_nickname = (TextView) convertView.findViewById(R.id.item_nickname);
            holder.item_moren = (TextView) convertView.findViewById(R.id.item_moren);
            holder.item_tel = (TextView) convertView.findViewById(R.id.item_tel);
            holder.item_address = (TextView) convertView.findViewById(R.id.item_address);
            holder.item_in = (ImageView) convertView.findViewById(R.id.item_in);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ShoppingAddress favour = lists.get(position);
        if (favour != null) {
            holder.item_nickname.setText(favour.getAccept_name());
            holder.item_tel.setText(favour.getPhone());
            holder.item_address.setText(favour.getProvinceName()+favour.getCityName() +favour.getAreaName()+ favour.getAddress());
            if (favour.getIs_default().equals("0")){
                //不是默认的，隐藏掉默认标签
               holder.item_moren.setVisibility(View.GONE);
            }else{
                holder.item_moren.setVisibility(View.VISIBLE);
            }

        }

        return convertView;
    }

    class ViewHolder {
        TextView item_nickname;
        TextView item_moren;
        TextView item_tel;
        TextView item_address;
        ImageView item_in;
    }

}
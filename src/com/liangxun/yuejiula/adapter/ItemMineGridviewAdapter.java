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
import com.liangxun.yuejiula.entity.MinePicsObj;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * author: ${zhanghailong}
 * 我的主页功能菜单
 *
 */
public class ItemMineGridviewAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<MinePicsObj> pics;
    private Context mContext;
    Resources res;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public ItemMineGridviewAdapter(List<MinePicsObj> pics, Context mContext) {
        this.pics = pics;
        this.mContext = mContext;
        res = mContext.getResources();
    }

    @Override
    public int getCount() {
        return pics.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mine_gridview, null);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.item_pic = (ImageView) convertView.findViewById(R.id.item_pic);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MinePicsObj strpic = pics.get(position);
        holder.item_pic.setImageResource(strpic.getInteger());
        holder.title.setText(strpic.getTitle()==null?"":strpic.getTitle());
        return convertView;
    }

    class ViewHolder {
        TextView title;
        ImageView item_pic;

    }

}
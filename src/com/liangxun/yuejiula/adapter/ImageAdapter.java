package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.Goodstype;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.List;

/**
 * 市场分类
 */
public class ImageAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<Goodstype> goodstypes;
    private Context mContext;


    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }


    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    public ImageAdapter(List<Goodstype> goodstypes, Context mContext) {
        this.goodstypes = goodstypes;
        this.mContext = mContext;

    }

    @Override
    public int getCount() {
        return goodstypes.size();
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
            convertView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.grid_image_item, parent, false);
            holder.goodstype_item_cover = (ImageView) convertView.findViewById(R.id.goodstype_item_cover);
            holder.goodstype_item_title = (TextView) convertView.findViewById(R.id.goodstype_item_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Goodstype cell = goodstypes.get(position);//获得元素
        if (cell != null) {
            imageLoader.displayImage(cell.getTypeCover(), holder.goodstype_item_cover, UniversityApplication.txOptions, animateFirstListener);
            holder.goodstype_item_title.setText(cell.getTypeName());
        }
        return convertView;
    }

    class ViewHolder {
        ImageView goodstype_item_cover;
        TextView goodstype_item_title;
    }

}

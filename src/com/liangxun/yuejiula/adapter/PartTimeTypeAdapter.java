package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.PartTimeType;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.List;

/**
 * 市场分类
 */
public class PartTimeTypeAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<PartTimeType> goodstypes;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    public PartTimeTypeAdapter(List<PartTimeType> goodstypes, Context mContext) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.parttimetype_item, parent, false);
            holder.parttimetype_item_cover = (ImageView) convertView.findViewById(R.id.parttimetype_item_cover);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final PartTimeType cell = goodstypes.get(position);//获得元素
        if (cell != null) {
            if(!cell.getId().equals("0")){
                imageLoader.displayImage(cell.getCover(), holder.parttimetype_item_cover, UniversityApplication.txOptions, animateFirstListener);
            }else {
                holder.parttimetype_item_cover.setImageResource(R.drawable.icon_resume_all);
            }
        }
        return convertView;
    }

    class ViewHolder {
        ImageView parttimetype_item_cover;
    }

}
